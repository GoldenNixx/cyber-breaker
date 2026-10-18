package puppy.code.bloques;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.mundo.EntornoJuego;

/**
 * [GM-8: Template Method] Clase Concreta: "Núcleo-X" (jefe final).
 * Bloque grande que se desplaza de lado a lado, resiste 20 de daño y, al ser destruido,
 * detona todos los bloques restantes del sector.
 */
public class BloqueNucleo extends Bloque {

    private static final int RESISTENCIA = 20;
    private static final int PUNTOS = 1000;
    private static final float ANCHO = 170f;
    private static final float ALTO = 52f;
    private static final float AMPLITUD_MOVIMIENTO = 160f;
    private static final float VELOCIDAD_MOVIMIENTO = 0.7f;
    private static final Color COLOR = new Color(0.85f, 0.15f, 0.95f, 1f);

    private final float xInicial;
    private float tiempo;
    private boolean enMovimiento;

    /** Crea el núcleo centrado horizontalmente en centroX, con su borde inferior en y. */
    public BloqueNucleo(float centroX, float y) {
        super(centroX - ANCHO / 2f, y, ANCHO, ALTO, COLOR, RESISTENCIA, PUNTOS);
        this.xInicial = centroX - ANCHO / 2f;
        this.enMovimiento = true;
    }

    /** Permite dejar el núcleo quieto (se usa al mostrarlo en la pantalla de ayuda). */
    public void setEnMovimiento(boolean enMovimiento) {
        this.enMovimiento = enMovimiento;
    }

    @Override
    public void update(float delta) {
        super.update(delta);
        tiempo += delta;
        if (enMovimiento) {
            x = xInicial + MathUtils.sin(tiempo * VELOCIDAD_MOVIMIENTO) * AMPLITUD_MOVIMIENTO;
        }
    }

    @Override
    protected void recibirDano(int danio) {
        resistencia -= danio;
    }

    @Override
    protected void reproducirEfecto(EntornoJuego entorno) {
        entorno.generarParticulas(getCentroX(), getY(), color, 10);
        entorno.sacudirPantalla(3f, 0.15f);
    }

    @Override
    protected void aplicarEfectoEspecial(EntornoJuego entorno) {
        entorno.generarParticulas(getCentroX(), getCentroY(), color, 60);
        entorno.generarParticulas(getCentroX(), getCentroY(), Color.WHITE, 40);
        entorno.sacudirPantalla(14f, 0.9f);
        entorno.mostrarMensaje("¡NÚCLEO-X DESTRUIDO!", color);
        entorno.destruirTodosLosBloques();
    }

    @Override
    public boolean isAtravesable() {
        return false;
    }

    @Override
    protected void dibujarCuerpo(ShapeRenderer shape) {
        float pulso = 0.5f + 0.5f * MathUtils.sin(tiempo * 4f);
        // Aura
        shape.setColor(color.r, color.g, color.b, 0.15f + 0.15f * pulso);
        shape.rect(x - 8f, y - 8f, width + 16f, height + 16f);
        // Carcasa
        dibujarBiselado(shape, 0.32f, 0.04f, 0.38f);
        shape.setColor(color);
        shape.rect(x, y, width, 3f);
        shape.rect(x, y + height - 3f, width, 3f);
        shape.triangle(x, y, x - 14f, y + height / 2f, x, y + height);
        shape.triangle(x + width, y, x + width + 14f, y + height / 2f, x + width, y + height);
        // Ojo central
        float cx = getCentroX();
        float cy = getCentroY();
        shape.setColor(1f, 1f, 1f, 1f);
        shape.circle(cx, cy, 17f, 24);
        shape.setColor(1f, 0.1f + 0.3f * pulso, 0.3f, 1f);
        shape.circle(cx, cy, 11f, 24);
        shape.setColor(0.05f, 0f, 0.05f, 1f);
        shape.circle(cx, cy, 5f, 16);
        // Barra de vida
        float anchoBarra = width - 20f;
        shape.setColor(0.25f, 0.02f, 0.08f, 1f);
        shape.rect(x + 10f, y + height + 6f, anchoBarra, 6f);
        shape.setColor(1f, 0.2f + 0.6f * getProporcionVida(), 0.35f, 1f);
        shape.rect(x + 10f, y + height + 6f, anchoBarra * getProporcionVida(), 6f);
    }
}
