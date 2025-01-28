package tn.esprit.ecommerce.exception;


// TODO: attention to spelling mistakes
public class EmailExistsExecption extends RuntimeException{
    public EmailExistsExecption(String message){
        super(message);
    }
}