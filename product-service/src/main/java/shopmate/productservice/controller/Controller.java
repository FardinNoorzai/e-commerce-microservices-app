package shopmate.productservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import shopmate.productservice.dto.ProductRequest;
import shopmate.productservice.models.Product;
import shopmate.productservice.services.ProductService;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class Controller {

    @Autowired
    ProductService productService;


    @PostMapping("api/products")
    public Product saveProduct(@RequestPart("product") ProductRequest productRequest,
                               @RequestPart("image") MultipartFile image) throws IOException {
        return productService.save(productRequest,image);
    }


    @GetMapping("/images/{imageName}")
    public ResponseEntity<FileSystemResource> getImage(@PathVariable String imageName) {
        return productService.getFile(imageName);
    }


}
