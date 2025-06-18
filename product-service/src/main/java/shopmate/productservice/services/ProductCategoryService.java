package shopmate.productservice.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shopmate.productservice.models.ProductCategory;
import shopmate.productservice.repositories.ProductCategoryRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductCategoryService {

    @Autowired
    private ProductCategoryRepository categoryRepository;

    public ProductCategory saveCategory(ProductCategory category) {
        return categoryRepository.save(category);
    }

    public Optional<ProductCategory> getCategoryById(Integer id) {
        return categoryRepository.findById(id);
    }

    public List<ProductCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    public ProductCategory updateCategory(Integer id, ProductCategory updatedCategory) {
        return categoryRepository.findById(id).map(category -> {
            category.setName(updatedCategory.getName());
            category.setDescription(updatedCategory.getDescription());
            return categoryRepository.save(category);
        }).orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public void deleteCategory(Integer id) {
        categoryRepository.deleteById(id);
    }
}
