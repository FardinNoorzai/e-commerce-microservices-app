package shopmate.productservice.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import shopmate.productservice.models.Product;

import java.util.List;

@RestController
@RequestMapping("api/products")
public class Controller {

    @GetMapping("/list")
    public String getProducts() {
        return "list of all products";
    }

}
