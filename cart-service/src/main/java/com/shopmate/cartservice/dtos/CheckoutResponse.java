package com.shopmate.cartservice.dtos;

import com.shopmate.cartservice.models.Checkout;

public class CheckoutResponse {
    Checkout checkout;
    String url;

    public Checkout getCheckout() {
        return checkout;
    }

    public void setCheckout(Checkout checkout) {
        this.checkout = checkout;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "CheckoutResponse{" +
                "checkout=" + checkout +
                ", url='" + url + '\'' +
                '}';
    }
}
