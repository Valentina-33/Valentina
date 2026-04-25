package com.videoclub.service;

import com.videoclub.model.DigitalMovie;
import com.videoclub.model.Movie;
import com.videoclub.model.PhysicalMovie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Catalogo de peliculas disponibles en el videoclub.
 *
 * SOLID:
 *  - SRP: se ocupa unicamente de gestionar la coleccion de peliculas.
 *  - DIP: expone Movie (abstraccion) en lugar de tipos concretos.
 */
public class MovieCatalog {

    private final List<Movie> movies = new ArrayList<>();

    public MovieCatalog() {
        loadDefaultCatalog();
    }

    private void loadDefaultCatalog() {
        movies.add(new PhysicalMovie("Interestellar", 8000, true));
        movies.add(new PhysicalMovie("El Padrino", 7000, false));
        movies.add(new DigitalMovie("Inception", 5000, true));
        movies.add(new DigitalMovie("Matrix", 6000, true));
    }

    public List<Movie> getAll() {
        return Collections.unmodifiableList(movies);
    }

    public Movie getByIndex(int oneBasedIndex) {
        int idx = oneBasedIndex - 1;
        if (idx < 0 || idx >= movies.size()) {
            throw new IndexOutOfBoundsException("Pelicula #" + oneBasedIndex + " no existe");
        }
        return movies.get(idx);
    }

    public int size() {
        return movies.size();
    }
}
