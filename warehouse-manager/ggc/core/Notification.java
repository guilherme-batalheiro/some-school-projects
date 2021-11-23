package ggc.core;

import java.io.Serializable;

public abstract class Notification implements Serializable {

  private static final long serialVersionUID = 202109192006L;

  private Product _product;
  private double _price;

  Notification(Product product, double price) {
    _product = product;
    _price = price;
  }

  Product getProduct() {
    return _product;
  }

  double getPrice() {
    return _price;
  }

  @Override
  public String toString() {
      return getProduct().getId() + "|" + Math.round(getPrice());
  }

}