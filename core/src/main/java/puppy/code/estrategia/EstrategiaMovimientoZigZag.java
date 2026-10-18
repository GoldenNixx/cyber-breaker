package puppy.code.estrategia;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.entidades.Bola;

/**
 * [GM-7: Patrón Strategy] ConcreteStrategy: "Glitch" (efecto negativo del virus).
 * A la trayectoria recta se le suma una oscilación lateral sinusoidal, lo que hace que la
 * bola avance en zig-zag y sea más difícil de atrapar.
 *
 * Cada bola recibe su propia instancia porque la estrategia guarda su fase (tiempo).
 */
public class EstrategiaMovimientoZigZag implements EstrategiaMovimientoBola {

    private static final float AMPLITUD = 260f;
    private static final float FRECUENCIA = 9f;
    private static final Color COLOR = new Color(0.85f, 0.3f, 1f, 1f);

    private float tiempo;

    @Override
    public void mover(Bola bola, float delta) {
        tiempo += delta;
        float desplazamientoLateral = MathUtils.sin(tiempo * FRECUENCIA) * AMPLITUD * delta;
        bola.trasladar(bola.getVelocidadX() * delta + desplazamientoLateral, bola.getVelocidadY() * delta);
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
        return "Glitch";
    }
}
