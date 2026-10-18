package puppy.code.mundo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/**
 * Partícula visual (chispa o fragmento) generada proceduralmente: no necesita imágenes externas.
 */
public class Particula {

    private static final float GRAVEDAD = 260f;

    private float x;
    private float y;
    private float velocidadX;
    private float velocidadY;
    private float vida;
    private final float vidaMaxima;
    private final float tamano;
    private final Color color;

    public Particula(float x, float y, Color color) {
        this.x = x;
        this.y = y;
        float angulo = MathUtils.random(0f, MathUtils.PI2);
        float rapidez = MathUtils.random(60f, 260f);
        this.velocidadX = MathUtils.cos(angulo) * rapidez;
        this.velocidadY = MathUtils.sin(angulo) * rapidez;
        this.vidaMaxima = MathUtils.random(0.35f, 0.9f);
        this.vida = vidaMaxima;
        this.tamano = MathUtils.random(2f, 5f);
        this.color = new Color(color);
    }

    public void update(float delta) {
        x += velocidadX * delta;
        y += velocidadY * delta;
        velocidadY -= GRAVEDAD * delta;
        velocidadX *= 0.98f;
        vida -= delta;
    }

    public void draw(ShapeRenderer shape) {
        float alfa = Math.max(0f, vida / vidaMaxima);
        shape.setColor(color.r, color.g, color.b, alfa);
        shape.rect(x - tamano / 2f, y - tamano / 2f, tamano, tamano);
    }

    public boolean estaViva() {
        return vida > 0f;
    }
}
