package com.example.mreview.controller;

import com.example.mreview.dto.ReviewDTO;
import com.example.mreview.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@Slf4j
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @GetMapping("/{mno}/all")
    public ResponseEntity<List<ReviewDTO>> getList(@PathVariable("mno") long mno){
        List<ReviewDTO> reviewDTOList = reviewService.getListOfMovie(mno);
        return new ResponseEntity<>(reviewDTOList, HttpStatus.OK);
    }

    @PostMapping("/{mno}")
    public ResponseEntity<Long> addReview(@RequestBody ReviewDTO movieReviewDTO){
        System.out.println(movieReviewDTO);
        Long reviewnum =  reviewService.register(movieReviewDTO);
        return new ResponseEntity<>(reviewnum, HttpStatus.OK);
    }

    @PutMapping("/{mno}/{reviewnum}")
    public ResponseEntity<Long> modifyReview(@PathVariable Long reviewnum , @RequestBody ReviewDTO movieReviewDTO){
        //RequestBody에 있는 ReviewDTO를 가져오겠다는 뜻.  putMapping은 form 객체의 데이터를 가져올 테니까!
        reviewService.modify(movieReviewDTO);
        return new ResponseEntity<>(reviewnum, HttpStatus.OK);
    }

    @DeleteMapping("/{mno}/{reviewnum}")
    public ResponseEntity<Long> removeReview(@PathVariable Long reviewnum ){
        reviewService.remove(reviewnum);
        return new ResponseEntity<>(reviewnum, HttpStatus.OK);
    }

}
