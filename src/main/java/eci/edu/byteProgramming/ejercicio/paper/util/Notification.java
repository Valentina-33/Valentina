package eci.edu.byteProgramming.ejercicio.paper.util;

/**
 * Modulo de NOTIFICACIONES. Envia un correo de confirmacion al
 * cliente despues de un pago exitoso.
 */
public class Notification implements PaymentObserver {

    @Override
    public void onPaymentProcessed(ECIPayment payment) {
        System.out.println("[Notificacion] Enviando correo a "
                + payment.getCustomerEmail()
                + " -> 'Su pago via " + payment.getMethod().getDisplayName()
                + " por $" + String.format("%,.0f", payment.getAmount())
                + " fue exitoso. Gracias por su compra!'");
    }
}
