package ggc.core.exception;

/** Exception thrown when a partner id already exits. */
public class NonUniqueProductIdException extends Exception {
  
  /** Serial number for serialization. */
  private static final long serialVersionUID = 291702503210L;

  /** Partner id. */
  private String _id;

  /** @param id 
      @param maxPrice */
  public NonUniqueProductIdException(String id) {
    super("O produto com o id " + id + " já existe");
    _id = id;
  }
  
  /** @return name */
  public String getId() {
    return _id;
  }
}