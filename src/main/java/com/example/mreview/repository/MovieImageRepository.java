package com.example.mreview.repository;

import com.example.mreview.entity.MovieImage;
import org.springframework.data.jpa.repository.JpaRepository;

interface MovieImageRepository extends JpaRepository<MovieImage, Long> {

}
