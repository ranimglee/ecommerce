package tn.esprit.ecommerce.service;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tn.esprit.ecommerce.entity.PaymentInfo;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Value("${stripe.secret.key}")
    private String secretKey;

    PaymentIntent createPaymentIntent(double amount, String paymentMethodId) throws StripeException {
        // Set your secret key here (or inject from application.properties)
        Stripe.apiKey = secretKey;  // Replace with your actual secret key

        // Set parameters for the payment intent
        Map<String, Object> params = new HashMap<>();
        params.put("amount", (long) (amount * 100)); // Amount should be in cents
        params.put("currency", "usd");  // You can change this to your preferred currency
        params.put("payment_method", paymentMethodId);
        params.put("confirmation_method", "automatic");

        // Create the PaymentIntent with the parameters
        PaymentIntent paymentIntent = PaymentIntent.create(params);
        return paymentIntent;
    }
}
