package org.zerock.b01.domain;

import lombok.*;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;


@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "board")  // 객체로 사용해야 함.
public class BoardImage implements Comparable<BoardImage> { //Comparable<BoardImage> @OneToMany 처리에서 순번 정렬 처리용)
    // JPA에서 게시글을 중심으로 해석하는지, 첨부파일 중심으로 해석하는지에 따라서 다른 결과가 나옴
    // @ManyToOne을 이용하는 것은 게시물+댓글 관계임 -> 다른 엔티티 객체의 참조로 fk를 가지는 쪽에서 하는 방식
    // @OneToMany는 하나의 게시물은 많은 이미지를 가진다. 로 해석 -> pk를 가진 쪽에서 사용함.
    // 1. 상위 엔티티에서 하위 엔티티를 관리한다.
    // 2. JPA를 상위 엔티티 기준으로 생성한다. , 하위 엔티티에 대한 Repository의 생성이 잘못된 것은 아니지만 하위 엔티티들의 변경은 상위 엔티티에도 반영 되어야 함.
    // 3. 상위 엔티티 상태가 변경되면 하위 엔티티들의 상태들도 같이 처리
    // 4. 상위 엔티티 하나와 하위 엔티티 여러개를 처리하는 경우 N+1문제가 발생됨(주의)

    @Id
    private String uuid;

    private String fileName;

    private int ord;  // 이미지 순서(프론트에서 적용)

    @ManyToOne // 나는 많다 너는 1개다.
    private Board board;


    @Override
    public int compareTo(BoardImage other) {  // 2개의 값 비교용
        return this.ord - other.ord;
    }

    public void changeBoard(Board board){  // board 객체 삭제시 참조 변경용
        this.board = board;
    }

}
