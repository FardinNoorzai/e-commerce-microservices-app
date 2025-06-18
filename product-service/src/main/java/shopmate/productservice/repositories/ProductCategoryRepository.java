package shopmate.productservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import shopmate.productservice.models.ProductCategory;

@Repository
public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Integer> {
}
