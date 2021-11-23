package ggc.core.exception;

/** Exception thrown when a partner id already exits. */
public class NonUniquePartnerIdException extends Exception {
  
  /** Serial number for serialization. */
  private static final long serialVersionUID = 201772301210L;

  /** Partner id. */
  private String _id;

  /**
   * @param id;
   */
  public NonUniquePartnerIdException (String id) {
    super("O parceiro com o id " + id + " já existe");
    _id = id;
  }
  
  /** @return name */
  public String getId() {
    return _id;
  }
}