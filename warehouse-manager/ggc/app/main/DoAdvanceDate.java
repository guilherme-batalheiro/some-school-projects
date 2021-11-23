package ggc.app.main;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import ggc.app.exception.InvalidDateException;
import ggc.core.WarehouseManager;
import ggc.core.exception.DateNotValidException;

/**
 * Advance current date.
 */
class DoAdvanceDate extends Command<WarehouseManager> {

  DoAdvanceDate(WarehouseManager receiver) {
    super(Label.ADVANCE_DATE, receiver);
    addIntegerField("daysToAdvance", Message.requestDaysToAdvance());
  }

  @Override
  public final void execute() throws CommandException {
    Integer number = integerField("daysToAdvance");

    try {
      _receiver.advanceDate(number);
    } catch (DateNotValidException e) {
      throw new InvalidDateException(e.getDays());
    }
  }
}
