package shopmate.productservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.stereotype.Repository;
import shopmate.productservice.models.Product;

import java.util.Optional;

@Repository
@RepositoryRestResource(path = "products")
public interface ProductRepository extends JpaRepository<Product, Integer> {
    public Product findByImageUrl(String imageUrl);
    public Optional<Product> findById(Integer productId);
}
