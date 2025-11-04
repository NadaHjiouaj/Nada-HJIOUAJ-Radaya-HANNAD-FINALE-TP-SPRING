package com.example.recommendationservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.recommendationservice.model.Recommendation;
import java.util.List;
public interface RecommendationRepository extends JpaRepository<Recommendation, Long>{
    List<Recommendation> findByProductId(Long productId);
}
