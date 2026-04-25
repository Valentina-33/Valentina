package com.videoclub;

import com.videoclub.membership.Membership;
import com.videoclub.membership.MembershipFactory;
import com.videoclub.model.Movie;
import com.videoclub.service.MovieCatalog;
import com.videoclub.service.Receipt;
import com.videoclub.service.RentalService;
import com.videoclub.view.ReceiptPrinter;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;

/**
 * Punto de entrada del Videoclub de Don Mario.
 *
 * Aqui se conectan los componentes (composition root): catalogo,
 * servicio de alquiler, membresia e impresora de recibos.
 */
public class App {

    private static final DecimalFormat MONEY =
            new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.GERMANY));

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        MovieCatalog catalog = new MovieCatalog();
        RentalService rentalService = new RentalService();
        ReceiptPrinter printer = new ReceiptPrinter();

        System.out.println("====================================");
        System.out.println("   VIDEOCLUB DE DON MARIO");
        System.out.println("====================================");
        System.out.println();

        showCatalog(catalog);

        Membership membership = readMembership(sc);
        List<Movie> selected = readSelection(sc, catalog);

        try {
            Receipt receipt = rentalService.rent(selected, membership);
            printer.print(receipt);
        } catch (RuntimeException ex) {
            System.out.println();
            System.out.println("[ERROR] " + ex.getMessage());
        }
    }

    private static void showCatalog(MovieCatalog catalog) {
        System.out.println("Peliculas disponibles:");
        List<Movie> all = catalog.getAll();
        for (int i = 0; i < all.size(); i++) {
            Movie m = all.get(i);
            System.out.printf("  %d. [%s] %s - $%s - %s%n",
                    i + 1,
                    m.getType(),
                    m.getTitle(),
                    MONEY.format(m.getPrice()),
                    m.isAvailable() ? "Disponible" : "No disponible");
        }
        System.out.println();
    }

    private static Membership readMembership(Scanner sc) {
        while (true) {
            System.out.print("Membresia del cliente (1=Basica / 2=Premium): ");
            String input = sc.nextLine().trim();
            try {
                return MembershipFactory.create(input);
            } catch (IllegalArgumentException ex) {
                System.out.println("  -> Opcion invalida. Intente de nuevo.");
            }
        }
    }

    private static List<Movie> readSelection(Scanner sc, MovieCatalog catalog) {
        while (true) {
            System.out.print("Seleccione peliculas (numeros separados por coma): ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) {
                System.out.println("  -> Debe ingresar al menos una pelicula.");
                continue;
            }

            List<Movie> selected = new ArrayList<>();
            boolean ok = true;
            for (String token : line.split(",")) {
                String t = token.trim();
                try {
                    int idx = Integer.parseInt(t);
                    Movie movie = catalog.getByIndex(idx);
                    if (!movie.isAvailable()) {
                        System.out.println("  -> '" + movie.getTitle()
                                + "' no esta disponible.");
                        ok = false;
                        break;
                    }
                    selected.add(movie);
                } catch (NumberFormatException nfe) {
                    System.out.println("  -> '" + t + "' no es un numero valido.");
                    ok = false;
                    break;
                } catch (IndexOutOfBoundsException oob) {
                    System.out.println("  -> " + oob.getMessage());
                    ok = false;
                    break;
                }
            }
            if (ok && !selected.isEmpty()) {
                return selected;
            }
        }
    }
}
