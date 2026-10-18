package puppy.code.potenciadores;

import com.badlogic.gdx.graphics.Color;

import puppy.code.entidades.Bola;
import puppy.code.estrategia.EstrategiaMovimientoLento;
import puppy.code.mundo.EntornoJuego;

/** Power-up "L": cambia la estrategia de todas las bolas a EstrategiaMovimientoLento por 8 segundos. */
public class PotenciadorLentitud extends Potenciador {

    private static final float DURACION = 8f;
    private static final Color COLOR = new Color(0.25f, 0.95f, 1f, 1f);

    public PotenciadorLentitud(float centroX, float centroY) {
        super(centroX, centroY, COLOR);
    }

    @Override
    protected void aplicarEfecto(EntornoJuego entorno) {
        for (Bola bola : entorno.getBolas()) {
            bola.aplicarEstrategiaTemporal(new EstrategiaMovimientoLento(), DURACION); // [GM-7]
        }
    }

    @Override
    public String getEtiqueta() {
        return "L";
    }

    @Override
    protected String getMensaje() {
        return "MODO LENTO ACTIVADO";
    }

    @Override
    public String getDescripcion() {
        return "Lento: la bola va al 60% (8 s)";
    }
}
