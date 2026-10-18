package puppy.code.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import puppy.code.JuegoBlockBreaker;

/** Lanza la aplicación de escritorio (LWJGL3). */
public class LanzadorLwjgl3 {
    public static void main(String[] args) {
        // Esto da soporte a macOS y ayuda en Windows (ver AyudanteInicio).
        if (AyudanteInicio.iniciarNuevaJvmSiEsNecesario()) return;
        crearAplicacion();
    }

    private static Lwjgl3Application crearAplicacion() {
        return new Lwjgl3Application(new JuegoBlockBreaker(), getConfiguracionPorDefecto());
    }

    private static Lwjgl3ApplicationConfiguration getConfiguracionPorDefecto() {
        Lwjgl3ApplicationConfiguration configuracion = new Lwjgl3ApplicationConfiguration();
        configuracion.setTitle("Cyber Breaker: Protocolo Firewall");
        // Vsync limita los cuadros por segundo a lo que soporta el monitor y evita el "tearing"
        // (cortes en la imagen). En Linux a veces no funciona, por eso existe la línea siguiente.
        configuracion.useVsync(true);
        // Limita los FPS a la frecuencia de refresco del monitor activo (+1 para frecuencias
        // fraccionarias). Con Vsync activado esta línea sirve solo como respaldo.
        configuracion.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        // Si se quita la línea anterior y Vsync se pone en false, los FPS quedan sin límite: sirve
        // para probar rendimiento, pero puede exigir mucho al hardware.
        configuracion.setWindowedMode(800, 600); // misma proporción que el mundo virtual (800x600)
        // Los íconos se pueden cambiar; están en lwjgl3/src/main/resources/
        configuracion.setWindowIcon("libgdx128.png", "libgdx64.png", "libgdx32.png", "libgdx16.png");
        return configuracion;
    }
}
