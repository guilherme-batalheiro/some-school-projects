package ggc.app.products;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import ggc.core.WarehouseManager;
import ggc.core.exception.NoSuchProductIdException;
import ggc.app.exception.UnknownProductKeyException;

/**
 * Show all batches of one type of product
 */
class DoShowBatchesByProduct extends Command<WarehouseManager> {

  DoShowBatchesByProduct(WarehouseManager receiver) {
    super(Label.SHOW_BATCHES_BY_PRODUCT, receiver);
    addStringField("productId", Message.requestProductKey());
  }

  @Override
  public final void execute() throws CommandException {
    String productId = stringField("productId");
    try {
      _display.popup(_receiver.getBatchesByProduct(productId));
    } catch (NoSuchProductIdException e) {
      throw new UnknownProductKeyException(e.getId());
    }
  }

}
