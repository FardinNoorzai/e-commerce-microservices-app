package shopmate.productservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import shopmate.productservice.models.Product;

@Repository
@RepositoryRestResource()
public interface ProductRepository extends JpaRepository<Product, Integer> {
    public Product findByImageUrl(String imageUrl);
}
