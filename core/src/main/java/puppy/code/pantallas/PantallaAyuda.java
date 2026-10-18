package puppy.code.pantallas;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import puppy.code.JuegoBlockBreaker;
import puppy.code.ConfiguracionJuego;
import puppy.code.bloques.Bloque;
import puppy.code.bloques.BloqueNucleo;
import puppy.code.bloques.BloqueExplosivo;
import puppy.code.bloques.BloqueMetalico;
import puppy.code.bloques.BloqueNormal;
import puppy.code.bloques.BloqueReforzado;
import puppy.code.potenciadores.PotenciadorExpansion;
import puppy.code.potenciadores.PotenciadorVidaExtra;
import puppy.code.potenciadores.PotenciadorFuego;
import puppy.code.potenciadores.PotenciadorFallo;
import puppy.code.potenciadores.PotenciadorMultibola;
import puppy.code.potenciadores.Potenciador;
import puppy.code.potenciadores.PotenciadorLentitud;

/**
 * Manual del jugador: muestra cada tipo de bloque y de power-up dibujado con las mismas
 * clases del juego. Las listas se recorren de forma polimórfica (cada objeto sabe dibujarse).
 * Hija de la clase abstracta {@link PantallaBase} [GM-4].
 */
public class PantallaAyuda extends PantallaBase {

    private static final Color FONDO_ARRIBA = new Color(0.03f, 0.04f, 0.14f, 1f);
    private static final Color FONDO_ABAJO = new Color(0f, 0f, 0.03f, 1f);
    private static final Color GRILLA = new Color(0.3f, 0.5f, 1f, 1f);
    private static final Color CIAN = new Color(0.3f, 0.95f, 1f, 1f);
    private static final Color MAGENTA = new Color(1f, 0.3f, 0.7f, 1f);
    private static final Color GRIS = new Color(0.72f, 0.76f, 0.86f, 1f);
    private static final float COLUMNA_BLOQUES = 40f;
    private static final float COLUMNA_POWERUPS = 430f;

    private final List<Bloque> bloquesMuestra = new ArrayList<>();
    private final List<String[]> textosBloques = new ArrayList<>();
    private final List<Potenciador> potenciadoresMuestra = new ArrayList<>();
    private float tiempo;

    public PantallaAyuda(JuegoBlockBreaker juego) {
        super(juego);
        bloquesMuestra.add(new BloqueNormal(COLUMNA_BLOQUES, 470f, 70f, 22f, new Color(0.1f, 0.85f, 1f, 1f)));
        textosBloques.add(new String[] {"Bloque de Datos", "1 golpe. 10 pts. Puede soltar power-ups."});
        bloquesMuestra.add(new BloqueReforzado(COLUMNA_BLOQUES, 415f, 70f, 22f));
        textosBloques.add(new String[] {"Bloque Cifrado", "3 golpes. 30 pts. Más power-ups."});
        bloquesMuestra.add(new BloqueExplosivo(COLUMNA_BLOQUES, 360f, 70f, 22f));
        textosBloques.add(new String[] {"Bloque Virus", "Explota y daña a sus vecinos."});
        bloquesMuestra.add(new BloqueMetalico(COLUMNA_BLOQUES, 305f, 70f, 22f));
        textosBloques.add(new String[] {"Bloque Firewall", "Indestructible. Rodéalo."});
        BloqueNucleo nucleo = new BloqueNucleo(COLUMNA_BLOQUES + 100f, 165f);
        nucleo.setEnMovimiento(false);
        bloquesMuestra.add(nucleo);
        textosBloques.add(new String[] {"Núcleo-X (jefe final)", "20 de vida. Dispara virus. 1000 pts."});

        float y = 478f;
        potenciadoresMuestra.add(new PotenciadorExpansion(COLUMNA_POWERUPS + 20f, y));
        potenciadoresMuestra.add(new PotenciadorFuego(COLUMNA_POWERUPS + 20f, y - 45f));
        potenciadoresMuestra.add(new PotenciadorLentitud(COLUMNA_POWERUPS + 20f, y - 90f));
        potenciadoresMuestra.add(new PotenciadorMultibola(COLUMNA_POWERUPS + 20f, y - 135f));
        potenciadoresMuestra.add(new PotenciadorVidaExtra(COLUMNA_POWERUPS + 20f, y - 180f));
        potenciadoresMuestra.add(new PotenciadorFallo(COLUMNA_POWERUPS + 20f, y - 225f));
    }

