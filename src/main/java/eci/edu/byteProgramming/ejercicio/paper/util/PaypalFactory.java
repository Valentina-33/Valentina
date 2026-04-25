package eci.edu.byteProgramming.ejercicio.paper.util;

import java.util.List;

/**
 * Factory concreta para pagos con PAYPAL.
 */
public class PaypalFactory implements ECIPayment.Factory {

    private static final double MAX_AMOUNT = 8_000_000.0;

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.PAYPAL;
    }

    @Override
    public ECIPayment createPayment(String customerEmail, double amount, List<Product> products) {
        return new ECIPayment(customerEmail, amount, products) {
            @Override
            public PaymentMethod getMethod() {
                return PaymentMethod.PAYPAL;
            }

            @Override
            public void execute() {
                System.out.printf("  [PayPal] Redirigiendo a PayPal para cobrar $%,.0f a %s%n",
                        getAmount(), getCustomerEmail());
                setStatus(PaymentStatus.COMPLETED);
            }
        };
    }

    @Override
    public ValidatePayment createValidator() {
        return payment -> {
            boolean correoValido = payment.getCustomerEmail().contains("@")
                    && payment.getCustomerEmail().contains(".");
            boolean montoValido = payment.getAmount() > 0
                    && payment.getAmount() <= MAX_AMOUNT;
            boolean ok = correoValido && montoValido;
            System.out.println("  [PayPal] Validacion (correo y monto<= $"
                    + String.format("%,.0f", MAX_AMOUNT) + "): "
                    + (ok ? "APROBADA" : "RECHAZADA"));
            return ok;
        };
    }
}
