package ggc.core;

import java.io.Serializable;

/**
 * Partner and warehouse transition object.
 */
public abstract class Transaction implements Serializable {
  private static final long serialVersionUID = 202109192006L;
  private int _id;
  private Date _creationDate;
  private double _baseValue;
  private int _quantity;
  private Product _product;
  private Partner _partner;

  Transaction(int id, Date creationDate, double baseValue, int quantity, Product product, Partner partner) {
    _id = id;
    _creationDate = creationDate;
    _baseValue = baseValue;
    _quantity = quantity;
    _product = product;
    _partner = partner;
  }

  int getId() {
    return _id;
  }

  Date getCreationDate() {
    return _creationDate;
  }

  double getBaseValue() {
    return _baseValue;
  }

  int getQuantity() {
    return _quantity;
  }

  Product getProduct() {
    return _product;
  }

  Partner getPartner() {
    return _partner;
  }

  @Override
  public String toString() {
    return _id + "|" + _partner.getId() + "|" + _product.getId() + "|" + _quantity + "|" + Math.round(_baseValue);
  }

}