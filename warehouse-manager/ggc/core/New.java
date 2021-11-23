package ggc.core;

public class New extends Notification {

    New(Product product, double price) {
        super(product, price);
    }

    @Override
    public String toString() {
        return "NEW|" + super.toString();
    }
}