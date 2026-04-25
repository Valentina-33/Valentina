package eci.edu.byteProgramming.ejercicio.paper.util;

/**
 * Producto del catalogo. Mantiene su stock encapsulado.
 *
 * SOLID:
 *  - SRP: solo representa un producto y su stock.
 */
public class Product {

    private final String id;
    private final String name;
    private final double price;
    private int stock;

    public Product(String id, String name, double price, int stock) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void decreaseStock(int qty) {
        if (qty < 0) {
            throw new IllegalArgumentException("La cantidad a descontar no puede ser negativa");
        }
        this.stock = Math.max(0, this.stock - qty);
    }

    @Override
    public String toString() {
        return name + " (id=" + id + ", $" + price + ", stock=" + stock + ")";
    }
}
