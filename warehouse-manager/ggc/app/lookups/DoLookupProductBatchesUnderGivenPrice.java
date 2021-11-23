package ggc.app.lookups;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.core.WarehouseManager;

/**
 * Lookup products cheaper than a given price.
 */
public class DoLookupProductBatchesUnderGivenPrice extends Command<WarehouseManager> {

  public DoLookupProductBatchesUnderGivenPrice(WarehouseManager receiver) {
    super(Label.PRODUCTS_UNDER_PRICE, receiver);
    addIntegerField("priceLimit", Message.requestPriceLimit());
  }

  @Override
  public void execute() throws CommandException {
    double priceLimit = integerField("priceLimit");
    _display.popup(_receiver.getBatchesUnderGivenPrice(priceLimit));
  }

}
