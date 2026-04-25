package com.videoclub.model;

/**
 * Pelicula digital (streaming/descarga).
 */
public class DigitalMovie extends Movie {

    public DigitalMovie(String title, double price, boolean available) {
        super(title, price, available);
    }

    @Override
    public String getType() {
        return "Digital";
    }
}
