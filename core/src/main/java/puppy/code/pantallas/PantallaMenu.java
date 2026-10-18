package puppy.code.pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

import puppy.code.JuegoBlockBreaker;
import puppy.code.ConfiguracionJuego;
import puppy.code.gestores.GestorJuego;

/**
 * Pantalla de título: presenta la narrativa, el récord y las opciones del juego.
 * Hija de la clase abstracta {@link PantallaBase} [GM-4].
 */
public class PantallaMenu extends PantallaBase {

    private static final int GOTAS = 46;
    private static final Color FONDO_ARRIBA = new Color(0.05f, 0.02f, 0.16f, 1f);
    private static final Color FONDO_ABAJO = new Color(0f, 0f, 0.03f, 1f);
    private static final Color GRILLA = new Color(0.3f, 0.5f, 1f, 1f);
    private static final Color CIAN = new Color(0.3f, 0.95f, 1f, 1f);
    private static final Color MAGENTA = new Color(1f, 0.25f, 0.7f, 1f);
    private static final Color AMARILLO = new Color(1f, 0.9f, 0.3f, 1f);
    private static final Color GRIS = new Color(0.7f, 0.75f, 0.85f, 1f);

    private final float[] gotaX = new float[GOTAS];
    private final float[] gotaY = new float[GOTAS];
    private final float[] gotaVelocidad = new float[GOTAS];
    private final float[] gotaLargo = new float[GOTAS];
    private float tiempo;

    public PantallaMenu(JuegoBlockBreaker juego) {
        super(juego);
        for (int i = 0; i < GOTAS; i++) {
            reiniciarGota(i, MathUtils.random(0f, ConfiguracionJuego.getAltoMundo()));
        }
    }

    private void reiniciarGota(int i, float y) {
        gotaX[i] = MathUtils.random(0, 39) * 20f + 9f;
        gotaY[i] = y;
        gotaVelocidad[i] = MathUtils.random(60f, 190f);
        gotaLargo[i] = MathUtils.random(20f, 90f);
    }

    @Override
    protected void actualizar(float delta) {
        tiempo += delta;
        for (int i = 0; i < GOTAS; i++) {
            gotaY[i] -= gotaVelocidad[i] * delta;
            if (gotaY[i] + gotaLargo[i] < 0f) {
                reiniciarGota(i, ConfiguracionJuego.getAltoMundo());
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            GestorJuego.getInstance().nuevaPartida(); // [GM-6]
            juego.setScreen(new PantallaJuego(juego));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            juego.setScreen(new PantallaAyuda(juego));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            Gdx.app.exit();
        }
    }

    @Override
    protected void dibujarFormas(ShapeRenderer shape) {
        dibujarFondoCyber(shape, FONDO_ARRIBA, FONDO_ABAJO, GRILLA, tiempo);
        // Lluvia de datos
        for (int i = 0; i < GOTAS; i++) {
            Color c = i % 3 == 0 ? MAGENTA : CIAN;
            shape.setColor(c.r, c.g, c.b, 0.25f);
            shape.rect(gotaX[i], gotaY[i], 2f, gotaLargo[i]);
            shape.setColor(c.r, c.g, c.b, 0.8f);
            shape.rect(gotaX[i] - 1f, gotaY[i], 4f, 4f);
        }
        // Fila decorativa de bloques bajo el título
        float[][] colores = {
            {0.1f, 0.85f, 1f}, {0.15f, 1f, 0.6f}, {1f, 0.85f, 0.2f}, {1f, 0.55f, 0.15f},
            {1f, 0.3f, 0.6f}, {0.65f, 0.45f, 1f}, {0.9f, 0.16f, 0.12f}, {0.55f, 0.58f, 0.63f}
        };
        float anchoBloque = 56f;
        float espacio = 8f;
        float inicio = (ConfiguracionJuego.getAnchoMundo() - (colores.length * anchoBloque + (colores.length - 1) * espacio)) / 2f;
        for (int i = 0; i < colores.length; i++) {
            float desfase = MathUtils.sin(tiempo * 2.5f + i * 0.6f) * 3f;
            shape.setColor(colores[i][0], colores[i][1], colores[i][2], 1f);
            shape.rect(inicio + i * (anchoBloque + espacio), 405f + desfase, anchoBloque, 14f);
        }
        // Panel de historia
        shape.setColor(0f, 0f, 0f, 0.6f);
        shape.rect(80f, 175f, 640f, 200f);
        shape.setColor(CIAN.r, CIAN.g, CIAN.b, 0.9f);
        shape.rect(80f, 373f, 640f, 2f);
        shape.rect(80f, 175f, 640f, 2f);
        shape.setColor(MAGENTA.r, MAGENTA.g, MAGENTA.b, 0.9f);
        shape.rect(80f, 175f, 3f, 200f);
        shape.rect(717f, 175f, 3f, 200f);
    }

    @Override
    protected void dibujarTextos(SpriteBatch batch, BitmapFont font) {
        float centro = ConfiguracionJuego.getAnchoMundo() / 2f;
        float brillo = 0.5f + 0.5f * MathUtils.sin(tiempo * 3f);

        // Título con efecto "glitch" neón (capas desplazadas)
        dibujarTextoCentrado(batch, font, "CYBER BREAKER", centro - 3f, 531f, 3.4f, new Color(1f, 0.2f, 0.6f, 0.75f));
        dibujarTextoCentrado(batch, font, "CYBER BREAKER", centro + 3f, 527f, 3.4f, new Color(0.2f, 0.9f, 1f, 0.75f));
        dibujarTextoCentrado(batch, font, "CYBER BREAKER", centro, 529f, 3.4f, Color.WHITE);
        dibujarTextoCentrado(batch, font, "P R O T O C O L O   F I R E W A L L", centro, 455f, 1.2f, CIAN);

        dibujarTextoCentrado(batch, font, "Año 2049. La IA rebelde NÚCLEO-X tomó el control de la red", centro, 355f, 1.05f, Color.WHITE);
        dibujarTextoCentrado(batch, font, "de Valparaíso y levantó cinco sectores de firewall.", centro, 332f, 1.05f, Color.WHITE);
        dibujarTextoCentrado(batch, font, "Eres AEGIS, el último antivirus: controla la plataforma,", centro, 300f, 1.05f, GRIS);
        dibujarTextoCentrado(batch, font, "lanza paquetes de datos, rompe los muros y destruye el núcleo.", centro, 277f, 1.05f, GRIS);
        dibujarTextoCentrado(batch, font, "Mover: Flechas / A-D     Lanzar: ESPACIO     Pausa: P", centro, 230f, 1f, CIAN);

        dibujarTextoCentrado(batch, font, "Presiona ENTER para iniciar", centro, 140f, 1.5f,
                new Color(AMARILLO.r, AMARILLO.g, AMARILLO.b, 0.4f + 0.6f * brillo));
        dibujarTextoCentrado(batch, font, "H: Manual del jugador        ESC: Salir", centro, 100f, 1f, GRIS);

        String record = String.format("RÉCORD  %06d", GestorJuego.getInstance().getRecord()); // [GM-6]
        dibujarTextoCentrado(batch, font, record, centro, 50f, 1.2f, MAGENTA);
    }
}
