package tn.esprit.ecommerce.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import tn.esprit.ecommerce.enums.OrderStatus;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Document(collection = "orders")
public class Order {
    @Id
    private String id;

    private LocalDate date;

    private Double total;

    private OrderStatus status;

    @DBRef
    private User user;

    @DBRef
    private List<OrderLine> orderLines;


    /**
     * Calculates the total price for the order based on the order lines.
     */
    public void calculateTotal() {
        if (orderLines != null) {
            this.total = orderLines.stream()
                    .mapToDouble(OrderLine::getSousTotal)
                    .sum();
        }
    }
}
