package puppy.code.entidades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.ConfiguracionJuego;

/**
 * Plataforma de contención "AEGIS" controlada por el jugador.
 *
 * Hereda de {@link ObjetoJuego} [GM-4] e implementa {@link Colisionable} [GM-4].
 *
 * Cambios respecto al juego base: ya no lee el teclado dentro de draw() (mezclaba lógica y
 * dibujo); ahora PantallaJuego le indica una dirección y el movimiento depende del tiempo (delta),
 * por lo que es igual de rápido en cualquier computador. Además puede expandirse
 * temporalmente con el power-up de expansión.
 */
public class Plataforma extends ObjetoJuego implements Colisionable {

    private static final float ANCHO_BASE = 110f;
    private static final float ALTO = 14f;
    private static final float VELOCIDAD = 560f;
    private static final float FACTOR_EXPANSION = 1.6f;
    private static final float DURACION_DESTELLO = 0.12f;

    // [GM-5] Estado interno privado
    private int direccion;
    private float anchoObjetivo;
    private float tiempoExpansion;
    private float destello;

    public Plataforma(float centroX, float y) {
        super(centroX - ANCHO_BASE / 2f, y, ANCHO_BASE, ALTO, new Color(0.15f, 0.85f, 1f, 1f));
        this.anchoObjetivo = ANCHO_BASE;
    }

    /** -1 = izquierda, 0 = quieto, 1 = derecha. */
    public void setDireccion(int direccion) {
        this.direccion = MathUtils.clamp(direccion, -1, 1);
    }

    @Override
    public void update(float delta) {
        if (tiempoExpansion > 0) {
            tiempoExpansion -= delta;
            if (tiempoExpansion <= 0) {
                tiempoExpansion = 0;
                anchoObjetivo = ANCHO_BASE;
            }
        }
        // Animación suave del ancho manteniendo el centro
        float centro = getCentroX();
        width += (anchoObjetivo - width) * Math.min(1f, delta * 10f);
        x = centro - width / 2f;

        x += direccion * VELOCIDAD * delta;
        x = MathUtils.clamp(x, 0f, ConfiguracionJuego.getAnchoMundo() - width);

        if (destello > 0) {
            destello -= delta;
        }
    }

    /** Ensancha la plataforma durante "duracion" segundos (power-up de expansión). */
    public void expandir(float duracion) {
        anchoObjetivo = ANCHO_BASE * FACTOR_EXPANSION;
        tiempoExpansion = duracion;
    }

    /** Efecto visual cuando la bola rebota en la plataforma. */
    public void registrarGolpe() {
        destello = DURACION_DESTELLO;
    }

    public float getTiempoExpansion() {
        return tiempoExpansion;
    }

    @Override
    public boolean isColisionable() {
        return true;
    }

    @Override
    public void draw(ShapeRenderer shape) {
        // Halo de energía
        shape.setColor(color.r, color.g, color.b, 0.22f);
        shape.rect(x - 5, y - 5, width + 10, height + 10);
        // Cuerpo principal (blanco por un instante al recibir la bola)
        if (destello > 0) {
            shape.setColor(1f, 1f, 1f, 1f);
        } else {
            shape.setColor(color);
        }
        shape.rect(x, y, width, height);
        // Línea central y propulsores laterales
        shape.setColor(0.03f, 0.18f, 0.32f, 1f);
        shape.rect(x + 8, y + height / 2f - 1f, width - 16, 2f);
        shape.setColor(1f, 0.25f, 0.6f, 1f);
        shape.rect(x, y, 7, height);
        shape.rect(x + width - 7, y, 7, height);
        // Brillo superior
        shape.setColor(1f, 1f, 1f, 0.45f);
        shape.rect(x + 7, y + height - 3, width - 14, 2f);
    }
}
