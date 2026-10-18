# Cyber Breaker: Protocolo Firewall

Rediseño del juego base **Block Breaker 2024** (LibGDX 1.12.1) para el proyecto de
Programación Orientada a Objetos. Todo se dibuja con primitivas de LibGDX
(`ShapeRenderer`, `SpriteBatch` y la `BitmapFont` incluida), por lo que **no requiere
assets externos**.

> Año 2049. La IA rebelde NÚCLEO-X tomó el control de la red de Valparaíso y levantó cinco
> sectores de firewall. Eres AEGIS, el último antivirus: controla la plataforma, lanza
> paquetes de datos, rompe los muros y destruye el núcleo.

## Requisitos

- JDK 8 o JDK 11 (el código compila con nivel Java 8, por lo que funciona en ambos).
- Gradle 6.9.4 (lo descarga solo el wrapper `gradlew`; compatible con NetBeans 12).
- Conexión a internet la primera vez (Gradle descarga LibGDX 1.12.1).

## Cómo ejecutar

**Desde consola** (en la carpeta del proyecto):

```bash
# Windows
gradlew.bat lwjgl3:run
# Linux / macOS
./gradlew lwjgl3:run
```

**NetBeans:** File → Open Project → seleccionar la carpeta `BlockBreaker2024` (proyecto
Gradle) → clic derecho sobre `LanzadorLwjgl3.java` (subproyecto `lwjgl3`) → Run File.
Si NetBeans no carga el proyecto, ejecutar por consola con `gradlew.bat lwjgl3:run`.

**Eclipse:** File → Import → Gradle → Existing Gradle Project → seleccionar la carpeta →
ejecutar `lwjgl3/src/main/java/puppy/code/lwjgl3/LanzadorLwjgl3.java` como Java Application.
Si los acentos se ven mal, configurar la codificación del proyecto en UTF-8.

## Controles

| Tecla | Acción |
|---|---|
| ← / → o A / D | Mover la plataforma |
| ESPACIO | Lanzar la bola |
| P o ESC | Pausar / continuar |
| Q (en pausa) | Volver al menú |
| H (en el menú) | Manual del jugador |
| ENTER | Comenzar / jugar de nuevo |

## Estructura del código (`core/src/main/java/puppy/code`)

| Paquete | Contenido |
|---|---|
| `puppy.code` | `JuegoBlockBreaker` (extiende `Game`), `ConfiguracionJuego` |
| `gestores` | `GestorJuego` — **Singleton** (GM-6) |
| `entidades` | `ObjetoJuego` (abstracta), `Colisionable` (interfaz), `Plataforma`, `Bola` (Context de Strategy) |
| `estrategia` | `EstrategiaMovimientoBola` + 4 estrategias concretas — **Strategy** (GM-7) |
| `bloques` | `Bloque` (método plantilla `alGolpear`) + 5 bloques — **Template Method** (GM-8) |
| `potenciadores` | `Potenciador` (abstracta) + 6 potenciadores |
| `niveles` | `Nivel`, `ConstructorNivel`, `ConstructorNivelEstandar`, `ConstructorNivelArena`, `DirectorNivel` — **Builder** (GM-9) |
| `mundo` | `MundoJuego`, `EntornoJuego`, `Particula`, `TextoFlotante` |
| `pantallas` | `PantallaBase` (abstracta), `PantallaMenu`, `PantallaAyuda`, `PantallaJuego`, `PantallaFinJuego` |

Los comentarios `[GM-x]` dentro del código indican dónde se aplica cada requerimiento.
