package puppy.code.mundo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.ConfiguracionJuego;
import puppy.code.bloques.Bloque;
import puppy.code.bloques.BloqueNucleo;
import puppy.code.entidades.Colisionable;
import puppy.code.entidades.Plataforma;
import puppy.code.entidades.Bola;
import puppy.code.niveles.Nivel;
import puppy.code.gestores.GestorJuego;
import puppy.code.potenciadores.PotenciadorExpansion;
import puppy.code.potenciadores.PotenciadorVidaExtra;
import puppy.code.potenciadores.PotenciadorFuego;
import puppy.code.potenciadores.PotenciadorFallo;
import puppy.code.potenciadores.PotenciadorMultibola;
import puppy.code.potenciadores.Potenciador;
import puppy.code.potenciadores.PotenciadorLentitud;

/**
 * Simulación de un sector en juego: plataforma, bolas, bloques, power-ups y efectos.
 *
 * Separa la lógica del dominio de la pantalla (PantallaJuego), que solo maneja entrada,
 * estados y HUD. En el juego base todo esto estaba mezclado dentro de render().
 * Implementa {@link EntornoJuego}, la abstracción que usan bloques y power-ups.
 */
public class MundoJuego implements EntornoJuego {

    private static final int SUBPASOS = 4;
    private static final int MAX_BOLAS = 6;
    private static final int MAX_PARTICULAS = 700;
    private static final int DANIO_DETONACION = 99;
    private static final float INTERVALO_VIRUS_JEFE = 6.5f;
    private static final float DURACION_MENSAJE = 2.2f;
    private static final float TAMANO_GRILLA = 40f;
    private static final Color COLOR_CHISPA_PADDLE = new Color(0.4f, 0.95f, 1f, 1f);
    private static final Color COLOR_VIRUS = new Color(0.9f, 0.35f, 1f, 1f);

    private final Nivel nivel;
    private final Plataforma plataforma;
    private final List<Bola> bolas = new ArrayList<>();
    private final List<Bola> bolasPendientes = new ArrayList<>();
    private final List<Potenciador> potenciadores = new ArrayList<>();
    private final List<Particula> particulas = new ArrayList<>();
    private final List<TextoFlotante> textos = new ArrayList<>();
    private final GlyphLayout layout = new GlyphLayout();
    private final Color colorFondoSuperior;
    private final Color colorFondoInferior;
    private final Color colorGrilla;
    private final Color colorMensaje = new Color(Color.WHITE);

    private String mensaje;
    private float tiempoMensaje;
    private float tiempoSacudida;
    private float duracionSacudida;
    private float intensidadSacudida;
    private float sacudidaX;
    private float sacudidaY;
    private float tiempoTotal;
    private float temporizadorJefe;

    public MundoJuego(Nivel nivel) {
        this.nivel = nivel;
        this.plataforma = new Plataforma(ConfiguracionJuego.getAnchoMundo() / 2f, ConfiguracionJuego.getAlturaPlataforma());
        this.colorFondoSuperior = nivel.getColorFondoSuperior();
        this.colorFondoInferior = nivel.getColorFondoInferior();
        this.colorGrilla = nivel.getColorGrilla();
        prepararBolas();
    }

    // ============================ Ciclo de juego ============================

    /** Deja las bolas iniciales del sector en reposo sobre la plataforma. */
    public void prepararBolas() {
        bolas.clear();
        bolasPendientes.clear();
        for (int i = 0; i < nivel.getCantidadBolas(); i++) {
            Bola bola = new Bola(plataforma.getCentroX(), plataforma.getY() + plataforma.getHeight() + 9f,
                    nivel.getVelocidadBola());
            bola.seguir(plataforma);
            bolas.add(bola);
        }
    }

    /** Lanza todas las bolas que están en reposo (si son varias, en abanico). */
    public void lanzarBolas() {
        int enReposo = 0;
        for (Bola bola : bolas) {
            if (bola.estaQuieto()) {
                enReposo++;
            }
        }
        int i = 0;
        for (Bola bola : bolas) {
            if (bola.estaQuieto()) {
                if (enReposo == 1) {
                    bola.lanzar();
                } else {
                    bola.lanzarConAngulo(-30f + 60f * i / (enReposo - 1));
                }
                i++;
            }
        }
    }

