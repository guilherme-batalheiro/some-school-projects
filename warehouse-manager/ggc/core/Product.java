package ggc.core;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Collections;

import ggc.core.exception.NoAvailableQuantityException;

//--
/**
 * A product that can be simple or derivative
 */
public abstract class Product implements Serializable {
  /** Serial number for serialization. */
  private static final long serialVersionUID = 202129192006L;
  /** Identifier of the Product */
  private String _id;
  /** Highest price */
  private double _maxPrice;
  /** list of batches of this product */
  private List<Batch> _batches = new ArrayList<>();
  private List<Batch> _batchesCopy = new ArrayList<>();
  /** Set of partners interested in receiving notifications from this product */
  private List<Partner> _observers = new ArrayList<>();
  private NotificationMethod _notificationMethod = new DefaultMethodNotification();

  /**
   * @param id the product's unique identifier
   */
  Product(String id) {
    _id = id;
  }

  /**
   * Add a batch to this product's batch list and all batches' ever list.
   * 
   * Notifies its observers if there's new stocks of this product (if it already
   * existed). Notifies its observers if there's lower prices of this product
   * 
   * @param batch batch to add to the list of batches of this product
   * @return
   */
  void addBatch(Batch batch) {
    if (_maxPrice != 0 && (_batches.size() == 0))
      _notificationMethod.notifyPartnersNew(this, batch.getPrice());

    if (batch.getPrice() < getMinimumPrice())
      _notificationMethod.notifyPartnersBargain(this, batch.getPrice());

    _batches.add(batch);
    updateMaxPrice(batch.getPrice());
  }

  void removeBatch(Batch batch) {
    _batches.remove(batch);
  }

  void removeBatchCopy(Batch batch) {
    _batchesCopy.remove(batch);
  }

  /**
   * @return list of all the batches that this product has
   */
  List<Batch> getBatches() {
    return new ArrayList<>(_batches);
  }

  List<Batch> getBatchesCopy() {
    return new ArrayList<>(_batchesCopy);
  }

  void updateCopyOfBatches() {
    _batchesCopy.clear();
    for (Batch b : _batches)
      _batchesCopy.add(b.getCopy());
  }

  /**
   * Return the number of units available of this product.
   * 
   * @return total of units
   */
  int getTotalQuantity() {
    int total = 0;
    for (Batch b : _batches)
      total += b.getQuantity();
    return total;
  }

  /**
   * @return the product's identifier
   */
  String getId() {
    return _id;
  }

  /**
   * @return the product's maximum price
   */
  double getMaxPrice() {
    return _maxPrice;
  }

  double getMinimumPrice() {
    double price = 0;
    double priceOfProduct = 0;

    for (Batch b : _batches) {
      priceOfProduct = b.getPrice();
      if (priceOfProduct < price || price == 0)
        price = priceOfProduct;
    }
    return price;
  }

  double getPriceForComponent() {
    double price = getMinimumPrice();
    return price != 0 ? price : _maxPrice;
  }

  /**
   * If the new price is higher than the previous set maximum price and update it.
   * 
   * @param newPrice price to compare to the maximum price
   * @return If the price was changed, return true otherwise return false.
   */
  boolean updateMaxPrice(double newPrice) {
    if (_maxPrice < newPrice) {
      _maxPrice = newPrice;
    }
    return _maxPrice < newPrice;
  }

  void setMaxPrice(double newPrice) {
    _maxPrice = newPrice;
  }

  Batch getBiggestLot() {
    int quantity = 0;
    Batch batch = null;

    for(Batch b : _batches){
      if(b.getQuantity() > quantity)
        batch = b;
    }

    return batch;
  }

