package eci.edu.byteProgramming.ejercicio.paper;

import eci.edu.byteProgramming.ejercicio.paper.util.CreditCardFactory;
import eci.edu.byteProgramming.ejercicio.paper.util.CryptoFactory;
import eci.edu.byteProgramming.ejercicio.paper.util.ECIPayment;
import eci.edu.byteProgramming.ejercicio.paper.util.Facturation;
import eci.edu.byteProgramming.ejercicio.paper.util.Inventory;
import eci.edu.byteProgramming.ejercicio.paper.util.Notification;
import eci.edu.byteProgramming.ejercicio.paper.util.PaymentEventObserver;
import eci.edu.byteProgramming.ejercicio.paper.util.PaymentMethod;
import eci.edu.byteProgramming.ejercicio.paper.util.PaymentStatus;
import eci.edu.byteProgramming.ejercicio.paper.util.PaypalFactory;
import eci.edu.byteProgramming.ejercicio.paper.util.Product;
import eci.edu.byteProgramming.ejercicio.paper.util.ValidatePayment;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Aplicacion principal de la Tienda Virtual.
 *
 * Es el "composition root": conecta las factories (Abstract Factory)
 * con el bus de eventos (Observer) y simula tres compras con
 * distintos metodos de pago.
 *
 * El flujo de compra NO conoce ninguna clase concreta de pago,
 * validador u observador: depende solo de las abstracciones
 * (Dependency Inversion).
 */
public class Application {

    public static void main(String[] args) {

        System.out.println("==========================================");
        System.out.println("  TIENDA VIRTUAL - Sistema de Pagos");
        System.out.println("==========================================");

        // 1) Bus de eventos + suscripcion de los modulos observadores
        PaymentEventObserver eventBus = new PaymentEventObserver();
        eventBus.subscribe(new Inventory());
        eventBus.subscribe(new Facturation());
        eventBus.subscribe(new Notification());
        System.out.println("Modulos suscritos al bus de eventos: "
                + eventBus.getObserverCount());

        // 2) Catalogo de productos
        Product laptop = new Product("P001", "Laptop", 3_000_000, 5);
        Product mouse  = new Product("P002", "Mouse",     80_000, 20);
        Product audifo = new Product("P003", "Audifonos", 250_000, 8);

        // 3) Mapa de factories (extensible: agregar otra es UNA linea)
        Map<PaymentMethod, ECIPayment.Factory> factories =
                new EnumMap<>(PaymentMethod.class);
        factories.put(PaymentMethod.CREDIT_CARD, new CreditCardFactory());
        factories.put(PaymentMethod.PAYPAL,      new PaypalFactory());
        factories.put(PaymentMethod.CRYPTO,      new CryptoFactory());

        // 4) Tres compras de demostracion
        processPurchase(factories.get(PaymentMethod.CREDIT_CARD), eventBus,
                "ana@correo.com", Arrays.asList(laptop, mouse), 3_080_000);

        processPurchase(factories.get(PaymentMethod.PAYPAL), eventBus,
                "luis@correo.com", Arrays.asList(audifo, mouse), 330_000);

        processPurchase(factories.get(PaymentMethod.CRYPTO), eventBus,
                "marta@correo.com", Arrays.asList(laptop), 3_000_000);

        // 5) Caso de pago RECHAZADO (monto negativo)
        processPurchase(factories.get(PaymentMethod.CREDIT_CARD), eventBus,
                "fraude@correo.com", Arrays.asList(mouse), -100);

        System.out.println();
        System.out.println("Stock final del catalogo:");
        System.out.println("  - " + laptop);
        System.out.println("  - " + mouse);
        System.out.println("  - " + audifo);
    }

    /**
     * Procesa una compra usando una factory cualquiera. Dispara el
     * evento solo si el pago se completa, evitando notificar a los
     * observadores cuando la validacion falla.
     */
    private static void processPurchase(ECIPayment.Factory factory,
                                        PaymentEventObserver bus,
                                        String email,
                                        List<Product> products,
                                        double amount) {
        System.out.println();
        System.out.println("--- Compra via " + factory.getMethod().getDisplayName()
                + " | cliente: " + email + " | monto: $"
                + String.format("%,.0f", amount) + " ---");

        ECIPayment payment;
        try {
            payment = factory.createPayment(email, amount, products);
        } catch (IllegalArgumentException ex) {
            System.out.println("  [ERROR] No se pudo crear el pago: " + ex.getMessage());
            return;
        }

        ValidatePayment validator = factory.createValidator();
        if (!validator.validate(payment)) {
            payment.setStatus(PaymentStatus.REJECTED);
            System.out.println("  >> Pago RECHAZADO. No se notifica a los observadores.");
            return;
        }

        payment.setStatus(PaymentStatus.VALIDATED);
        payment.execute();

        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            bus.notifyPaymentProcessed(payment);
        } else {
            System.out.println("  >> Pago FALLIDO durante la ejecucion.");
        }
    }
}
