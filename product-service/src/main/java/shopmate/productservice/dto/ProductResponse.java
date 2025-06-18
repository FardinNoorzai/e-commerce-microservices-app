package shopmate.productservice.dto;

import java.util.Map;

public class ProductResponse {
    Map<String, Object> products;

    public Map<String, Object> getProducts() {
        return products;
    }

    public void setProducts(Map<String, Object> products) {
        this.products = products;
    }
}
