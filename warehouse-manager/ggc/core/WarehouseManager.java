package ggc.core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import ggc.core.exception.BadEntryException;
import ggc.core.exception.DateNotValidException;
import ggc.core.exception.ImportFileException;
import ggc.core.exception.MissingFileAssociationException;
import ggc.core.exception.NoSuchPartnerIdException;
import ggc.core.exception.NoSuchProductIdException;
import ggc.core.exception.UnavailableFileException;
import ggc.core.exception.NoAvailableQuantityException;
import ggc.core.exception.NoSuchTransactionIdException;

public class WarehouseManager {
  private String _filename = "";
  private Warehouse _warehouse;

  public WarehouseManager() {
    _warehouse = new Warehouse();
  }

  public void save() throws IOException, FileNotFoundException, MissingFileAssociationException {
    ObjectOutputStream obOut = null;
    FileOutputStream fpout = null;

    if (_filename.isEmpty())
      throw new MissingFileAssociationException();

    try {
      fpout = new FileOutputStream(_filename);
      DeflaterOutputStream dOut = new DeflaterOutputStream(fpout);
      obOut = new ObjectOutputStream(dOut);
      obOut.writeObject(_warehouse);
      obOut.writeObject(Date.now());
    } finally {
      if (obOut != null)
        obOut.close();
      if (fpout != null)
        fpout.close();
    }
  }

  public void saveAs(String filename) throws MissingFileAssociationException, FileNotFoundException, IOException {
    _filename = filename;
    save();
  }

  public void load(String filename) throws UnavailableFileException, ClassNotFoundException, IOException {
    ObjectInputStream obIn = null;
    FileInputStream fpin = null;
    Object anObject = null;

    if (_filename.isEmpty())
      _filename = filename;

    try {
      fpin = new FileInputStream(filename);
      InflaterInputStream inflateIn = new InflaterInputStream(fpin);
      obIn = new ObjectInputStream(inflateIn);
      anObject = obIn.readObject();
      _warehouse = (Warehouse) (anObject);
      anObject = obIn.readObject();
      Date.setNewCurrentDate((Date) anObject);
    } catch (FileNotFoundException fnfe) {
      throw new UnavailableFileException(fnfe.getMessage());
    } finally {
      if (obIn != null)
        obIn.close();
      if (fpin != null)
        fpin.close();
    }
  }
  
  public boolean haveFileName() {
    return !_filename.isEmpty();
  }
  
  public void importFile(String textfile) throws ImportFileException {
    try {
      _warehouse.importFile(textfile);
    } catch (IOException | BadEntryException e) {
      throw new ImportFileException(textfile, e);
    }
  }

  public int getDateDays() {
    return Date.now().getDays();
  }

  public void advanceDate(int days) throws DateNotValidException {
    Date.now().add(days);
  }
  
  public boolean registerSimpleProduct(String productId) {
    Product product = new SimpleProduct(productId);
    return _warehouse.registerProduct(product);
  }

  public boolean registerAggregateProduct(String productId, int numberOfComponents, double alpha,
      ArrayList<String> componentsIds, ArrayList<Integer> componentsAmounts) throws NoSuchProductIdException {
    List<Component> componentsOfProduct = new ArrayList<>();

    Iterator<String> iterComponentsIds = componentsIds.iterator();
    Iterator<Integer> iterComponentsAmounts = componentsAmounts.iterator();

    while (iterComponentsIds.hasNext() && iterComponentsAmounts.hasNext()) {
      Product p = _warehouse.getProduct(iterComponentsIds.next());
      int cAmount = iterComponentsAmounts.next();
      Component c = new Component(p, cAmount);
      componentsOfProduct.add(c);
    }

    AggregateProduct product = new AggregateProduct(productId);
    Recipe recipe = new Recipe(alpha, product, componentsOfProduct);
    product.addRecipe(recipe);
    return _warehouse.registerProduct(product);
  }

  public boolean haveProductById(String id) {
    return _warehouse.haveProductById(id);
  }

  public Partner getPartner(String id) throws NoSuchPartnerIdException {
    return _warehouse.getPartner(id);
  }

  public boolean registerPartner(String id, String name, String address) {
    Partner p = new Partner(id, name, address);
    return _warehouse.registerPartner(p);
  }

  public List<Partner> getPartners() {
    return _warehouse.getPartners();
  }

  public List<Product> getProducts() {
    return _warehouse.getProducts();
  }

  public List<Batch> getBatches() {
    return _warehouse.getBatches();
  }

  public Transaction getTransaction(int transactionId) throws NoSuchTransactionIdException {
    return _warehouse.getTransaction(transactionId);
  }

  public List<Batch> getBatchesByProduct(String productId) throws NoSuchProductIdException {
    return _warehouse.getBatchesByProduct(productId);
  }

  public List<Batch> getBatchesByPartner(String partnerId) throws NoSuchPartnerIdException {
    return _warehouse.getBatchesByPartner(partnerId);
  }

  public List<Acquisition> getAcquisitionsByPartner(String partnerId) throws NoSuchPartnerIdException {
    Partner partner = _warehouse.getPartner(partnerId);
    return partner.getAcquisitions();
  }
  
  public List<Sale> getPartnerSales(String partnerId) throws NoSuchPartnerIdException {
    Partner partner = getPartner(partnerId);
    return partner.getPayedSales();
  }
  
  public List<Sale> getSalesByPartner(String partnerId) throws NoSuchPartnerIdException {
    Partner partner = _warehouse.getPartner(partnerId);
    return partner.getSales();
  }

  public List<Batch> getBatchesUnderGivenPrice(double givenPrice) {
    return _warehouse.getBatchesUnderGivenPrice(givenPrice);
  }

  public void toggleProductNotifications(String idPartner, String idProduct)
      throws NoSuchPartnerIdException, NoSuchProductIdException {
    _warehouse.toggleProductNotifications(idPartner, idProduct);
  }
  
  public void registerBreakDown(String partnerId, String productId, int quantity)
      throws NoSuchPartnerIdException, NoSuchProductIdException, NoAvailableQuantityException {
    Partner partner = _warehouse.getPartner(partnerId);
    Product product = _warehouse.getProduct(productId);
    _warehouse.registerBreakDown(partner, product, quantity);

  }

  public void registerSale(String partnerId, int limitDateDays, String productId, int quantity)
      throws NoSuchPartnerIdException, NoSuchProductIdException, NoAvailableQuantityException {
    Partner partner = _warehouse.getPartner(partnerId);
    Product product = _warehouse.getProduct(productId);
    Date limitDate = new Date(limitDateDays);
    _warehouse.registerSale(partner, limitDate, product, quantity);

  }

  public void registerPurchase(String partnerId, String productId, double productPrice, int quantity)
      throws NoSuchPartnerIdException, NoSuchProductIdException {
    Partner partner = _warehouse.getPartner(partnerId);
    Product product = _warehouse.getProduct(productId);
    _warehouse.registerPurchase(partner, product, productPrice, quantity);
  }


  public void receivePayment(int saleId) throws NoSuchTransactionIdException {
    _warehouse.receivePayment(saleId);
  }

  public double getAvailableBalance() {
    return _warehouse.getAvailableBalance();
  }

  public double getAccountingBalance() {
    return _warehouse.getAccountingBalance();
  }
}