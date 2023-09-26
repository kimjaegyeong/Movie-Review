package com.example.mreview.service;

import com.example.mreview.dto.MovieDTO;
import com.example.mreview.dto.PageRequestDTO;
import com.example.mreview.dto.PageResultDTO;
import com.example.mreview.entity.Movie;
import com.example.mreview.entity.MovieImage;
import com.example.mreview.repository.MovieImageRepository;
import com.example.mreview.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovieServiceImpl implements  MovieService{
    private final MovieRepository movieRepository;
    private final MovieImageRepository movieImageRepository;

    @Transactional
    @Override
    public Long register(MovieDTO movieDTO) {

        Map<String,Object> entityMap = dtoToEntity(movieDTO);
        Movie movie = (Movie) entityMap.get("movie");
        List<MovieImage> movieImageList = (List<MovieImage>) entityMap.get("imgList");

        movieRepository.save(movie);
        movieImageList.forEach(movieImage -> {
            movieImageRepository.save(movieImage);
        });
        return movie.getMno();
    }
    public PageResultDTO<MovieDTO, Object[]> getList(PageRequestDTO requestDTO){
        Pageable pageable= requestDTO.getPageable(Sort.by("mno").descending());
        Page<Object[]> result = movieRepository.getListPage(pageable);

        Function<Object[],MovieDTO> fn = ( arr->entitiesToDTO(
                (Movie) arr[0],
                (List<MovieImage>)(Arrays.asList((MovieImage)arr[1])), //Arrays.asList는 일반 배열을 ArrayList로 바꿔준다
                (Double) arr[2],
                (Long)arr[3])
         );
    return new PageResultDTO<>(result,fn);
    }

}
