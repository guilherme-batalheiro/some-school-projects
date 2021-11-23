package ggc.core;

import java.io.Serializable;
import java.util.Comparator;

//--
/**
 * Set of products (simple/derivatives) that are bought, sold or manufactured.
 */
public class Batch implements Serializable {
  /** Serial number for serialization. */
  private static final long serialVersionUID = 202109192006L;
  /** The associated Partner */
  private Partner _partner;
  /** Type of product */
  private Product _product;
  /** Price */
  private double _price;
  /** Number of product units */
  private int _quantity;

  /**
   * Create a batch
   * 
   * @param partner  Partner who bought or sold the batch to the warehouse
   * @param product  Type of the product of the batch
   * @param price    Price at which the batch was bought or sold
   * @param quantity Number of products at the batch
   */
  Batch(Partner partner, Product product, double price, int quantity) {
    _partner = partner;
    _product = product;
    _price = price;
    _quantity = quantity;
  }

  Batch getCopy(){
    return new Batch(_partner, _product, _price, _quantity);
  }

  /**
   * @return the partner associated to the batch
   */
  Partner getPartner() {
    return _partner;
  }

  /**
   * @return the type of product of the batch
   */
  Product getProduct() {
    return _product;
  }

  /**
   * @return the price of the batch
   */
  double getPrice() {
    return _price;
  }

  /**
   * @return number of product units of the batch
   */
  int getQuantity() {
    return _quantity;
  }

  void removeQuantity(int quantityToRemove) {
    _quantity -= quantityToRemove;
  }

  /**
   * @return copy of the batch
   */
  protected Batch makeCopy() {
    Batch copyOfThisBatch = new Batch(this.getPartner(), this.getProduct(), this.getPrice(), this.getQuantity());

    return copyOfThisBatch;
  }

  /**
   * @return comparator to order instances of the class Batch
   */
  public static Comparator<Batch> getComparatorOfBatches() {
    return COMPARE_BATCHES;
  }

  /**
   * @return comparator to order instances of the class Batch
   */
  public static Comparator<Batch> getComparatorOfBatchesByPrice() {
    return COMPARE_BATCHES_BY_PRICE;
  }

  private static final Comparator<Batch> COMPARE_BATCHES_BY_PRICE = new Comparator<Batch>() {
    @Override
    /**
     * @param batch      one of the batches to make the comparison
     * @param otherBatch the other one to make the comparison
     */
    public int compare(Batch batch, Batch otherBatch) {
      int compareByPrice = Double.compare(batch.getPrice(), otherBatch.getPrice());
      return compareByPrice;
    }
  };

  /**
   * Compare batches by the identifier of the Product in the first place if
   * they're equal, compares by the identifier of the Partner if its the same,
   * compares by price and then by quantity
   * 
   * @return comparator of the comparison between objects of class Batch
   */
  private static final Comparator<Batch> COMPARE_BATCHES = new Comparator<Batch>() {
    @Override
    /**
     * @param batch      one of the batches to make the comparison
     * @param otherBatch the other one to make the comparison
     */
    public int compare(Batch batch, Batch otherBatch) {
      int compareByProductId = batch.getProduct().getId().compareTo(otherBatch.getProduct().getId());
      if (compareByProductId != 0)
        return compareByProductId;

      int compareByPartnerId = batch.getPartner().getId().compareTo(otherBatch.getPartner().getId());
      if (compareByPartnerId != 0)
        return compareByPartnerId;

      int compareByPrice = Double.compare(batch.getPrice(), otherBatch.getPrice());
      if (compareByPrice != 0)
        return compareByPrice;

      int compareByQuantity = Integer.compare(batch.getQuantity(), otherBatch.getQuantity());
      return compareByQuantity;
    }
  };

  /**
   * @return String representation of the batch and its attributes
   */
  @Override
  public String toString() {
    return _product.getId() + "|" + _partner.getId() + "|" + Math.round(_price) + "|" + _quantity;
  }
}