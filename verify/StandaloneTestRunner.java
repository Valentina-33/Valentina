// Verificacion en Java puro (sin JUnit) - solo se usa aqui para validar
// la solucion en un entorno sin acceso a Maven Central.  En el proyecto
// real se ejecuta con `mvn test` y los archivos JUnit en src/test/java.

import eci.edu.byteProgramming.ejercicio.paper.util.*;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StandaloneTestRunner {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {
        run("creditCardSuccessNotifiesAllObservers", StandaloneTestRunner::creditCardSuccess);
        run("paypalRejectsAmountAboveLimit", StandaloneTestRunner::paypalRejects);
        run("cryptoRejectsAmountBelowMinimum", StandaloneTestRunner::cryptoRejects);
        run("rejectedPaymentDoesNotNotifyObservers", StandaloneTestRunner::rejectedNoNotify);
        run("factoryDeliversCoherentMethod", StandaloneTestRunner::factoryCoherent);
        run("productDecreaseStockNeverGoesNegative", StandaloneTestRunner::stockNotNegative);
        run("productRejectsNegativeDecrease", StandaloneTestRunner::stockNegativeArg);
        run("eventObserverDispatchesToAllSubscribers", StandaloneTestRunner::dispatchAll);
        run("eventObserverUnsubscribeStopsDelivery", StandaloneTestRunner::unsubscribe);
        run("eventObserverSurvivesObserverException", StandaloneTestRunner::survivesException);
        run("eciPaymentConstructorRejectsBadInputs", StandaloneTestRunner::badInputs);
        run("factoryAndValidatorBelongToSameFamily", StandaloneTestRunner::sameFamily);
        run("allPaymentMethodsAreSupported", StandaloneTestRunner::allMethods);

        System.out.println();
        System.out.println("============================================");
        System.out.println(" RESULTADO: " + passed + " passed, " + failed + " failed");
        System.out.println("============================================");
        System.exit(failed == 0 ? 0 : 1);
    }

    interface TestCase { void run() throws Exception; }

    private static void run(String name, TestCase t) {
        try {
            t.run();
            System.out.println("[PASS] " + name);
            passed++;
        } catch (Throwable th) {
            System.out.println("[FAIL] " + name + " -> " + th.getMessage());
            failed++;
        }
    }

    private static void assertTrue(boolean cond, String msg) {
        if (!cond) throw new AssertionError(msg);
    }
    private static void assertEquals(Object expected, Object actual, String msg) {
        if (expected == null ? actual != null : !expected.equals(actual)) {
            throw new AssertionError(msg + " (expected=" + expected + ", actual=" + actual + ")");
        }
    }
    private static void assertThrows(Class<? extends Throwable> exType, Runnable r, String msg) {
        try { r.run(); }
        catch (Throwable th) {
            if (exType.isInstance(th)) return;
            throw new AssertionError(msg + " threw " + th.getClass().getSimpleName());
        }
        throw new AssertionError(msg + " did not throw");
    }

    // ---------- Tests ----------

    static void creditCardSuccess() {
        ECIPayment.Factory factory = new CreditCardFactory();
        PaymentEventObserver bus = new PaymentEventObserver();
        AtomicInteger calls = new AtomicInteger();
        bus.subscribe(p -> calls.incrementAndGet());
        bus.subscribe(new Inventory());
        bus.subscribe(new Facturation());
        bus.subscribe(new Notification());

        Product p = new Product("P", "Item", 1000, 10);
        ECIPayment payment = factory.createPayment("user@mail.com", 1000,
                Collections.singletonList(p));
        ValidatePayment v = factory.createValidator();
        assertTrue(v.validate(payment), "validacion debe pasar");
        payment.execute();
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus(), "status COMPLETED");
        bus.notifyPaymentProcessed(payment);
        assertEquals(1, calls.get(), "espia recibe 1 evento");
        assertEquals(9, p.getStock(), "inventario descuenta 1");
    }

    static void paypalRejects() {
        ECIPayment.Factory factory = new PaypalFactory();
        ECIPayment payment = factory.createPayment("u@m.com", 9_000_000,
                Collections.singletonList(new Product("P", "X", 1, 1)));
        assertTrue(!factory.createValidator().validate(payment),
                "paypal rechaza monto excedido");
    }

    static void cryptoRejects() {
        ECIPayment.Factory factory = new CryptoFactory();
        ECIPayment payment = factory.createPayment("u@m.com", 500,
                Collections.singletonList(new Product("P", "X", 1, 1)));
        assertTrue(!factory.createValidator().validate(payment),
                "crypto rechaza monto bajo");
    }

    static void rejectedNoNotify() {
        ECIPayment.Factory factory = new CreditCardFactory();
        PaymentEventObserver bus = new PaymentEventObserver();
        AtomicInteger calls = new AtomicInteger();
        bus.subscribe(p -> calls.incrementAndGet());
        ECIPayment payment = factory.createPayment("u@m.com", -50,
                Collections.singletonList(new Product("P", "X", 1, 1)));
        if (!factory.createValidator().validate(payment)) {
            payment.setStatus(PaymentStatus.REJECTED);
        } else {
            bus.notifyPaymentProcessed(payment);
        }
        assertEquals(0, calls.get(), "rechazado no notifica");
        assertEquals(PaymentStatus.REJECTED, payment.getStatus(), "status REJECTED");
    }

    static void factoryCoherent() {
        List<ECIPayment.Factory> all = List.of(
                new CreditCardFactory(), new PaypalFactory(), new CryptoFactory());
        for (ECIPayment.Factory f : all) {
            ECIPayment payment = f.createPayment("x@y.com", 50_000,
                    Collections.singletonList(new Product("P", "X", 50_000, 1)));
            assertEquals(f.getMethod(), payment.getMethod(),
                    "method coincide " + f.getClass().getSimpleName());
        }
    }

    static void stockNotNegative() {
        Product p = new Product("ID", "X", 1, 2);
        p.decreaseStock(5);
        assertEquals(0, p.getStock(), "stock no negativo");
    }

    static void stockNegativeArg() {
        Product p = new Product("ID", "X", 1, 5);
        assertThrows(IllegalArgumentException.class,
                () -> p.decreaseStock(-1), "rechaza decremento negativo");
    }

    static void dispatchAll() {
        PaymentEventObserver bus = new PaymentEventObserver();
        AtomicInteger a = new AtomicInteger(); AtomicInteger b = new AtomicInteger();
        bus.subscribe(p -> a.incrementAndGet());
        bus.subscribe(p -> b.incrementAndGet());
        assertEquals(2, bus.getObserverCount(), "2 observers");
        ECIPayment pay = new CreditCardFactory().createPayment("x@y.com", 100,
                Collections.singletonList(new Product("P", "X", 100, 1)));
        bus.notifyPaymentProcessed(pay);
        assertEquals(1, a.get(), "a llamado");
        assertEquals(1, b.get(), "b llamado");
    }

    static void unsubscribe() {
        PaymentEventObserver bus = new PaymentEventObserver();
        AtomicInteger a = new AtomicInteger();
        PaymentObserver l = p -> a.incrementAndGet();
        bus.subscribe(l); bus.unsubscribe(l);
        ECIPayment pay = new PaypalFactory().createPayment("x@y.com", 100,
                Collections.singletonList(new Product("P", "X", 100, 1)));
        bus.notifyPaymentProcessed(pay);
        assertEquals(0, a.get(), "no entrega tras unsubscribe");
    }

    static void survivesException() {
        PaymentEventObserver bus = new PaymentEventObserver();
        AtomicInteger ok = new AtomicInteger();
        bus.subscribe(p -> { throw new RuntimeException("boom"); });
        bus.subscribe(p -> ok.incrementAndGet());
        ECIPayment pay = new CryptoFactory().createPayment("x@y.com", 50_000,
                Collections.singletonList(new Product("P", "X", 50_000, 1)));
        bus.notifyPaymentProcessed(pay);
        assertEquals(1, ok.get(), "no propaga excepcion");
    }

    static void badInputs() {
        Product p = new Product("P", "X", 1, 1);
        assertThrows(IllegalArgumentException.class,
                () -> new CreditCardFactory().createPayment("", 100, Collections.singletonList(p)),
                "correo vacio");
        assertThrows(IllegalArgumentException.class,
                () -> new CreditCardFactory().createPayment("x@y.com", 100, Collections.emptyList()),
                "productos vacios");
    }

    static void sameFamily() {
        ECIPayment.Factory factory = new PaypalFactory();
        ECIPayment payment = factory.createPayment("x@y.com", 100,
                Collections.singletonList(new Product("P", "X", 100, 1)));
        assertTrue(factory.createValidator().validate(payment),
                "validador de la misma familia aprueba");
        assertEquals(PaymentMethod.PAYPAL, payment.getMethod(), "method PAYPAL");
    }

    static void allMethods() {
        assertEquals(3, PaymentMethod.values().length, "3 metodos");
        for (PaymentMethod m : new PaymentMethod[]{
                PaymentMethod.CREDIT_CARD, PaymentMethod.PAYPAL, PaymentMethod.CRYPTO}) {
            boolean found = false;
            for (PaymentMethod x : PaymentMethod.values()) if (x == m) found = true;
            assertTrue(found, m + " presente");
        }
    }
}
