package puppy.code.entidades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

import puppy.code.ConfiguracionJuego;
import puppy.code.estrategia.EstrategiaMovimientoBola;
import puppy.code.estrategia.EstrategiaMovimientoNormal;

/**
 * "Paquete de datos" (la bola) que el jugador lanza contra el firewall.
 *
 * [GM-7: Patrón Strategy] Participante "Context".
 * Mantiene una referencia a la interfaz {@link EstrategiaMovimientoBola} y DELEGA en ella el
 * algoritmo de movimiento (ver {@link #update(float)}). La estrategia se cambia en tiempo de
 * ejecución con {@link #setStrategy(EstrategiaMovimientoBola)} cuando el jugador recoge un power-up
 * (fuego, modo lento) o un virus (glitch).
 *
 * [GM-4] Hereda de la clase abstracta {@link ObjetoJuego} y usa polimórficamente la interfaz
 * {@link Colisionable} para chocar con la plataforma y con cualquier bloque.
 */
public class Bola extends ObjetoJuego {

    private static final float RADIO = 8f;
    private static final float FACTOR_ACELERACION = 1.025f;
    private static final float MULTIPLICADOR_VELOCIDAD_MAXIMA = 1.45f;
    private static final float ANGULO_MAXIMO_PADDLE = 60f;
    private static final float PROPORCION_VERTICAL_MINIMA = 0.3f;
    private static final float VARIACION_REBOTE = 2.5f;
    private static final int LARGO_ESTELA = 10;
    private static final float INTERVALO_ESTELA = 0.02f;

    // [GM-5] Estado privado: solo se modifica mediante métodos de comportamiento
    private float velocidadX;
    private float velocidadY;
    private final float velocidadBase;
    private final float velocidadMaxima;
    private boolean estaQuieto;

    // [GM-7] Referencia a la Strategy (se programa contra la interfaz, no contra una clase concreta)
    private EstrategiaMovimientoBola strategy;
    private float tiempoEstrategia;

    private final float[] estelaX = new float[LARGO_ESTELA];
    private final float[] estelaY = new float[LARGO_ESTELA];
    private int indiceEstela;
    private float acumuladorEstela;

    /**
     * Crea una bola en reposo centrada en (centroX, centroY).
     *
     * @param velocidadBase rapidez inicial en unidades del mundo por segundo
     */
    public Bola(float centroX, float centroY, float velocidadBase) {
        super(centroX - RADIO, centroY - RADIO, RADIO * 2f, RADIO * 2f, Color.WHITE);
        this.velocidadBase = velocidadBase;
        this.velocidadMaxima = velocidadBase * MULTIPLICADOR_VELOCIDAD_MAXIMA;
        this.estaQuieto = true;
        // [GM-7] Estrategia por defecto con la que se configura el Context
        this.strategy = new EstrategiaMovimientoNormal();
        reiniciarEstela();
    }

    // ===================== [GM-7: Strategy] Métodos del Context =====================

    /** [GM-7] Setter que permite cambiar el algoritmo de movimiento en tiempo de ejecución. */
    public void setStrategy(EstrategiaMovimientoBola strategy) {
        this.strategy = strategy;
        this.tiempoEstrategia = 0f;
    }

    /** Cambia la estrategia solo por "duracion" segundos; luego vuelve a EstrategiaMovimientoNormal. */
    public void aplicarEstrategiaTemporal(EstrategiaMovimientoBola nuevaEstrategia, float duracion) {
        setStrategy(nuevaEstrategia);
        this.tiempoEstrategia = duracion;
    }

    public EstrategiaMovimientoBola getStrategy() {
        return strategy;
    }

    public float getTiempoEstrategia() {
        return tiempoEstrategia;
    }

    /** contextInterface(): el Context no sabe CÓMO se mueve la bola, se lo pide a su estrategia. */
    @Override
    public void update(float delta) {
        actualizarTemporizadorEstrategia(delta);
        if (estaQuieto) {
            return;
        }
        strategy.mover(this, delta); // [GM-7] delegación polimórfica del algoritmo
        rebotarEnParedes();
        registrarEstela(delta);
    }

    private void actualizarTemporizadorEstrategia(float delta) {
        if (tiempoEstrategia > 0f) {
            tiempoEstrategia -= delta;
            if (tiempoEstrategia <= 0f) {
                setStrategy(new EstrategiaMovimientoNormal());
            }
        }
    }

