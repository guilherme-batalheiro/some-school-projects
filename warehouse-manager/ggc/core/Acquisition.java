package ggc.core;

public class Acquisition extends Transaction {
  Acquisition(int transactionId, Date paymentDate, double baseValue, int quantity, Product product, Partner partner) {
    super(transactionId, paymentDate, baseValue, quantity, product, partner);
  }

  public String toString() {
    return "COMPRA|" + super.toString() + "|" + super.getCreationDate().getDays();
  }
}
