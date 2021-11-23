package ggc.core;

import java.util.List;
import java.io.Serializable;

import ggc.core.exception.NoAvailableQuantityException;

//--
public class Recipe implements Serializable {
  private double _alpha;
  private AggregateProduct _aggregateProduct;
  private List<Component> _components;

  Recipe(double alpha, AggregateProduct aggregateProduct, List<Component> components) {
    _alpha = alpha;
    _aggregateProduct = aggregateProduct;
    _components = components;
  }

  double getAlpha() {
    return _alpha;
  }

  AggregateProduct getAggregateProduct() {
    return _aggregateProduct;
  }

  List<Component> getComponents() {
    return _components;
  }

  double getMaxPriceOfCreation() {
    double price = 0;
    for (Component c : _components)
      if (c.getProduct() instanceof AggregateProduct) {
        AggregateProduct aggregateProduct = (AggregateProduct) c.getProduct();
        price += aggregateProduct.getMaxPriceOfCreation() * c.getQuantity();
      } else
        price += c.getProduct().getMaxPrice() * c.getQuantity();
    return price * (1 + _alpha);
  }

  double createProduct(int quantity) {
    double price = 0;

    for (Component c : _components)
      price += c.getProduct().sellProduct(quantity * c.getQuantity());
    return price * (1 + _alpha);
  }

  double addComponentsOfDesegreatedProductToNewBatch(int quantityOfProductCreated, Partner partner) {

    double price = 0;
    int componentQuantity;
    double componentPrice;
    Batch batch;
    Product product;

    for (Component c : _components) {
      product = c.getProduct();
      componentQuantity = c.getQuantity();
      componentPrice = product.getPriceForComponent();

      batch = new Batch(partner, product, componentPrice, componentQuantity * quantityOfProductCreated);

      product.addBatch(batch);
      partner.addBatch(batch);

      price += componentPrice * componentQuantity * quantityOfProductCreated;
    }

    return price;
  }

  void checkIfCanCreate(int quantityRequested) throws NoAvailableQuantityException {
    for (Component c : _components) {
      if (c.getProduct() instanceof AggregateProduct) {
        AggregateProduct aggregateProduct = (AggregateProduct) c.getProduct();
        aggregateProduct.checkIfCanSale(c.getQuantity() * quantityRequested);
      } else {
        c.getProduct().checkIfCanSale(c.getQuantity() * quantityRequested);
      }
    }
  }

  int getTotalNeededForCreation(String productId){
    int total = 0;
    
    for(Component c : _components) {
      if (c.getProduct() instanceof AggregateProduct)
        total += ((AggregateProduct) c.getProduct()).getTotalNeededForCreation(productId);
      else if (c.getProduct().getId().equals(productId))
        total += c.getQuantity();
    }

    return total;

  }

  @Override
  public String toString() {
    String recipeString = "";
    for (Component c : _components)
      recipeString += c.getProduct().getId() + ":" + c.getQuantity() + "#";

    return recipeString.substring(0, recipeString.length() - 1);
  }
}