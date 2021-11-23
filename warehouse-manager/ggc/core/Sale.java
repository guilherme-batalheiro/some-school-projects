package ggc.core;

//--
public abstract class Sale extends Transaction {
  Sale(int id, Date creationDate, double baseValue, int quantity, Product product, Partner partner) {
    super(id, creationDate, baseValue, quantity, product, partner);
  }

  @Override 
  int getQuantity() {
    return super.getQuantity();
  }
}