    // ===================== Movimiento =====================

    /** Desplaza la bola. Lo usan las estrategias concretas para no acceder a x/y directamente. */
    public void trasladar(float dx, float dy) {
        x += dx;
        y += dy;
    }

    /** Lanza la bola en reposo con un ángulo aleatorio leve hacia arriba. */
    public void lanzar() {
        lanzarConAngulo(MathUtils.random(-25f, 25f));
    }

    /** Lanza la bola con un ángulo en grados medido desde la vertical (0 = recto hacia arriba). */
    public void lanzarConAngulo(float anguloGrados) {
        estaQuieto = false;
        velocidadX = velocidadBase * MathUtils.sinDeg(anguloGrados);
        velocidadY = velocidadBase * MathUtils.cosDeg(anguloGrados);
    }

    /** Mientras está en reposo, la bola se mantiene sobre el centro de la plataforma. */
    public void seguir(Plataforma plataforma) {
        x = plataforma.getCentroX() - RADIO;
        y = plataforma.getY() + plataforma.getHeight() + 1f;
        reiniciarEstela();
    }

    private void rebotarEnParedes() {
        float ancho = ConfiguracionJuego.getAnchoMundo();
        float techo = ConfiguracionJuego.getTechoJuego();
        if (x < 0f) {
            x = 0f;
            velocidadX = Math.abs(velocidadX);
            aplicarVariacion();
        } else if (x + width > ancho) {
            x = ancho - width;
            velocidadX = -Math.abs(velocidadX);
            aplicarVariacion();
        }
        if (y + height > techo) {
            y = techo - height;
            velocidadY = -Math.abs(velocidadY);
            aplicarVariacion();
        }
    }

    // ===================== Colisiones ([GM-4] uso polimórfico de Colisionable) =====================

    /** Colisión círculo-rectángulo contra CUALQUIER objeto Colisionable (plataforma o bloque). */
    public boolean colisionaCon(Colisionable objeto) {
        if (!objeto.isColisionable()) {
            return false;
        }
        Rectangle r = objeto.getLimites();
        float cx = getCentroX();
        float cy = getCentroY();
        float puntoX = MathUtils.clamp(cx, r.x, r.x + r.width);
        float puntoY = MathUtils.clamp(cy, r.y, r.y + r.height);
        float dx = cx - puntoX;
        float dy = cy - puntoY;
        return dx * dx + dy * dy <= RADIO * RADIO;
    }

    /** Rebota contra un Colisionable eligiendo el eje de menor penetración y saca la bola del objeto. */
    public void rebotarContra(Colisionable objeto) {
        Rectangle r = objeto.getLimites();
        float cx = getCentroX();
        float cy = getCentroY();
        float solapeIzquierda = (cx + RADIO) - r.x;
        float solapeDerecha = (r.x + r.width) - (cx - RADIO);
        float solapeAbajo = (cy + RADIO) - r.y;
        float solapeArriba = (r.y + r.height) - (cy - RADIO);
        float minimoX = Math.min(solapeIzquierda, solapeDerecha);
        float minimoY = Math.min(solapeAbajo, solapeArriba);

        if (minimoX < minimoY) {
            if (solapeIzquierda < solapeDerecha) {
                x = r.x - width;
                velocidadX = -Math.abs(velocidadX);
            } else {
                x = r.x + r.width;
                velocidadX = Math.abs(velocidadX);
            }
        } else {
            if (solapeAbajo < solapeArriba) {
                y = r.y - height;
                velocidadY = -Math.abs(velocidadY);
            } else {
                y = r.y + r.height;
                velocidadY = Math.abs(velocidadY);
            }
        }
        aplicarVariacion();
    }

    /**
     * Rebote "arcade" en la plataforma: el ángulo de salida depende del punto de impacto
     * (centro = vertical, extremos = hasta 60°) y la bola acelera levemente en cada golpe.
     */
    public void rebotarEnPlataforma(Plataforma plataforma) {
        if (velocidadY > 0f) {
            return; // ya va subiendo: evita dobles rebotes
        }
        float relativo = (getCentroX() - plataforma.getCentroX()) / (plataforma.getWidth() / 2f);
        relativo = MathUtils.clamp(relativo, -1f, 1f);
        float angulo = relativo * ANGULO_MAXIMO_PADDLE;
        float rapidez = Math.min(getVelocidadActual() * FACTOR_ACELERACION, velocidadMaxima);
        velocidadX = rapidez * MathUtils.sinDeg(angulo);
        velocidadY = rapidez * MathUtils.cosDeg(angulo);
        y = plataforma.getY() + plataforma.getHeight();
    }

