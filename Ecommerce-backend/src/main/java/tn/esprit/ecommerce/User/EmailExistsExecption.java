package tn.esprit.ecommerce.User;

public class EmailExistsExecption extends RuntimeException{
    public EmailExistsExecption(String message){
        super(message);
    }
}