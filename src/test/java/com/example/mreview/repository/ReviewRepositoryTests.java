package com.example.mreview.repository;

import com.example.mreview.entity.Member;
import com.example.mreview.entity.Movie;
import com.example.mreview.entity.Review;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.stream.IntStream;

@SpringBootTest
public class ReviewRepositoryTests {

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void insertMovieReviwes() {
        IntStream.rangeClosed(1, 200).forEach(i ->
        {
            Long mno = (long) (Math.random() * 100) + 1;
            Long mid = ((long) (Math.random() * 100) + 1);
            Member member = Member.builder().mid(mid).build();

            Review movieReview = Review.builder()
                    .member(member)
                    .movie(Movie.builder().mno(mno).build())
                    .grade((int) (Math.random() * 5) + 1)
                    .text("이 영화에 대한 느낌.." + i)
                    .build();
            reviewRepository.save(movieReview);
        });
    }

    @Test
    public void insertOneMovieReview() {
        Long mno = 100L;
        Long mid = 100L;
        Member member = new Member();
        member.setMid(mid);
        Movie movie = new Movie();
        movie.setMno(mno);

        Review review = new Review();
        review.setMovie(movie);
        review.setMember(member);
        review.setGrade(5);
        review.setText("리뷰수동저장");
        reviewRepository.save(review);
    }

    @Test
    public void testGetMovieReviews() {
        Movie movie = Movie.builder().mno(30L).build();
        List<Review> result = reviewRepository.findByMovie(movie);
        result.forEach(movieReview -> {
            System.out.print(movieReview.getReviewnum());
            System.out.print("\t" + movieReview.getGrade());
            System.out.print("\t" + movieReview.getText());
            System.out.print("\t" + movieReview.getMember().getEmail());
            System.out.println("=========================");
        });

    }
}
