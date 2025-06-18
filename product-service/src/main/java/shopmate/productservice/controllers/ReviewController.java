package shopmate.productservice.controllers;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import shopmate.productservice.dto.ReviewRequest;
import shopmate.productservice.models.Product;
import shopmate.productservice.models.Review;
import shopmate.productservice.services.ProductService;
import shopmate.productservice.services.ReviewService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody ReviewRequest request, HttpServletRequest httpRequest) {
        String username = (String)httpRequest.getAttribute("username");
        Review review = new Review();
        review.setUsername(username);
        Product product = productService.findById(request.getProduct()).orElse(null);
        review.setProduct(product);
        review.setText(request.getText());
        review.setRating(request.getRating());
        return ResponseEntity.ok(reviewService.saveReview(review));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Review> getReview(@PathVariable Integer id) {
        return reviewService.getReviewById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<Review>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<Map<String,List<Review>>> getReviewsByProduct(@PathVariable Integer productId) {
        return ResponseEntity.ok(Map.of("reviews",reviewService.getReviewsByProductId(productId)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable Integer id) {
        reviewService.deleteReview(id);
        return ResponseEntity.noContent().build();
    }
}