    public void update(float delta) {
        plataforma.update(delta);
        actualizarBolas(delta);
        actualizarPotenciadores(delta);
        actualizarJefe(delta);
        for (Bloque bloque : nivel.getBloques()) {
            bloque.update(delta);
        }
        nivel.eliminarBloquesDestruidos();
        actualizarEfectos(delta);
    }

    /** Solo anima efectos (partículas, textos, sacudida). Se usa durante las transiciones. */
    public void actualizarEfectos(float delta) {
        tiempoTotal += delta;
        for (int i = particulas.size() - 1; i >= 0; i--) {
            Particula particula = particulas.get(i);
            particula.update(delta);
            if (!particula.estaViva()) {
                particulas.remove(i);
            }
        }
        for (int i = textos.size() - 1; i >= 0; i--) {
            TextoFlotante texto = textos.get(i);
            texto.update(delta);
            if (!texto.estaVivo()) {
                textos.remove(i);
            }
        }
        if (tiempoMensaje > 0f) {
            tiempoMensaje -= delta;
        }
        actualizarSacudida(delta);
    }

    private void actualizarBolas(float delta) {
        float paso = delta / SUBPASOS;
        for (Bola bola : bolas) {
            if (bola.estaQuieto()) {
                bola.seguir(plataforma);
                bola.update(delta);
                continue;
            }
            // Sub-pasos: evitan que una bola rápida atraviese un bloque entre dos frames
            for (int i = 0; i < SUBPASOS && !bola.seCayo(); i++) {
                bola.update(paso);
                procesarColisiones(bola);
            }
        }
        for (int i = bolas.size() - 1; i >= 0; i--) {
            if (bolas.get(i).seCayo()) {
                bolas.remove(i);
            }
        }
        bolas.addAll(bolasPendientes);
        bolasPendientes.clear();
    }

    private void procesarColisiones(Bola bola) {
        // [GM-4] La plataforma se trata como Colisionable...
        if (bola.colisionaCon(plataforma)) {
            if (bola.getVelocidadY() <= 0f) {
                bola.rebotarEnPlataforma(plataforma);
                plataforma.registrarGolpe();
                GestorJuego.getInstance().reiniciarCombo(); // [GM-6]
                generarParticulas(bola.getCentroX(), plataforma.getY() + plataforma.getHeight(), COLOR_CHISPA_PADDLE, 4);
            }
            return;
        }
        // [GM-4] ...y cada bloque también: el mismo método sirve para las 5 subclases de Bloque
        for (Bloque bloque : nivel.getBloques()) {
            if (bola.colisionaCon(bloque)) {
                boolean atraviesa = bola.atraviesaBloques() && bloque.isAtravesable();
                if (!atraviesa) {
                    bola.rebotarContra(bloque);
                }
                bloque.alGolpear(bola.getDanio(), this); // [GM-8] invoca el método plantilla
                if (!atraviesa) {
                    return; // un solo rebote por sub-paso
                }
            }
        }
    }

    private void actualizarPotenciadores(float delta) {
        for (int i = potenciadores.size() - 1; i >= 0; i--) {
            Potenciador potenciador = potenciadores.get(i);
            potenciador.update(delta);
            if (hayContacto(plataforma, potenciador)) {
                potenciador.activar(this);
            }
            if (potenciador.isRecogido() || potenciador.salioDePantalla()) {
                potenciadores.remove(i);
            }
        }
    }

    /** [GM-4] Detección genérica entre dos Colisionable cualesquiera. */
    private boolean hayContacto(Colisionable a, Colisionable b) {
        return a.isColisionable() && b.isColisionable() && a.getLimites().overlaps(b.getLimites());
    }

    /** En el sector final, el Núcleo-X dispara cápsulas de virus periódicamente. */
    private void actualizarJefe(float delta) {
        BloqueNucleo nucleo = nivel.getNucleo();
        if (nucleo == null || nucleo.isDestruido() || !hayBolasEnMovimiento()) {
            return;
        }
        temporizadorJefe += delta;
        if (temporizadorJefe >= INTERVALO_VIRUS_JEFE) {
            temporizadorJefe = 0f;
            potenciadores.add(new PotenciadorFallo(nucleo.getCentroX(), nucleo.getY() - 12f));
            mostrarTextoFlotante("¡VIRUS!", nucleo.getCentroX(), nucleo.getY() - 24f, COLOR_VIRUS);
        }
    }

