package ggc.core;

public class BreakdownSale extends Sale {
  private double _payedValue;

  BreakdownSale(int id, Date creationDate, double baseValue, int quantity, Product product, Partner partner,
      double payedValue) {
    super(id, creationDate, baseValue, quantity, product, partner);
    _payedValue = payedValue;
  }

  public String toString() {
    String components = "";

    for (Component c : ((AggregateProduct) super.getProduct()).getRecipe().getComponents())
      components += c.getProduct().getId() + ":" + c.getQuantity() * super.getQuantity() + ":"
          + Math.round(c.getProduct().getMaxPrice() * c.getQuantity() * super.getQuantity()) + "#";

    components = components.substring(0, components.length() - 1);

    return "DESAGREGAÇÃO|" + super.toString() + "|" + Math.round(_payedValue) + "|" + super.getCreationDate().getDays()
        + "|" + components;
  }
}
