package puppy.code.bloques;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import puppy.code.entidades.Colisionable;
import puppy.code.entidades.ObjetoJuego;
import puppy.code.gestores.GestorJuego;
import puppy.code.mundo.EntornoJuego;

/**
 * [GM-8: Patrón Template Method] Participante "Clase Abstracta (Padre)".
 *
 * Define en {@link #alGolpear(int, EntornoJuego)} el esqueleto FIJO de lo que ocurre cuando un
 * bloque recibe un impacto. Los pasos variables los implementan las subclases (Clases
 * Concretas): {@link BloqueNormal}, {@link BloqueReforzado}, {@link BloqueExplosivo},
 * {@link BloqueMetalico} y {@link BloqueNucleo}.
 *
 * También es [GM-4] clase abstracta (hija de ObjetoJuego y padre de 5 clases) e implementa la
 * interfaz {@link Colisionable} [GM-4].
 */
public abstract class Bloque extends ObjetoJuego implements Colisionable {

    private static final float DURACION_DESTELLO = 0.12f;

    // [GM-5] protected: las subclases los usan; desde afuera solo hay getters
    protected int resistencia;
    protected final int resistenciaMaxima;
    protected final int puntos;

    private boolean destruido;
    private float destello;
    private int impactosRecibidos;

    protected Bloque(float x, float y, float width, float height, Color color, int resistencia, int puntos) {
        super(x, y, width, height, color);
        this.resistencia = resistencia;
        this.resistenciaMaxima = resistencia;
        this.puntos = puntos;
    }

    // =================== [GM-8: Template Method - Método Plantilla Final] ===================

    /**
     * MÉTODO PLANTILLA. Secuencia fija para procesar un impacto:
     * 1) registrarImpacto  2) recibirDano  3) reproducirEfecto  4) verificarDestruccion
     * 5) aplicarEfectoEspecial  6) otorgarPuntaje.
     * Es final: ninguna subclase puede alterar el orden del algoritmo, solo sus pasos.
     *
     * @param danio   daño del impacto (1 normal, 2 bola de fuego, 2 explosión)
     * @param entorno servicios del mundo de juego (partículas, explosiones, power-ups...)
     */
    public final void alGolpear(int danio, EntornoJuego entorno) {
        if (destruido) {
            return;
        }
        registrarImpacto();                 // paso 1: concreto, común a todos
        recibirDano(danio);                 // paso 2: abstracto
        reproducirEfecto(entorno);          // paso 3: abstracto
        if (verificarDestruccion()) {       // paso 4: concreto con implementación por defecto (hook)
            destruido = true;
            aplicarEfectoEspecial(entorno); // paso 5: abstracto
            otorgarPuntaje(entorno);        // paso 6: concreto, común a todos
        }
    }

    /** Paso 1 (concreto): activa el destello blanco y cuenta el impacto. */
    protected void registrarImpacto() {
        impactosRecibidos++;
        destello = DURACION_DESTELLO;
    }

    /** Paso 2 (abstracto): cómo pierde resistencia este tipo de bloque. */
    protected abstract void recibirDano(int danio);

    /** Paso 3 (abstracto): efecto visual del impacto. */
    protected abstract void reproducirEfecto(EntornoJuego entorno);

    /** Paso 4 (hook): por defecto se destruye al quedar sin resistencia. BloqueMetalico lo redefine. */
    protected boolean verificarDestruccion() {
        return resistencia <= 0;
    }

    /** Paso 5 (abstracto): efecto especial al ser destruido (power-up, explosión, detonación...). */
    protected abstract void aplicarEfectoEspecial(EntornoJuego entorno);

    /** Paso 6 (concreto): suma puntos usando el Singleton GestorJuego [GM-6]. */
    protected void otorgarPuntaje(EntornoJuego entorno) {
        int ganados = GestorJuego.getInstance().registrarBloqueDestruido(puntos);
        entorno.mostrarTextoFlotante("+" + ganados, getCentroX(), getCentroY(), color);
    }

    // =================== Comportamiento común ===================

    /** Los bloques que cuentan deben destruirse para completar el sector (los metálicos no). */
    public boolean cuentaParaCompletar() {
        return true;
    }

    /** Si una bola de fuego puede atravesarlo sin rebotar. */
    public boolean isAtravesable() {
        return true;
    }

    @Override
    public boolean isColisionable() {
        return !destruido;
    }

    @Override
    public void update(float delta) {
        if (destello > 0f) {
            destello -= delta;
        }
    }

    @Override
    public void draw(ShapeRenderer shape) {
        if (destruido) {
            return;
        }
        dibujarCuerpo(shape);
        if (destello > 0f) {
            shape.setColor(1f, 1f, 1f, 0.8f * destello / DURACION_DESTELLO);
            shape.rect(x, y, width, height);
        }
    }

    /** Cada tipo de bloque decide su apariencia. */
    protected abstract void dibujarCuerpo(ShapeRenderer shape);

    /** Utilidad de dibujo compartida: rectángulo con borde oscuro y brillo superior. */
    protected void dibujarBiselado(ShapeRenderer shape, float r, float g, float b) {
        shape.setColor(r * 0.45f, g * 0.45f, b * 0.45f, 1f);
        shape.rect(x, y, width, height);
        shape.setColor(r, g, b, 1f);
        shape.rect(x + 2f, y + 2f, width - 4f, height - 4f);
        shape.setColor(Math.min(1f, r + 0.35f), Math.min(1f, g + 0.35f), Math.min(1f, b + 0.35f), 1f);
        shape.rect(x + 2f, y + height - 5f, width - 4f, 3f);
    }

    public boolean isDestruido() {
        return destruido;
    }

    public int getResistencia() {
        return resistencia;
    }

    public int getResistenciaMaxima() {
        return resistenciaMaxima;
    }

    public int getPuntos() {
        return puntos;
    }

    public int getImpactosRecibidos() {
        return impactosRecibidos;
    }

    /** Proporción de vida restante entre 0 y 1. */
    protected float getProporcionVida() {
        return Math.max(0f, resistencia / (float) resistenciaMaxima);
    }
}
