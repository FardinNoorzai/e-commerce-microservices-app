package shopmate.productservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import shopmate.productservice.models.ProductCategory;

@Repository
@RepositoryRestResource(path = "categories",collectionResourceRel = "categories")
public interface ProductCategoryRepository extends JpaRepository<ProductCategory,Integer> {
}
