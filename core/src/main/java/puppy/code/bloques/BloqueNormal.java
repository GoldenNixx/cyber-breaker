package puppy.code.bloques;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import puppy.code.mundo.EntornoJuego;

/**
 * [GM-8: Template Method] Clase Concreta: "Bloque de Datos".
 * Se destruye de un golpe, entrega 10 puntos y puede soltar un power-up.
 */
public class BloqueNormal extends Bloque {

    private static final int RESISTENCIA = 1;
    private static final int PUNTOS = 10;

    public BloqueNormal(float x, float y, float width, float height, Color color) {
        super(x, y, width, height, color, RESISTENCIA, PUNTOS);
    }

    @Override
    protected void recibirDano(int danio) {
        resistencia -= danio;
    }

    @Override
    protected void reproducirEfecto(EntornoJuego entorno) {
        entorno.generarParticulas(getCentroX(), getCentroY(), color, 6);
    }

    @Override
    protected void aplicarEfectoEspecial(EntornoJuego entorno) {
        entorno.generarParticulas(getCentroX(), getCentroY(), color, 12);
        entorno.intentarSoltarPotenciador(getCentroX(), getCentroY(), 1f);
    }

    @Override
    protected void dibujarCuerpo(ShapeRenderer shape) {
        dibujarBiselado(shape, color.r, color.g, color.b);
        // "bits" decorativos
        shape.setColor(color.r * 0.6f, color.g * 0.6f, color.b * 0.6f, 1f);
        shape.rect(x + 8f, y + 6f, 5f, 4f);
        shape.rect(x + 16f, y + 6f, 5f, 4f);
        shape.rect(x + width - 13f, y + 6f, 5f, 4f);
    }
}
