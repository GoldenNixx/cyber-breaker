package puppy.code.estrategia;

import com.badlogic.gdx.graphics.Color;

import puppy.code.entidades.Bola;

/**
 * [GM-7: Patrón Strategy] ConcreteStrategy: "Modo Lento".
 * La bola se mueve al 60% de su velocidad, lo que facilita reaccionar.
 */
public class EstrategiaMovimientoLento implements EstrategiaMovimientoBola {

    private static final float MULTIPLICADOR_VELOCIDAD = 0.6f;
    private static final Color COLOR = new Color(0.3f, 1f, 0.95f, 1f);

    @Override
    public void mover(Bola bola, float delta) {
        bola.trasladar(bola.getVelocidadX() * MULTIPLICADOR_VELOCIDAD * delta,
                bola.getVelocidadY() * MULTIPLICADOR_VELOCIDAD * delta);
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
        return "Lenta";
    }
}
