# Tienda Virtual - Sistema de Pagos

Solucion del **Problema #2 - Tienda Virtual**.

Sistema de pagos que soporta tarjeta de credito, PayPal y criptomonedas.
Cada metodo tiene su propio par de **pago + validador** y, ante un pago
exitoso, notifica automaticamente a los modulos de **inventario,
facturacion y notificaciones**.

## Estructura

```
Valentina/
├── pom.xml
├── README.md
├── SOLUCION.md
└── src/
    ├── main/
    │   ├── java/eci/edu/byteProgramming/ejercicio/paper/
    │   │   ├── Application.java
    │   │   └── util/
    │   │       ├── ECIPayment.java          (abstracta + Factory)
    │   │       ├── ValidatePayment.java     (interfaz validador)
    │   │       ├── CreditCardFactory.java   (Abstract Factory)
    │   │       ├── PaypalFactory.java       (Abstract Factory)
    │   │       ├── CryptoFactory.java       (Abstract Factory)
    │   │       ├── PaymentObserver.java     (Observer - listener)
    │   │       ├── PaymentEventObserver.java(Observer - subject)
    │   │       ├── Inventory.java           (observer concreto)
    │   │       ├── Facturation.java         (observer concreto)
    │   │       ├── Notification.java        (observer concreto)
    │   │       ├── PaymentMethod.java       (enum)
    │   │       ├── PaymentStatus.java       (enum)
    │   │       └── Product.java
    │   └── resources/application.properties
    └── test/java/eci/edu/byteProgramming/ejercicio/paper/
        ├── ApplicationTest.java
        └── util/auxiliaryTest.java
```

## Patrones de diseno

1. **Abstract Factory** - `ECIPayment.Factory` con
   `CreditCardFactory`, `PaypalFactory`, `CryptoFactory`. Cada factory
   crea una *familia coherente* de objetos relacionados (pago +
   validador). El cliente nunca instancia clases concretas.
2. **Observer** - `PaymentEventObserver` (subject) +
   `PaymentObserver` (listener). `Inventory`, `Facturation` y
   `Notification` se suscriben y reaccionan automaticamente a un pago
   exitoso.

## Como compilar / ejecutar

Con Maven:

```bash
cd Valentina
mvn clean package
java -jar target/tienda-virtual.jar
```

O directamente con `exec`:

```bash
mvn -q compile exec:java
```

Para correr las pruebas:

```bash
mvn test
```

## Salida esperada (resumen)

La `Application` ejecuta cuatro compras de demostracion. Para cada
pago exitoso se ven los tres modulos reaccionando en cadena:

```
--- Compra via Tarjeta de credito | cliente: ana@correo.com | monto: $3,080,000 ---
  [Tarjeta] Validacion (...): APROBADA
  [Tarjeta] Cargando $3,080,000 a la tarjeta de ana@correo.com
[Inventario] Descontando stock por compra de 2 producto(s):
    - Laptop -> stock restante: 4
    - Mouse  -> stock restante: 19
[Facturacion] Factura #FAC-1001 generada por $3,080,000 via Tarjeta de credito ...
[Notificacion] Enviando correo a ana@correo.com -> '...'
```

Ver `EVIDENCIA-EJECUCION.txt` para la salida completa.