    /** Pequeña variación aleatoria del ángulo: evita trayectorias que se repitan para siempre. */
    private void aplicarVariacion() {
        float angulo = MathUtils.random(-VARIACION_REBOTE, VARIACION_REBOTE) * MathUtils.degreesToRadians;
        float cos = MathUtils.cos(angulo);
        float sin = MathUtils.sin(angulo);
        float nuevoX = velocidadX * cos - velocidadY * sin;
        float nuevoY = velocidadX * sin + velocidadY * cos;
        velocidadX = nuevoX;
        velocidadY = nuevoY;
        corregirAngulo();
    }

    /** Garantiza una componente vertical mínima para que la bola no quede rebotando casi horizontal. */
    private void corregirAngulo() {
        float rapidez = getVelocidadActual();
        if (rapidez == 0f) {
            return;
        }
        float minimoVertical = rapidez * PROPORCION_VERTICAL_MINIMA;
        if (Math.abs(velocidadY) < minimoVertical) {
            float signoY = velocidadY >= 0f ? 1f : -1f;
            float signoX = velocidadX >= 0f ? 1f : -1f;
            velocidadY = signoY * minimoVertical;
            velocidadX = signoX * (float) Math.sqrt(rapidez * rapidez - minimoVertical * minimoVertical);
        }
    }

    // ===================== Consultas =====================

    /** true si la bola atraviesa bloques sin rebotar (lo decide la estrategia actual). */
    public boolean atraviesaBloques() {
        return strategy.atraviesaBloques();
    }

    /** Daño que causa al golpear un bloque (lo decide la estrategia actual). */
    public int getDanio() {
        return strategy.getDanio();
    }

    public boolean seCayo() {
        return y + height < 0f;
    }

    public boolean estaQuieto() {
        return estaQuieto;
    }

    public float getVelocidadX() {
        return velocidadX;
    }

    public float getVelocidadY() {
        return velocidadY;
    }

    public float getVelocidadBase() {
        return velocidadBase;
    }

    public float getVelocidadActual() {
        return (float) Math.sqrt(velocidadX * velocidadX + velocidadY * velocidadY);
    }

    public float getRadio() {
        return RADIO;
    }

    // ===================== Dibujo =====================

    private void reiniciarEstela() {
        for (int i = 0; i < LARGO_ESTELA; i++) {
            estelaX[i] = getCentroX();
            estelaY[i] = getCentroY();
        }
        indiceEstela = 0;
        acumuladorEstela = 0f;
    }

    private void registrarEstela(float delta) {
        acumuladorEstela += delta;
        if (acumuladorEstela >= INTERVALO_ESTELA) {
            acumuladorEstela = 0f;
            estelaX[indiceEstela] = getCentroX();
            estelaY[indiceEstela] = getCentroY();
            indiceEstela = (indiceEstela + 1) % LARGO_ESTELA;
        }
    }

    @Override
    public void draw(ShapeRenderer shape) {
        Color c = strategy.getColor(); // el color depende de la estrategia activa
        for (int i = 0; i < LARGO_ESTELA; i++) {
            int indice = (indiceEstela - 1 - i + LARGO_ESTELA) % LARGO_ESTELA;
            float factor = 1f - i / (float) LARGO_ESTELA;
            shape.setColor(c.r, c.g, c.b, 0.35f * factor);
            shape.circle(estelaX[indice], estelaY[indice], RADIO * (0.4f + 0.6f * factor), 12);
        }
        float cx = getCentroX();
        float cy = getCentroY();
        shape.setColor(c.r, c.g, c.b, 0.25f);
        shape.circle(cx, cy, RADIO * 1.9f, 20);
        shape.setColor(c.r, c.g, c.b, 1f);
        shape.circle(cx, cy, RADIO, 20);
        shape.setColor(1f, 1f, 1f, 0.85f);
        shape.circle(cx - RADIO * 0.3f, cy + RADIO * 0.3f, RADIO * 0.35f, 10);
    }
}
