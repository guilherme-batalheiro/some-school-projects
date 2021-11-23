package ggc.core;

//--
public class SaleByCredit extends Sale {
  private Date _deadlineDate;
  private Date _paymentDate;
  private boolean _isPayed = false;
  private double _payedValue;

  SaleByCredit(int id, Date creationDate, double baseValue, int quantity, Product product, Partner partner,
      Date deadlineDate) {
    super(id, creationDate, baseValue, quantity, product, partner);
    _deadlineDate = deadlineDate;
    _payedValue = 0;
  }

  Date getDeadLineDate() {
    return _deadlineDate;
  }

  void paySale(double payedValue) {
    _paymentDate = Date.now();
    _payedValue = payedValue;
    _isPayed = true;
  }

  double getPayedValue() {
    return _payedValue;
  }

  double getSimulatedValue() {

    return super.getBaseValue() + super.getBaseValue() * super.getPartner().getFine(super.getProduct(), _deadlineDate)
        - super.getBaseValue() * super.getPartner().getDiscount(super.getProduct(), _deadlineDate);
  }

  boolean isPaid() {
    return _isPayed;
  }

  boolean payedInTime() {
    return _deadlineDate.difference(Date.now()) >= 0;
  }

  public String toString() {
    return "VENDA|" + super.toString() + "|" + Math.round(_payedValue == 0 ? getSimulatedValue() : _payedValue) + "|"
        + _deadlineDate.getDays() + (isPaid() ? "|" + _paymentDate.getDays() : "");
  }
}