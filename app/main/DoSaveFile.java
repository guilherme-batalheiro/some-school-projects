package ggc.app.main;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import pt.tecnico.uilib.forms.Form;

import ggc.core.WarehouseManager;
import ggc.app.exception.FileOpenFailedException;
import ggc.core.exception.MissingFileAssociationException;

import java.io.IOException;

/**
 * Save current state to file under current name (if unnamed, query for name).
 */
class DoSaveFile extends Command<WarehouseManager> {

  /** @param receiver */
  DoSaveFile(WarehouseManager receiver) {
    super(Label.SAVE, receiver);
  }

  @Override
  public final void execute() throws CommandException {
    String fileName = null;
    Form f = new Form("askNameFile");
    f.addStringField("nameFile", Message.newSaveAs());

    try {
      if (!_receiver.haveFileName()) {
        f.parse();
        fileName = f.stringField("nameFile");
        _receiver.saveAs(fileName);
      } else {
        _receiver.save();
      }
    } catch (MissingFileAssociationException | IOException e) {
      throw new FileOpenFailedException(e.getMessage());
    }
  }
}
