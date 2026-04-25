package com.videoclub.view;

import com.videoclub.model.Movie;
import com.videoclub.service.Receipt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Impresora de recibos por consola.
 *
 * SOLID:
 *  - SRP: solo se encarga de la presentacion (UI).
 *  - DIP: el resto del sistema no depende de la forma de imprimir.
 *
 * Si manana se quiere imprimir en HTML, JSON o PDF, basta con crear
 * otra implementacion sin tocar el dominio (ISP/OCP).
 */
public class ReceiptPrinter {

    private static final DecimalFormat MONEY =
            new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.GERMANY));

    public void print(Receipt receipt) {
        System.out.println();
        System.out.println("--- RECIBO DE ALQUILER ---");
        System.out.println("Cliente: " + receipt.getMembership().getName());
        System.out.println("Peliculas:");
        for (Movie m : receipt.getMovies()) {
            System.out.printf(" - %s (%s) - $%s%n",
                    m.getTitle(),
                    m.getType(),
                    MONEY.format(m.getPrice()));
        }
        System.out.println("Subtotal: $" + MONEY.format(receipt.getSubtotal()));
        int discountPct = (int) Math.round(receipt.getMembership().getDiscountRate() * 100);
        System.out.printf("Descuento (%d%%): $%s%n",
                discountPct,
                MONEY.format(receipt.getDiscount()));
        System.out.println("Total a pagar: $" + MONEY.format(receipt.getTotal()));
        System.out.println("--------------------------");
        System.out.println("¡Disfrute su pelicula!");
    }
}
