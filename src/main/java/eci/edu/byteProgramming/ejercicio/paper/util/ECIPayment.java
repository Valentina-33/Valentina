package eci.edu.byteProgramming.ejercicio.paper.util;

import java.util.Collections;
import java.util.List;

/**
 * Pago abstracto de la tienda virtual (ECI = E-Commerce Institutional).
 *
 * Define los datos comunes (cliente, monto, productos, estado) y delega
 * en sus subclases la implementacion polimorfica de {@link #execute()}
 * y {@link #getMethod()}.
 *
 * Tambien expone la interfaz anidada {@link Factory}, que es el
 * contrato del PATRON ABSTRACT FACTORY: cada factory concreta
 * (CreditCardFactory, PaypalFactory, CryptoFactory) crea una familia
 * coherente de objetos relacionados (pago + validador).
 *
 * SOLID:
 *  - SRP: solo modela el dato + comportamiento del pago.
 *  - OCP: nuevos tipos de pago se agregan extendiendo esta clase.
 *  - LSP: cualquier subclase puede sustituir a ECIPayment.
 *  - DIP: la logica principal depende de esta abstraccion, no de
 *    implementaciones concretas.
 */
public abstract class ECIPayment {

    private final String customerEmail;
    private final double amount;
    private final List<Product> products;
    private PaymentStatus status;

    protected ECIPayment(String customerEmail, double amount, List<Product> products) {
        if (customerEmail == null || customerEmail.isBlank()) {
            throw new IllegalArgumentException("El correo del cliente es obligatorio");
        }
        if (products == null || products.isEmpty()) {
            throw new IllegalArgumentException("El pago debe tener al menos un producto");
        }
        this.customerEmail = customerEmail;
        this.amount = amount;
        this.products = products;
        this.status = PaymentStatus.PENDING;
    }

    /** Polimorfismo: cada subclase indica su metodo de pago. */
    public abstract PaymentMethod getMethod();

    /** Polimorfismo: cada subclase implementa su forma de cobrar. */
    public abstract void execute();

    public String getCustomerEmail() {
        return customerEmail;
    }

    public double getAmount() {
        return amount;
    }

    public List<Product> getProducts() {
        return Collections.unmodifiableList(products);
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    /**
     * PATRON ABSTRACT FACTORY.
     *
     * Define como construir una familia de objetos relacionados
     * (un {@link ECIPayment} y su {@link ValidatePayment}) para un
     * metodo de pago especifico, sin que el cliente conozca las
     * clases concretas.
     */
    public interface Factory {

        PaymentMethod getMethod();

        ECIPayment createPayment(String customerEmail, double amount, List<Product> products);

        ValidatePayment createValidator();
    }
}
