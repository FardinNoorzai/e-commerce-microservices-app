package shopmate.productservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import shopmate.productservice.models.Product;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
@RepositoryRestResource(path = "products",collectionResourceRel = "products")
public interface ProductRepository extends JpaRepository<Product, Integer> {
    public Product findByImageUrl(String imageUrl);
    public Optional<Product> findById(Integer productId);
    public Product findByName(@Param("name") String name);
    public List<Product> findByNameContaining(@Param("name") String name);
    public Product findByBrand(@Param("name") String name);
    public List<Product> findByBrandContaining(@Param("name") String name);
    public Product findByPrice(@Param("price")BigDecimal price);
    public List<Product> findByPriceBetween(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice);
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :price - :tolerance AND :price + :tolerance")
    List<Product> findByPriceCloseTo(
            @Param("price") BigDecimal price,
            @Param("tolerance") BigDecimal tolerance
    );
    public List<Product> findByPriceBetweenAndName(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice,@Param("name") String name);
    public List<Product> findByPriceBetweenAndNameContaining(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice,@Param("name") String name);
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :price - :tolerance AND :price + :tolerance AND p.name = :name")
    List<Product> findByPriceAndNameCloseTo(
            @Param("price") BigDecimal price,
            @Param("tolerance") BigDecimal tolerance,
            @Param("name") String name
    );
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.productCategory.name = :categoryName")
    public List<Product> findByPriceBetweenAndProductCategory(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice,@Param("categoryName") String categoryName);
    @Query("SELECT p FROM Product p WHERE p.price BETWEEN :minPrice AND :maxPrice AND p.productCategory.name LIKE %:categoryName%")
    public List<Product> findByPriceBetweenAndProductCategoryContaining(@Param("minPrice") BigDecimal minPrice, @Param("maxPrice") BigDecimal maxPrice,@Param("categoryName") String categoryName);
}
