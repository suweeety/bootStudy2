package org.zerock.b01.domain;

import lombok.*;

import jakarta.persistence.*;
import org.hibernate.annotations.BatchSize;

import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "imageSet") // 파일 용 객체
public class Board extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bno;

    @Column(length = 500, nullable = false) //컬럼의 길이와 null허용여부
    private String title;

    @Column(length = 2000, nullable = false)
    private String content;

    @Column(length = 50, nullable = false)
    private String writer;


    public void change(String title, String content) {
        this.title = title;
        this.content = content;
    }

    //    @OneToMany(mappedBy = "board",
//            cascade = {CascadeType.ALL},
//            fetch = FetchType.LAZY,
//            orphanRemoval = true)
    @OneToMany(mappedBy = "board", // BoardImage의 board 변수
                cascade = {CascadeType.ALL}, // 영속성 전이 처리(부모삭제시 자식 제거)
                        //CascadeType.ALL: 모든 Cascade를 적용
                        //CascadeType.PERSIST: 엔티티를 영속화할 때, 연관된 엔티티도 함께 유지
                        //CascadeType.MERGE: 엔티티 상태를 병합(Merge)할 때, 연관된 엔티티도 모두 병합
                        //CascadeType.REMOVE: 엔티티를 제거할 때, 연관된 엔티티도 모두 제거
                        //CascadeType.DETACH: 부모 엔티티를 detach() 수행하면, 연관 엔티티도 detach()상태가 되어 변경 사항 반영 X
                        //CascadeType.REFRESH: 상위 엔티티를 새로고침(Refresh)할 때, 연관된 엔티티도 모두 새로고침
                fetch = FetchType.LAZY, //지연로딩
                orphanRemoval = true // 실제 파일 삭제용 추가(고아 객체 삭제)
    )
    //기존  db를 drop 하고 실행하면 테이블 재성됨.
    @Builder.Default
    @BatchSize(size = 20)  // BoardImage를 조회할 때 한번에 20개만 처리 632 추가
    private Set<BoardImage> imageSet = new HashSet<>();  // 연관관계설정

    public void addImage(String uuid, String fileName) {  

        BoardImage boardImage = BoardImage.builder()
                .uuid(uuid)
                .fileName(fileName)
                .board(this)
                .ord(imageSet.size())
                .build();
        imageSet.add(boardImage);

//        Hibernate: @OneToMany만 설정시 관계용 테이블이 생성됨.
//        create table board (
//                bno bigint not null auto_increment,
//                moddate datetime(6),
//                regdate datetime(6),
//                content varchar(2000) not null,
//                title varchar(500) not null,
//                writer varchar(50) not null,
//                primary key (bno)
//    ) engine=InnoDB
//        Hibernate:
//        create table board_image_set (   ----------연관관계용 테이블 ------------------
//                board_bno bigint not null,
//                image_set_uuid varchar(255) not null,
//                primary key (board_bno, image_set_uuid)
//    ) engine=InnoDB
//        Hibernate:
//        create table board_image (
//                uuid varchar(255) not null,
//                file_name varchar(255),
//                ord integer not null,
//                board_bno bigint,
//                primary key (uuid)
//    ) engine=InnoDB
//        Hibernate:
//        create table reply (
//                rno bigint not null auto_increment,
//                moddate datetime(6),
//                regdate datetime(6),
//                reply_text varchar(255),
//                replyer varchar(255),
//                board_bno bigint,
//                primary key (rno)
//    ) engine=InnoDB
//        Hibernate:
//        alter table if exists board_image_set
//        drop index if exists UK_mwoejkkvwfy86igld28gfl0lu
//        Hibernate:
//        alter table if exists board_image_set
//        add constraint UK_mwoejkkvwfy86igld28gfl0lu unique (image_set_uuid)
//                Hibernate:
//        create index idx_reply_board_bno
//        on reply (board_bno)
//                Hibernate:
//        alter table if exists board_image_set
//        add constraint FKrbo9mc1849iaq7bvrn5k6hr00
//        foreign key (image_set_uuid)
//                references board_image (uuid)
//        Hibernate:
//        alter table if exists board_image_set
//        add constraint FKm1w00fu9pb2gphxlayngvfjfc
//        foreign key (board_bno)
//                references board (bno)
//        Hibernate:
//        alter table if exists board_image
//        add constraint FKo4dbcmbib7vwlk8eplv2cwbe2
//        foreign key (board_bno)
//                references board (bno)
//        Hibernate:
//        alter table if exists reply
//        add constraint FKr1bmblqir7dalmh47ngwo7mcs
//        foreign key (board_bno)
//                references board (bno)

        // Hibernate:   @OneToMany(mappedBy = "board") // BoardImage의 board 변수 설정시 테이블이 아닌 관계가 설정됨.
        //    create table board (
        //        bno bigint not null auto_increment,
        //        moddate datetime(6),
        //        regdate datetime(6),
        //        content varchar(2000) not null,
        //        title varchar(500) not null,
        //        writer varchar(50) not null,
        //        primary key (bno)
        //    ) engine=InnoDB
        //Hibernate:
        //    create table board_image (
        //        uuid varchar(255) not null,
        //        file_name varchar(255),
        //        ord integer not null,
        //        board_bno bigint,   ------테이블 대신 필드가 처리함.-------
        //        primary key (uuid)
        //    ) engine=InnoDB
        //Hibernate:
        //    create table reply (
        //        rno bigint not null auto_increment,
        //        moddate datetime(6),
        //        regdate datetime(6),
        //        reply_text varchar(255),
        //        replyer varchar(255),
        //        board_bno bigint,
        //        primary key (rno)
        //    ) engine=InnoDB
        //Hibernate:
        //    create index idx_reply_board_bno
        //       on reply (board_bno)
        //Hibernate:
        //    alter table if exists board_image
        //       add constraint FKo4dbcmbib7vwlk8eplv2cwbe2
        //       foreign key (board_bno)
        //       references board (bno)
        //Hibernate:
        //    alter table if exists reply
        //       add constraint FKr1bmblqir7dalmh47ngwo7mcs
        //       foreign key (board_bno)
        //       references board (bno)
    }

    public void clearImages() {  // 618 추가

        imageSet.forEach(boardImage -> boardImage.changeBoard(null));

        this.imageSet.clear();


    }
}
