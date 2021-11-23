package ggc.core.exception;

/** Exception thrown when the requested partner does not exist. */
public class NoSuchPartnerIdException extends Exception {
  
  /** Serial number for serialization. */
  private static final long serialVersionUID = 201778301010L;

  /** Partner id. */
  private String _id;
  
  /**
   * @param id
   */
  public NoSuchPartnerIdException(String id) {
    super("O parceiro com o id " + id + " não foi encontrado");
    _id = id;
  }

  /** @return id */
  public String getId() {
    return _id;
  }
}