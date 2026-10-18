package puppy.code.potenciadores;

import com.badlogic.gdx.graphics.Color;

import puppy.code.entidades.Bola;
import puppy.code.estrategia.EstrategiaMovimientoFuego;
import puppy.code.mundo.EntornoJuego;

/** Power-up "F": cambia la estrategia de todas las bolas a EstrategiaMovimientoFuego por 8 segundos. */
public class PotenciadorFuego extends Potenciador {

    private static final float DURACION = 8f;
    private static final Color COLOR = new Color(1f, 0.5f, 0.1f, 1f);

    public PotenciadorFuego(float centroX, float centroY) {
        super(centroX, centroY, COLOR);
    }

    @Override
    protected void aplicarEfecto(EntornoJuego entorno) {
        for (Bola bola : entorno.getBolas()) {
            // [GM-7] El cliente cambia el algoritmo del Context en tiempo de ejecución
            bola.aplicarEstrategiaTemporal(new EstrategiaMovimientoFuego(), DURACION);
        }
    }

    @Override
    public String getEtiqueta() {
        return "F";
    }

    @Override
    protected String getMensaje() {
        return "¡BOLA DE FUEGO!";
    }

    @Override
    public String getDescripcion() {
        return "Fuego: atraviesa bloques, doble daño (8 s)";
    }
}
