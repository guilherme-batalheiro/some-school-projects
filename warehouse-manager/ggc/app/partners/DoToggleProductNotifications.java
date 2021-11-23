package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.core.WarehouseManager;
import ggc.core.exception.NoSuchProductIdException;
import ggc.core.exception.NoSuchPartnerIdException;
import ggc.app.exception.UnknownProductKeyException;
import ggc.app.exception.UnknownPartnerKeyException;

/**
 * Toggle product-related notifications.
 */
class DoToggleProductNotifications extends Command<WarehouseManager> {

  DoToggleProductNotifications(WarehouseManager receiver) {
    super(Label.TOGGLE_PRODUCT_NOTIFICATIONS, receiver);
    addStringField("IdPartner", Message.requestPartnerKey());
    addStringField("IdProduct", Message.requestProductKey());
  }

  @Override
  public void execute() throws CommandException {
    String idPartner = stringField("IdPartner");
    String idProduct = stringField("IdProduct");
    try {
      _receiver.toggleProductNotifications(idPartner, idProduct); 
    } catch (NoSuchPartnerIdException e) {
      throw new UnknownPartnerKeyException(idPartner);
    } catch (NoSuchProductIdException e) {
      throw new UnknownProductKeyException(idProduct);
    }
  }

}
