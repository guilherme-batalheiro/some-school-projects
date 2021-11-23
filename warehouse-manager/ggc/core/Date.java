package ggc.core;

import ggc.core.exception.DateNotValidException;
import java.io.Serializable;

public class Date implements Serializable {
  private static Date _currentDate = new Date(0);
  private int _days;

  private static final long serialVersionUID = 202109192006L;

  static void setNewCurrentDate(Date newCurrentDate) {
    _currentDate = newCurrentDate;
  }

  Date(int days) {
    _days = days;
  }

  public int getDays() {
    return _days;
  }

  void add(int days) throws DateNotValidException {
    if (!(days > 0))
      throw new DateNotValidException(days);
    _days += days;
  }

  public int difference(Date other) {
    return _days - other.getDays();
  }

  public static Date now() {
    return _currentDate;
  }
}
