package puppy.code.bloques;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.mundo.EntornoJuego;

/**
 * [GM-8: Template Method] Clase Concreta: "Bloque Virus".
 * Al destruirse EXPLOTA y daña a todos los bloques cercanos, lo que puede generar
 * reacciones en cadena con otros bloques virus.
 */
public class BloqueExplosivo extends Bloque {

    private static final int RESISTENCIA = 1;
    private static final int PUNTOS = 20;
    private static final float RADIO_EXPLOSION = 95f;
    private static final int DANIO_EXPLOSION = 2;
    private static final Color COLOR = new Color(0.9f, 0.16f, 0.12f, 1f);
    private static final Color COLOR_FUEGO = new Color(1f, 0.7f, 0.1f, 1f);

    private float tiempo;

    public BloqueExplosivo(float x, float y, float width, float height) {
        super(x, y, width, height, COLOR, RESISTENCIA, PUNTOS);
        this.tiempo = MathUtils.random(0f, 3f); // desfase para que no pulsen todos a la vez
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        tiempo += delta;
    }

    @Override
    protected void recibirDano(int danio) {
        resistencia -= danio;
    }

    @Override
    protected void reproducirEfecto(EntornoJuego entorno) {
        entorno.generarParticulas(getCentroX(), getCentroY(), COLOR_FUEGO, 8);
    }

    @Override
    protected void aplicarEfectoEspecial(EntornoJuego entorno) {
        entorno.generarParticulas(getCentroX(), getCentroY(), COLOR_FUEGO, 30);
        entorno.generarParticulas(getCentroX(), getCentroY(), color, 20);
        entorno.sacudirPantalla(6f, 0.25f);
        entorno.explotar(getCentroX(), getCentroY(), RADIO_EXPLOSION, DANIO_EXPLOSION);
    }

    @Override
    protected void dibujarCuerpo(ShapeRenderer shape) {
        dibujarBiselado(shape, color.r, color.g, color.b);
        // Franjas de advertencia
        shape.setColor(0.25f, 0.02f, 0.02f, 1f);
        shape.rectLine(x + 6f, y + 3f, x + 14f, y + height - 3f, 3f);
        shape.rectLine(x + width - 14f, y + 3f, x + width - 6f, y + height - 3f, 3f);
        // Núcleo pulsante
        float pulso = 0.5f + 0.5f * MathUtils.sin(tiempo * 6f);
        shape.setColor(1f, 0.85f, 0.2f, 0.35f + 0.4f * pulso);
        shape.circle(getCentroX(), getCentroY(), 7f + 2f * pulso, 16);
        shape.setColor(1f, 1f, 0.6f, 1f);
        shape.circle(getCentroX(), getCentroY(), 3.5f, 12);
    }
}
