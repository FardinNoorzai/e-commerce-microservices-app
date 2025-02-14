package shopmate.productservice.models;

public class Product {
    String id;
    String name;
    String description;

    public Product(String id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
}
