package eci.edu.byteProgramming.ejercicio.paper.util;

/**
 * Estado del flujo de un pago dentro del sistema.
 */
public enum PaymentStatus {
    PENDING,     // creado, sin validar
    VALIDATED,   // valido, pendiente de ejecucion
    REJECTED,    // no paso la validacion
    COMPLETED,   // ejecutado correctamente
    FAILED       // error durante la ejecucion
}
