package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import ggc.core.WarehouseManager;
import ggc.core.exception.NoSuchTransactionIdException;
import ggc.app.exception.UnknownTransactionKeyException;

/**
 * Receive payment for sale transaction.
 */
public class DoReceivePayment extends Command<WarehouseManager> {

  public DoReceivePayment(WarehouseManager receiver) {
    super(Label.RECEIVE_PAYMENT, receiver);
    addIntegerField("transitionId", Message.requestTransactionKey());
  }

  @Override
  public final void execute() throws CommandException {
    int transactionId = integerField("transitionId");
     
    try{
      _receiver.receivePayment(transactionId);
    } catch(NoSuchTransactionIdException e) {
      throw new UnknownTransactionKeyException(e.getId());
    }
  }
}
