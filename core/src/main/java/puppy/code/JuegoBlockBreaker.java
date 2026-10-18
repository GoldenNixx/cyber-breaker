package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import puppy.code.gestores.GestorJuego;
import puppy.code.pantallas.PantallaMenu;

/**
 * Punto de entrada de "Cyber Breaker: Protocolo Firewall".
 *
 * En el juego base esta clase extendía ApplicationAdapter y concentraba TODO: creación de
 * bloques, reglas, colisiones, puntaje, vidas y dibujo dentro de render(). Ahora extiende
 * {@link Game} de LibGDX y solo crea los recursos gráficos compartidos (SpriteBatch,
 * ShapeRenderer y BitmapFont) y delega cada etapa a una pantalla (Screen).
 */
public class JuegoBlockBreaker extends Game {

    // [GM-5] Recursos compartidos privados, accesibles solo por getters
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private BitmapFont font;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont(); // fuente incluida en LibGDX: no requiere assets externos
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font.setUseIntegerPositions(false);

        GestorJuego.getInstance().cargarRecord(); // [GM-6] primer uso del Singleton
        setScreen(new PantallaMenu(this));
    }

    public SpriteBatch getBatch() {
        return batch;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public BitmapFont getFont() {
        return font;
    }

    @Override
    public void dispose() {
        super.dispose();
        GestorJuego.getInstance().guardarRecord();
        batch.dispose();
        shapeRenderer.dispose();
        font.dispose();
    }
}
