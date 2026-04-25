package eci.edu.byteProgramming.ejercicio.paper.util;

/**
 * Contrato de validacion de un pago.
 *
 * Cada metodo de pago tiene su propia implementacion (creada por su
 * factory correspondiente). El nucleo de la aplicacion solo conoce
 * esta abstraccion (DIP).
 */
@FunctionalInterface
public interface ValidatePayment {

    /**
     * @return true si el pago cumple las reglas de validacion del metodo.
     */
    boolean validate(ECIPayment payment);
}
