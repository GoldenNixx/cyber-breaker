package puppy.code.potenciadores;

import com.badlogic.gdx.graphics.Color;

import puppy.code.gestores.GestorJuego;
import puppy.code.mundo.EntornoJuego;

/** Power-up "+": suma una vida (máximo 5). Si ya está al máximo, entrega puntos extra. */
public class PotenciadorVidaExtra extends Potenciador {

    private static final int PUNTOS_SI_LLENO = 250;
    private static final Color COLOR = new Color(1f, 0.35f, 0.6f, 1f);

    private String mensaje;

    public PotenciadorVidaExtra(float centroX, float centroY) {
        super(centroX, centroY, COLOR);
        this.mensaje = "+1 VIDA";
    }

    @Override
    protected void aplicarEfecto(EntornoJuego entorno) {
        GestorJuego gestor = GestorJuego.getInstance(); // [GM-6]
        if (gestor.ganarVida()) {
            mensaje = "+1 VIDA";
        } else {
            gestor.sumarPuntos(PUNTOS_SI_LLENO);
            mensaje = "VIDAS AL MÁXIMO: +" + PUNTOS_SI_LLENO;
        }
    }

    @Override
    public String getEtiqueta() {
        return "+";
    }

    @Override
    protected String getMensaje() {
        return mensaje;
    }

    @Override
    public String getDescripcion() {
        return "Vida extra: +1 vida (máximo 5)";
    }
}
