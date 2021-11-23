package ggc.core;

import ggc.core.exception.NoAvailableQuantityException;

public class AggregateProduct extends Product {
  private Recipe _recipe;

  AggregateProduct(String id) {
    super(id);
  }

  Recipe getRecipe() {
    return _recipe;
  }

  void addRecipe(Recipe recipe) {
    _recipe = recipe;
  }

  double getMaxPriceOfCreation() {
    return _recipe.getMaxPriceOfCreation();
  }

  double createProduct(int quantity) {
    return _recipe.createProduct(quantity);
  }

  double breakProduct(int quantity, Partner partner) {
    double priceOfAggregatedProduct = this.sellProduct(quantity);
    double priceOfComponentsAfterCreation = _recipe.addComponentsOfDesegreatedProductToNewBatch(quantity, partner);

    return priceOfAggregatedProduct - priceOfComponentsAfterCreation;
  }

  int getTotalNeededForCreation(String productId) {
    return _recipe.getTotalNeededForCreation(productId);
  }

  void checkIfCanCreate(int quantityRequested) throws NoAvailableQuantityException {
    _recipe.checkIfCanCreate(quantityRequested);
  }

  @Override
  public String toString() {
    return super.toString() + "|" + _recipe.toString();
  }
}