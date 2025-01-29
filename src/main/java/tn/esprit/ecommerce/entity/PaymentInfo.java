package tn.esprit.ecommerce.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter

public class PaymentInfo {

    private int amount;
    private String cardNumber;
    private LocalDate expirationDate;
    private int ccv;
}