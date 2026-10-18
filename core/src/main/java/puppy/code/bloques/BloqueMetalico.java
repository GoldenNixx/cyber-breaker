package puppy.code.bloques;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import puppy.code.mundo.EntornoJuego;

/**
 * [GM-8: Template Method] Clase Concreta: "Bloque Firewall" (indestructible).
 * Redefine el hook {@link #verificarDestruccion()} para no destruirse nunca, por eso
 * tampoco cuenta para completar el sector y detiene incluso a la bola de fuego.
 */
public class BloqueMetalico extends Bloque {

    private static final int RESISTENCIA = 1;
    private static final int PUNTOS = 0;
    private static final Color COLOR = new Color(0.55f, 0.58f, 0.63f, 1f);
    private static final Color COLOR_CHISPAS = new Color(0.85f, 0.88f, 0.92f, 1f);

    public BloqueMetalico(float x, float y, float width, float height) {
        super(x, y, width, height, COLOR, RESISTENCIA, PUNTOS);
    }

    @Override
    protected void recibirDano(int danio) {
        // Indestructible: el daño no tiene efecto sobre el firewall
    }

    @Override
    protected void reproducirEfecto(EntornoJuego entorno) {
        entorno.generarParticulas(getCentroX(), getCentroY(), COLOR_CHISPAS, 4);
    }

    /** Hook redefinido: un bloque firewall nunca se destruye. */
    @Override
    protected boolean verificarDestruccion() {
        return false;
    }

    @Override
    protected void aplicarEfectoEspecial(EntornoJuego entorno) {
        // Nunca se ejecuta porque verificarDestruccion() siempre retorna false
    }

    @Override
    public boolean cuentaParaCompletar() {
        return false;
    }

    @Override
    public boolean isAtravesable() {
        return false;
    }

    @Override
    protected void dibujarCuerpo(ShapeRenderer shape) {
        dibujarBiselado(shape, color.r, color.g, color.b);
        // Placas diagonales
        shape.setColor(0.4f, 0.42f, 0.47f, 1f);
        shape.rectLine(x + 18f, y + 3f, x + 28f, y + height - 3f, 3f);
        shape.rectLine(x + width - 28f, y + 3f, x + width - 18f, y + height - 3f, 3f);
        // Remaches
        shape.setColor(0.3f, 0.32f, 0.36f, 1f);
        shape.circle(x + 6f, y + 6f, 2f, 8);
        shape.circle(x + width - 6f, y + 6f, 2f, 8);
        shape.circle(x + 6f, y + height - 6f, 2f, 8);
        shape.circle(x + width - 6f, y + height - 6f, 2f, 8);
    }
}
