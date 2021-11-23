package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import ggc.app.exception.UnknownTransactionKeyException;
import ggc.core.Transaction;
import ggc.core.WarehouseManager;
import ggc.core.exception.NoSuchTransactionIdException;

/**
 * Show specific transaction.
 */
public class DoShowTransaction extends Command<WarehouseManager> {

  public DoShowTransaction(WarehouseManager receiver) {
    super(Label.SHOW_TRANSACTION, receiver);
    addIntegerField("transitionId", Message.requestTransactionKey());
  }

  @Override
  public final void execute() throws CommandException {
    int transactionId = integerField("transitionId");
    try {
      Transaction transaction = _receiver.getTransaction(transactionId);
      _display.popup(transaction);
    } catch (NoSuchTransactionIdException e) {
      throw new UnknownTransactionKeyException(transactionId);
    }
  }
}
