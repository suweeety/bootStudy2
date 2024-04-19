package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.BoardImage;
import org.zerock.b01.dto.BoardListAllDTO;
import org.zerock.b01.dto.BoardListReplyCountDTO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

    @Autowired // 626 추가
    private ReplyRepository replyRepository;

    @Test
    public void testInsert() {
        IntStream.rangeClosed(1,100).forEach(i -> {
            Board board = Board.builder()
                    .title("title..." +i)
                    .content("content..." + i)
                    .writer("user"+ (i % 10))
                    .build();

            Board result = boardRepository.save(board);
            log.info("BNO: " + result.getBno());
        });
    }

    @Test
    public void testSelect() {
        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);
        // findById()을 리턴 타입은 Optional 참조하더라도 NPE가 발생하지 않도록 도와준다.
        // Optional 클래스는 아래와 같은 value에 값을 저장하기 때문에 값이 null이더라도 바로 NPE가 발생하지 않으며, 클래스이기 때문에 각종 메소드를 제공해준다.
        // NPE : 널포인트익셉션
        // Optional.empty() -> 널값인 경우,  Optional.of() -> 널값이 아닌 경우, Optional.ofNullbale() - 값이 Null일수도, 아닐수도 있는 경우

        Board board = result.orElseThrow();
        // Optional 객체의 유무를 판단하고 예외를 처리하기 위해 if문을 사용해왔습니다.
        // if문을 사용하면서 예외 처리 또는 값을 반환하다보니 코드의 가독성이 떨어졌습니다.
        // orElseThrow를 통해 Optional에서 원하는 객체를 바로 얻거나 예외를 던질 수 있습니다.

        log.info(board);

    }

    @Test
    public void testUpdate() {

        Long bno = 100L;

        Optional<Board> result = boardRepository.findById(bno);

        Board board = result.orElseThrow();

        board.change("update..title 100", "update content 100");

        boardRepository.save(board);

    }

    @Test
    public void testDelete() {
        Long bno = 1L;

        boardRepository.deleteById(bno);
    }

    @Test
    public void testPaging() {

        //1 page order by bno desc
        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.findAll(pageable);


        log.info("total count: "+result.getTotalElements());
        log.info( "total pages:" +result.getTotalPages());
        log.info("page number: "+result.getNumber());
        log.info("page size: "+result.getSize());

        List<Board> todoList = result.getContent();

        todoList.forEach(board -> log.info(board));


    }

    @Test
    public void testSearch1() {

        //2 page order by bno desc
        Pageable pageable = PageRequest.of(1,10, Sort.by("bno").descending());

        boardRepository.search1(pageable);

    }

    @Test
    public void testSearchAll() {

        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable );

    }

    @Test
    public void testSearchAll2() {

        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<Board> result = boardRepository.searchAll(types, keyword, pageable );

        //total pages
        log.info(result.getTotalPages());

        //pag size
        log.info(result.getSize());

        //pageNumber
        log.info(result.getNumber());

        //prev next
        log.info(result.hasPrevious() +": " + result.hasNext());

        result.getContent().forEach(board -> log.info(board));
    }

    // 댓글 카운트 처리 테스트 545
    @Test
    public void testSearchReplyCount() {

        String[] types = {"t","c","w"};

        String keyword = "1";

        Pageable pageable = PageRequest.of(0,10, Sort.by("bno").descending());

        Page<BoardListReplyCountDTO> result = boardRepository.searchWithReplyCount(types, keyword, pageable );

        //total pages
        log.info(result.getTotalPages());
        //pag size
        log.info(result.getSize());
        //pageNumber
        log.info(result.getNumber());
        //prev next
        log.info(result.hasPrevious() +": " + result.hasNext());

        result.getContent().forEach(board -> log.info(board));

        //Hibernate:
        //    select
        //        count(distinct b1_0.bno)
        //    from
        //        board b1_0
        //    left join
        //        reply r1_0
        //            on r1_0.board_bno=b1_0.bno
        //    where
        //        (
        //            b1_0.title like ? escape '!'
        //            or b1_0.content like ? escape '!'
        //            or b1_0.writer like ? escape '!'
        //        )
        //        and b1_0.bno>?
        //2024-04-12T14:25:03.556+09:00  INFO 7044 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : 2
        //2024-04-12T14:25:03.558+09:00  INFO 7044 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : 10
        //2024-04-12T14:25:03.558+09:00  INFO 7044 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : 0
        //2024-04-12T14:25:03.574+09:00  INFO 7044 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : false: true
        //2024-04-12T14:25:03.589+09:00  INFO 7044 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  :
        // BoardListReplyCountDTO(bno=100, title=title...10011, writer=user0, regDate=2024-04-09T17:38:21.909360, replyCount=2)
        //                                                                                                        100번 개시물에 댓글 수 체크
    }

    @Test
    public void testInsertWithImages() { // 619 테스트

        Board board = Board.builder()
                .title("Image Test")
                .content("첨부파일 테스트")
                .writer("tester")
                .build();

        for (int i = 0; i < 3; i++) {  //3개의 파일 저장

            board.addImage(UUID.randomUUID().toString(), "file"+i+".jpg");

        }//end for

        boardRepository.save(board);

        //Hibernate:
        //    insert
        //    into
        //        board
        //        (content,moddate,regdate,title,writer)
        //    values
        //        (?,?,?,?,?)
        //Hibernate:
        //    insert
        //    into
        //        board_image
        //        (board_bno,file_name,ord,uuid)
        //    values
        //        (?,?,?,?)
        //Hibernate:
        //    insert
        //    into
        //        board_image
        //        (board_bno,file_name,ord,uuid)
        //    values
        //        (?,?,?,?)
        //Hibernate:
        //    insert
        //    into
        //        board_image
        //        (board_bno,file_name,ord,uuid)
        //    values
        //        (?,?,?,?)
        
    }

    @Transactional
    @Test
    public void testReadWithImages() { // 620 테스트

        //반드시 존재하는 bno로 확인
        Optional<Board> result = boardRepository.findById(1L);

        Board board = result.orElseThrow();

        log.info(board);
        log.info("--------------------");
        log.info(board.getImageSet());
        //org.zerock.b01.domain.Board.imageSet: could not initialize proxy - no Session -> 트렌젝션 추가
        
        //Hibernate:  @Transactional 추가 후
        //    select
        //        b1_0.bno,
        //        b1_0.content,
        //        b1_0.moddate,
        //        b1_0.regdate,
        //        b1_0.title,
        //        b1_0.writer 
        //    from
        //        board b1_0 
        //    where
        //        b1_0.bno=?
        //2024-04-15T11:53:56.226+09:00  INFO 1408 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : Board(bno=1, title=Image Test, content=첨부파일 테스트, writer=tester)
        //2024-04-15T11:53:56.228+09:00  INFO 1408 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : --------------------
        //Hibernate: 
        //    select
        //        i1_0.board_bno,
        //        i1_0.uuid,
        //        i1_0.file_name,
        //        i1_0.ord 
        //    from
        //        board_image i1_0 
        //    where
        //        i1_0.board_bno=?
        //2024-04-15T11:53:56.279+09:00  INFO 1408 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : [BoardImage(uuid=d9d61e2f-1fcf-41b3-a1bc-f53c4e9fb604, fileName=file0.jpg, ord=0), BoardImage(uuid=fe7a5a0e-a9d9-4c4e-9c92-8065d248970a, fileName=file2.jpg, ord=2), BoardImage(uuid=1d549a10-ad78-4267-a390-26b38a3ccd1a, fileName=file1.jpg, ord=1)]
    }
    @Test
    public void testReadWithImagesEntityGraph() {

        //반드시 존재하는 bno로 확인
        Optional<Board> result = boardRepository.findByIdWithImages(1L);

        Board board = result.orElseThrow();

        log.info(board);
        log.info("--------------------");
        for (BoardImage boardImage : board.getImageSet()) {
            log.info(boardImage);
        }

        //Hibernate:  테스트 결과 조인 처리가 된 상태로 select가 실행 됨.
        //    select
        //        b1_0.bno,
        //        b1_0.content,
        //        i1_0.board_bno,
        //        i1_0.uuid,
        //        i1_0.file_name,
        //        i1_0.ord,
        //        b1_0.moddate,
        //        b1_0.regdate,
        //        b1_0.title,
        //        b1_0.writer
        //    from
        //        board b1_0
        //    left join  ------------조인 처리됨.----(@OneToMany구조의 장점)---------------------
        //        board_image i1_0
        //            on b1_0.bno=i1_0.board_bno
        //    where
        //        b1_0.bno=?
        //2024-04-15T11:59:54.713+09:00  INFO 9828 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : Board(bno=1, title=Image Test, content=첨부파일 테스트, writer=tester)
        //2024-04-15T11:59:54.725+09:00  INFO 9828 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : --------------------
        //2024-04-15T11:59:54.732+09:00  INFO 9828 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : BoardImage(uuid=d9d61e2f-1fcf-41b3-a1bc-f53c4e9fb604, fileName=file0.jpg, ord=0)
        //2024-04-15T11:59:54.732+09:00  INFO 9828 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : BoardImage(uuid=1d549a10-ad78-4267-a390-26b38a3ccd1a, fileName=file1.jpg, ord=1)
        //2024-04-15T11:59:54.732+09:00  INFO 9828 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : BoardImage(uuid=fe7a5a0e-a9d9-4c4e-9c92-8065d248970a, fileName=file2.jpg, ord=2)
    }
