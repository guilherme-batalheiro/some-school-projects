package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.core.WarehouseManager;
import ggc.core.exception.NoSuchPartnerIdException;
import ggc.core.exception.NoSuchProductIdException;
import ggc.core.exception.NoAvailableQuantityException;

import ggc.app.exception.UnknownPartnerKeyException;
import ggc.app.exception.UnknownProductKeyException;
import ggc.app.exception.UnavailableProductException;

/**
 * Register order.
 */
public class DoRegisterBreakdownTransaction extends Command<WarehouseManager> {

  public DoRegisterBreakdownTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_BREAKDOWN_TRANSACTION, receiver);
    addStringField("partnerId", Message.requestPartnerKey());
    addStringField("productId", Message.requestProductKey());
    addIntegerField("quantity", Message.requestAmount());
  }

  @Override
  public final void execute() throws CommandException {
    String partnerId = stringField("partnerId");
    String productId = stringField("productId");
    int quantity = integerField("quantity");

    try {
      _receiver.registerBreakDown(partnerId, productId, quantity);
    } catch (NoSuchPartnerIdException e) {
      throw new UnknownPartnerKeyException(e.getId());
    } catch (NoSuchProductIdException e) {
      throw new UnknownProductKeyException(e.getId());
    } catch (NoAvailableQuantityException e) {
      throw new UnavailableProductException(e.getId(), e.getQuantity(), e.getQuantityAvailable());
    }
  }

}
