package puppy.code.potenciadores;

import com.badlogic.gdx.graphics.Color;

import puppy.code.entidades.Bola;
import puppy.code.estrategia.EstrategiaMovimientoZigZag;
import puppy.code.mundo.EntornoJuego;

/**
 * Cápsula NEGATIVA "?" (virus): cambia la estrategia de las bolas a EstrategiaMovimientoZigZag
 * por 6 segundos. Conviene esquivarla. El Núcleo-X también la dispara en el sector final.
 */
public class PotenciadorFallo extends Potenciador {

    private static final float DURACION = 6f;
    private static final Color COLOR = new Color(0.85f, 0.25f, 1f, 1f);

    public PotenciadorFallo(float centroX, float centroY) {
        super(centroX, centroY, COLOR);
    }

    @Override
    protected void aplicarEfecto(EntornoJuego entorno) {
        for (Bola bola : entorno.getBolas()) {
            bola.aplicarEstrategiaTemporal(new EstrategiaMovimientoZigZag(), DURACION); // [GM-7]
        }
    }

    @Override
    public String getEtiqueta() {
        return "?";
    }

    @Override
    protected String getMensaje() {
        return "¡GLITCH! TRAYECTORIA INESTABLE";
    }

    @Override
    public String getDescripcion() {
        return "Virus: la bola avanza en zig-zag (6 s)";
    }
}
