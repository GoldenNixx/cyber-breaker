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
 * [GM-9: Patrón Builder] Participante "ConcreteBuilder" (representación "estándar").
 *
 * Construye sectores con formación de CUADRÍCULA rectangular y paleta azul/cian.
 * Mantiene una instancia privada temporal del producto ({@link #nivel}) que crea en
 * {@link #reset()}, configura paso a paso con sus setters y entrega en {@link #getResultado()}.
 */
public class ConstructorNivelEstandar implements ConstructorNivel {

    private static final int COLUMNAS = 10;
    private static final float ANCHO_BLOQUE = 70f;
    private static final float ALTO_BLOQUE = 22f;
    private static final float ESPACIO = 8f;
    private static final float BORDE_SUPERIOR = 535f;
    private static final float BORDE_SUPERIOR_CON_NUCLEO = 452f;
    private static final float Y_NUCLEO = 476f;
    private static final long SEMILLA = 2024L;
    private static final Color[] PALETA = {
        new Color(0.1f, 0.85f, 1f, 1f),
        new Color(0.15f, 1f, 0.6f, 1f),
        new Color(1f, 0.85f, 0.2f, 1f),
        new Color(1f, 0.55f, 0.15f, 1f),
        new Color(1f, 0.3f, 0.6f, 1f),
        new Color(0.65f, 0.45f, 1f, 1f)
    };

    // [GM-9] Producto en construcción y representación interna temporal
    private Nivel nivel;
    private List<Bloque> bloques;
    private List<Integer> indicesNormales;
    private List<Integer> indicesUltimaFila;
    private float bordeSuperior;
    private Random random;

    public ConstructorNivelEstandar() {
        reset();
    }

    @Override
    public void reset() {
        nivel = new Nivel();
        bloques = new ArrayList<>();
        indicesNormales = new ArrayList<>();
        indicesUltimaFila = new ArrayList<>();
        bordeSuperior = BORDE_SUPERIOR;
        random = new Random(SEMILLA);
        nivel.setTipoFormacion("Cuadrícula estándar");
    }

    @Override
    public void construirInformacion(int numero, String nombre, String descripcion) {
        nivel.setNumero(numero);
        nivel.setNombre(nombre);
        nivel.setDescripcion(descripcion);
        random.setSeed(SEMILLA + numero); // misma formación cada vez que se juega el sector
    }

    @Override
    public void construirFondo() {
        nivel.setColorFondoSuperior(new Color(0.02f, 0.06f, 0.18f, 1f));
        nivel.setColorFondoInferior(new Color(0f, 0.01f, 0.04f, 1f));
        nivel.setColorGrilla(new Color(0.15f, 0.45f, 1f, 1f));
        nivel.setColorAcento(new Color(0.2f, 0.9f, 1f, 1f));
    }

    @Override
    public void construirNucleo() {
        BloqueNucleo nucleo = new BloqueNucleo(ConfiguracionJuego.getAnchoMundo() / 2f, Y_NUCLEO);
        bloques.add(nucleo);
        nivel.setNucleo(nucleo);
        bordeSuperior = BORDE_SUPERIOR_CON_NUCLEO;
        nivel.setBloques(bloques);
    }

    @Override
    public void construirFormacion(int filas) {
        float anchoTotal = COLUMNAS * ANCHO_BLOQUE + (COLUMNAS - 1) * ESPACIO;
        float margen = (ConfiguracionJuego.getAnchoMundo() - anchoTotal) / 2f;
        indicesUltimaFila.clear();
        for (int fila = 0; fila < filas; fila++) {
            float y = bordeSuperior - ALTO_BLOQUE - fila * (ALTO_BLOQUE + ESPACIO);
            Color color = PALETA[fila % PALETA.length];
            for (int columna = 0; columna < COLUMNAS; columna++) {
                float x = margen + columna * (ANCHO_BLOQUE + ESPACIO);
                int indice = bloques.size();
                indicesNormales.add(indice);
                if (fila == filas - 1) {
                    indicesUltimaFila.add(indice);
                }
                bloques.add(new BloqueNormal(x, y, ANCHO_BLOQUE, ALTO_BLOQUE, color));
            }
        }
        nivel.setBloques(bloques);
    }

    @Override
    public void construirBloquesReforzados(int cantidad) {
        for (int i = 0; i < cantidad && !indicesNormales.isEmpty(); i++) {
            int indice = indicesNormales.get(random.nextInt(indicesNormales.size()));
            Bloque original = bloques.get(indice);
            reemplazar(indice, new BloqueReforzado(original.getX(), original.getY(),
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

    /** En la cuadrícula estándar los firewalls forman una barrera repartida en la fila inferior. */
    @Override
    public void construirBloquesMetalicos(int cantidad) {
        List<Integer> candidatos = new ArrayList<>(indicesUltimaFila);
        candidatos.retainAll(indicesNormales);
        if (candidatos.isEmpty() || cantidad <= 0) {
            return;
        }
        List<Integer> elegidos = new ArrayList<>();
        for (int k = 0; k < cantidad; k++) {
            int posicion = (int) Math.floor((k + 1) * candidatos.size() / (double) (cantidad + 1));
            Integer indice = candidatos.get(Math.min(posicion, candidatos.size() - 1));
            if (!elegidos.contains(indice)) {
                elegidos.add(indice);
            }
        }
        for (Integer indice : elegidos) {
            Bloque original = bloques.get(indice);
            reemplazar(indice, new BloqueMetalico(original.getX(), original.getY(),
                    original.getWidth(), original.getHeight()));
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
}
