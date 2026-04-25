package com.videoclub.model;

/**
 * Clase abstracta que representa una pelicula.
 * Aplica ENCAPSULAMIENTO (atributos privados con getters/setters controlados)
 * y POLIMORFISMO (metodo abstracto getType() implementado por subclases).
 *
 * Principio SOLID aplicado:
 *  - LSP (Liskov Substitution): cualquier subclase de Movie puede usarse
 *    donde se espera una Movie sin romper el comportamiento.
 *  - OCP (Open/Closed): se pueden agregar nuevos tipos de peliculas
 *    extendiendo esta clase, sin modificar el codigo existente.
 */
public abstract class Movie {

    private final String title;
    private final double price;
    private boolean available;

    protected Movie(String title, double price, boolean available) {
        this.title = title;
        this.price = price;
        this.available = available;
    }

    public String getTitle() {
        return title;
    }

    public double getPrice() {
        return price;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    /**
     * Polimorfismo: cada subclase define su propio tipo.
     */
    public abstract String getType();
}