    private void actualizarSacudida(float delta) {
        if (tiempoSacudida > 0f) {
            tiempoSacudida -= delta;
            float factor = duracionSacudida > 0f ? Math.max(0f, tiempoSacudida / duracionSacudida) : 0f;
            sacudidaX = MathUtils.random(-1f, 1f) * intensidadSacudida * factor;
            sacudidaY = MathUtils.random(-1f, 1f) * intensidadSacudida * factor;
        } else {
            sacudidaX = 0f;
            sacudidaY = 0f;
            intensidadSacudida = 0f;
        }
    }

    private Potenciador crearPotenciadorAleatorio(float x, float y) {
        int r = MathUtils.random(99);
        if (r < 20) {
            return new PotenciadorExpansion(x, y);
        } else if (r < 38) {
            return new PotenciadorFuego(x, y);
        } else if (r < 53) {
            return new PotenciadorLentitud(x, y);
        } else if (r < 68) {
            return new PotenciadorMultibola(x, y);
        } else if (r < 78) {
            return new PotenciadorVidaExtra(x, y);
        }
        return new PotenciadorFallo(x, y);
    }

    /** Lluvia de partículas para celebrar que se liberó el sector. */
    public void celebrar() {
        Color acento = nivel.getColorAcento();
        for (int i = 0; i < 6; i++) {
            float x = MathUtils.random(120f, ConfiguracionJuego.getAnchoMundo() - 120f);
            float y = MathUtils.random(220f, 480f);
            generarParticulas(x, y, i % 2 == 0 ? acento : Color.WHITE, 25);
        }
    }

    public void limpiarPotenciadores() {
        potenciadores.clear();
    }

    // ============================ EntornoJuego ============================

    @Override
    public void generarParticulas(float x, float y, Color color, int cantidad) {
        for (int i = 0; i < cantidad && particulas.size() < MAX_PARTICULAS; i++) {
            particulas.add(new Particula(x, y, color));
        }
    }

    @Override
    public void mostrarTextoFlotante(String texto, float x, float y, Color color) {
        textos.add(new TextoFlotante(texto, x, y, color));
    }

    @Override
    public void mostrarMensaje(String mensaje, Color color) {
        this.mensaje = mensaje;
        this.colorMensaje.set(color);
        this.tiempoMensaje = DURACION_MENSAJE;
    }

    @Override
    public void explotar(float centroX, float centroY, float radio, int danio) {
        List<Bloque> bloques = nivel.getBloques();
        float radioCuadrado = radio * radio;
        for (int i = 0; i < bloques.size(); i++) {
            Bloque bloque = bloques.get(i);
            if (bloque.isDestruido()) {
                continue;
            }
            float dx = bloque.getCentroX() - centroX;
            float dy = bloque.getCentroY() - centroY;
            if (dx * dx + dy * dy <= radioCuadrado) {
                bloque.alGolpear(danio, this); // puede provocar otras explosiones (reacción en cadena)
            }
        }
    }

    @Override
    public void intentarSoltarPotenciador(float x, float y, float multiplicadorProbabilidad) {
        if (MathUtils.random() < nivel.getProbabilidadPotenciador() * multiplicadorProbabilidad) {
            potenciadores.add(crearPotenciadorAleatorio(x, y));
        }
    }

    @Override
    public void sacudirPantalla(float intensidad, float duracion) {
        intensidadSacudida = Math.max(intensidadSacudida, intensidad);
        tiempoSacudida = Math.max(tiempoSacudida, duracion);
        duracionSacudida = tiempoSacudida;
    }

    @Override
    public void destruirTodosLosBloques() {
        List<Bloque> bloques = nivel.getBloques();
        for (int i = 0; i < bloques.size(); i++) {
            Bloque bloque = bloques.get(i);
            if (!bloque.isDestruido() && bloque.cuentaParaCompletar()) {
                bloque.alGolpear(DANIO_DETONACION, this);
            }
        }
    }

