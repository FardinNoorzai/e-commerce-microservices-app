package shopmate.productservice.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopmate.productservice.dto.CategoryResponse;
import shopmate.productservice.models.ProductCategory;
import shopmate.productservice.services.ProductCategoryService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/products/categories")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService categoryService;

    @PostMapping
    public ResponseEntity<ProductCategory> createCategory(@RequestBody ProductCategory category) {
        return ResponseEntity.ok(categoryService.saveCategory(category));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String,CategoryResponse>> getCategory(@PathVariable Integer id) {
        Optional<ProductCategory> category = categoryService.getCategoryById(id);
        if(category.isPresent()) {
            ProductCategory productCategory = category.get();
            CategoryResponse categoryResponse = new CategoryResponse();
            categoryResponse.setId(productCategory.getId());
            categoryResponse.setName(productCategory.getName());
            categoryResponse.setDescription(productCategory.getDescription());
            categoryResponse.setProducts(productCategory.getProducts());
            return ResponseEntity.ok(Map.of("category", categoryResponse));
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductCategory> updateCategory(@PathVariable Integer id, @RequestBody ProductCategory category) {
        return ResponseEntity.ok(categoryService.updateCategory(id, category));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Integer id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
