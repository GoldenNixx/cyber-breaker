package puppy.code.mundo;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Texto que sube y se desvanece (puntos ganados, avisos breves).
 */
public class TextoFlotante {

    private static final float DURACION = 0.9f;
    private static final float VELOCIDAD_SUBIDA = 45f;

    private final String texto;
    private final float x;
    private float y;
    private float vida;
    private final Color color;

    public TextoFlotante(String texto, float x, float y, Color color) {
        this.texto = texto;
        this.x = x;
        this.y = y;
        this.vida = DURACION;
        this.color = new Color(color);
    }

    public void update(float delta) {
        y += VELOCIDAD_SUBIDA * delta;
        vida -= delta;
    }

    public void draw(SpriteBatch batch, BitmapFont font, GlyphLayout layout) {
        float alfa = Math.max(0f, vida / DURACION);
        font.getData().setScale(1f);
        font.setColor(Math.min(1f, color.r + 0.3f), Math.min(1f, color.g + 0.3f), Math.min(1f, color.b + 0.3f), alfa);
        layout.setText(font, texto);
        font.draw(batch, texto, x - layout.width / 2f, y + layout.height / 2f);
    }

    public boolean estaVivo() {
        return vida > 0f;
    }
}
