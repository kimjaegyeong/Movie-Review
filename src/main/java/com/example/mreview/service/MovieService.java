package com.example.mreview.service;

import com.example.mreview.dto.MovieDTO;
import com.example.mreview.dto.MovieImageDTO;
import com.example.mreview.dto.PageRequestDTO;
import com.example.mreview.dto.PageResultDTO;
import com.example.mreview.entity.Movie;
import com.example.mreview.entity.MovieImage;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public interface MovieService {
    Long register(MovieDTO movieDTO);
    PageResultDTO<MovieDTO, Object[]> getList(PageRequestDTO pageRequestDTO);

    default Map<String, Object> dtoToEntity(MovieDTO movieDTO){
        //Map타입으로 반환
        Map<String, Object> entityMap= new HashMap<>();

        Movie movie = Movie.builder()
                .mno(movieDTO.getMno())
                .title(movieDTO.getTitle())
                .build(); //dto에서 빼낼 수 있는 정보는 빼서

        entityMap.put("movie",movie);  //hashMap으로 만듦.
        List<MovieImageDTO> imageDTOList = movieDTO.getImageDTOList(); //dto에서 imageList를 받음

        //MovieImageDTO 처리
        if(imageDTOList != null && imageDTOList.size() > 0){
            List<MovieImage> movieImageList = imageDTOList.stream().map(movieImageDTO -> {
                MovieImage movieImage = MovieImage.builder()
                        .path(movieImageDTO.getPath())
                        .imgName(movieImageDTO.getImgName())
                        .uuid(movieImageDTO.getUuid())
                        .movie(movie)
                        .build();
                return movieImage;
            }).collect(Collectors.toList());
            entityMap.put("imgList", movieImageList); //한 번에 두가지 종류의 객체를 반환하기 위해 Map타입 사용
            //hashMap 내용물 : "movie" 이름의 movie 엔티티 객체, imgList 이름의 movieImage 엔티티 리스트
        }
        return entityMap;
    }

    default MovieDTO entitiesToDTO(Movie movie, List<MovieImage> movieImages, Double avg, Long reviewCnt){
        MovieDTO movieDTO = MovieDTO.builder()
                .mno(movie.getMno())
                .title(movie.getTitle())
                .regDate(movie.getRegDate())
                .modDate(movie.getModDate())
                .build();

        List<MovieImageDTO> movieImageDTOList = movieImages.stream().map(movieImage ->
        {
            return MovieImageDTO.builder().imgName(movieImage.getImgName())
                    .path(movieImage.getPath())
                    .uuid(movieImage.getUuid())
                    .build();
        }).collect(Collectors.toList());
        movieDTO.setImageDTOList(movieImageDTOList);
        movieDTO.setAvg(avg);
        movieDTO.setReviewCnt(reviewCnt.intValue());
        return movieDTO;
    }


}
