package com.example.mreview.repository;

import com.example.mreview.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

interface MovieRepository extends JpaRepository<Movie, Long> {
}
