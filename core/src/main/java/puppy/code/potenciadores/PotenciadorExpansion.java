package puppy.code.potenciadores;

import com.badlogic.gdx.graphics.Color;

import puppy.code.mundo.EntornoJuego;

/** Power-up "E": ensancha la plataforma durante 10 segundos. */
public class PotenciadorExpansion extends Potenciador {

    private static final float DURACION = 10f;
    private static final Color COLOR = new Color(0.3f, 1f, 0.45f, 1f);

    public PotenciadorExpansion(float centroX, float centroY) {
        super(centroX, centroY, COLOR);
    }

    @Override
    protected void aplicarEfecto(EntornoJuego entorno) {
        entorno.getPlataforma().expandir(DURACION);
    }

    @Override
    public String getEtiqueta() {
        return "E";
    }

    @Override
    protected String getMensaje() {
        return "EXPANSIÓN DE PLATAFORMA";
    }

    @Override
    public String getDescripcion() {
        return "Expansión: plataforma más ancha (10 s)";
    }
}
