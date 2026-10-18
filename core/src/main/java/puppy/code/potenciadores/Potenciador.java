package puppy.code.potenciadores;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.entidades.Colisionable;
import puppy.code.entidades.ObjetoJuego;
import puppy.code.gestores.GestorJuego;
import puppy.code.mundo.EntornoJuego;

/**
 * [GM-4: Clase abstracta] Cápsula de mejora que cae cuando se destruye un bloque.
 *
 * Es padre de 6 clases concretas: {@link PotenciadorExpansion}, {@link PotenciadorFuego},
 * {@link PotenciadorLentitud}, {@link PotenciadorFallo}, {@link PotenciadorVidaExtra} y
 * {@link PotenciadorMultibola}. Hereda de ObjetoJuego e implementa {@link Colisionable} [GM-4]
 * (la plataforma la atrapa al tocarla).
 *
 * {@link #activar(EntornoJuego)} es final y fija la secuencia común (marcar como recogida,
 * aplicar el efecto propio de cada subclase, mostrar mensaje y sumar puntos).
 */
public abstract class Potenciador extends ObjetoJuego implements Colisionable {

    private static final float ANCHO = 36f;
    private static final float ALTO = 16f;
    private static final float VELOCIDAD_CAIDA = 140f;
    private static final int PUNTOS_AL_RECOGER = 50;

    private boolean recogido;
    private float tiempo;

    protected Potenciador(float centroX, float centroY, Color color) {
        super(centroX - ANCHO / 2f, centroY - ALTO / 2f, ANCHO, ALTO, color);
    }

    /** Secuencia común al ser atrapada por la plataforma. */
    public final void activar(EntornoJuego entorno) {
        if (recogido) {
            return;
        }
        recogido = true;
        aplicarEfecto(entorno);
        entorno.mostrarMensaje(getMensaje(), color);
        entorno.generarParticulas(getCentroX(), getCentroY(), color, 14);
        GestorJuego.getInstance().sumarPuntos(PUNTOS_AL_RECOGER); // [GM-6] uso del Singleton
    }

    /** Efecto propio de cada power-up (polimorfismo). */
    protected abstract void aplicarEfecto(EntornoJuego entorno);

    /** Texto corto que se dibuja sobre la cápsula. */
    public abstract String getEtiqueta();

    /** Mensaje grande que aparece al recogerla. */
    protected abstract String getMensaje();

    /** Descripción para la pantalla de ayuda. */
    public abstract String getDescripcion();

    @Override
    public void update(float delta) {
        tiempo += delta;
        y -= VELOCIDAD_CAIDA * delta;
    }

    @Override
    public void draw(ShapeRenderer shape) {
        float pulso = 0.5f + 0.5f * MathUtils.sin(tiempo * 8f);
        float radio = height / 2f;
        shape.setColor(color.r, color.g, color.b, 0.18f + 0.22f * pulso);
        shape.rect(x - 4f, y - 4f, width + 8f, height + 8f);
        shape.setColor(color);
        shape.rect(x + radio, y, width - 2f * radio, height);
        shape.circle(x + radio, y + radio, radio, 16);
        shape.circle(x + width - radio, y + radio, radio, 16);
        shape.setColor(1f, 1f, 1f, 0.55f);
        shape.rect(x + radio, y + height - 4f, width - 2f * radio, 2f);
    }

    public boolean isRecogido() {
        return recogido;
    }

    public boolean salioDePantalla() {
        return y + height < 0f;
    }

    @Override
    public boolean isColisionable() {
        return !recogido;
    }
}
