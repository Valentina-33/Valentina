package eci.edu.byteProgramming.ejercicio.paper.util;

import java.util.ArrayList;
import java.util.List;

/**
 * PATRON OBSERVER - rol "Subject" (publicador).
 *
 * Mantiene la lista de {@link PaymentObserver} y los notifica cuando
 * un pago se procesa exitosamente. La logica de compra solo dispara
 * el evento; los modulos suscritos reaccionan automaticamente.
 *
 * Permite agregar / quitar observadores en caliente, lo que cumple el
 * requisito de extensibilidad sin tocar el core.
 *
 * SOLID:
 *  - SRP: solo gestiona la suscripcion y la difusion de eventos.
 *  - OCP: nuevos observadores se agregan sin modificar esta clase.
 *  - DIP: depende de la abstraccion {@link PaymentObserver}.
 */
public class PaymentEventObserver {

    private final List<PaymentObserver> observers = new ArrayList<>();

    public void subscribe(PaymentObserver observer) {
        if (observer == null) {
            throw new IllegalArgumentException("El observer no puede ser nulo");
        }
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void unsubscribe(PaymentObserver observer) {
        observers.remove(observer);
    }

    public int getObserverCount() {
        return observers.size();
    }

    /**
     * Notifica a todos los observadores suscritos que el pago fue
     * procesado exitosamente. Si algun observer lanza excepcion, se
     * captura y los demas continuan recibiendo el evento.
     */
    public void notifyPaymentProcessed(ECIPayment payment) {
        for (PaymentObserver observer : observers) {
            try {
                observer.onPaymentProcessed(payment);
            } catch (RuntimeException ex) {
                System.err.println("[EventBus] Error en observer "
                        + observer.getClass().getSimpleName() + ": " + ex.getMessage());
            }
        }
    }
}
