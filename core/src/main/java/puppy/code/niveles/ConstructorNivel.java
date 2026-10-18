package puppy.code.niveles;

/**
 * [GM-9: Patrón Builder] Participante "Builder".
 *
 * Interfaz que declara los pasos para construir las partes de un {@link Nivel} (Product).
 * Implementada por los ConcreteBuilders {@link ConstructorNivelEstandar} y
 * {@link ConstructorNivelArena}; el {@link DirectorNivel} define en qué orden se llaman.
 */
public interface ConstructorNivel {

    /** Descarta cualquier construcción previa y crea un Nivel vacío. */
    void reset();

    /** Número, nombre y texto narrativo del sector. */
    void construirInformacion(int numero, String nombre, String descripcion);

    /** Paleta de colores del fondo y de la grilla. */
    void construirFondo();

    /** Núcleo jefe (solo en el sector final). Debe llamarse antes de construirFormacion. */
    void construirNucleo();

    /** Formación base de bloques normales con la cantidad de filas indicada. */
    void construirFormacion(int filas);

    /** Reemplaza bloques normales por bloques cifrados (3 golpes). */
    void construirBloquesReforzados(int cantidad);

    /** Reemplaza bloques normales por bloques virus (explosivos). */
    void construirBloquesExplosivos(int cantidad);

    /** Reemplaza bloques normales por bloques firewall (indestructibles). */
    void construirBloquesMetalicos(int cantidad);

    /** Velocidad inicial de la bola. */
    void construirVelocidadBola(float velocidad);

    /** Probabilidad base de que un bloque suelte un power-up. */
    void construirPotenciadores(float probabilidad);

    /** Cantidad de bolas con que se inicia el sector. */
    void construirBolasIniciales(int cantidad);

    /** Retorna el producto terminado. */
    Nivel getResultado();
}
