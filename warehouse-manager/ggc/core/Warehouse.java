package ggc.core;

import java.io.Serializable;
import java.io.IOException;
import java.util.Map;

import javax.xml.crypto.KeySelector.Purpose;

import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collections;

import ggc.app.exception.UnknownPartnerKeyException;
import ggc.core.exception.BadEntryException;
import ggc.core.exception.NoSuchPartnerIdException;
import ggc.core.exception.NoSuchProductIdException;
import ggc.core.exception.NoAvailableQuantityException;
import ggc.core.exception.NoSuchTransactionIdException;

/**
 * Class Warehouse implements a warehouse.
 */
public class Warehouse implements Serializable {

  private static final long serialVersionUID = 202109192006L;

  private Map<String, Partner> _partners = new HashMap<>();
  private Map<String, Product> _products = new HashMap<>();
  private LinkedList<Transaction> _transactions = new LinkedList<>();

  /**
   * @param txtfile filename to be loaded.
   * @throws IOException
   * @throws BadEntryException
   */
  void importFile(String txtfile) throws IOException, BadEntryException {
    Parser parser = new Parser(this);

    try {
      parser.parseFile(txtfile);
    } catch (IOException | BadEntryException e) {
      throw e;
    }
  }

  /**
   * @param idToKey id to convert to key.
   * @return Key of one partner/product of the partners'/products' HashMap.
   */
  String convertIdToKey(String idToKey) {
    return idToKey.toLowerCase();
  }

  /**
   * @param partner partner to be register.
   * @return true if the given partner is registered, false otherwise.
   */
  boolean registerPartner(Partner partner) {
    if (havePartnerById(partner.getId()))
      return false;
    
    _partners.put(convertIdToKey(partner.getId()), partner);
    addObserverToAllProducts(partner);
    return true;
  }

  /**
   * @param product product to be register.
   * @return true if the given product is registered, false otherwise.
   */
  boolean registerProduct(Product product) {
    if (haveProductById(product.getId()))
      return false;

    _products.put(convertIdToKey(product.getId()), product);
    addProductToAllObservers(product);
    return true;
  }

  /**
   * @param id identifier of the partner to know if exists on the warehouse
   *           system.
   * @return true or false depending on whether the partner exists or not.
   */
  boolean havePartnerById(String id) {
    return _partners.containsKey(convertIdToKey(id));
  }

  /**
   * @param product product to be register.
   * @return true or false depending on whether the product exits or not.
   */
  boolean haveProductById(String id) {
    return _products.containsKey(convertIdToKey(id));
  }

  /**
   * @param id partner's identifier.
   * @return Partner with given id.
   * @throws NoSuchProductIdException
   */
  Partner getPartner(String id) throws NoSuchPartnerIdException {
    if (!havePartnerById(id))
      throw new NoSuchPartnerIdException(id);

    return _partners.get(convertIdToKey(id));
  }

  /**
   * @param id product's identifier.
   * @return Product with the given id.
   * @throws NoSuchProductIdException
   */
  Product getProduct(String id) throws NoSuchProductIdException {
    if (!haveProductById(id))
      throw new NoSuchProductIdException(id);

    return _products.get(convertIdToKey(id));
  }

  /**
   * @return List with all partners sorted.
   */
  List<Partner> getPartners() {
    List<Partner> keySetArray = new ArrayList<>(_partners.values());

    keySetArray.sort(Partner.getComparatorById());

    return keySetArray;
  }

  /**
   * @return List with all products sorted.
   */
  List<Product> getProducts() {
    List<Product> keySetArray = new ArrayList<>(_products.values());

    keySetArray.sort(Product.getComparatorById());

    return keySetArray;
  }

  /**
   * @param id transaction's identifier.
   * @return Transaction with the given identifier.
   * @throws NoSuchTransactionIdException
   */
  Transaction getTransaction(int id) throws NoSuchTransactionIdException {
    try {
      return _transactions.get(id);
    } catch (IndexOutOfBoundsException e) {
      throw new NoSuchTransactionIdException(id);
    }
  }

