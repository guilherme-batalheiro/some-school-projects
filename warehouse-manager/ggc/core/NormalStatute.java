package ggc.core;

import java.io.Serializable;

public class NormalStatute implements Statute, Serializable {
  public double getDiscount(Product product, Date limitDate) {
    int period = limitDate.difference(Date.now());
    int N;

    if (product instanceof SimpleProduct)
      N = 5;
    else
      N = 3;

    if (period >= N) {
      return 0.1;
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

    if (period >= 0)
      return 0;

    if (period >= -N)
      return 0.05 * Math.abs(period);

    return 0.1 * Math.abs(period);

  }

  public double applyDiscountAndFine(Product product, Partner partner, Date limitDate, double baseValue) {

    double price = baseValue + baseValue * getFine(product, limitDate) - baseValue * getDiscount(product, limitDate);

    if (limitDate.difference(Date.now()) >= 0) {
      partner.addPoints(10 * price);
    } else
      partner.removePoints(partner.getPoints());

    if (partner.getPoints() > 2000)
      partner.setStatute(new SelectionStatute());

    if (partner.getPoints() > 25000)
      partner.setStatute(new EliteStatute());

    return price;
  }

  @Override
  public String toString() {
    return "NORMAL";
  }
}
