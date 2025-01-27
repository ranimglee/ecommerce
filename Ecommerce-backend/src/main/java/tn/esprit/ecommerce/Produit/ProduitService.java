package tn.esprit.ecommerce.Produit;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RequiredArgsConstructor
@Service
public class ProduitService {

    private final ProduitRepository produitRepository;

    private final FileNamingUtil fileNamingUtil;

    @Value("${uploadProductImages}")
    private String uploadProductImages;


    public Produit addProduct(String name, String description, int nbrProduits,
                              double prix, MultipartFile imageFile,
                              Categorie categorie) {
        try {
            // Save the image to the server
            String fileName = fileNamingUtil.nameFile(imageFile);
            Path destinationPath = Paths.get(uploadProductImages, fileName);
            Files.copy(imageFile.getInputStream(), destinationPath);


            // Build the new product using the builder pattern
            Produit newProduct = Produit.builder()
                    .name(name)
                    .description(description)
                    .nbrProduits(nbrProduits)
                    .prix(prix)
                    .categorie(categorie)
                    .image(fileName)
                    .build();

            // Save the product to the database
            return produitRepository.save(newProduct);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }

}
