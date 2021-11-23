package ggc.app.main;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import ggc.core.WarehouseManager;

/**
 * Show global balance.
 */
class DoShowGlobalBalance extends Command<WarehouseManager> {

  DoShowGlobalBalance(WarehouseManager receiver) {
    super(Label.SHOW_BALANCE, receiver);
  }

  @Override
  public final void execute() throws CommandException {
    Double availableBalance = _receiver.getAvailableBalance();
    Double accountingBalance = _receiver.getAccountingBalance();

    _display.addLine("" + Message.currentBalance(availableBalance, accountingBalance));
    _display.display();
  }
}
