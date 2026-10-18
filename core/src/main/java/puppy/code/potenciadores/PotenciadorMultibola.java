package puppy.code.potenciadores;

import com.badlogic.gdx.graphics.Color;

import puppy.code.entidades.Bola;
import puppy.code.mundo.EntornoJuego;

/** Power-up "M": divide la primera bola en movimiento en tres. */
public class PotenciadorMultibola extends Potenciador {

    private static final float ANGULO_SEPARACION = 30f;
    private static final Color COLOR = new Color(1f, 0.9f, 0.2f, 1f);

    public PotenciadorMultibola(float centroX, float centroY) {
        super(centroX, centroY, COLOR);
    }

    @Override
    protected void aplicarEfecto(EntornoJuego entorno) {
        Bola origen = null;
        for (Bola bola : entorno.getBolas()) {
            if (!bola.estaQuieto()) {
                origen = bola;
                break;
            }
        }
        if (origen == null) {
            return;
        }
        float[] angulos = {-ANGULO_SEPARACION, ANGULO_SEPARACION};
        for (float angulo : angulos) {
            Bola nueva = new Bola(origen.getCentroX(), origen.getCentroY(), origen.getVelocidadBase());
            nueva.lanzarConAngulo(angulo);
            entorno.agregarBola(nueva);
        }
    }

    @Override
    public String getEtiqueta() {
        return "M";
    }

    @Override
    protected String getMensaje() {
        return "¡MULTIBOLA!";
    }

    @Override
    public String getDescripcion() {
        return "Multibola: +2 bolas en juego";
    }
}
