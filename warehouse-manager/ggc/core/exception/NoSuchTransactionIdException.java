package ggc.core.exception;

/** Exception thrown when the requested transaction does not exist. */
public class NoSuchTransactionIdException extends Exception{

  /** Serial number for serialization. */
  private static final long serialVersionUID = 2017992343212L;

  /** Transaction id. */
  private int _id;
  
  /**
   * @param id
   */
  public NoSuchTransactionIdException(int id) {
    super("A transação com o id " + id + " não foi encontrado");
    _id = id;
  }

  /** @return id */
  public int getId() {
    return _id;
  }
}
