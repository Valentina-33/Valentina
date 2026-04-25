package eci.edu.byteProgramming.ejercicio.paper.util;

import java.util.List;

/**
 * Factory concreta para pagos con CRIPTOMONEDAS.
 */
public class CryptoFactory implements ECIPayment.Factory {

    private static final double MIN_AMOUNT = 10_000.0;

    @Override
    public PaymentMethod getMethod() {
        return PaymentMethod.CRYPTO;
    }

    @Override
    public ECIPayment createPayment(String customerEmail, double amount, List<Product> products) {
        return new ECIPayment(customerEmail, amount, products) {
            @Override
            public PaymentMethod getMethod() {
                return PaymentMethod.CRYPTO;
            }

            @Override
            public void execute() {
                System.out.printf("  [Crypto] Confirmando transaccion on-chain por $%,.0f para %s%n",
                        getAmount(), getCustomerEmail());
                setStatus(PaymentStatus.COMPLETED);
            }
        };
    }

    @Override
    public ValidatePayment createValidator() {
        return payment -> {
            // Para criptomonedas se exige un minimo (cubrir fees) y correo valido
            boolean ok = payment.getAmount() >= MIN_AMOUNT
                    && payment.getCustomerEmail().contains("@");
            System.out.println("  [Crypto] Validacion (monto>= $"
                    + String.format("%,.0f", MIN_AMOUNT) + "): "
                    + (ok ? "APROBADA" : "RECHAZADA"));
            return ok;
        };
    }
}