  double sellProduct(int totalQuantityToSell) {
    ArrayList<Batch> batches = new ArrayList<>(getBatches());

    Collections.sort(batches, Batch.getComparatorOfBatchesByPrice());

    Iterator<Batch> iteratorOfBatches = batches.iterator();

    int soldQuantity = 0;
    double salePrice = 0;

    while (iteratorOfBatches.hasNext() && soldQuantity < totalQuantityToSell) {
      Batch batch = iteratorOfBatches.next();
      if (soldQuantity + batch.getQuantity() <= totalQuantityToSell) {
        soldQuantity += batch.getQuantity();
        salePrice += batch.getPrice() * batch.getQuantity();
        batch.getPartner().removeBatch(batch);
        removeBatch(batch);
      } else {
        salePrice += batch.getPrice() * (totalQuantityToSell - soldQuantity);
        batch.removeQuantity(totalQuantityToSell - soldQuantity);
        soldQuantity += totalQuantityToSell - soldQuantity;
      }
    }

    if (soldQuantity < totalQuantityToSell && this instanceof AggregateProduct)
      salePrice += ((AggregateProduct) this).createProduct(totalQuantityToSell - soldQuantity);

    if (this.getTotalQuantity() == 0 && this instanceof AggregateProduct) {
      try {
        ((AggregateProduct) this).checkIfCanSale(1);
        setMaxPrice(((AggregateProduct) this).getMaxPriceOfCreation());
      } catch (NoAvailableQuantityException e) {
      }
    }

    return salePrice;
  }

  void checkIfCanSale(int quantityRequested) throws NoAvailableQuantityException {
    int quantityAvailable = 0;
    ArrayList<Batch> batches = new ArrayList<>(getBatchesCopy());
    Iterator<Batch> iteratorOfBatches = batches.iterator();

    while (iteratorOfBatches.hasNext() && quantityAvailable < quantityRequested) {
      Batch batch = iteratorOfBatches.next();
      if (quantityAvailable + batch.getQuantity() <= quantityRequested) {
        quantityAvailable += batch.getQuantity();
        removeBatchCopy(batch);
      } else {
        batch.removeQuantity(quantityRequested - quantityAvailable);
        quantityAvailable += quantityRequested - quantityAvailable;
      }
    }

    if (this instanceof AggregateProduct && quantityAvailable < quantityRequested) {
      ((AggregateProduct) this).checkIfCanCreate(quantityRequested - quantityAvailable);
    } else if (quantityAvailable < quantityRequested)
      throw new NoAvailableQuantityException(this.getId(), quantityRequested, quantityAvailable);
  }

  /**
   * @return hashcode based on the id of the product
   */
  @Override
  public int hashCode() {
    return _id.hashCode();
  }

  /**
   * @param obj the other product used in the comparison
   * @return true if the products are both from class Product and have the same
   *         id, false otherwise.
   */
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Product && _id.toLowerCase().equals(((Product) obj).getId().toLowerCase());
  }

  /**
   * @return comparator to order instances of the class Product
   */
  public static Comparator<Product> getComparatorById() {
    return COMPARE_BY_ID;
  }

  /**
   * Compare products by their identifier
   * 
   * @return comparator of the comparison between objects of class Product
   */
  private static final Comparator<Product> COMPARE_BY_ID = new Comparator<Product>() {
    @Override
    public int compare(Product product, Product otherProduct) {
      return product.getId().toLowerCase().compareTo(otherProduct.getId().toLowerCase());
    }
  };

  /** Add partner to be notified of the events related to this product */
  void addObserver(Partner partner) {
    _observers.add(partner);
  }

  /**
   * Remove partner to stop being notified of the events related to this product
   */
  void removeObserver(Partner partner) {
    _observers.remove(partner);
  }

  List<Partner> getListOfObservers() {
    return new ArrayList<Partner>(_observers);
  }

  /**
   * @return true if the partner is an observer of this product and false
   *         otherwise
   */
  boolean searchObserver(Partner partner) {
    return _observers.contains(partner);
  }

  /**
   * ToString method
   */
  @Override
  public String toString() {
    return _id + "|" + Math.round(_maxPrice) + "|" + getTotalQuantity();
  }
}