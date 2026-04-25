package eci.edu.byteProgramming.ejercicio.paper.util;

/**
 * Metodos de pago soportados por la tienda virtual.
 *
 * Aplica encapsulamiento (campo final, acceso solo via getter) y permite
 * extender el sistema agregando nuevos metodos sin tocar la logica
 * existente del flujo de compra.
 */
public enum PaymentMethod {

    CREDIT_CARD("Tarjeta de credito"),
    PAYPAL("PayPal"),
    CRYPTO("Criptomonedas");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
