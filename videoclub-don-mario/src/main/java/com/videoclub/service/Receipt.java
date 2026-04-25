package com.videoclub.service;

import com.videoclub.membership.Membership;
import com.videoclub.model.Movie;

import java.util.Collections;
import java.util.List;

/**
 * Objeto inmutable que representa el recibo de un alquiler.
 * Aplica ENCAPSULAMIENTO: todos los campos son privados y finales.
 *
 * SOLID:
 *  - SRP: solo transporta los datos del recibo (DTO).
 */
public class Receipt {

    private final Membership membership;
    private final List<Movie> movies;
    private final double subtotal;
    private final double discount;
    private final double total;

    public Receipt(Membership membership,
                   List<Movie> movies,
                   double subtotal,
                   double discount,
                   double total) {
        this.membership = membership;
        this.movies = Collections.unmodifiableList(movies);
        this.subtotal = subtotal;
        this.discount = discount;
        this.total = total;
    }

    public Membership getMembership() {
        return membership;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTotal() {
        return total;
    }
}
