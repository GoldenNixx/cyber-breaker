package puppy.code.bloques;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import puppy.code.mundo.EntornoJuego;

/**
 * [GM-8: Template Method] Clase Concreta: "Bloque Cifrado".
 * Necesita 3 golpes; muestra su resistencia restante como indicadores y grietas.
 * Al romperse tiene el doble de probabilidad de soltar un power-up.
 */
public class BloqueReforzado extends Bloque {

    private static final int RESISTENCIA = 3;
    private static final int PUNTOS = 30;
    private static final Color COLOR = new Color(0.22f, 0.3f, 0.85f, 1f);
    private static final Color COLOR_CHISPAS = new Color(0.6f, 0.95f, 1f, 1f);

    public BloqueReforzado(float x, float y, float width, float height) {
        super(x, y, width, height, COLOR, RESISTENCIA, PUNTOS);
    }

    @Override
    protected void recibirDano(int danio) {
        resistencia -= danio;
    }

    @Override
    protected void reproducirEfecto(EntornoJuego entorno) {
        entorno.generarParticulas(getCentroX(), getCentroY(), COLOR_CHISPAS, 8);
    }

    @Override
    protected void aplicarEfectoEspecial(EntornoJuego entorno) {
        entorno.generarParticulas(getCentroX(), getCentroY(), color, 18);
        entorno.intentarSoltarPotenciador(getCentroX(), getCentroY(), 2f);
    }

    @Override
    protected void dibujarCuerpo(ShapeRenderer shape) {
        float vida = getProporcionVida();
        dibujarBiselado(shape, color.r * (0.55f + 0.45f * vida), color.g * (0.55f + 0.45f * vida), color.b);
        // Marco cian de "cifrado"
        shape.setColor(0.45f, 0.95f, 1f, 1f);
        shape.rect(x + 3f, y + 3f, width - 6f, 2f);
        // Indicadores de resistencia restante
        float tamano = 6f;
        float separacion = 4f;
        float anchoTotal = resistenciaMaxima * tamano + (resistenciaMaxima - 1) * separacion;
        float inicioX = getCentroX() - anchoTotal / 2f;
        for (int i = 0; i < resistenciaMaxima; i++) {
            if (i < resistencia) {
                shape.setColor(0.55f, 1f, 1f, 1f);
            } else {
                shape.setColor(0.08f, 0.1f, 0.3f, 1f);
            }
            shape.rect(inicioX + i * (tamano + separacion), getCentroY() - tamano / 2f, tamano, tamano);
        }
        // Grietas según el daño recibido
        shape.setColor(0.05f, 0.05f, 0.15f, 1f);
        if (resistencia < resistenciaMaxima) {
            shape.rectLine(x + 6f, y + height - 4f, x + 18f, y + 5f, 1.5f);
        }
        if (resistencia < resistenciaMaxima - 1) {
            shape.rectLine(x + width - 8f, y + 4f, x + width - 22f, y + height - 5f, 1.5f);
        }
    }
}
