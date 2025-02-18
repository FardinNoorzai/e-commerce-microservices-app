package shopmate.productservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import shopmate.productservice.models.Review;
@Repository
@RepositoryRestResource(path = "reviews",collectionResourceRel = "reviews")
public interface ReviewRepository extends JpaRepository<Review, Integer> {
}
