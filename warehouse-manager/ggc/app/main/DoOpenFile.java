package ggc.app.main;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import ggc.core.WarehouseManager;
import ggc.core.exception.UnavailableFileException;
import ggc.app.exception.FileOpenFailedException;

import java.io.IOException;

/**
 * Open existing saved state.
 */
class DoOpenFile extends Command<WarehouseManager> {

  /** @param receiver */
  DoOpenFile(WarehouseManager receiver) {
    super(Label.OPEN, receiver);
    addStringField("fileName", Message.openFile());
  }

  @Override
  public final void execute() throws CommandException {
    String fileName = stringField("fileName");
    
    try {
      _receiver.load(fileName);
    } catch (UnavailableFileException e) {
      throw new FileOpenFailedException(fileName);
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e){
      throw new FileOpenFailedException(fileName);
    }
  }
}
