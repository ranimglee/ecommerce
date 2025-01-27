package tn.esprit.ecommerce.Produit;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "produits")
@Builder
public class Produit {

    @Id
    private String id;

    private String name;
    private String description;
    private double prix;
    private Categorie categorie;
    private int nbrProduits;
    private String image ;



}
