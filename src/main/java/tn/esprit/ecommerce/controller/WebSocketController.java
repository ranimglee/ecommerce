package tn.esprit.ecommerce.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;

@Controller
public class WebSocketController {

    @MessageMapping("/newProduct")
    @SendTo("/topic/products")
    public String notifyNewProduct(String productName) {
        return "New product added: " + productName;
    }
}