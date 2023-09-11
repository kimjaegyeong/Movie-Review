package com.example.mreview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.mreview.entity.Review;
public interface ReviewRepository extends JpaRepository<Review, Long> {

}
