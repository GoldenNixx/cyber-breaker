package puppy.code.estrategia;

import com.badlogic.gdx.graphics.Color;

import puppy.code.entidades.Bola;

/**
 * [GM-7: Patrón Strategy] ConcreteStrategy: movimiento rectilíneo uniforme (comportamiento base).
 */
public class EstrategiaMovimientoNormal implements EstrategiaMovimientoBola {

    private static final Color COLOR = new Color(0.92f, 0.97f, 1f, 1f);

    @Override
    public void mover(Bola bola, float delta) {
        bola.trasladar(bola.getVelocidadX() * delta, bola.getVelocidadY() * delta);
    }

    @Override
    public boolean atraviesaBloques() {
        return false;
    }

    @Override
    public int getDanio() {
        return 1;
    }

    @Override
    public Color getColor() {
        return COLOR;
    }

    @Override
    public String getNombre() {
        return "Normal";
    }
}
