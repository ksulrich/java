// $Id: IllegalSizeException.java,v 1.3 2001/02/22 13:50:25 ku Exp $

package com.danet.exceptions;


public class IllegalSizeException extends RuntimeException {
  /**
   * Constructs an <code>IllegalSizeException</code> with no
   * detail message.
   */
  public IllegalSizeException() {
    super();
  }

  /**
   * Constructs an <code>IllegalSizeException</code> with the
   * specified detail message.
   *
   * @param   s   the detail message.
   */
  public IllegalSizeException(String s) {
    super(s);
  }
}
