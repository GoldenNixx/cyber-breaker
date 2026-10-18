package puppy.code.estrategia;

import com.badlogic.gdx.graphics.Color;

import puppy.code.entidades.Bola;

/**
 * [GM-7: Patrón Strategy] Participante "Strategy".
 *
 * Declara la interfaz común para todos los algoritmos de movimiento de la bola.
 * El Context ({@link Bola}) mantiene una referencia de este tipo y le delega el
 * movimiento, de modo que los algoritmos pueden variar sin modificar la clase Bola.
 *
 * ConcreteStrategies: {@link EstrategiaMovimientoNormal}, {@link EstrategiaMovimientoFuego},
 * {@link EstrategiaMovimientoLento} y {@link EstrategiaMovimientoZigZag}.
 */
public interface EstrategiaMovimientoBola {

    /** algorithmInterface(): desplaza la bola durante "delta" segundos. */
    void mover(Bola bola, float delta);

    /** Indica si con esta estrategia la bola atraviesa los bloques destructibles sin rebotar. */
    boolean atraviesaBloques();

    /** Daño que causa la bola por impacto. */
    int getDanio();

    /** Color con que se dibuja la bola mientras la estrategia está activa. */
    Color getColor();

    /** Nombre que se muestra en el HUD. */
    String getNombre();
}
