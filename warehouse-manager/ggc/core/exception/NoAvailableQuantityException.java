package ggc.core.exception;

/** Exception thrown when the quantity of product does not exist. */
public class NoAvailableQuantityException extends Exception {
  /** Serial number for serialization. */
  private static final long serialVersionUID = 2017992343212L;

  /** Transaction id. */
  private int _quantity;
  private String _productId;
  private int _available;
  
  /**
   * @param id
   */
  public NoAvailableQuantityException(String productId, int quantity, int available) {
    super("Não existem " + quantity + " do produto " + productId + " disponiveis para realizar a operação");
    _quantity = quantity;
    _productId = productId;
    _available = available;
  }

  /** @return quantity */
  public int getQuantity() {
    return _quantity;
  }
  
  /** @return quantity */
  public int getQuantityAvailable() {
    return _available;
  }

  /** @return id */
  public String getId() {
    return _productId;
  }
}
