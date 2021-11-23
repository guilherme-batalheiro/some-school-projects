package ggc.app.partners;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import ggc.core.WarehouseManager;
import ggc.core.exception.NoSuchPartnerIdException;
import ggc.app.exception.UnknownPartnerKeyException;

/**
 * Show all transactions for a specific partner.
 */
class DoShowPartnerSales extends Command<WarehouseManager> {

  DoShowPartnerSales(WarehouseManager receiver) {
    super(Label.SHOW_PARTNER_SALES, receiver);
    addStringField("partnerId", Message.requestPartnerKey());
  }

  @Override
  public void execute() throws CommandException {
    String partnerId = stringField("partnerId");

    try {
      _display.popup(_receiver.getSalesByPartner(partnerId));
    } catch (NoSuchPartnerIdException e) {
      throw new UnknownPartnerKeyException(e.getId());
    }
  }

}
