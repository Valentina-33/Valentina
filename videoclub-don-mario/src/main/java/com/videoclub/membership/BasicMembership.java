package com.videoclub.membership;

/**
 * Membresia basica: precio normal, sin descuento.
 */
public class BasicMembership implements Membership {

    @Override
    public String getName() {
        return "Basica";
    }

    @Override
    public double getDiscountRate() {
        return 0.0;
    }
}
