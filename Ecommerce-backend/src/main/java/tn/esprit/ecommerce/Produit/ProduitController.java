package tn.esprit.ecommerce.Produit;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping( "produit")
@RequiredArgsConstructor
public class ProduitController {

private final ProduitService produitService;

    @PostMapping("/addProduit")
    public ResponseEntity<Produit> addProduct(
            @RequestParam String nom,
            @RequestParam String description,
            @RequestParam int nbrProduits,
            @RequestParam double prix,
            @RequestParam MultipartFile imageFile,
            @RequestParam Categorie categorie) {

        Produit produit = produitService.addProduct(nom, description, nbrProduits, prix, imageFile, categorie);
        return ResponseEntity.ok(produit);
    }

}
