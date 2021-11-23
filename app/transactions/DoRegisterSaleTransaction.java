package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import ggc.app.exception.UnknownPartnerKeyException;
import ggc.app.exception.UnknownProductKeyException;
import ggc.app.exception.UnavailableProductException;
import ggc.core.WarehouseManager;
import ggc.core.exception.NoSuchPartnerIdException;
import ggc.core.exception.NoSuchProductIdException;
import ggc.core.exception.NoAvailableQuantityException;

/**
 * 
 */
public class DoRegisterSaleTransaction extends Command<WarehouseManager> {

  public DoRegisterSaleTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_SALE_TRANSACTION, receiver);
    addStringField("partnerId", Message.requestPartnerKey());
    addIntegerField("limitDate", Message.requestPaymentDeadline());
    addStringField("productId", Message.requestProductKey());
    addIntegerField("quantity", Message.requestAmount());
  }

  @Override
  public final void execute() throws CommandException {
    String partnerId = stringField("partnerId");
    int limitDate = integerField("limitDate");
    String productId = stringField("productId");
    int quantity = integerField("quantity");

    try {
      _receiver.registerSale(partnerId, limitDate, productId, quantity);
    } catch (NoSuchPartnerIdException e) {
      throw new UnknownPartnerKeyException(e.getId());
    } catch (NoSuchProductIdException e) {
      throw new UnknownProductKeyException(e.getId());
    } catch (NoAvailableQuantityException e) {
      throw new UnavailableProductException(e.getId(), e.getQuantity(), e.getQuantityAvailable());
    }
  }

}
