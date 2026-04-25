package com.videoclub.membership;

/**
 * Membresia premium: 20% de descuento.
 */
public class PremiumMembership implements Membership {

    private static final double DISCOUNT = 0.20;

    @Override
    public String getName() {
        return "Premium";
    }

    @Override
    public double getDiscountRate() {
        return DISCOUNT;
    }
}
