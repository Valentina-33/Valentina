package com.videoclub.membership;

/**
 * Estrategia de membresia (PATRON STRATEGY).
 *
 * Cada implementacion define su propia regla de descuento, lo que permite
 * agregar nuevas membresias (VIP, Estudiante, etc.) sin modificar el
 * RentalService.
 *
 * Principios SOLID aplicados:
 *  - SRP (Single Responsibility): solo se ocupa de calcular descuentos.
 *  - OCP (Open/Closed): abierto a extension, cerrado a modificacion.
 *  - DIP (Dependency Inversion): el servicio de alquiler depende de esta
 *    abstraccion, no de implementaciones concretas.
 *  - ISP (Interface Segregation): interfaz pequenia y enfocada.
 */
public interface Membership {

    String getName();

    double getDiscountRate();

    default double calculateDiscount(double subtotal) {
        return subtotal * getDiscountRate();
    }

    default double calculateTotal(double subtotal) {
        return subtotal - calculateDiscount(subtotal);
    }
}
