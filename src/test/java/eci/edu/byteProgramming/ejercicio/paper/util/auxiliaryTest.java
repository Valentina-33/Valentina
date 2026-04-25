package eci.edu.byteProgramming.ejercicio.paper.util;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas auxiliares de las clases del paquete util:
 *  - Product (encapsulamiento de stock)
 *  - PaymentEventObserver (suscripcion / desuscripcion)
 *  - ECIPayment (validaciones de constructor)
 */
class auxiliaryTest {

    @Test
    void productDecreaseStockNeverGoesNegative() {
        Product p = new Product("ID", "Item", 1000, 2);
        p.decreaseStock(5);
        assertEquals(0, p.getStock(),
                "El stock no debe ser negativo aunque se descuente de mas");
    }

    @Test
    void productRejectsNegativeDecrease() {
        Product p = new Product("ID", "Item", 1000, 5);
        assertThrows(IllegalArgumentException.class, () -> p.decreaseStock(-1));
    }

    @Test
    void eventObserverDispatchesToAllSubscribers() {
        PaymentEventObserver bus = new PaymentEventObserver();
        AtomicInteger a = new AtomicInteger();
        AtomicInteger b = new AtomicInteger();
        bus.subscribe(p -> a.incrementAndGet());
        bus.subscribe(p -> b.incrementAndGet());
        assertEquals(2, bus.getObserverCount());

        ECIPayment payment = new CreditCardFactory().createPayment(
                "x@y.com", 100, Collections.singletonList(
                        new Product("P", "X", 100, 1)));
        bus.notifyPaymentProcessed(payment);

        assertEquals(1, a.get());
        assertEquals(1, b.get());
    }

    @Test
    void eventObserverUnsubscribeStopsDelivery() {
        PaymentEventObserver bus = new PaymentEventObserver();
        AtomicInteger a = new AtomicInteger();
        PaymentObserver listener = p -> a.incrementAndGet();
        bus.subscribe(listener);
        bus.unsubscribe(listener);

        ECIPayment payment = new PaypalFactory().createPayment(
                "x@y.com", 100, Collections.singletonList(
                        new Product("P", "X", 100, 1)));
        bus.notifyPaymentProcessed(payment);
        assertEquals(0, a.get());
    }

    @Test
    void eventObserverSurvivesObserverException() {
        PaymentEventObserver bus = new PaymentEventObserver();
        AtomicInteger ok = new AtomicInteger();
        bus.subscribe(p -> { throw new RuntimeException("boom"); });
        bus.subscribe(p -> ok.incrementAndGet());

        ECIPayment payment = new CryptoFactory().createPayment(
                "x@y.com", 50_000, Collections.singletonList(
                        new Product("P", "X", 50_000, 1)));
        bus.notifyPaymentProcessed(payment); // no debe propagar la excepcion
        assertEquals(1, ok.get(),
                "Los demas observers deben seguir recibiendo aunque uno falle");
    }

    @Test
    void eciPaymentConstructorRejectsBadInputs() {
        Product p = new Product("P", "X", 1, 1);
        assertThrows(IllegalArgumentException.class,
                () -> new CreditCardFactory().createPayment(
                        "", 100, Collections.singletonList(p)));
        assertThrows(IllegalArgumentException.class,
                () -> new CreditCardFactory().createPayment(
                        "x@y.com", 100, Collections.emptyList()));
    }

    @Test
    void factoryAndValidatorBelongToSameFamily() {
        // Abstract Factory: la factory entrega un par coherente.
        ECIPayment.Factory factory = new PaypalFactory();
        ECIPayment payment = factory.createPayment(
                "x@y.com", 100, Collections.singletonList(
                        new Product("P", "X", 100, 1)));
        ValidatePayment validator = factory.createValidator();
        assertTrue(validator.validate(payment));
        assertEquals(PaymentMethod.PAYPAL, payment.getMethod());
    }

    @Test
    void allPaymentMethodsAreSupported() {
        // Si manana se agrega un nuevo metodo, este test guia el cambio.
        assertEquals(3, PaymentMethod.values().length);
        assertTrue(Arrays.asList(PaymentMethod.values())
                .containsAll(Arrays.asList(
                        PaymentMethod.CREDIT_CARD,
                        PaymentMethod.PAYPAL,
                        PaymentMethod.CRYPTO)));
    }
}
