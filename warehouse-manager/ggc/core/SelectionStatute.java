package ggc.core;

import java.io.Serializable;

public class SelectionStatute implements Statute, Serializable {
  public double getDiscount(Product product, Date limitDate) {
    int period = limitDate.difference(Date.now());
    int N;

    if (product instanceof SimpleProduct)
      N = 5;
    else
      N = 3;

    if (period >= N)
      return 0.1;

    if (period >= 0 && period < N) {
      if (period >= 2)
        return 0.05;
    }

    return 0;
  }

  public double getFine(Product product, Date limitDate) {
    int period = limitDate.difference(Date.now());
    int N;

    if (product instanceof SimpleProduct)
      N = 5;
    else
      N = 3;

    if (-period > 0 && -period <= N) {
      if (period < -1)
        return 0.02 * Math.abs(period);
    }

    if ((-period) > N)
      return 0.05 * Math.abs(period);

    return 0;
  }

  public double applyDiscountAndFine(Product product, Partner partner, Date limitDate, double baseValue) {
    double price = baseValue + baseValue * getFine(product, limitDate) - baseValue * getDiscount(product, limitDate);

    if (limitDate.difference(Date.now()) > 0) {
      partner.addPoints(10 * price);
      if (partner.getPoints() > 25000)
        partner.setStatute(new EliteStatute());
    } else if (limitDate.difference(Date.now()) < -2) {
      partner.removePoints(partner.getPoints() * 0.9);
      partner.setStatute(new NormalStatute());
    }

    return price;
  }

  @Override
  public String toString() {
    return "SELECTION";
  }

}