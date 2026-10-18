package puppy.code.mundo;

import java.util.List;

import com.badlogic.gdx.graphics.Color;

import puppy.code.entidades.Plataforma;
import puppy.code.entidades.Bola;

/**
 * Abstracción del mundo de juego que "ven" los bloques y los power-ups.
 *
 * Aplica el principio de Inversión de Dependencias (la D de SOLID): Bloque y Potenciador dependen
 * de esta interfaz y no de la clase concreta MundoJuego, por lo que solo conocen los servicios
 * que realmente necesitan (generar efectos, soltar power-ups, acceder a bolas y plataforma).
 *
 * Implementada por {@link MundoJuego}.
 */
public interface EntornoJuego {

    /** Genera una lluvia de partículas de un color en la posición indicada. */
    void generarParticulas(float x, float y, Color color, int cantidad);

    /** Muestra un texto pequeño que sube y se desvanece (p. ej. "+30"). */
    void mostrarTextoFlotante(String texto, float x, float y, Color color);

    /** Muestra un mensaje grande en el centro de la pantalla durante unos segundos. */
    void mostrarMensaje(String mensaje, Color color);

    /** Daña a todos los bloques dentro del radio indicado (permite reacciones en cadena). */
    void explotar(float centroX, float centroY, float radio, int danio);

    /** Con cierta probabilidad deja caer una cápsula de power-up. */
    void intentarSoltarPotenciador(float x, float y, float multiplicadorProbabilidad);

    /** Hace vibrar la cámara. */
    void sacudirPantalla(float intensidad, float duracion);

    /** Destruye todos los bloques destructibles restantes (detonación final del núcleo). */
    void destruirTodosLosBloques();

    Plataforma getPlataforma();

    /** Bolas en juego (lista de solo lectura). */
    List<Bola> getBolas();

    /** Agrega una bola nueva al juego (multibola). */
    void agregarBola(Bola bola);
}
