package com.videoclub.membership;

/**
 * PATRON FACTORY METHOD: encapsula la creacion de membresias para que el
 * cliente (App) no tenga que conocer las clases concretas.
 *
 * SOLID:
 *  - SRP: unica responsabilidad es construir membresias a partir de un
 *    identificador.
 *  - OCP: si se agrega una nueva membresia, solo se modifica este factory
 *    (o se reemplaza por uno basado en registro), no el resto del sistema.
 */
public class MembershipFactory {

    private MembershipFactory() {
        // factory estatico
    }

    public static Membership create(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Tipo de membresia no puede ser nulo");
        }
        switch (type.trim().toLowerCase()) {
            case "1":
            case "basica":
            case "basic":
                return new BasicMembership();
            case "2":
            case "premium":
                return new PremiumMembership();
            default:
                throw new IllegalArgumentException("Membresia desconocida: " + type);
        }
    }
}
