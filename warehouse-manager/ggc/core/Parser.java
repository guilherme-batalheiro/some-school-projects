package ggc.core;

import java.io.IOException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.List;
import java.util.ArrayList;

import ggc.core.exception.BadEntryException;
import ggc.core.exception.NoSuchPartnerIdException;
import ggc.core.exception.NoSuchProductIdException;

public class Parser {

  private Warehouse _store;

  public Parser(Warehouse w) {
    _store = w;
  }

  void parseFile(String filename) throws IOException, BadEntryException {
    try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
      String line;

      while ((line = reader.readLine()) != null)
        parseLine(line);
    }
  }

  private void parseLine(String line) throws BadEntryException {
    String[] components = line.split("\\|");

    switch (components[0]) {
    case "PARTNER":
      parsePartner(components, line);
      break;
    case "BATCH_S":
      parseSimpleProduct(components, line);
      break;

    case "BATCH_M":
      parseAggregateProduct(components, line);
      break;

    default:
      throw new BadEntryException("Invalid type element: " + components[0]);
    }
  }

  // PARTNER|id|nome|endereço
  private void parsePartner(String[] components, String line) throws BadEntryException {
    if (components.length != 4)
      throw new BadEntryException("Invalid partner with wrong number of fields (4): " + line);

    String id = components[1];
    String name = components[2];
    String address = components[3];

    if (!_store.registerPartner(new Partner(id, name, address)))
      throw new BadEntryException("A partner with the id " + id + " already exits: " + line);
  }

  private void createAndAddBatch(String idProduct, String idPartner, Double price, int stock)
      throws NoSuchProductIdException, NoSuchPartnerIdException {
    Product product = _store.getProduct(idProduct);
    Partner partner = _store.getPartner(idPartner);
    Batch batch = new Batch(partner, product, price, stock);
    partner.addBatch(batch);
    product.addBatch(batch);
  }

  // BATCH_S|idProduto|idParceiro|preco|stock-actual
  private void parseSimpleProduct(String[] components, String line) throws BadEntryException {
    if (components.length != 5)
      throw new BadEntryException("Invalid number of fields (4) in simple batch description: " + line);

    String idProduct = components[1];
    String idPartner = components[2];
    double price = Double.parseDouble(components[3]);
    int stock = Integer.parseInt(components[4]);

    if (!_store.haveProductById(idProduct)) {
      _store.registerProduct(new SimpleProduct(idProduct));
    }

    try {
      createAndAddBatch(idProduct, idPartner, price, stock);
    } catch (NoSuchProductIdException | NoSuchPartnerIdException e) {
      throw new BadEntryException("Failed to add batch: " + line, e);
    }
  }

  // BATCH_M|idProduto|idParceiro|preco|stock-actual|agravamento|componente-1:quantidade-1#...#componente-n:quantidade-n
  private void parseAggregateProduct(String[] components, String line) throws BadEntryException {
    if (components.length != 7)
      throw new BadEntryException("Invalid number of fields (7) in aggregate batch description: " + line);

    String idProduct = components[1];
    String idPartner = components[2];
    double price = Double.parseDouble(components[3]);
    int stock = Integer.parseInt(components[4]);
    double alpha = Double.parseDouble(components[5]);

    if (!_store.haveProductById(idProduct)) {
      List<Component> componentsOfProduct = new ArrayList<Component>();

      for (String component : components[6].split("#")) {
        String[] recipeComponent = component.split(":");
        try {
          componentsOfProduct
              .add(new Component(_store.getProduct(recipeComponent[0]), Integer.parseInt(recipeComponent[1])));
        } catch (NoSuchProductIdException e) {
          throw new BadEntryException("Failed to add product: " + line, e);
        }
      }

      AggregateProduct product = new AggregateProduct(idProduct);
      Recipe recipe = new Recipe(alpha, product, componentsOfProduct);
      product.addRecipe(recipe);
      _store.registerProduct(product);
    }

    try {
      createAndAddBatch(idProduct, idPartner, price, stock);
    } catch (NoSuchProductIdException | NoSuchPartnerIdException e) {
      throw new BadEntryException("Failed to add batch: " + line, e);
    }
  }
}
