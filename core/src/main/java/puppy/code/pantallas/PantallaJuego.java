package puppy.code.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.JuegoBlockBreaker;
import puppy.code.ConfiguracionJuego;
import puppy.code.entidades.Bola;
import puppy.code.niveles.ConstructorNivelArena;
import puppy.code.niveles.Nivel;
import puppy.code.niveles.ConstructorNivel;
import puppy.code.niveles.DirectorNivel;
import puppy.code.niveles.ConstructorNivelEstandar;
import puppy.code.gestores.GestorJuego;
import puppy.code.mundo.MundoJuego;

/**
 * Pantalla de juego: lee el teclado, controla los estados de la partida (listo, jugando,
 * pausa, sector liberado) y dibuja el HUD. La simulación la delega en {@link MundoJuego}.
 *
 * Hija de la clase abstracta {@link PantallaBase} [GM-4]. Es el "Cliente" del patrón Builder
 * [GM-9] (elige el ConcreteBuilder y le pide al Director construir cada sector) y uno de los
 * clientes del Singleton GestorJuego [GM-6].
 */
public class PantallaJuego extends PantallaBase {

    private static final int ESTADO_LISTO = 0;
    private static final int ESTADO_JUGANDO = 1;
    private static final int ESTADO_PAUSA = 2;
    private static final int ESTADO_SECTOR_LIBERADO = 3;
    private static final float DURACION_TRANSICION = 3f;
    private static final float ALTO_HUD = ConfiguracionJuego.getAltoHud();
    private static final Color FONDO_HUD = new Color(0.01f, 0.02f, 0.06f, 0.94f);
    private static final Color GRIS = new Color(0.6f, 0.66f, 0.78f, 1f);
    private static final Color AMARILLO = new Color(1f, 0.9f, 0.3f, 1f);
    private static final Color VERDE = new Color(0.35f, 1f, 0.55f, 1f);
    private static final Color ROSADO = new Color(1f, 0.3f, 0.5f, 1f);

    private MundoJuego mundo;
    private Nivel nivel;
    private Color colorAcento;
    private int estado;
    private int estadoAntesDePausa;
    private float temporizador;
    private float tiempo;
    private int bonoSector;

    public PantallaJuego(JuegoBlockBreaker juego) {
        super(juego);
        cargarNivel(GestorJuego.getInstance().getNivelActual()); // [GM-6]
    }

    // ===================== [GM-9: Builder] Cliente =====================

    /** Configura el Builder, se lo entrega al Director y obtiene el Nivel (Product). */
    private void cargarNivel(int numero) {
        ConstructorNivel constructor = elegirConstructor(numero);
        DirectorNivel director = new DirectorNivel(constructor);
        nivel = construirSector(director, numero);
        mundo = new MundoJuego(nivel);
        colorAcento = nivel.getColorAcento();
        estado = ESTADO_LISTO;
        temporizador = 0f;
        Gdx.app.log("CyberBreaker", "Nivel construido: " + nivel);
    }

    /** Sectores 1 y 3 usan la cuadrícula estándar; los sectores 2, 4 y 5 la arena en diamante. */
    private ConstructorNivel elegirConstructor(int numero) {
        if (numero == 1 || numero == 3) {
            return new ConstructorNivelEstandar();
        }
        return new ConstructorNivelArena();
    }

    private Nivel construirSector(DirectorNivel director, int numero) {
        switch (numero) {
            case 1:
                return director.construirNivelTutorial();
            case 2:
                return director.construirNivelIntermedio();
            case 3:
                return director.construirNivelAvanzado();
            case 4:
                return director.construirNivelExperto();
            default:
                return director.construirNivelJefe();
        }
    }

    // ===================== Lógica =====================

