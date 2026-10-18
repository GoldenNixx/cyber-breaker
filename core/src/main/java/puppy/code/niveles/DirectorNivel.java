package puppy.code.niveles;

/**
 * [GM-9: Patrón Builder] Participante "Director".
 *
 * Recibe un {@link ConstructorNivel} y define las RECETAS (orden de los pasos) de cada sector.
 * El Director no sabe qué representación se obtiene: con un {@link ConstructorNivelEstandar}
 * resulta una cuadrícula azul y con un {@link ConstructorNivelArena} un diamante carmesí,
 * aunque la receta sea exactamente la misma.
 */
public class DirectorNivel {

    private ConstructorNivel constructor;

    public DirectorNivel(ConstructorNivel constructor) {
        this.constructor = constructor;
    }

    /** Permite reutilizar el mismo Director con otro ConcreteBuilder. */
    public void setConstructor(ConstructorNivel constructor) {
        this.constructor = constructor;
    }

    /** Sector 1: pocas filas, bola lenta y muchos power-ups para aprender. */
    public Nivel construirNivelTutorial() {
        constructor.reset();
        constructor.construirInformacion(1, "Red Perimetral",
                "Los primeros muros del firewall. Calibra tu plataforma.");
        constructor.construirFondo();
        constructor.construirFormacion(3);
        constructor.construirVelocidadBola(300f);
        constructor.construirPotenciadores(0.25f);
        constructor.construirBolasIniciales(1);
        return constructor.getResultado();
    }

    /** Sector 2: aparecen bloques cifrados y virus. */
    public Nivel construirNivelIntermedio() {
        constructor.reset();
        constructor.construirInformacion(2, "Pirámide de Datos",
                "Los bloques cifrados resisten 3 golpes. Cuidado con los virus.");
        constructor.construirFondo();
        constructor.construirFormacion(4);
        constructor.construirBloquesReforzados(4);
        constructor.construirBloquesExplosivos(3);
        constructor.construirVelocidadBola(320f);
        constructor.construirPotenciadores(0.22f);
        constructor.construirBolasIniciales(1);
        return constructor.getResultado();
    }

    /** Sector 3: se suman los firewalls indestructibles. */
    public Nivel construirNivelAvanzado() {
        constructor.reset();
        constructor.construirInformacion(3, "Servidores Blindados",
                "Los firewalls grises son indestructibles: rodéalos.");
        constructor.construirFondo();
        constructor.construirFormacion(5);
        constructor.construirBloquesReforzados(8);
        constructor.construirBloquesExplosivos(4);
        constructor.construirBloquesMetalicos(3);
        constructor.construirVelocidadBola(340f);
        constructor.construirPotenciadores(0.2f);
        constructor.construirBolasIniciales(1);
        return constructor.getResultado();
    }

    /** Sector 4: formación grande y se juega con dos bolas desde el inicio. */
    public Nivel construirNivelExperto() {
        constructor.reset();
        constructor.construirInformacion(4, "Laberinto Cifrado",
                "Dos paquetes de datos a la vez. No pierdas ninguno.");
        constructor.construirFondo();
        constructor.construirFormacion(6);
        constructor.construirBloquesReforzados(10);
        constructor.construirBloquesExplosivos(5);
        constructor.construirBloquesMetalicos(4);
        constructor.construirVelocidadBola(355f);
        constructor.construirPotenciadores(0.2f);
        constructor.construirBolasIniciales(2);
        return constructor.getResultado();
    }

    /** Sector 5 (jefe): el Núcleo-X protegido por su escolta. */
    public Nivel construirNivelJefe() {
        constructor.reset();
        constructor.construirInformacion(5, "Núcleo-X",
                "Destruye el núcleo de la IA. Esquiva los virus que dispara.");
        constructor.construirFondo();
        constructor.construirNucleo();
        constructor.construirFormacion(3);
        constructor.construirBloquesReforzados(6);
        constructor.construirBloquesExplosivos(4);
        constructor.construirBloquesMetalicos(4);
        constructor.construirVelocidadBola(360f);
        constructor.construirPotenciadores(0.25f);
        constructor.construirBolasIniciales(1);
        return constructor.getResultado();
    }
}
