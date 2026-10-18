package puppy.code.entidades;

import com.badlogic.gdx.math.Rectangle;

/**
 * [GM-4: Interfaz] Contrato de todo objeto con el que se puede chocar.
 *
 * Implementada por {@link Plataforma}, {@code Bloque} (y sus 5 subclases) y {@code Potenciador}
 * (y sus 6 subclases).
 *
 * Uso polimórfico en el programa:
 *  - {@link Bola#colisionaCon(Colisionable)} y {@link Bola#rebotarContra(Colisionable)}
 *    funcionan igual para la plataforma y para cualquier tipo de bloque. En el juego base
 *    existían dos métodos duplicados collidesWith(Plataforma) y collidesWith(Bloque).
 *  - {@code MundoJuego.hayContacto(Colisionable, Colisionable)} detecta cuándo la plataforma
 *    atrapa una cápsula de power-up.
 */
public interface Colisionable {

    /** Caja de colisión actual del objeto. */
    Rectangle getLimites();

    /** Indica si el objeto participa en colisiones en este momento (p. ej. un bloque destruido no). */
    boolean isColisionable();
}
