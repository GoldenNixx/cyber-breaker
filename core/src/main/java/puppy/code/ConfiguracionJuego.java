package puppy.code;

/**
 * Constantes globales de configuración del juego.
 *
 * [GM-5: Encapsulamiento] Todas las constantes son privadas y solo se exponen mediante
 * métodos de consulta (getters estáticos). La clase es final y su constructor es privado
 * porque no tiene sentido crear instancias de ella.
 */
public final class ConfiguracionJuego {

    private static final String TITULO = "Cyber Breaker: Protocolo Firewall";
    private static final float ANCHO_MUNDO = 800f;
    private static final float ALTO_MUNDO = 600f;
    private static final float ALTO_HUD = 40f;
    private static final float ALTURA_PADDLE = 40f;

    private ConfiguracionJuego() {
        // Clase utilitaria: no se instancia
    }

    public static String getTitulo() {
        return TITULO;
    }

    /** Ancho del mundo virtual (unidades de la cámara). */
    public static float getAnchoMundo() {
        return ANCHO_MUNDO;
    }

    /** Alto del mundo virtual (unidades de la cámara). */
    public static float getAltoMundo() {
        return ALTO_MUNDO;
    }

    /** Alto de la barra superior de información (HUD). */
    public static float getAltoHud() {
        return ALTO_HUD;
    }

    /** Límite superior del área de juego (la bola rebota aquí, bajo el HUD). */
    public static float getTechoJuego() {
        return ALTO_MUNDO - ALTO_HUD;
    }

    /** Coordenada Y en la que se ubica la plataforma del jugador. */
    public static float getAlturaPlataforma() {
        return ALTURA_PADDLE;
    }
}
