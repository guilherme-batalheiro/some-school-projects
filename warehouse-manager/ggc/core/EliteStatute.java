package ggc.core;

import java.io.Serializable;

public class EliteStatute implements Statute, Serializable {
  public double getDiscount(Product product, Date limitDate) {
    int period = limitDate.difference(Date.now());
    int N;

    if (product instanceof SimpleProduct)
      N = 5;
    else
      N = 3;

    if (period >= 0) {
      return 0.1;
    }
    if (period >= -N) {
      return 0.05;
    }

    return 0;
  }

  public double getFine(Product product, Date limitDate) {
    return 0;
  }

  public double applyDiscountAndFine(Product product, Partner partner, Date limitDate, double baseValue) {

    double price = baseValue + baseValue * getFine(product, limitDate) - baseValue * getDiscount(product, limitDate);

    if (limitDate.difference(Date.now()) >= 0) {
      partner.addPoints(10 * baseValue);
    } else if (limitDate.difference(Date.now()) < -15) {
      partner.setStatute(new SelectionStatute());
      partner.removePoints(partner.getPoints() * 0.75);
      if (partner.getPoints() <= 2000)
        partner.setStatute(new NormalStatute());

    }

    return price;
  }

  @Override
  public String toString() {
    return "ELITE";
  }
}