    @Override
    public Plataforma getPlataforma() {
        return plataforma;
    }

    @Override
    public List<Bola> getBolas() {
        return Collections.unmodifiableList(bolas);
    }

    @Override
    public void agregarBola(Bola bola) {
        if (bolas.size() + bolasPendientes.size() < MAX_BOLAS) {
            bolasPendientes.add(bola);
        }
    }

    // ============================ Consultas ============================

    public int getCantidadBolas() {
        return bolas.size() + bolasPendientes.size();
    }

    public boolean hayBolasEnReposo() {
        for (Bola bola : bolas) {
            if (bola.estaQuieto()) {
                return true;
            }
        }
        return false;
    }

    private boolean hayBolasEnMovimiento() {
        for (Bola bola : bolas) {
            if (!bola.estaQuieto()) {
                return true;
            }
        }
        return false;
    }

    public Nivel getNivel() {
        return nivel;
    }

    public float getSacudidaX() {
        return sacudidaX;
    }

    public float getSacudidaY() {
        return sacudidaY;
    }

    /** Estrategia temporal activa en la primera bola (para el HUD); null si no hay. */
    public Bola getBolaConEfecto() {
        for (Bola bola : bolas) {
            if (bola.getTiempoEstrategia() > 0f) {
                return bola;
            }
        }
        return null;
    }

    // ============================ Dibujo ============================

    public void draw(ShapeRenderer shape) {
        dibujarFondo(shape);
        for (Bloque bloque : nivel.getBloques()) {
            bloque.draw(shape); // polimorfismo: cada subclase se dibuja distinto
        }
        for (Potenciador potenciador : potenciadores) {
            potenciador.draw(shape);
        }
        plataforma.draw(shape);
        for (Bola bola : bolas) {
            bola.draw(shape);
        }
        for (Particula particula : particulas) {
            particula.draw(shape);
        }
    }

    private void dibujarFondo(ShapeRenderer shape) {
        float ancho = ConfiguracionJuego.getAnchoMundo();
        float techo = ConfiguracionJuego.getTechoJuego();
        shape.rect(0f, 0f, ancho, ConfiguracionJuego.getAltoMundo(),
                colorFondoInferior, colorFondoInferior, colorFondoSuperior, colorFondoSuperior);
        shape.setColor(colorGrilla.r, colorGrilla.g, colorGrilla.b, 0.10f);
        for (float x = 0f; x <= ancho; x += TAMANO_GRILLA) {
            shape.rect(x, 0f, 1f, techo);
        }
        float desplazamiento = (tiempoTotal * 18f) % TAMANO_GRILLA;
        for (float y = techo - desplazamiento; y > 0f; y -= TAMANO_GRILLA) {
            shape.rect(0f, y, ancho, 1f);
        }
        float pulso = 0.5f + 0.5f * MathUtils.sin(tiempoTotal * 3f);
        shape.setColor(1f, 0.15f, 0.3f, 0.3f + 0.25f * pulso);
        shape.rect(0f, 0f, ancho, 3f);
    }

    public void dibujarTextos(SpriteBatch batch, BitmapFont font) {
        font.getData().setScale(0.95f);
        for (Potenciador potenciador : potenciadores) {
            font.setColor(0.03f, 0.03f, 0.08f, 1f);
            layout.setText(font, potenciador.getEtiqueta());
            font.draw(batch, potenciador.getEtiqueta(), potenciador.getCentroX() - layout.width / 2f,
                    potenciador.getCentroY() + layout.height / 2f);
        }
        for (TextoFlotante texto : textos) {
            texto.draw(batch, font, layout);
        }
        if (tiempoMensaje > 0f && mensaje != null) {
            float alfa = Math.min(1f, tiempoMensaje / 0.4f);
            font.getData().setScale(1.6f);
            font.setColor(colorMensaje.r, colorMensaje.g, colorMensaje.b, alfa);
            layout.setText(font, mensaje);
            font.draw(batch, mensaje, (ConfiguracionJuego.getAnchoMundo() - layout.width) / 2f, 150f);
        }
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
    }
}
