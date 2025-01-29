package tn.esprit.ecommerce.exception;


public class EmailSendingException extends RuntimeException {

  // Default constructor
  public EmailSendingException(String message) {
    super(message);
  }

  // Constructor with message and cause
  public EmailSendingException(String message, Throwable cause) {
    super(message, cause);
  }
}
