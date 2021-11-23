package ggc.app.transactions;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import pt.tecnico.uilib.forms.Form;

import java.util.ArrayList;

import ggc.app.exception.UnknownPartnerKeyException;
import ggc.app.exception.UnknownProductKeyException;
import ggc.core.WarehouseManager;
import ggc.core.exception.NoSuchPartnerIdException;
import ggc.core.exception.NoSuchProductIdException;

/**
 * Register order.
 */
public class DoRegisterAcquisitionTransaction extends Command<WarehouseManager> {

  public DoRegisterAcquisitionTransaction(WarehouseManager receiver) {
    super(Label.REGISTER_ACQUISITION_TRANSACTION, receiver);
    addStringField("partnerId", Message.requestPartnerKey());
    addStringField("productId", Message.requestProductKey());
    addRealField("price", Message.requestPrice());
    addIntegerField("quantity", Message.requestAmount());
  }

  @Override
  public final void execute() throws CommandException {
    Form recipeForm = new Form("recipe");
    recipeForm.addBooleanField("addRecipe", Message.requestAddRecipe());
    Form recipeInformationForm = new Form("recipeInformation");
    recipeInformationForm.addIntegerField("numberOfComponents", Message.requestNumberOfComponents());
    recipeInformationForm.addRealField("alpha", Message.requestAlpha());
    Form componentForm = new Form("componentInformation");
    componentForm.addStringField("componentId", Message.requestProductKey());
    componentForm.addIntegerField("componentAmount", Message.requestAmount());

    String partnerId = stringField("partnerId");
    String productId = stringField("productId");
    Double productPrice = realField("price");
    int quantity = integerField("quantity");

    try {
      if (!_receiver.haveProductById(productId)) {
        recipeForm.parse();
        if (!recipeForm.booleanField("addRecipe"))
          _receiver.registerSimpleProduct(productId);
        else {
          ArrayList<String> componentsIds = new ArrayList<>();
          ArrayList<Integer> componentsAmounts = new ArrayList<>();

          recipeInformationForm.parse();
          for (int i = 0; i < recipeInformationForm.integerField("numberOfComponents"); i++) {
            componentForm.parse();
            componentsIds.add(componentForm.stringField("componentId"));
            componentsAmounts.add(componentForm.integerField("componentAmount"));
          }
          _receiver.registerAggregateProduct(productId, recipeInformationForm.integerField("numberOfComponents"),
              recipeInformationForm.realField("alpha"), componentsIds, componentsAmounts);
        }
      }
      _receiver.registerPurchase(partnerId, productId, productPrice, quantity);
    } catch (NoSuchPartnerIdException e) {
      throw new UnknownPartnerKeyException(e.getId());
    } catch (NoSuchProductIdException e) {
      throw new UnknownProductKeyException(e.getId());
    }
  }
}
