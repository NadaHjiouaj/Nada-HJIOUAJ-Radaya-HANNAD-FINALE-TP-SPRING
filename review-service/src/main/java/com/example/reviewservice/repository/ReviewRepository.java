package com.example.reviewservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.reviewservice.model.Review;
import java.util.List;
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByProductId(Long productId);
}
