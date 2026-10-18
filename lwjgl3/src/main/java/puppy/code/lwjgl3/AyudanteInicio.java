/*
 * Copyright 2020 damios
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
// Nota: la licencia y el copyright anteriores se aplican solo a este archivo.

package puppy.code.lwjgl3;

import org.lwjgl.system.macosx.LibC;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;

/**
 * Utilidades para asegurar que la JVM se inicie con el argumento
 * {@code -XstartOnFirstThread}, necesario en macOS para que LWJGL 3 funcione.
 * También ayuda en Windows cuando el nombre de usuario tiene caracteres fuera del
 * alfabeto latino, una causa común de fallos al iniciar.
 * <br>
 * <a href="https://jvm-gaming.org/t/starting-jvm-on-mac-with-xstartonfirstthread-programmatically/57547">Basado en esta publicación de kappa</a>
 * @author damios
 */
public class AyudanteInicio {

    private static final String ARG_JVM_REINICIADA = "jvmIsRestarted";

    private AyudanteInicio() {
        throw new UnsupportedOperationException();
    }

    /**
     * Inicia una nueva JVM si la aplicación se ejecutó en macOS sin el argumento
     * {@code -XstartOnFirstThread}. Incluye además código para Windows, para el caso en que
     * la carpeta del usuario tenga ciertos caracteres no latinos (sin este código, la mayoría
     * de las aplicaciones LWJGL3 fallan al iniciar para esos usuarios).
     * <p>
     * <u>Uso:</u>
     *
     * <pre><code>
     * public static void main(String... args) {
     *     if (AyudanteInicio.iniciarNuevaJvmSiEsNecesario(true)) return;
     *     // aquí sigue el código real del main
     * }
     * </code></pre>
     *
     * @param redirigirSalida
     *            si es true, la salida de la nueva JVM se redirige a la antigua para verla en
     *            un solo lugar; en ese caso la JVM antigua sigue ejecutándose
     * @return true si se inició una nueva JVM y, por lo tanto, esta no debe ejecutar más código
     */
    public static boolean iniciarNuevaJvmSiEsNecesario(boolean redirigirSalida) {
        String nombreSo = System.getProperty("os.name").toLowerCase();
        if (!nombreSo.contains("mac")) {
            if (nombreSo.contains("windows")) {
                // Se evita un problema con la forma en que LWJGL3 carga sus archivos .dll extraídos.
                // Por defecto los extrae en "java.io.tmpdir", que suele estar en la carpeta del usuario.
                // Si el nombre del usuario tiene caracteres no ASCII, la carga falla.
                // Al extraer en la carpeta "ProgramData" (normalmente "C:\ProgramData") se evita.
                System.setProperty("java.io.tmpdir", System.getenv("ProgramData") + "/libGDX-temp");
            }
            return false;
        }

        // En una imagen nativa de Graal no hace falta -XstartOnFirstThread
        if (!System.getProperty("org.graalvm.nativeimage.imagecode", "").isEmpty()) {
            return false;
        }

        long pid = LibC.getpid();

        // Revisa si -XstartOnFirstThread ya está activo
        if ("1".equals(System.getenv("JAVA_STARTED_ON_FIRST_THREAD_" + pid))) {
            return false;
        }

        // Revisa si la JVM ya se había reiniciado antes (evita un ciclo infinito,
        // aunque lo más probable es que termine en un fallo)
        if ("true".equals(System.getProperty(ARG_JVM_REINICIADA))) {
            System.err.println(
                    "Hubo un problema al comprobar si la JVM se inició con el argumento -XstartOnFirstThread.");
            return false;
        }

        // Reinicia la JVM con -XstartOnFirstThread
        ArrayList<String> argumentosJvm = new ArrayList<>();
        String separador = System.getProperty("file.separator");
        // Esta línea supone que se usa Java 8, el mínimo para LWJGL3.
        String rutaJava = System.getProperty("java.home") + separador + "bin" + separador + "java";
        // Con Java 9 o superior se podría usar en su lugar:
        // String rutaJava = ProcessHandle.current().info().command().orElseThrow();

        if (!(new File(rutaJava)).exists()) {
            System.err.println(
                    "No se encontró una instalación de Java. Si distribuyes esta aplicación con un JRE incluido, "
                    + "agrega manualmente el argumento -XstartOnFirstThread.");
            return false;
        }

        argumentosJvm.add(rutaJava);
        argumentosJvm.add("-XstartOnFirstThread");
        argumentosJvm.add("-D" + ARG_JVM_REINICIADA + "=true");
        argumentosJvm.addAll(ManagementFactory.getRuntimeMXBean().getInputArguments());
        argumentosJvm.add("-cp");
        argumentosJvm.add(System.getProperty("java.class.path"));
        String claseMain = System.getenv("JAVA_MAIN_CLASS_" + pid);
        if (claseMain == null) {
            StackTraceElement[] traza = Thread.currentThread().getStackTrace();
            if (traza.length > 0) {
                claseMain = traza[traza.length - 1].getClassName();
            } else {
                System.err.println("No se pudo determinar la clase principal.");
                return false;
            }
        }
        argumentosJvm.add(claseMain);

        try {
            if (!redirigirSalida) {
                ProcessBuilder constructorProceso = new ProcessBuilder(argumentosJvm);
                constructorProceso.start();
            } else {
                Process proceso = (new ProcessBuilder(argumentosJvm))
                        .redirectErrorStream(true).start();
                BufferedReader salidaProceso = new BufferedReader(
                        new InputStreamReader(proceso.getInputStream()));
                String linea;

                while ((linea = salidaProceso.readLine()) != null) {
                    System.out.println(linea);
                }

                proceso.waitFor();
            }
        } catch (Exception e) {
            System.err.println("Hubo un problema al reiniciar la JVM");
            e.printStackTrace();
        }

        return true;
    }

    /**
     * Inicia una nueva JVM si la aplicación se ejecutó en macOS sin el argumento
     * {@code -XstartOnFirstThread}. Redirige la salida de la nueva JVM a la antigua.
     * <p>
     * <u>Uso:</u>
     *
     * <pre>
     * public static void main(String... args) {
     *     if (AyudanteInicio.iniciarNuevaJvmSiEsNecesario()) return;
     *     // aquí sigue el código real del main
     * }
     * </pre>
     *
     * @return true si se inició una nueva JVM y, por lo tanto, esta no debe ejecutar más código
     */
    public static boolean iniciarNuevaJvmSiEsNecesario() {
        return iniciarNuevaJvmSiEsNecesario(true);
    }
}
