package eci.edu.byteProgramming.ejercicio.paper.util;

/**
 * PATRON OBSERVER - rol "Observer" (suscriptor).
 *
 * Cualquier modulo que quiera reaccionar a un pago exitoso debe
 * implementar este contrato y registrarse en {@link PaymentEventObserver}.
 *
 * Implementaciones de la tienda:
 *   - {@link Inventory}     -> descuenta stock
 *   - {@link Facturation}   -> genera factura
 *   - {@link Notification}  -> envia correo al cliente
 *
 * Asi se cumple el requisito tecnico:
 *   "Permitir que nuevos modulos reaccionen a eventos de pago sin
 *    cambiar el core" (OCP).
 */
@FunctionalInterface
public interface PaymentObserver {

    void onPaymentProcessed(ECIPayment payment);
}
