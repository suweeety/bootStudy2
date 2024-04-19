package org.zerock.b01.repository;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.zerock.b01.domain.Board;
import org.zerock.b01.repository.search.BoardSearch;

import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long>, BoardSearch {

    // 하위 이미지 엔티티를 로딩하는 가장 간단한 방법은 eager(즉시) 로딩을 이용하면 되지만
    // @EntityGraph를 이용하여 연관관계가 있는 엔티티를 조회할 경우 지연 로딩으로 설정되어 있으면 연관관계에서 종속된 엔티티는
    // 쿼리 실행 시 select 되지 않고 proxy 객체를 만들어 엔티티가 적용시킨다.
    // 그 후 해당 프락시 객체를 호출할 때마다 그때그때 select 쿼리가 실행된다.
    @EntityGraph(attributePaths = {"imageSet"}) // attributePaths 같이 로딩해야 하는 속성을 명시함.
    @Query("select b from Board b where b.bno =:bno")
    Optional<Board> findByIdWithImages(Long bno); // bno를 이용해 이미지를 찾아옴.

}
