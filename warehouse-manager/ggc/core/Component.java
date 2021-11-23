package ggc.core;

import java.io.Serializable;

public class Component implements Serializable {
  private int _quantity;
  Product _product;

  Component(Product product, int quantity) {
    _product = product;
    _quantity = quantity;
  }

  Product getProduct() {
    return _product;
  }

  int getQuantity() {
    return _quantity;
  }

  void addQuantity(int quantity) {
    _quantity += quantity;
  }

  public String toString() {
    return "" + _product.getId() + ":" + _quantity + ":" + Math.round(_product.getMaxPrice() * _quantity) + "#";
  }

}