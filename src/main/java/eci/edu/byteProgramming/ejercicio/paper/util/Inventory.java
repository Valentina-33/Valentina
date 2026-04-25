package eci.edu.byteProgramming.ejercicio.paper.util;

/**
 * Modulo de INVENTARIO. Reacciona al evento de pago exitoso
 * descontando el stock de los productos comprados.
 */
public class Inventory implements PaymentObserver {

    @Override
    public void onPaymentProcessed(ECIPayment payment) {
        System.out.println("[Inventario] Descontando stock por compra de "
                + payment.getProducts().size() + " producto(s):");
        for (Product p : payment.getProducts()) {
            p.decreaseStock(1);
            System.out.println("    - " + p.getName()
                    + " -> stock restante: " + p.getStock());
        }
    }
}
