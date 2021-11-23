package ggc.core;

import java.io.Serializable;

public class DefaultMethodNotification implements NotificationMethod, Serializable {
  /** Serial number for serialization. */
  private static final long serialVersionUID = 202129192006L;

  /**
   * Notifies its observers if there's new stocks of this product (if it already
   * existed)
   */
  public void notifyPartnersNew(Product product, double price) {
    Notification notification = new New(product, price);

    for (Partner o : product.getListOfObservers()) {
      o.addNotification(notification);
    }
  }

  /** Notifies its observers if there's lower prices of this product */
  public void notifyPartnersBargain(Product product, double price) {
    Notification notification = new Bargain(product, price);

    for (Partner o : product.getListOfObservers()) {
      o.addNotification(notification);
    }
  }
}
