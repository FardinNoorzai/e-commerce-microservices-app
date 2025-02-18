package shopmate.productservice.services;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.rest.core.annotation.HandleBeforeCreate;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;
import shopmate.productservice.models.Review;

@RepositoryEventHandler
@Component
public class ReviewInterceptor {

    HttpServletRequest request;

    public ReviewInterceptor(HttpServletRequest request) {
        this.request = request;
    }
    @HandleBeforeCreate
    public void handleBeforeCreate(Review review) {
        String username = (String) request.getAttribute("username");
        System.out.println("username was added to the review " + username);
        review.setUsername(username);
    }
}