//
    @Transactional
    @Commit
    @Test  
    public void testModifyImages() { // 게시물 첨부파일 수정 테스트 : 실제 삭제가 안됨, orphanRemoval = true 추가(고아 객체 삭제용)
            //부모 엔티티와 연관관계가 끊어진 자식 엔티티를 가리킵니다.
            //부모가 제거될때, 부모와 연관되어있는 모든 자식 엔티티들은 고아객체가 됩니다.
            //부모 엔티티와 자식 엔티티 사이의 연관관계를 삭제할때, 해당 자식 엔티티는 고아객체가 됩니다.

        Optional<Board> result = boardRepository.findByIdWithImages(1L);

        Board board = result.orElseThrow();

        //기존의 첨부파일들은 삭제
        board.clearImages();

        //새로운 첨부파일들
        for (int i = 0; i < 2; i++) {

            board.addImage(UUID.randomUUID().toString(), "updatefile"+i+".jpg");
        }

        boardRepository.save(board);

    }

    @Test
    @Transactional
    @Commit
    public void testRemoveAll() {  // 1번 개시물 삭제시 댓글도 삭제 627

        Long bno = 1L;

        replyRepository.deleteByBoard_Bno(bno);

        boardRepository.deleteById(bno);

    }
//
    @Test
    public void testInsertAll() { // 627 100게시물, 3개의 파일 추가, 5의 배수는 첨부 없음

        for (int i = 1; i <= 100; i++) {

            Board board  = Board.builder()
                    .title("Title.."+i)
                    .content("Content.." + i)
                    .writer("writer.." + i)
                    .build();

            for (int j = 0; j < 3; j++) {

                if(i % 5 == 0){
                    continue;
                }
                board.addImage(UUID.randomUUID().toString(),i+"file"+j+".jpg");
            }
            boardRepository.save(board);

        }//end for
    }

    @Transactional
    @Test
    public void testSearchImageReplyCount() {

        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());

        // 636 제거 boardRepository.searchWithAll(null, null,pageable);

        Page<BoardListAllDTO> result = boardRepository.searchWithAll(null,null,pageable);

        log.info("---------------------------");
        log.info(result.getTotalElements());

        result.getContent().forEach(boardListAllDTO -> log.info(boardListAllDTO));

        //Hibernate:  이미지 제외한 처리 완료
        //    select
        //        b1_0.bno,
        //        b1_0.content,
        //        b1_0.moddate,
        //        b1_0.regdate,
        //        b1_0.title,
        //        b1_0.writer,
        //        count(distinct r1_0.rno) 
        //    from
        //        board b1_0 
        //    left join
        //        reply r1_0 
        //            on r1_0.board_bno=b1_0.bno 
        //    order by
        //        b1_0.bno desc limit ?,
        //        ?
        //Hibernate: 
        //    select
        //        count(b1_0.bno) 
        //    from
        //        board b1_0 
        //    left join
        //        reply r1_0 
        //            on r1_0.board_bno=b1_0.bno
        //2024-04-15T13:06:13.765+09:00  INFO 7376 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : ---------------------------
        //2024-04-15T13:06:13.767+09:00  INFO 7376 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : 100
        //2024-04-15T13:06:13.790+09:00  INFO 7376 --- [    Test worker] o.z.b01.repository.BoardRepositoryTests  : BoardListAllDTO(bno=1, title=Title..1, writer=writer..1, regDate=2024-04-15T12:12:10.097394, replyCount=0, boardImages=null)
    }



}