    @Override
    protected void actualizar(float delta) {
        tiempo += delta;
        temporizador += delta;
        switch (estado) {
            case ESTADO_LISTO:
                if (pidioPausa()) {
                    pausar();
                    break;
                }
                leerMovimiento();
                mundo.update(delta);
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                    mundo.lanzarBolas();
                    estado = ESTADO_JUGANDO;
                }
                break;
            case ESTADO_JUGANDO:
                if (pidioPausa()) {
                    pausar();
                    break;
                }
                leerMovimiento();
                mundo.update(delta);
                if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && mundo.hayBolasEnReposo()) {
                    mundo.lanzarBolas();
                }
                verificarEstadoPartida();
                break;
            case ESTADO_PAUSA:
                if (pidioPausa()) {
                    estado = estadoAntesDePausa;
                } else if (Gdx.input.isKeyJustPressed(Input.Keys.Q)) {
                    GestorJuego.getInstance().guardarRecord();
                    juego.setScreen(new PantallaMenu(juego));
                    return;
                }
                break;
            case ESTADO_SECTOR_LIBERADO:
                mundo.actualizarEfectos(delta);
                if (temporizador >= DURACION_TRANSICION || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                    avanzarSector();
                    return;
                }
                break;
            default:
                break;
        }
        centrarCamara(mundo.getSacudidaX(), mundo.getSacudidaY());
    }

    private boolean pidioPausa() {
        return Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE);
    }

    private void pausar() {
        estadoAntesDePausa = estado;
        estado = ESTADO_PAUSA;
    }

    private void leerMovimiento() {
        int direccion = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            direccion--;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            direccion++;
        }
        mundo.getPlataforma().setDireccion(direccion);
    }

    private void verificarEstadoPartida() {
        GestorJuego gestor = GestorJuego.getInstance(); // [GM-6]
        if (nivel.isCompletado()) {
            bonoSector = gestor.bonificarNivelCompletado();
            gestor.guardarRecord();
            mundo.limpiarPotenciadores();
            mundo.celebrar();
            estado = ESTADO_SECTOR_LIBERADO;
            temporizador = 0f;
            return;
        }
        if (mundo.getCantidadBolas() == 0) {
            gestor.perderVida();
            if (gestor.isFinDelJuego()) {
                gestor.guardarRecord();
                juego.setScreen(new PantallaFinJuego(juego, false));
                return;
            }
            mundo.prepararBolas();
            mundo.sacudirPantalla(8f, 0.35f);
            mundo.mostrarMensaje("¡CONEXIÓN PERDIDA!  -1 VIDA", ROSADO);
            estado = ESTADO_LISTO;
            temporizador = 0f;
        }
    }

    private void avanzarSector() {
        GestorJuego gestor = GestorJuego.getInstance();
        if (gestor.esUltimoNivel()) {
            gestor.guardarRecord();
            juego.setScreen(new PantallaFinJuego(juego, true));
        } else {
            gestor.avanzarNivel();
            cargarNivel(gestor.getNivelActual());
        }
    }

    // ===================== Dibujo =====================

    @Override
    protected void dibujarFormas(ShapeRenderer shape) {
        mundo.draw(shape);
        dibujarHud(shape);
        dibujarIndicadoresEfecto(shape);
        if (estado == ESTADO_LISTO || estado == ESTADO_SECTOR_LIBERADO) {
            dibujarFranja(shape, 185f, 115f);
        } else if (estado == ESTADO_PAUSA) {
            shape.setColor(0f, 0f, 0f, 0.65f);
            shape.rect(0f, 0f, ConfiguracionJuego.getAnchoMundo(), ConfiguracionJuego.getTechoJuego());
        }
    }

    private void dibujarHud(ShapeRenderer shape) {
        float techo = ConfiguracionJuego.getTechoJuego();
        shape.setColor(FONDO_HUD);
        shape.rect(0f, techo, ConfiguracionJuego.getAnchoMundo(), ALTO_HUD);
        shape.setColor(colorAcento.r, colorAcento.g, colorAcento.b, 1f);
        shape.rect(0f, techo, ConfiguracionJuego.getAnchoMundo(), 2f);
        GestorJuego gestor = GestorJuego.getInstance();
        for (int i = 0; i < gestor.getVidasMaximas(); i++) {
            boolean llena = i < gestor.getVidas();
            dibujarCorazon(shape, 676f + i * 24f, techo + 16f, 6f, llena);
        }
    }

    private void dibujarCorazon(ShapeRenderer shape, float cx, float cy, float tamano, boolean lleno) {
        if (lleno) {
            shape.setColor(ROSADO);
        } else {
            shape.setColor(0.25f, 0.1f, 0.18f, 1f);
        }
        shape.circle(cx - tamano * 0.5f, cy, tamano * 0.6f, 12);
        shape.circle(cx + tamano * 0.5f, cy, tamano * 0.6f, 12);
        shape.triangle(cx - tamano * 1.08f, cy - tamano * 0.2f, cx + tamano * 1.08f, cy - tamano * 0.2f,
                cx, cy - tamano * 1.45f);
    }

    private void dibujarIndicadoresEfecto(ShapeRenderer shape) {
        Bola bola = mundo.getBolaConEfecto();
        if (bola != null) {
            Color c = bola.getStrategy().getColor();
            shape.setColor(c.r, c.g, c.b, 0.9f);
            shape.rect(12f, 8f, 90f * Math.min(1f, bola.getTiempoEstrategia() / 8f), 4f);
        }
        float expansion = mundo.getPlataforma().getTiempoExpansion();
        if (expansion > 0f) {
            shape.setColor(0.3f, 1f, 0.45f, 0.9f);
            shape.rect(ConfiguracionJuego.getAnchoMundo() - 102f, 8f, 90f * Math.min(1f, expansion / 10f), 4f);
        }
    }

    private void dibujarFranja(ShapeRenderer shape, float y, float alto) {
        shape.setColor(0f, 0f, 0f, 0.6f);
        shape.rect(0f, y, ConfiguracionJuego.getAnchoMundo(), alto);
        shape.setColor(colorAcento.r, colorAcento.g, colorAcento.b, 0.9f);
        shape.rect(0f, y, ConfiguracionJuego.getAnchoMundo(), 2f);
        shape.rect(0f, y + alto - 2f, ConfiguracionJuego.getAnchoMundo(), 2f);
    }

    @Override
    protected void dibujarTextos(SpriteBatch batch, BitmapFont font) {
        mundo.dibujarTextos(batch, font);
        dibujarTextosHud(batch, font);
        dibujarTextosEfecto(batch, font);
        float centro = ConfiguracionJuego.getAnchoMundo() / 2f;
        float parpadeo = 0.45f + 0.55f * Math.abs(MathUtils.sin(tiempo * 3f));

        if (estado == ESTADO_LISTO) {
            String titulo = "SECTOR " + nivel.getNumero() + "  ·  " + nivel.getNombre().toUpperCase();
            dibujarTextoCentrado(batch, font, titulo, centro, 284f, 1.9f, colorAcento);
            dibujarTextoCentrado(batch, font, nivel.getDescripcion(), centro, 246f, 1.05f, Color.WHITE);
            dibujarTextoCentrado(batch, font, "Presiona ESPACIO para lanzar", centro, 216f, 1.15f,
                    new Color(AMARILLO.r, AMARILLO.g, AMARILLO.b, parpadeo));
        } else if (estado == ESTADO_SECTOR_LIBERADO) {
            GestorJuego gestor = GestorJuego.getInstance();
            dibujarTextoCentrado(batch, font, "¡SECTOR LIBERADO!", centro, 284f, 2.2f, VERDE);
            dibujarTextoCentrado(batch, font, "Bono de sector: +" + bonoSector + " pts", centro, 244f, 1.15f, Color.WHITE);
            String siguiente = gestor.esUltimoNivel() ? "Generando informe final..."
                    : "Conectando con el sector " + (gestor.getNivelActual() + 1) + "...";
            dibujarTextoCentrado(batch, font, siguiente, centro, 214f, 1f, GRIS);
        } else if (estado == ESTADO_PAUSA) {
            dibujarTextoCentrado(batch, font, "PAUSA", centro, 370f, 3f, Color.WHITE);
            dibujarTextoCentrado(batch, font, "P / ESC: continuar", centro, 290f, 1.2f, colorAcento);
            dibujarTextoCentrado(batch, font, "Q: abandonar y volver al menú", centro, 260f, 1.05f, GRIS);
        }
    }

    private void dibujarTextosHud(SpriteBatch batch, BitmapFont font) {
        GestorJuego gestor = GestorJuego.getInstance(); // [GM-6]
        float etiquetaY = ConfiguracionJuego.getAltoMundo() - 5f;
        float valorY = ConfiguracionJuego.getAltoMundo() - 19f;
        dibujarTexto(batch, font, "PUNTAJE", 14f, etiquetaY, 0.75f, GRIS);
        dibujarTexto(batch, font, String.format("%06d", gestor.getPuntaje()), 14f, valorY, 1.25f, Color.WHITE);
        dibujarTexto(batch, font, "RÉCORD", 130f, etiquetaY, 0.75f, GRIS);
        dibujarTexto(batch, font, String.format("%06d", gestor.getRecord()), 130f, valorY, 1.25f, AMARILLO);
        dibujarTexto(batch, font, "SECTOR", 250f, etiquetaY, 0.75f, GRIS);
        dibujarTexto(batch, font, gestor.getNivelActual() + "/" + gestor.getTotalNiveles() + "  " + nivel.getNombre(),
                250f, valorY, 1.1f, colorAcento);
        dibujarTexto(batch, font, "BLOQUES", 490f, etiquetaY, 0.75f, GRIS);
        dibujarTexto(batch, font, String.valueOf(nivel.contarBloquesRestantes()), 490f, valorY, 1.1f, Color.WHITE);
        dibujarTexto(batch, font, "COMBO", 570f, etiquetaY, 0.75f, GRIS);
        int multiplicador = gestor.getMultiplicador();
        dibujarTexto(batch, font, "x" + multiplicador, 570f, valorY, 1.1f, multiplicador > 1 ? AMARILLO : Color.WHITE);
        dibujarTexto(batch, font, "VIDAS", 662f, etiquetaY, 0.75f, GRIS);
    }

    private void dibujarTextosEfecto(SpriteBatch batch, BitmapFont font) {
        Bola bola = mundo.getBolaConEfecto();
        if (bola != null) {
            String texto = "BOLA: " + bola.getStrategy().getNombre().toUpperCase()
                    + String.format(" %.1fs", bola.getTiempoEstrategia());
            dibujarTexto(batch, font, texto, 12f, 30f, 0.85f, bola.getStrategy().getColor());
        }
        float expansion = mundo.getPlataforma().getTiempoExpansion();
        if (expansion > 0f) {
            String texto = String.format("EXPANSIÓN %.1fs", expansion);
            dibujarTexto(batch, font, texto, ConfiguracionJuego.getAnchoMundo() - 102f, 30f, 0.85f, VERDE);
        }
    }
}
