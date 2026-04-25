# Solucion - Problema #2: Tienda Virtual

## 1. Patrones de diseno aplicados

### 1.1 Abstract Factory  (familia: pago + validador)

> *"Se requiere un mecanismo para crear familias de objetos relacionados (pago + validador)"*

- `ECIPayment.Factory` (interfaz anidada): contrato comun.
- `CreditCardFactory`, `PaypalFactory`, `CryptoFactory`:
  factories concretas. Cada una crea **un par coherente** de objetos:
  un `ECIPayment` (subclase anonima con su propia ejecucion) y un
  `ValidatePayment` (lambda con sus propias reglas).
- La logica de compra (`Application#processPurchase`) recibe una
  `ECIPayment.Factory` como abstraccion y desconoce las clases
  concretas, lo que permite agregar un nuevo metodo de pago sin
  modificarla.

### 1.2 Observer  (notificaciones automaticas)

> *"Se requiere un mecanismo para notificar automaticamente a multiples observadores de eventos"*

- `PaymentObserver`: contrato del listener
  (`onPaymentProcessed(ECIPayment)`).
- `PaymentEventObserver`: subject que mantiene la lista de
  observadores y dispara el evento.
- Observers concretos:
  - `Inventory` -> descuenta stock
  - `Facturation` -> genera factura
  - `Notification` -> envia correo
- `Application` solo invoca `bus.notifyPaymentProcessed(payment)` y
  los modulos suscritos reaccionan automaticamente.

## 2. Principios SOLID

| Principio | Como se aplica |
|-----------|----------------|
| **S** Single Responsibility | Cada clase tiene una unica responsabilidad: `Inventory` solo gestiona stock, `Facturation` solo factura, `PaymentEventObserver` solo dispatch, los `*Factory` solo construyen sus familias. |
| **O** Open/Closed | Agregar un nuevo metodo de pago = crear una nueva `Factory`. Agregar un nuevo modulo reactivo = implementar `PaymentObserver` y suscribirlo. **Nunca se toca `Application`, `ECIPayment` ni el bus**. |
| **L** Liskov Substitution | Cualquier subclase de `ECIPayment` o implementacion de `ValidatePayment` / `PaymentObserver` es intercambiable sin alterar el comportamiento. |
| **I** Interface Segregation | `ValidatePayment` y `PaymentObserver` son interfaces minimas y cohesivas (un solo metodo cada una, marcadas `@FunctionalInterface`). |
| **D** Dependency Inversion | `Application` y `PaymentEventObserver` dependen de abstracciones (`ECIPayment.Factory`, `PaymentObserver`), no de implementaciones concretas. |

## 3. Encapsulamiento y polimorfismo

- **Encapsulamiento**: `ECIPayment` mantiene sus campos privados con
  `setStatus` controlado. `Product` expone su stock solo via
  `decreaseStock` (que ademas valida no permitir negativos).
- **Polimorfismo**: `ECIPayment#execute()` y `ECIPayment#getMethod()`
  son metodos abstractos que cada factory resuelve con una subclase
  anonima distinta. La logica principal nunca hace `instanceof`.

## 4. Mapeo requisitos -> solucion

| Requisito | Donde se cumple |
|-----------|-----------------|
| Crear objetos de pago + validador | `ECIPayment.Factory` (Abstract Factory) |
| No exponer detalles internos a la logica de compra | `Application` solo conoce `Factory`, `ECIPayment` y `PaymentObserver` |
| Notificar a inventario, facturacion, notificaciones tras un pago exitoso | `PaymentEventObserver.notifyPaymentProcessed` -> `Inventory` / `Facturation` / `Notification` |
| Soportar nuevos metodos de pago sin modificar la logica existente | Basta una nueva clase que implemente `ECIPayment.Factory` |
| Permitir nuevos modulos reactivos sin tocar el core | Implementar `PaymentObserver` y llamar `bus.subscribe(...)` |

## 5. Diagrama de clases (texto)

```
                         ECIPayment.Factory <<interface>>
                                  |
        +-------------------------+--------------------------+
        |                         |                          |
CreditCardFactory          PaypalFactory               CryptoFactory
        |                         |                          |
        | createPayment()         |                          |
        v                         v                          v
   ECIPayment (abstract)  -----  ValidatePayment <<interface>>
        ^
        |
   anonimas internas (CreditCard, Paypal, Crypto Payment)


   PaymentEventObserver  ---o  PaymentObserver <<interface>>
                               ^         ^         ^
                               |         |         |
                          Inventory  Facturation  Notification
```


**IA utilizada:** Claude
**Link:**
https://claude.ai/local_sessions/local_ad8bbb1b-f916-487b-a84c-c57e1fa1203c