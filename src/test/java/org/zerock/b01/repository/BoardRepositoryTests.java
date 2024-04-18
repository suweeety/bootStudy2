package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.BoardListReplyCountDTO;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@SpringBootTest
@Log4j2
public class BoardRepositoryTests {

    @Autowired
    private BoardRepository boardRepository;

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

//    @Test
//    public void testInsertWithImages() {
//
//        Board board = Board.builder()
//                .title("Image Test")
//                .content("첨부파일 테스트")
//                .writer("tester")
//                .build();
//
//        for (int i = 0; i < 3; i++) {
//
//            board.addImage(UUID.randomUUID().toString(), "file"+i+".jpg");
//
//        }//end for
//
//        boardRepository.save(board);
//    }
//
//    //    @Test
////    public void testReadWithImages() {
////
////        //반드시 존재하는 bno로 확인
////        Optional<Board> result = boardRepository.findById(1L);
////
////        Board board = result.orElseThrow();
////
////        log.info(board);
////        log.info("--------------------");
////        log.info(board.getImageSet());
////    }
//    @Test
//    public void testReadWithImages() {
//
//        //반드시 존재하는 bno로 확인
//        Optional<Board> result = boardRepository.findByIdWithImages(1L);
//
//        Board board = result.orElseThrow();
//
//        log.info(board);
//        log.info("--------------------");
//        for (BoardImage boardImage : board.getImageSet()) {
//            log.info(boardImage);
//        }
//    }
//
//    @Transactional
//    @Commit
//    @Test
//    public void testModifyImages() {
//
//        Optional<Board> result = boardRepository.findByIdWithImages(1L);
//
//        Board board = result.orElseThrow();
//
//        //기존의 첨부파일들은 삭제
//        board.clearImages();
//
//        //새로운 첨부파일들
//        for (int i = 0; i < 2; i++) {
//
//            board.addImage(UUID.randomUUID().toString(), "updatefile"+i+".jpg");
//        }
//
//        boardRepository.save(board);
//
//    }
//
//    @Test
//    @Transactional
//    @Commit
//    public void testRemoveAll() {
//
//        Long bno = 1L;
//
//        replyRepository.deleteByBoard_Bno(bno);
//
//        boardRepository.deleteById(bno);
//
//    }
//
//    @Test
//    public void testInsertAll() {
//
//        for (int i = 1; i <= 100; i++) {
//
//            Board board  = Board.builder()
//                    .title("Title.."+i)
//                    .content("Content.." + i)
//                    .writer("writer.." + i)
//                    .build();
//
//            for (int j = 0; j < 3; j++) {
//
//                if(i % 5 == 0){
//                    continue;
//                }
//                board.addImage(UUID.randomUUID().toString(),i+"file"+j+".jpg");
//            }
//            boardRepository.save(board);
//
//        }//end for
//    }
//
//    @Transactional
//    @Test
//    public void testSearchImageReplyCount() {
//
//        Pageable pageable = PageRequest.of(0,10,Sort.by("bno").descending());
//
//        //boardRepository.searchWithAll(null, null,pageable);
//
//        Page<BoardListAllDTO> result = boardRepository.searchWithAll(null,null,pageable);
//
//        log.info("---------------------------");
//        log.info(result.getTotalElements());
//
//        result.getContent().forEach(boardListAllDTO -> log.info(boardListAllDTO));
//
//
//    }



}
