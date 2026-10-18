package puppy.code.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import puppy.code.JuegoBlockBreaker;
import puppy.code.ConfiguracionJuego;

/**
 * [GM-4: Clase abstracta] Pantalla base del juego; implementa la interfaz Screen de LibGDX.
 *
 * Es padre de {@link PantallaMenu}, {@link PantallaAyuda}, {@link PantallaJuego} y
 * {@link PantallaFinJuego}. Centraliza lo que todas comparten (cámara, viewport y ciclo de
 * dibujo) y deja como abstractos los pasos que cambian en cada pantalla. Su método
 * {@link #render(float)} es final y sigue la misma idea de Template Method: actualizar,
 * limpiar, dibujar formas y dibujar textos, siempre en ese orden.
 *
 * Corrige un defecto del juego base: allí ShapeRenderer dibujaba en píxeles de ventana y el
 * texto con una cámara de 800x480, por lo que no coincidían al cambiar el tamaño de la
 * ventana. Ahora todo usa la misma cámara y un FitViewport.
 */
public abstract class PantallaBase implements Screen {

    private static final float DELTA_MAXIMO = 1f / 30f;

    protected final JuegoBlockBreaker juego;
    protected final OrthographicCamera camara;
    protected final Viewport viewport;
    private final GlyphLayout layout = new GlyphLayout();

    protected PantallaBase(JuegoBlockBreaker juego) {
        this.juego = juego;
        this.camara = new OrthographicCamera();
        this.viewport = new FitViewport(ConfiguracionJuego.getAnchoMundo(), ConfiguracionJuego.getAltoMundo(), camara);
        centrarCamara(0f, 0f);
    }

    /** Ciclo fijo de cada frame (las subclases no pueden alterar este orden). */
    @Override
    public final void render(float delta) {
        float dt = Math.min(delta, DELTA_MAXIMO);
        actualizar(dt);
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        viewport.apply();
        camara.update();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        ShapeRenderer shape = juego.getShapeRenderer();
        shape.setProjectionMatrix(camara.combined);
        shape.begin(ShapeRenderer.ShapeType.Filled);
        dibujarFormas(shape);
        shape.end();

        SpriteBatch batch = juego.getBatch();
        batch.setProjectionMatrix(camara.combined);
        batch.begin();
        dibujarTextos(batch, juego.getFont());
        batch.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    /** Lógica y lectura de teclado del frame. */
    protected abstract void actualizar(float delta);

    /** Dibujo con primitivas (ShapeRenderer en modo Filled). */
    protected abstract void dibujarFormas(ShapeRenderer shape);

    /** Dibujo de textos (SpriteBatch + BitmapFont). */
    protected abstract void dibujarTextos(SpriteBatch batch, BitmapFont font);

    // ---------------- Utilidades para las subclases ----------------

    protected void centrarCamara(float desplazamientoX, float desplazamientoY) {
        camara.position.set(ConfiguracionJuego.getAnchoMundo() / 2f + desplazamientoX,
                ConfiguracionJuego.getAltoMundo() / 2f + desplazamientoY, 0f);
    }

    protected void dibujarTexto(SpriteBatch batch, BitmapFont font, String texto, float x, float y,
            float escala, Color color) {
        font.getData().setScale(escala);
        font.setColor(color);
        font.draw(batch, texto, x, y);
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
    }

    protected void dibujarTextoCentrado(SpriteBatch batch, BitmapFont font, String texto, float centroX,
            float y, float escala, Color color) {
        font.getData().setScale(escala);
        font.setColor(color);
        layout.setText(font, texto);
        font.draw(batch, texto, centroX - layout.width / 2f, y);
        font.getData().setScale(1f);
        font.setColor(Color.WHITE);
    }

    protected float medirAncho(BitmapFont font, String texto, float escala) {
        font.getData().setScale(escala);
        layout.setText(font, texto);
        font.getData().setScale(1f);
        return layout.width;
    }

    /** Fondo degradado con grilla "cyber" usado por las pantallas de menú. */
    protected void dibujarFondoCyber(ShapeRenderer shape, Color arriba, Color abajo, Color grilla, float tiempo) {
        float ancho = ConfiguracionJuego.getAnchoMundo();
        float alto = ConfiguracionJuego.getAltoMundo();
        shape.rect(0f, 0f, ancho, alto, abajo, abajo, arriba, arriba);
        shape.setColor(grilla.r, grilla.g, grilla.b, 0.08f);
        for (float x = 0f; x <= ancho; x += 40f) {
            shape.rect(x, 0f, 1f, alto);
        }
        float desplazamiento = (tiempo * 15f) % 40f;
        for (float y = alto - desplazamiento; y > 0f; y -= 40f) {
            shape.rect(0f, y, ancho, 1f);
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
        // Sin recursos propios que preparar
    }

    @Override
    public void hide() {
        // Sin recursos propios que liberar
    }

    @Override
    public void pause() {
        // No requerido en escritorio
    }

    @Override
    public void resume() {
        // No requerido en escritorio
    }

    @Override
    public void dispose() {
        // Los recursos gráficos compartidos los libera JuegoBlockBreaker
    }
}
