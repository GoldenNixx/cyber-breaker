package puppy.code.niveles;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.graphics.Color;

import puppy.code.ConfiguracionJuego;
import puppy.code.bloques.Bloque;
import puppy.code.bloques.BloqueNucleo;
import puppy.code.bloques.BloqueExplosivo;
import puppy.code.bloques.BloqueMetalico;
import puppy.code.bloques.BloqueNormal;
import puppy.code.bloques.BloqueReforzado;

/**
 * [GM-9: Patrón Builder] Participante "ConcreteBuilder" (representación "arena").
 *
 * Con los MISMOS pasos que {@link ConstructorNivelEstandar} produce una representación distinta:
 * formación en DIAMANTE, paleta carmesí/magenta, bloques cifrados concentrados en el centro
 * de la formación y firewalls en los extremos de las filas (como murallas).
 */
public class ConstructorNivelArena implements ConstructorNivel {

    private static final int COLUMNAS = 10;
    private static final float ANCHO_BLOQUE = 70f;
    private static final float ALTO_BLOQUE = 22f;
    private static final float ESPACIO = 8f;
    private static final float BORDE_SUPERIOR = 535f;
    private static final float BORDE_SUPERIOR_CON_NUCLEO = 452f;
    private static final float Y_NUCLEO = 476f;
    private static final long SEMILLA = 4096L;
    private static final Color[] PALETA = {
        new Color(1f, 0.25f, 0.35f, 1f),
        new Color(1f, 0.5f, 0.2f, 1f),
        new Color(1f, 0.75f, 0.25f, 1f),
        new Color(0.95f, 0.3f, 0.75f, 1f),
        new Color(0.75f, 0.35f, 1f, 1f),
        new Color(1f, 0.45f, 0.55f, 1f)
    };

    // [GM-9] Producto en construcción y representación interna temporal
    private Nivel nivel;
    private List<Bloque> bloques;
    private List<Integer> indicesNormales;
    private List<int[]> extremosFilas;
    private float bordeSuperior;
    private Random random;

    public ConstructorNivelArena() {
        reset();
    }

    @Override
    public void reset() {
        nivel = new Nivel();
        bloques = new ArrayList<>();
        indicesNormales = new ArrayList<>();
        extremosFilas = new ArrayList<>();
        bordeSuperior = BORDE_SUPERIOR;
        random = new Random(SEMILLA);
        nivel.setTipoFormacion("Diamante (arena)");
    }

    @Override
    public void construirInformacion(int numero, String nombre, String descripcion) {
        nivel.setNumero(numero);
        nivel.setNombre(nombre);
        nivel.setDescripcion(descripcion);
        random.setSeed(SEMILLA + numero);
    }

    @Override
    public void construirFondo() {
        nivel.setColorFondoSuperior(new Color(0.16f, 0.01f, 0.09f, 1f));
        nivel.setColorFondoInferior(new Color(0.02f, 0f, 0.02f, 1f));
        nivel.setColorGrilla(new Color(1f, 0.2f, 0.5f, 1f));
        nivel.setColorAcento(new Color(1f, 0.3f, 0.6f, 1f));
    }

    @Override
    public void construirNucleo() {
        BloqueNucleo nucleo = new BloqueNucleo(ConfiguracionJuego.getAnchoMundo() / 2f, Y_NUCLEO);
        bloques.add(nucleo);
        nivel.setNucleo(nucleo);
        bordeSuperior = BORDE_SUPERIOR_CON_NUCLEO;
        nivel.setBloques(bloques);
    }

