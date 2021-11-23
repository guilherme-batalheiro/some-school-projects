package ggc.core;

import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.io.Serializable;

public class Partner implements Serializable, Observer {

  private static final long serialVersionUID = 202109192006L;

  private String _id;
  private String _name;
  private String _address;
  private Statute _statute = new NormalStatute();
  private double _points;
  private List<Batch> _batches = new ArrayList<Batch>();
  private List<Sale> _listOfSales = new ArrayList<>();
  private List<Acquisition> _listOfAcquisition = new ArrayList<>();
  private List<Notification> _listOfNotifications = new ArrayList<>();

  public Partner(String id, String name, String address) {
    _id = id;
    _name = name;
    _address = address;
  }

  String getId() {
    return _id;
  }

  String getName() {
    return _name;
  }

  String getAddress() {
    return _address;
  }

  Statute getStatus() {
    return _statute;
  }

  double getPoints() {
    return _points;
  }

  void addPoints(double points) {
    _points += points;
  }

  void setStatute(Statute statute) {
    _statute = statute;
  }

  List<Batch> getBatches() {
    return _batches;
  }

  void addBatch(Batch batch) {
    _batches.add(batch);
  }

  void removeBatch(Batch batch) {
    _batches.remove(batch);
  }

  void addAcquisition(Acquisition acquisition) {
    _listOfAcquisition.add(acquisition);
  }

  void addSale(Sale sale) {
    _listOfSales.add(sale);
  }

  List<Acquisition> getAcquisitions() {
    return _listOfAcquisition;
  }

  void removePoints(double pointsToRemove) {
    _points -= pointsToRemove;
  }

  List<Sale> getPayedSales() {
    List<Sale> salePayed = new ArrayList<>();

    for (Sale s : _listOfSales) {
      if (s instanceof SaleByCredit && ((SaleByCredit) s).isPaid())
        salePayed.add(s);
      else if (s instanceof BreakdownSale)
        salePayed.add(s);
    }

    return salePayed;
  }

  List<Sale> getSales() {
    return _listOfSales;
  }

  
  double getValueOfAcquisition() {
    double res = 0;
    
    for (Acquisition a : _listOfAcquisition)
    res += a.getBaseValue();
    
    return res;
  }
  
  double getValueOfSales() {
    double res = 0;
    
    for (Sale s : _listOfSales)
    if ((s instanceof SaleByCredit))
    res += s.getBaseValue();
    
    return res;
  }
  
  double getValueOfPaidSales() {
    double res = 0;
    
    for (Sale s : _listOfSales) {
      if ((s instanceof SaleByCredit) && ((SaleByCredit) s).isPaid())
      res += ((SaleByCredit) s).getPayedValue();
    }
    return res;
  }
  
  public List<Notification> getNotifications() {
    List<Notification> notifications = new ArrayList<Notification>(_listOfNotifications);
    _listOfNotifications = new ArrayList<Notification>();
    return notifications;
  }
  
  public void addNotification(Notification notification) {
    _listOfNotifications.add(notification);
  }
  
  @Override
  public int hashCode() {
    return _id.toLowerCase().hashCode();
  }
  
  @Override
  public boolean equals(Object obj) {
    return obj instanceof Partner && _id.toLowerCase().equals(((Partner) obj).getId().toLowerCase());
  }
  
  double getFine(Product product, Date paymentDate) {
    return _statute.getFine(product, paymentDate);
  }
  
  double getDiscount(Product product, Date paymentDate) {
    return _statute.getDiscount(product, paymentDate);
  }
  
  double applyDiscountAndFine(Product product, Date paymentDate, double baseValue) {
    return _statute.applyDiscountAndFine(product, this, paymentDate, baseValue);
  }
  
  public static Comparator<Partner> getComparatorById() {
    return COMPARE_BY_ID;
  }
  
  private static final Comparator<Partner> COMPARE_BY_ID = new Comparator<Partner>() {
    @Override
    public int compare(Partner partner, Partner otherPartner) {
      return partner.getId().toLowerCase().compareTo(otherPartner.getId().toLowerCase());
    }
  };
  
  @Override
  public String toString() {
    
    return _id + "|" + _name + "|" + _address + "|" + _statute + "|" + Math.round(_points) + "|"
        + Math.round(getValueOfAcquisition()) + "|" + Math.round(getValueOfSales()) + "|"
        + Math.round(getValueOfPaidSales());
  }

}