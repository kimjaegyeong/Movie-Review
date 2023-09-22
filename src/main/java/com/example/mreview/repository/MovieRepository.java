package com.example.mreview.repository;

import com.example.mreview.entity.Movie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long> {
    //coalesce 는 null 값을 설정한 값으로 변환해주는 함수. 해당 예제에서는 r.grade가 null이면 0이라는 값으로 치환한다.
//    @Query("select m, avg(coalesce(r.grade,0)), count(distinct r) from Movie m " +
//    "left outer join Review r on r.movie = m group by m")
//    Page<Object[]> getListPage(Pageable pageable);
// max(mi) 로 이미지 1개를 가져오면 N+1 문제가 발생한다. join 후에 또 다시 max(mi)를 찾기위해 쿼리가 n번 실행되기 때문이다.
    // 추측: max(mi)를 찾는 query = select mi.(mi의 pk값) from mi orber by asc limit 1; //페이징이 10이면 10번 수행됨
//    @Query("select m, max(mi), avg(coalesce(r.grade,0)), count(distinct r) from Movie m "+
//    "left outer join MovieImage mi on mi.movie = m " +
//    "left outer join Review r on r.movie = m group by m")
//    Page<Object[]> getListPage(Pageable pageable);

    @Query("select m, mi, avg(coalesce(r.grade,0)), count(distinct r) from Movie m "+
    "left outer join MovieImage mi on mi.movie = m " +
    "left outer join Review r on r.movie = m group by m")
    Page<Object[]> getListPage(Pageable pageable);

    //영화 이미지 중에서 가장 나중에 추가된 이미지를 가져오는 방법(성능은 조금 손해)
    @Query("select m,i, count(r) from Movie m left join MovieImage i on i.movie=m "+
        "and i.inum = (select max(i2.inum) from MovieImage i2 where i2.movie = m) "+
            "left outer join Review r on r.movie = m group by m")
    Page<Object[]> getListPage_latestImg(Pageable pageable);

//    @Query("select m, mi "+
//    "from Movie m left outer join MovieImage mi on mi.movie = m "+
//            "where m.mno = :mno")
//    List<Object[]> getMovieWithAll(Long mno);
//
    // 리뷰와 조인한 후에 count, avg 등 함수를 이용하게 되는데 이때 영화 이미지 별로 group by를 실행해야만 한다.
    //그래야 영화 이미지들의 개수만큼 데이터를 만들어낼 수 있다.
    @Query("select m, mi, avg(coalesce(r.grade,0)), count(distinct(r))" +
            "from Movie m left outer join MovieImage mi on mi.movie = m "+
            "left outer join Review r on r.movie =m "+
            "where m.mno=:mno group by mi")
    List<Object[]> getMovieWithAll(Long mno);

}
