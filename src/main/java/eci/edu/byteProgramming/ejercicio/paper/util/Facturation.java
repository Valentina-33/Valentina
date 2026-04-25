package eci.edu.byteProgramming.ejercicio.paper.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Modulo de FACTURACION. Genera una factura cuando un pago es
 * procesado correctamente.
 */
public class Facturation implements PaymentObserver {

    private static final AtomicLong COUNTER = new AtomicLong(1000);

    @Override
    public void onPaymentProcessed(ECIPayment payment) {
        long invoice = COUNTER.incrementAndGet();
        System.out.printf(
                "[Facturacion] Factura #FAC-%d generada por $%,.0f via %s para %s%n",
                invoice,
                payment.getAmount(),
                payment.getMethod().getDisplayName(),
                payment.getCustomerEmail());
    }
}
