package puppy.code.niveles;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.badlogic.gdx.graphics.Color;

import puppy.code.bloques.Bloque;
import puppy.code.bloques.BloqueNucleo;

/**
 * [GM-9: Patrón Builder] Participante "Product".
 *
 * Objeto complejo que representa un sector (nivel) del juego: información narrativa,
 * formación de bloques, colores del fondo, velocidad de la bola, probabilidad de power-ups,
 * cantidad de bolas iniciales y, en el sector final, el núcleo jefe.
 * Sus atributos son privados [GM-5] y se configuran paso a paso con setters desde un
 * ConcreteBuilder ({@link ConstructorNivelEstandar} o {@link ConstructorNivelArena}).
 */
public class Nivel {

    private int numero;
    private String nombre;
    private String descripcion;
    private String tipoFormacion;
    private final List<Bloque> bloques = new ArrayList<>();
    private Color colorFondoSuperior;
    private Color colorFondoInferior;
    private Color colorGrilla;
    private Color colorAcento;
    private float velocidadBola;
    private float probabilidadPotenciador;
    private int cantidadBolas;
    private BloqueNucleo nucleo;

    public Nivel() {
        this.nombre = "";
        this.descripcion = "";
        this.tipoFormacion = "";
        this.colorFondoSuperior = new Color(0.02f, 0.03f, 0.1f, 1f);
        this.colorFondoInferior = new Color(0f, 0f, 0f, 1f);
        this.colorGrilla = new Color(0.2f, 0.5f, 1f, 1f);
        this.colorAcento = new Color(0.2f, 0.9f, 1f, 1f);
        this.velocidadBola = 300f;
        this.probabilidadPotenciador = 0.2f;
        this.cantidadBolas = 1;
    }

    // ---------------- Setters usados por el ConcreteBuilder ----------------

    public void setNumero(int numero) {
        this.numero = numero;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setTipoFormacion(String tipoFormacion) {
        this.tipoFormacion = tipoFormacion;
    }

    /** Reemplaza la formación completa (se copia la lista para proteger el estado interno). */
    public void setBloques(List<Bloque> nuevosBloques) {
        bloques.clear();
        bloques.addAll(nuevosBloques);
    }

    public void setColorFondoSuperior(Color color) {
        this.colorFondoSuperior = new Color(color);
    }

    public void setColorFondoInferior(Color color) {
        this.colorFondoInferior = new Color(color);
    }

    public void setColorGrilla(Color color) {
        this.colorGrilla = new Color(color);
    }

    public void setColorAcento(Color color) {
        this.colorAcento = new Color(color);
    }

    public void setVelocidadBola(float velocidadBola) {
        this.velocidadBola = velocidadBola;
    }

    public void setProbabilidadPotenciador(float probabilidadPotenciador) {
        this.probabilidadPotenciador = probabilidadPotenciador;
    }

    public void setCantidadBolas(int cantidadBolas) {
        this.cantidadBolas = Math.max(1, cantidadBolas);
    }

    public void setNucleo(BloqueNucleo nucleo) {
        this.nucleo = nucleo;
    }

    // ---------------- Consultas y comportamiento ----------------

    /** [GM-5] Vista de solo lectura: nadie puede agregar o quitar bloques desde afuera. */
    public List<Bloque> getBloques() {
        return Collections.unmodifiableList(bloques);
    }

    /** Quita de la formación los bloques ya destruidos. */
    public void eliminarBloquesDestruidos() {
        for (int i = bloques.size() - 1; i >= 0; i--) {
            if (bloques.get(i).isDestruido()) {
                bloques.remove(i);
            }
        }
    }

    /** Bloques que aún deben destruirse para liberar el sector. */
    public int contarBloquesRestantes() {
        int restantes = 0;
        for (Bloque bloque : bloques) {
            if (!bloque.isDestruido() && bloque.cuentaParaCompletar()) {
                restantes++;
            }
        }
        return restantes;
    }

    public boolean isCompletado() {
        return contarBloquesRestantes() == 0;
    }

    public boolean isNivelJefe() {
        return nucleo != null;
    }

    public BloqueNucleo getNucleo() {
        return nucleo;
    }

    public int getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getTipoFormacion() {
        return tipoFormacion;
    }

    public Color getColorFondoSuperior() {
        return new Color(colorFondoSuperior);
    }

    public Color getColorFondoInferior() {
        return new Color(colorFondoInferior);
    }

    public Color getColorGrilla() {
        return new Color(colorGrilla);
    }

    public Color getColorAcento() {
        return new Color(colorAcento);
    }

    public float getVelocidadBola() {
        return velocidadBola;
    }

    public float getProbabilidadPotenciador() {
        return probabilidadPotenciador;
    }

    public int getCantidadBolas() {
        return cantidadBolas;
    }

    @Override
    public String toString() {
        return "Sector " + numero + " - " + nombre + " [" + tipoFormacion + "] bloques=" + bloques.size()
                + " velocidad=" + velocidadBola + " potenciadores=" + probabilidadPotenciador
                + " bolas=" + cantidadBolas + " jefe=" + isNivelJefe();
    }
}
