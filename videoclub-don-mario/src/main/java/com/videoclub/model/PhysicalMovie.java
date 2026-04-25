package com.videoclub.model;

/**
 * Pelicula fisica (DVD/Blu-ray).
 */
public class PhysicalMovie extends Movie {

    public PhysicalMovie(String title, double price, boolean available) {
        super(title, price, available);
    }

    @Override
    public String getType() {
        return "Fisica";
    }
}