  /**
   * @return list with all batches sorted.
   */
  List<Batch> getBatches() {
    List<Batch> batches = new ArrayList<>();

    for (Product p : getProducts()) {
      for (Batch b : p.getBatches())
        batches.add(b);
    }

    Collections.sort(batches, Batch.getComparatorOfBatches());

    return batches;
  }

  /**
   * @param partnerId partner's identifier.
   * @return list of sorted batches of the partner with the given partner
   *         identifier.
   * @throws NoSuchPartnerIdException
   */
  List<Batch> getBatchesByPartner(String partnerId) throws NoSuchPartnerIdException {
    List<Batch> batches = new ArrayList<>(getPartner(partnerId).getBatches());

    Collections.sort(batches, Batch.getComparatorOfBatches());

    return batches;
  }

  /**
   * @param productId product's identifier.
   * @return list of sorted batches of the product with the given product
   *         identifier.
   * @throws NoSuchProductIdException
   */
  List<Batch> getBatchesByProduct(String productId) throws NoSuchProductIdException {
    List<Batch> batches = new ArrayList<>(getProduct(productId).getBatches());

    Collections.sort(batches, Batch.getComparatorOfBatches());

    return batches;
  }

  /**
   * @param givenPrice limit price of batches.
   * @return list of sorted batches under the given price.
   */
  List<Batch> getBatchesUnderGivenPrice(double givenPrice) {
    List<Batch> batchesUnderGivenPrice = new ArrayList<>();

    for (Batch b : getBatches()) {
      if (b.getPrice() <= givenPrice)
        batchesUnderGivenPrice.add(b);
    }

    return batchesUnderGivenPrice;
  }

  /**
   * @param idPartner identifier of the partner to activate/desactivate certain
   *                  notifications.
   * @param idProduct identifier of the product to activate/desactivate
   *                  notifications.
   */
  void toggleProductNotifications(String idPartner, String idProduct)
      throws NoSuchPartnerIdException, NoSuchProductIdException {
    Partner partner = getPartner(idPartner);
    Product product = getProduct(idProduct);

    if (product.searchObserver(partner))
      product.removeObserver(partner);
    else
      product.addObserver(partner);
  }

  /**
   * @param partner partner to add to all products' list of observers.
   */
  void addObserverToAllProducts(Partner partner) {
    for (Product p : getProducts())
      p.addObserver(partner);
  }

  /**
   * @param product product to add all partners to its list of observers.
   */
  void addProductToAllObservers(Product product) {
    for (Partner p : getPartners())
      product.addObserver(p);
  }

  /**
   * @param partner           partner who asked for the breakdown.
   * @param product           product to break down.
   * @param quantityRequested quantity of the given product requested to break
   *                          down.
   * @throws NoAvailableQuantityException
   */
  void registerBreakDown(Partner partner, Product product, int quantityRequested) throws NoAvailableQuantityException {
    int availableQuantity = product.getTotalQuantity();
    if (availableQuantity < quantityRequested) {
      throw new NoAvailableQuantityException(product.getId(), quantityRequested, availableQuantity);
    }

    if (product instanceof AggregateProduct) {
      double price = ((AggregateProduct) product).breakProduct(quantityRequested, partner);

      double pricePayed = price;
      if (price < 0)
        pricePayed = 0;

      partner.addPoints(pricePayed * 10);

      int transactionId = _transactions.isEmpty() ? 0 : _transactions.getLast().getId() + 1;
      BreakdownSale sale = new BreakdownSale(transactionId, new Date(Date.now().getDays()), price, quantityRequested,
          product, partner, pricePayed);
      _transactions.add(sale);
      partner.addSale(sale);
    }
  }

