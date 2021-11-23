package ggc.core;

public interface Statute {
  double getDiscount(Product product, Date paymentDate);

  double getFine(Product product, Date paymentDate);

  double applyDiscountAndFine(Product product, Partner partner, Date paymentDate, double baseValue);

}