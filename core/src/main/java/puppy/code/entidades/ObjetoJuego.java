package puppy.code.entidades;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * [GM-4: Clase abstracta] Raíz de la jerarquía de entidades del dominio.
 *
 * Es padre de {@link Plataforma}, {@link Bola}, {@code Bloque} (también abstracta, con 5 hijas)
 * y {@code Potenciador} (también abstracta, con 6 hijas). Agrupa lo que todas las entidades tienen
 * en común (posición, tamaño, color y caja de colisión) y obliga a cada subclase a definir
 * cómo se actualiza y cómo se dibuja.
 *
 * [GM-5: Encapsulamiento] Los atributos son protected (solo visibles para las subclases) y
 * se exponen hacia afuera únicamente mediante getters.
 * Convención: (x, y) es la esquina inferior izquierda del objeto.
 */
public abstract class ObjetoJuego {

    protected float x;
    protected float y;
    protected float width;
    protected float height;
    protected Color color;
    private final Rectangle limites = new Rectangle();

    protected ObjetoJuego(float x, float y, float width, float height, Color color) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = new Color(color);
    }

    /** Actualiza la lógica del objeto. delta = segundos transcurridos desde el frame anterior. */
    public abstract void update(float delta);

    /** Dibuja el objeto con primitivas de ShapeRenderer (debe llamarse entre begin() y end()). */
    public abstract void draw(ShapeRenderer shape);

    /** Caja de colisión actualizada con la posición y tamaño actuales. */
    public Rectangle getLimites() {
        limites.set(x, y, width, height);
        return limites;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getCentroX() {
        return x + width / 2f;
    }

    public float getCentroY() {
        return y + height / 2f;
    }

    /** Retorna una copia para que nadie modifique el color interno desde afuera. */
    public Color getColor() {
        return new Color(color);
    }

    public void setPosicion(float x, float y) {
        this.x = x;
        this.y = y;
    }
}