    /** Formación en diamante: las filas centrales son las más anchas. */
    @Override
    public void construirFormacion(int filas) {
        float anchoTotal = COLUMNAS * ANCHO_BLOQUE + (COLUMNAS - 1) * ESPACIO;
        float margen = (ConfiguracionJuego.getAnchoMundo() - anchoTotal) / 2f;
        float filaCentral = (filas - 1) / 2f;
        for (int fila = 0; fila < filas; fila++) {
            int distancia = (int) Math.floor(Math.abs(fila - filaCentral));
            int bloquesEnFila = Math.max(2, COLUMNAS - 2 * distancia);
            int columnaInicial = (COLUMNAS - bloquesEnFila) / 2;
            float y = bordeSuperior - ALTO_BLOQUE - fila * (ALTO_BLOQUE + ESPACIO);
            Color color = PALETA[fila % PALETA.length];
            int primero = bloques.size();
            for (int c = 0; c < bloquesEnFila; c++) {
                float x = margen + (columnaInicial + c) * (ANCHO_BLOQUE + ESPACIO);
                indicesNormales.add(bloques.size());
                bloques.add(new BloqueNormal(x, y, ANCHO_BLOQUE, ALTO_BLOQUE, color));
            }
            extremosFilas.add(new int[] {primero, bloques.size() - 1});
        }
        nivel.setBloques(bloques);
    }

    /** Los bloques cifrados protegen el corazón de la formación. */
    @Override
    public void construirBloquesReforzados(int cantidad) {
        float centroX = ConfiguracionJuego.getAnchoMundo() / 2f;
        float centroY = calcularCentroYFormacion();
        for (int i = 0; i < cantidad && !indicesNormales.isEmpty(); i++) {
            // Busca el bloque normal más cercano al centro de la formación
            int masCercano = indicesNormales.get(0);
            for (Integer indice : indicesNormales) {
                if (distanciaAlCentro(indice, centroX, centroY) < distanciaAlCentro(masCercano, centroX, centroY)) {
                    masCercano = indice;
                }
            }
            Bloque original = bloques.get(masCercano);
            reemplazar(masCercano, new BloqueReforzado(original.getX(), original.getY(),
                    original.getWidth(), original.getHeight()));
        }
        nivel.setBloques(bloques);
    }

    @Override
    public void construirBloquesExplosivos(int cantidad) {
        for (int i = 0; i < cantidad && !indicesNormales.isEmpty(); i++) {
            int indice = indicesNormales.get(random.nextInt(indicesNormales.size()));
            Bloque original = bloques.get(indice);
            reemplazar(indice, new BloqueExplosivo(original.getX(), original.getY(),
                    original.getWidth(), original.getHeight()));
        }
        nivel.setBloques(bloques);
    }

    /** Los firewalls se ubican en los extremos de las filas, como murallas laterales. */
    @Override
    public void construirBloquesMetalicos(int cantidad) {
        int colocados = 0;
        for (int i = extremosFilas.size() - 1; i >= 0 && colocados < cantidad; i--) {
            int[] extremos = extremosFilas.get(i);
            for (int j = 0; j < extremos.length && colocados < cantidad; j++) {
                int indice = extremos[j];
                if (indicesNormales.contains(indice)) {
                    Bloque original = bloques.get(indice);
                    reemplazar(indice, new BloqueMetalico(original.getX(), original.getY(),
                            original.getWidth(), original.getHeight()));
                    colocados++;
                }
            }
        }
        nivel.setBloques(bloques);
    }

    @Override
    public void construirVelocidadBola(float velocidad) {
        nivel.setVelocidadBola(velocidad);
    }

    @Override
    public void construirPotenciadores(float probabilidad) {
        nivel.setProbabilidadPotenciador(probabilidad);
    }

    @Override
    public void construirBolasIniciales(int cantidad) {
        nivel.setCantidadBolas(cantidad);
    }

    @Override
    public Nivel getResultado() {
        return nivel;
    }

    private void reemplazar(int indice, Bloque nuevo) {
        bloques.set(indice, nuevo);
        indicesNormales.remove(Integer.valueOf(indice));
    }

    private float calcularCentroYFormacion() {
        if (indicesNormales.isEmpty()) {
            return bordeSuperior;
        }
        float suma = 0f;
        for (Integer indice : indicesNormales) {
            suma += bloques.get(indice).getCentroY();
        }
        return suma / indicesNormales.size();
    }

    private float distanciaAlCentro(int indice, float centroX, float centroY) {
        Bloque bloque = bloques.get(indice);
        float dx = bloque.getCentroX() - centroX;
        float dy = (bloque.getCentroY() - centroY) * 2f; // pesa más la cercanía vertical
        return dx * dx + dy * dy;
    }
}
