package ggc.core.exception;

/** Exception thrown when the requested product does not exist. */
public class NoSuchProductIdException extends Exception {
  
  /** Serial number for serialization. */
  private static final long serialVersionUID = 201799901012L;

  /** Product id. */
  private String _id;
  
  /**
   * @param id
   */
  public NoSuchProductIdException(String id) {
    super("O produto com o id " + id + " não foi encontrado");
    _id = id;
  }

  /** @return id */
  public String getId() {
    return _id;
  }
}