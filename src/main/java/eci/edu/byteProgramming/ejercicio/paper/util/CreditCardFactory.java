package eci.edu.byteProgramming.ejercicio.paper.util;

import java.util.List;

/**
 * Factory concreta para pagos con TARJETA DE CREDITO.
 *
 * Crea tanto el pago (con su comportamiento de ejecucion) como el
 * validador con sus propias reglas. La logica de compra solo conoce
 * la abstraccion {@link ECIPayment.Factory}.
 */
public class CreditCardFactory implements ECIPayment.Factory {

    private static final double MAX_AMOUNT = 10_000_000.0;

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.CREDIT_CARD;
    }

    @Override
    public ECIPayment createPayment(String customerEmail, double amount, List<Product> products) {
        return new ECIPayment(customerEmail, amount, products) {
            @Override
            public PaymentMethod getMethod() {
                return PaymentMethod.CREDIT_CARD;
            }

            @Override
            public void execute() {
                System.out.printf("  [Tarjeta] Cargando $%,.0f a la tarjeta de %s%n",
                        getAmount(), getCustomerEmail());
                setStatus(PaymentStatus.COMPLETED);
            }
        };
    }

    @Override
    public ValidatePayment createValidator() {
        return payment -> {
            boolean ok = payment.getAmount() > 0
                    && payment.getAmount() <= MAX_AMOUNT
                    && payment.getCustomerEmail().contains("@");
            System.out.println("  [Tarjeta] Validacion (monto<= $"
                    + String.format("%,.0f", MAX_AMOUNT) + " y correo valido): "
                    + (ok ? "APROBADA" : "RECHAZADA"));
            return ok;
        };
    }
}
