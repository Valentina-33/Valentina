package com.videoclub.service;

import com.videoclub.membership.Membership;
import com.videoclub.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio que orquesta el alquiler de peliculas.
 *
 * SOLID:
 *  - SRP: solo se encarga de procesar el alquiler.
 *  - DIP: depende de las abstracciones Movie y Membership, no de
 *    implementaciones concretas.
 *  - OCP: agregar nuevas membresias o tipos de pelicula NO requiere
 *    cambiar este servicio.
 */
public class RentalService {

    public Receipt rent(List<Movie> selectedMovies, Membership membership) {
        if (selectedMovies == null || selectedMovies.isEmpty()) {
            throw new IllegalArgumentException("Debe seleccionar al menos una pelicula");
        }
        if (membership == null) {
            throw new IllegalArgumentException("La membresia es obligatoria");
        }

        List<Movie> rented = new ArrayList<>();
        for (Movie movie : selectedMovies) {
            if (!movie.isAvailable()) {
                throw new IllegalStateException(
                        "La pelicula '" + movie.getTitle() + "' no esta disponible");
            }
            rented.add(movie);
        }

        double subtotal = rented.stream().mapToDouble(Movie::getPrice).sum();
        double discount = membership.calculateDiscount(subtotal);
        double total = membership.calculateTotal(subtotal);

        return new Receipt(membership, rented, subtotal, discount, total);
    }
}
