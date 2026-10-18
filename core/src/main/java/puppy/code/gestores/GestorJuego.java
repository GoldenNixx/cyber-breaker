package puppy.code.gestores;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/**
 * [GM-6: Patrón Singleton] Participante "Singleton".
 *
 * Centraliza el estado global de una partida: puntaje, vidas, sector (nivel) actual,
 * combo de destrucción y récord histórico (persistido con {@link Preferences} de LibGDX).
 *
 * Problema que resuelve: en el juego base el puntaje, las vidas y el nivel eran atributos
 * sueltos de JuegoBlockBreaker; cualquier otra clase (bloques, power-ups, pantallas) que
 * necesitara leerlos o modificarlos no tenía cómo hacerlo sin recibir referencias cruzadas.
 * Con el Singleton existe una única fuente de verdad, accesible desde cualquier parte.
 *
 * Clientes/Contexto: JuegoBlockBreaker, PantallaMenu, PantallaJuego, PantallaFinJuego, MundoJuego,
 * Bloque (otorgarPuntaje), Potenciador (activar) y PotenciadorVidaExtra.
 */
public class GestorJuego {

    // [GM-6] Atributo estático privado que guarda la ÚNICA instancia de la clase
    private static GestorJuego instance;

    private static final int VIDAS_INICIALES = 3;
    private static final int VIDAS_MAXIMAS = 5;
    private static final int TOTAL_NIVELES = 5;
    private static final int MULTIPLICADOR_MAXIMO = 5;
    private static final int BLOQUES_POR_NIVEL_DE_COMBO = 3;
    private static final String ARCHIVO_PREFERENCIAS = "cyber-breaker-protocolo-firewall";
    private static final String CLAVE_RECORD = "record";

    // [GM-5] Estado global encapsulado: solo se modifica mediante métodos con reglas de negocio
    private int puntaje;
    private int vidas;
    private int nivelActual;
    private int record;
    private int combo;
    private int bloquesDestruidos;
    private boolean nuevoRecord;
    private Preferences preferencias;

    // [GM-6] Constructor PRIVADO: impide hacer "new GestorJuego()" desde fuera de la clase
    private GestorJuego() {
        reiniciarEstado();
    }

    // [GM-6] Punto de acceso global con inicialización perezosa (lazy initialization)
    public static GestorJuego getInstance() {
        if (instance == null) {
            instance = new GestorJuego();
        }
        return instance;
    }

    /** Carga el récord guardado en disco. Se llama una vez al iniciar la aplicación. */
    public void cargarRecord() {
        Preferences prefs = obtenerPreferencias();
        if (prefs != null) {
            record = prefs.getInteger(CLAVE_RECORD, 0);
        }
    }

    /** Persiste el récord actual en disco (Preferences de LibGDX). */
    public void guardarRecord() {
        Preferences prefs = obtenerPreferencias();
        if (prefs != null) {
            prefs.putInteger(CLAVE_RECORD, record);
            prefs.flush();
        }
    }

    private Preferences obtenerPreferencias() {
        if (preferencias == null && Gdx.app != null) {
            preferencias = Gdx.app.getPreferences(ARCHIVO_PREFERENCIAS);
        }
        return preferencias;
    }

    /** Reinicia puntaje, vidas y sector para comenzar una partida nueva (el récord se conserva). */
    public void nuevaPartida() {
        reiniciarEstado();
    }

    private void reiniciarEstado() {
        puntaje = 0;
        vidas = VIDAS_INICIALES;
        nivelActual = 1;
        combo = 0;
        bloquesDestruidos = 0;
        nuevoRecord = false;
    }

    /**
     * Registra un bloque destruido aplicando el multiplicador de combo.
     *
     * @param puntosBase puntos que entrega el bloque
     * @return puntos realmente ganados (base x multiplicador)
     */
    public int registrarBloqueDestruido(int puntosBase) {
        bloquesDestruidos++;
        combo++;
        int ganados = puntosBase * getMultiplicador();
        sumarPuntos(ganados);
        return ganados;
    }

    public void sumarPuntos(int puntos) {
        if (puntos <= 0) {
            return;
        }
        puntaje += puntos;
        if (puntaje > record) {
            record = puntaje;
            nuevoRecord = true;
        }
    }

    /** El combo se pierde cada vez que la bola toca la plataforma. */
    public void reiniciarCombo() {
        combo = 0;
    }

    public int getMultiplicador() {
        return Math.min(1 + combo / BLOQUES_POR_NIVEL_DE_COMBO, MULTIPLICADOR_MAXIMO);
    }

    public void perderVida() {
        if (vidas > 0) {
            vidas--;
        }
        combo = 0;
    }

    /** @return true si se pudo sumar la vida; false si ya estaba en el máximo. */
    public boolean ganarVida() {
        if (vidas < VIDAS_MAXIMAS) {
            vidas++;
            return true;
        }
        return false;
    }

    public boolean isFinDelJuego() {
        return vidas <= 0;
    }

    /** Suma el bono por completar el sector actual y lo retorna. */
    public int bonificarNivelCompletado() {
        int bono = 200 * nivelActual + 100 * vidas;
        sumarPuntos(bono);
        return bono;
    }

    public void avanzarNivel() {
        if (nivelActual < TOTAL_NIVELES) {
            nivelActual++;
        }
    }

    public boolean esUltimoNivel() {
        return nivelActual >= TOTAL_NIVELES;
    }

    public int getPuntaje() {
        return puntaje;
    }

    public int getVidas() {
        return vidas;
    }

    public int getVidasMaximas() {
        return VIDAS_MAXIMAS;
    }

    public int getNivelActual() {
        return nivelActual;
    }

    public int getTotalNiveles() {
        return TOTAL_NIVELES;
    }

    public int getRecord() {
        return record;
    }

    public int getCombo() {
        return combo;
    }

    public int getBloquesDestruidos() {
        return bloquesDestruidos;
    }

    public boolean isNuevoRecord() {
        return nuevoRecord;
    }
}