  /**
   * @param partner           partner that buys from the warehouse.
   * @param limitDate         limit Date for the partner to pay.
   * @param product           product to sell.
   * @param quantityRequested quantity of the given product requested to sell.
   * @throws NoAvailableQuantityException
   */
  void registerSale(Partner partner, Date limitDate, Product product, int quantityRequested)
      throws NoAvailableQuantityException {

    updateCopyOfBatches();

    product.checkIfCanSale(quantityRequested);
    double price = product.sellProduct(quantityRequested);

    int transactionId = _transactions.isEmpty() ? 0 : _transactions.getLast().getId() + 1;
    SaleByCredit sale = new SaleByCredit(transactionId, new Date(Date.now().getDays()), price, quantityRequested,
        product, partner, limitDate);
    _transactions.add(sale);
    partner.addSale(sale);
  }

  /**
   * @param partner      partner to purchase from.
   * @param product      product to purchase.
   * @param productPrice price of the product to purchase.
   * @param quantity     quantity of the given product to purchase.
   * @throws NoAvailableQuantityException
   */
  void registerPurchase(Partner partner, Product product, double productPrice, int quantity) {
    Batch batch = new Batch(partner, product, productPrice, quantity);
    product.addBatch(batch);
    partner.addBatch(batch);

    int transactionId = _transactions.isEmpty() ? 0 : _transactions.getLast().getId() + 1;
    Acquisition acquisition = new Acquisition(transactionId, new Date(Date.now().getDays()), productPrice * quantity,
        quantity, product, partner);

    _transactions.add(acquisition);
    partner.addAcquisition(acquisition);
  }

  /**
   * @param saleId identifier of the transaction (sale) to receive payment from.
   * @throws NoSuchTransactionIdException
   */
  void receivePayment(int saleId) throws NoSuchTransactionIdException {
    Transaction transaction = getTransaction(saleId);

    if (transaction instanceof SaleByCredit && !((SaleByCredit) transaction).isPaid()) {
      SaleByCredit sale = (SaleByCredit) transaction;
      Partner partner = sale.getPartner();
      Product product = sale.getProduct();
      Date deadLineDate = sale.getDeadLineDate();
      double payedPrice = 0;
      payedPrice = partner.applyDiscountAndFine(product, deadLineDate, sale.getBaseValue());
      sale.paySale(payedPrice);
    }
  }

  List<Transaction> removeSales(Partner partner, double price){
    List<Transaction> res = new ArrayList<>();
    
    for(Transaction t : _transactions){
      if(t.getPartner().equals(partner) && (t instanceof BreakdownSale || (t instanceof SaleByCredit && ((SaleByCredit) t).isPaid()))){
        res.add(t);
        _transactions.remove(t);
      }
    }
    return res;
  }

  Batch getBiggestLot(){
    int quantity = 0;
    Product product = null;

    for(Product p : _products.values()){
      if(p.getTotalQuantity() > quantity){
        product = p;
      }
    }

    return product.getBiggestLot();
  }

  /**
   * @return system available balance.
   */
  double getAvailableBalance() {
    double balance = 0;

    for (Transaction t : _transactions) {
      if (t instanceof Acquisition)
        balance -= t.getBaseValue();
      else if (t instanceof SaleByCredit && ((SaleByCredit) t).isPaid())
        balance += ((SaleByCredit) t).getPayedValue();
      else if (t instanceof BreakdownSale)
        balance += t.getBaseValue() > 0 ? t.getBaseValue() : 0;
    }

    return balance;
  }

  /**
   * @return system accounting balance.
   */
  double getAccountingBalance() {
    double balance = 0;

    for (Transaction t : _transactions) {
      if (t instanceof Acquisition)
        balance -= t.getBaseValue();
      else if (t instanceof SaleByCredit){
        if(!((SaleByCredit) t).isPaid())
          balance += ((SaleByCredit) t).getSimulatedValue();
        else
          balance += ((SaleByCredit) t).getPayedValue();
      }
      else
        balance += t.getBaseValue() > 0 ? t.getBaseValue() : 0;
    }

    return balance;
  }

  void updateCopyOfBatches() {
    for (Product p : getProducts())
      p.updateCopyOfBatches();
  }
}