    @Override
    protected void actualizar(float delta) {
        tiempo += delta;
        for (Bloque bloque : bloquesMuestra) {
            bloque.update(delta); // animaciones (pulso del virus, ojo del núcleo)
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)
                || Gdx.input.isKeyJustPressed(Input.Keys.H)) {
            juego.setScreen(new PantallaMenu(juego));
        }
    }

    @Override
    protected void dibujarFormas(ShapeRenderer shape) {
        dibujarFondoCyber(shape, FONDO_ARRIBA, FONDO_ABAJO, GRILLA, tiempo);
        shape.setColor(0f, 0f, 0f, 0.55f);
        shape.rect(20f, 120f, 375f, 410f);
        shape.rect(410f, 120f, 370f, 410f);
        shape.setColor(CIAN.r, CIAN.g, CIAN.b, 0.8f);
        shape.rect(20f, 528f, 375f, 2f);
        shape.setColor(MAGENTA.r, MAGENTA.g, MAGENTA.b, 0.8f);
        shape.rect(410f, 528f, 370f, 2f);
        for (Bloque bloque : bloquesMuestra) {
            bloque.draw(shape); // polimorfismo
        }
        for (Potenciador potenciador : potenciadoresMuestra) {
            potenciador.draw(shape); // polimorfismo
        }
    }

    @Override
    protected void dibujarTextos(SpriteBatch batch, BitmapFont font) {
        float centro = ConfiguracionJuego.getAnchoMundo() / 2f;
        dibujarTextoCentrado(batch, font, "MANUAL DEL JUGADOR", centro, 582f, 2f, Color.WHITE);
        dibujarTexto(batch, font, "BLOQUES", 36f, 518f, 1.2f, CIAN);
        dibujarTexto(batch, font, "POWER-UPS (atrápalos con la plataforma)", 426f, 518f, 1f, MAGENTA);

        for (int i = 0; i < bloquesMuestra.size() - 1; i++) {
            Bloque bloque = bloquesMuestra.get(i);
            String[] texto = textosBloques.get(i);
            dibujarTexto(batch, font, texto[0], 125f, bloque.getY() + 24f, 1.05f, Color.WHITE);
            dibujarTexto(batch, font, texto[1], 125f, bloque.getY() + 6f, 0.85f, GRIS);
        }
        String[] textoNucleo = textosBloques.get(textosBloques.size() - 1);
        dibujarTexto(batch, font, textoNucleo[0], 36f, 282f, 1.05f, MAGENTA);
        dibujarTexto(batch, font, textoNucleo[1], 36f, 262f, 0.85f, GRIS);

        for (Potenciador potenciador : potenciadoresMuestra) {
            dibujarTextoCentrado(batch, font, potenciador.getEtiqueta(), potenciador.getCentroX(),
                    potenciador.getCentroY() + 7f, 0.95f, new Color(0.03f, 0.03f, 0.08f, 1f));
            dibujarTexto(batch, font, potenciador.getDescripcion(), COLUMNA_POWERUPS + 55f,
                    potenciador.getCentroY() + 7f, 0.9f, Color.WHITE);
        }
        dibujarTexto(batch, font, "El \"?\" es un virus: ¡esquívalo!", COLUMNA_POWERUPS + 10f, 200f, 0.9f, MAGENTA);
        dibujarTexto(batch, font, "Combo: rompe bloques sin tocar la", COLUMNA_POWERUPS + 10f, 170f, 0.9f, GRIS);
        dibujarTexto(batch, font, "plataforma y multiplica tus puntos (x5).", COLUMNA_POWERUPS + 10f, 152f, 0.9f, GRIS);

        dibujarTextoCentrado(batch, font, "Mover: Flechas / A-D    Lanzar: ESPACIO    Pausa: P / ESC",
                centro, 90f, 1.05f, CIAN);
        dibujarTextoCentrado(batch, font, "ENTER / ESC: volver al menú", centro, 50f, 1f, GRIS);
    }
}
