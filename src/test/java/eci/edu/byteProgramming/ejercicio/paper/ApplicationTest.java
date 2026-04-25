package eci.edu.byteProgramming.ejercicio.paper;

import eci.edu.byteProgramming.ejercicio.paper.util.CreditCardFactory;
import eci.edu.byteProgramming.ejercicio.paper.util.CryptoFactory;
import eci.edu.byteProgramming.ejercicio.paper.util.ECIPayment;
import eci.edu.byteProgramming.ejercicio.paper.util.Facturation;
import eci.edu.byteProgramming.ejercicio.paper.util.Inventory;
import eci.edu.byteProgramming.ejercicio.paper.util.Notification;
import eci.edu.byteProgramming.ejercicio.paper.util.PaymentEventObserver;
import eci.edu.byteProgramming.ejercicio.paper.util.PaymentMethod;
import eci.edu.byteProgramming.ejercicio.paper.util.PaymentObserver;
import eci.edu.byteProgramming.ejercicio.paper.util.PaymentStatus;
import eci.edu.byteProgramming.ejercicio.paper.util.PaypalFactory;
import eci.edu.byteProgramming.ejercicio.paper.util.Product;
import eci.edu.byteProgramming.ejercicio.paper.util.ValidatePayment;

import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Pruebas de integracion del flujo completo: factories + observers.
 */
class ApplicationTest {

    @Test
    void creditCardSuccessNotifiesAllObservers() {
        ECIPayment.Factory factory = new CreditCardFactory();
        PaymentEventObserver bus = new PaymentEventObserver();

        AtomicInteger calls = new AtomicInteger();
        bus.subscribe(p -> calls.incrementAndGet());
        bus.subscribe(new Inventory());
        bus.subscribe(new Facturation());
        bus.subscribe(new Notification());

        Product p = new Product("P", "Item", 1000, 10);
        ECIPayment payment = factory.createPayment(
                "user@mail.com", 1000, Collections.singletonList(p));
        ValidatePayment validator = factory.createValidator();

        assertTrue(validator.validate(payment), "El pago debe ser valido");
        payment.execute();
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());

        bus.notifyPaymentProcessed(payment);
        assertAll(
                () -> assertEquals(1, calls.get(), "El observer espia debe recibir 1 evento"),
                () -> assertEquals(9, p.getStock(), "Inventario debe descontar 1 unidad")
        );
    }

    @Test
    void paypalRejectsAmountAboveLimit() {
        ECIPayment.Factory factory = new PaypalFactory();
        Product p = new Product("P", "Item", 1, 1);
        ECIPayment payment = factory.createPayment(
                "user@mail.com", 9_000_000, Collections.singletonList(p));
        ValidatePayment validator = factory.createValidator();
        assertFalse(validator.validate(payment),
                "PayPal debe rechazar montos por encima del limite");
    }

    @Test
    void cryptoRejectsAmountBelowMinimum() {
        ECIPayment.Factory factory = new CryptoFactory();
        Product p = new Product("P", "Item", 1, 1);
        ECIPayment payment = factory.createPayment(
                "user@mail.com", 500, Collections.singletonList(p));
        ValidatePayment validator = factory.createValidator();
        assertFalse(validator.validate(payment),
                "Crypto debe rechazar montos por debajo del minimo");
    }

    @Test
    void rejectedPaymentDoesNotNotifyObservers() {
        ECIPayment.Factory factory = new CreditCardFactory();
        PaymentEventObserver bus = new PaymentEventObserver();
        AtomicInteger calls = new AtomicInteger();
        PaymentObserver spy = p -> calls.incrementAndGet();
        bus.subscribe(spy);

        Product p = new Product("P", "Item", 1, 1);
        ECIPayment payment = factory.createPayment(
                "user@mail.com", -50, Collections.singletonList(p));
        ValidatePayment validator = factory.createValidator();

        if (!validator.validate(payment)) {
            payment.setStatus(PaymentStatus.REJECTED);
        } else {
            bus.notifyPaymentProcessed(payment);
        }
        assertEquals(0, calls.get(),
                "Un pago rechazado no debe disparar eventos");
        assertEquals(PaymentStatus.REJECTED, payment.getStatus());
    }

    @Test
    void factoryDeliversCoherentMethod() {
        List<ECIPayment.Factory> all = List.of(
                new CreditCardFactory(), new PaypalFactory(), new CryptoFactory());
        for (ECIPayment.Factory f : all) {
            ECIPayment payment = f.createPayment(
                    "x@y.com", 50_000, Collections.singletonList(
                            new Product("P", "Item", 50_000, 1)));
            assertEquals(f.getMethod(), payment.getMethod(),
                    "El metodo del pago debe coincidir con el de la factory ("
                            + f.getClass().getSimpleName() + ")");
        }
    }
}
