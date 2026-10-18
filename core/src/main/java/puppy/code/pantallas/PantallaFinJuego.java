package puppy.code.pantallas;

import java.util.ArrayList;
import java.util.List;

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
import puppy.code.mundo.Particula;

/**
 * Pantalla final: "¡SISTEMA LIBERADO!" si se destruyó el Núcleo-X, o "CONEXIÓN TERMINADA"
 * si se acabaron las vidas. Muestra el resumen de la partida guardado en el Singleton.
 * Hija de la clase abstracta {@link PantallaBase} [GM-4].
 */
public class PantallaFinJuego extends PantallaBase {

    private static final float INTERVALO_FUEGOS = 0.35f;
    private static final Color VERDE = new Color(0.35f, 1f, 0.55f, 1f);
    private static final Color ROJO = new Color(1f, 0.22f, 0.3f, 1f);
    private static final Color AMARILLO = new Color(1f, 0.9f, 0.3f, 1f);
    private static final Color GRIS = new Color(0.72f, 0.76f, 0.86f, 1f);
    private static final Color CIAN = new Color(0.3f, 0.95f, 1f, 1f);

    private final boolean victoria;
    private final List<Particula> particulas = new ArrayList<>();
    private final Color fondoArriba;
    private final Color fondoAbajo;
    private final Color grilla;
    private float tiempo;
    private float temporizadorFuegos;

    public PantallaFinJuego(JuegoBlockBreaker juego, boolean victoria) {
        super(juego);
        this.victoria = victoria;
        if (victoria) {
            fondoArriba = new Color(0.01f, 0.12f, 0.12f, 1f);
            fondoAbajo = new Color(0f, 0.02f, 0.03f, 1f);
            grilla = new Color(0.3f, 1f, 0.7f, 1f);
        } else {
            fondoArriba = new Color(0.16f, 0.01f, 0.03f, 1f);
            fondoAbajo = new Color(0.02f, 0f, 0f, 1f);
            grilla = new Color(1f, 0.2f, 0.3f, 1f);
        }
        GestorJuego.getInstance().guardarRecord(); // [GM-6]
    }

    @Override
    protected void actualizar(float delta) {
        tiempo += delta;
        temporizadorFuegos += delta;
        if (temporizadorFuegos >= INTERVALO_FUEGOS) {
            temporizadorFuegos = 0f;
            lanzarFuego();
        }
        for (int i = particulas.size() - 1; i >= 0; i--) {
            Particula particula = particulas.get(i);
            particula.update(delta);
            if (!particula.estaViva()) {
                particulas.remove(i);
            }
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            GestorJuego.getInstance().nuevaPartida();
            juego.setScreen(new PantallaJuego(juego));
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            juego.setScreen(new PantallaMenu(juego));
        }
    }

    private void lanzarFuego() {
        float x = MathUtils.random(80f, ConfiguracionJuego.getAnchoMundo() - 80f);
        float y = MathUtils.random(330f, 560f);
        Color color;
        if (victoria) {
            int r = MathUtils.random(2);
            color = r == 0 ? VERDE : (r == 1 ? CIAN : AMARILLO);
        } else {
            color = ROJO;
        }
        int cantidad = victoria ? 28 : 10;
        for (int i = 0; i < cantidad; i++) {
            particulas.add(new Particula(x, y, color));
        }
    }

    @Override
    protected void dibujarFormas(ShapeRenderer shape) {
        dibujarFondoCyber(shape, fondoArriba, fondoAbajo, grilla, tiempo);
        for (Particula particula : particulas) {
            particula.draw(shape);
        }
        shape.setColor(0f, 0f, 0f, 0.6f);
        shape.rect(170f, 150f, 460f, 200f);
        Color borde = victoria ? VERDE : ROJO;
        shape.setColor(borde.r, borde.g, borde.b, 0.9f);
        shape.rect(170f, 348f, 460f, 2f);
        shape.rect(170f, 150f, 460f, 2f);
    }

    @Override
    protected void dibujarTextos(SpriteBatch batch, BitmapFont font) {
        GestorJuego gestor = GestorJuego.getInstance(); // [GM-6]
        float centro = ConfiguracionJuego.getAnchoMundo() / 2f;
        if (victoria) {
            dibujarTextoCentrado(batch, font, "¡SISTEMA LIBERADO!", centro, 500f, 2.8f, VERDE);
            dibujarTextoCentrado(batch, font, "NÚCLEO-X fue destruido. La red de Valparaíso vuelve a estar a salvo.",
                    centro, 425f, 1f, Color.WHITE);
        } else {
            dibujarTextoCentrado(batch, font, "CONEXIÓN TERMINADA", centro, 510f, 2.8f, ROJO);
            dibujarTextoCentrado(batch, font, "G A M E   O V E R", centro, 450f, 1.6f, Color.WHITE);
            dibujarTextoCentrado(batch, font, "NÚCLEO-X sigue activo... AEGIS debe reiniciarse.", centro, 405f, 1f, GRIS);
        }
        float etiquetaX = 200f;
        float valorX = 470f;
        dibujarTexto(batch, font, "PUNTAJE FINAL", etiquetaX, 330f, 1.15f, GRIS);
        dibujarTexto(batch, font, String.format("%06d", gestor.getPuntaje()), valorX, 330f, 1.15f, Color.WHITE);
        dibujarTexto(batch, font, "SECTOR ALCANZADO", etiquetaX, 295f, 1.15f, GRIS);
        dibujarTexto(batch, font, gestor.getNivelActual() + " / " + gestor.getTotalNiveles(), valorX, 295f, 1.15f, Color.WHITE);
        dibujarTexto(batch, font, "BLOQUES DESTRUIDOS", etiquetaX, 260f, 1.15f, GRIS);
        dibujarTexto(batch, font, String.valueOf(gestor.getBloquesDestruidos()), valorX, 260f, 1.15f, Color.WHITE);
        dibujarTexto(batch, font, "RÉCORD", etiquetaX, 225f, 1.15f, GRIS);
        dibujarTexto(batch, font, String.format("%06d", gestor.getRecord()), valorX, 225f, 1.15f, AMARILLO);
        if (gestor.isNuevoRecord()) {
            float parpadeo = 0.4f + 0.6f * Math.abs(MathUtils.sin(tiempo * 4f));
            dibujarTextoCentrado(batch, font, "¡NUEVO RÉCORD!", centro, 185f, 1.4f,
                    new Color(AMARILLO.r, AMARILLO.g, AMARILLO.b, parpadeo));
        }
        dibujarTextoCentrado(batch, font, "ENTER: jugar de nuevo        ESC: menú principal", centro, 100f, 1.1f, CIAN);
    }
}
