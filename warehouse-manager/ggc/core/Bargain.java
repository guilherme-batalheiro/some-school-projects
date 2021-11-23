package ggc.core;

public class Bargain extends Notification {

    Bargain(Product product, double price) {
        super(product, price);
    }

    @Override
    public String toString() {
        return "BARGAIN|" + super.toString();
    }
}