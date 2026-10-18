package puppy.code.estrategia;

import com.badlogic.gdx.graphics.Color;

import puppy.code.entidades.Bola;

/**
 * [GM-7: Patrón Strategy] ConcreteStrategy: "Bola de Fuego".
 * La bola avanza un 30% más rápido, causa el doble de daño y atraviesa los bloques
 * destructibles sin rebotar (los bloques metálicos y el núcleo sí la detienen).
 */
public class EstrategiaMovimientoFuego implements EstrategiaMovimientoBola {

    private static final float MULTIPLICADOR_VELOCIDAD = 1.3f;
    private static final Color COLOR = new Color(1f, 0.45f, 0.08f, 1f);

    @Override
    public void mover(Bola bola, float delta) {
        bola.trasladar(bola.getVelocidadX() * MULTIPLICADOR_VELOCIDAD * delta,
                bola.getVelocidadY() * MULTIPLICADOR_VELOCIDAD * delta);
    }

    @Override
    public boolean atraviesaBloques() {
        return true;
    }

    @Override
    public int getDanio() {
        return 2;
    }

    @Override
    public Color getColor() {
        return COLOR;
    }

    @Override
    public String getNombre() {
        return "Fuego";
    }
}
