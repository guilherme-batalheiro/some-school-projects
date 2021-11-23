package ggc.core.exception;

public class DateNotValidException extends Exception {
  private int _days;

  public DateNotValidException(int days) {
    super("Data inválida! Tem de ser um número inteiro positivo");
    _days = days;
  }

  public int getDays(){
    return _days;
  }

}