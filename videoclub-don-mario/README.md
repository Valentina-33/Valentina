# Videoclub de Don Mario

Sistema de alquiler de peliculas (fisicas y digitales) con calculo de
descuentos por tipo de membresia. Implementado en **Java 11** con
estructura **Maven**.

## Estructura del proyecto

```
videoclub-don-mario/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/videoclub/
                ├── App.java                       (entrada / consola)
                ├── model/
                │   ├── Movie.java                 (abstracta)
                │   ├── PhysicalMovie.java
                │   └── DigitalMovie.java
                ├── membership/
                │   ├── Membership.java            (Strategy)
                │   ├── BasicMembership.java
                │   ├── PremiumMembership.java
                │   └── MembershipFactory.java     (Factory Method)
                ├── service/
                │   ├── MovieCatalog.java
                │   ├── RentalService.java
                │   └── Receipt.java
                └── view/
                    └── ReceiptPrinter.java
```

## Patrones de diseno aplicados

1. **Strategy** (`Membership` + `BasicMembership` / `PremiumMembership`).
   Cada membresia define su propia regla de descuento. El
   `RentalService` no sabe cual usa: solo invoca
   `calculateDiscount` / `calculateTotal`. Agregar una nueva
   membresia (VIP, Estudiante...) es crear otra clase, sin tocar el
   resto.

2. **Factory Method** (`MembershipFactory`). Encapsula la creacion de
   membresias para que la capa de UI no dependa de implementaciones
   concretas.

3. **Template / Polimorfismo** (`Movie` abstracta con `getType()`).
   `PhysicalMovie` y `DigitalMovie` heredan el comportamiento comun
   y especializan el tipo, cumpliendo Liskov.

## Principios SOLID

- **S — Single Responsibility:** cada clase tiene un unico motivo de
  cambio (`MovieCatalog` administra peliculas, `RentalService`
  procesa alquileres, `ReceiptPrinter` imprime, etc.).
- **O — Open/Closed:** se pueden agregar nuevos tipos de pelicula o
  membresia extendiendo `Movie` o implementando `Membership` sin
  modificar codigo existente.
- **L — Liskov Substitution:** cualquier subclase de `Movie` o
  implementacion de `Membership` puede usarse donde se espera la
  abstraccion sin alterar el comportamiento.
- **I — Interface Segregation:** `Membership` es una interfaz pequenia
  y enfocada (nombre + tasa de descuento).
- **D — Dependency Inversion:** `RentalService` depende de
  abstracciones (`Movie`, `Membership`), no de clases concretas.

## Encapsulamiento

Todas las clases del modelo exponen sus datos solo a traves de
getters/setters controlados; los campos son `private`/`final`.
`Receipt` es inmutable y devuelve la lista de peliculas como
`unmodifiableList`.

## Como ejecutar

Con Maven instalado:

```bash
cd videoclub-don-mario
mvn -q clean package
java -jar target/videoclub-don-mario.jar
```

O usando el plugin `exec`:

```bash
mvn -q compile exec:java
```

## Ejemplo de ejecucion (caso del enunciado)

Entrada:
```
Membresia del cliente (1=Basica / 2=Premium): 2
Seleccione peliculas (numeros separados por coma): 1,3
```

Salida:
```
--- RECIBO DE ALQUILER ---
Cliente: Premium
Peliculas:
 - Interestellar (Fisica) - $8.000
 - Inception (Digital) - $5.000
Subtotal: $13.000
Descuento (20%): $2.600
Total a pagar: $10.400
--------------------------
¡Disfrute su pelicula!
```
