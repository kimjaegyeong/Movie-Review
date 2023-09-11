package com.example.mreview.repository;

import com.example.mreview.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

interface MovieRepository extends JpaRepository<Movie, Long> {
    //coalesce 는 null 값을 설정한 값으로 변환해주는 함수. 해당 예제에서는 r.grade가 null이면 0이라는 값으로 치환한다.
//    @Query("select m, avg(coalesce(r.grade,0)), count(distinct r) from Movie m " +
//    "left outer join Review r on r.movie = m group by m")
//    Page<Object[]> getListPage(Pageable pageable);
    @Query("select m, max(mi), avg(coalesce(r.grade,0)), count(distinct r) from Movie m "+
    "left outer join MovieImage mi on mi.movie = m " +
    "left outer join Review r on r.movie = m group by m")
    Page<Object[]> getListPage(Pageable pageable);

}
