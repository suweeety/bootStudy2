package org.zerock.b01.service;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.zerock.b01.dto.*;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@Log4j2
public class BoardServiceTests {

    @Autowired
    private BoardService boardService;

    @Test
    public void testRegister() {

        log.info(boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("Sample Title...")
                .content("Sample Content...")
                .writer("user00")
                .build();

        Long bno = boardService.register(boardDTO);

        log.info("bno: " + bno);
    }

// 642 제외   @Test
//    public void testModify() {
//
//        //변경에 필요한 데이터만
//        BoardDTO boardDTO = BoardDTO.builder()
//                .bno(101L)
//                .title("Updated....101")
//                .content("Updated content 101...")
//                .build();
//
//        boardService.modify(boardDTO);
//
//    }
//
//    @Test
//    public void testList() {
//
//        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
//                .type("tcw")
//                .keyword("1")
//                .page(1)
//                .size(10)
//                .build();
//
//        PageResponseDTO<BoardDTO> responseDTO = boardService.list(pageRequestDTO);
//
//        log.info(responseDTO);
//
//    }




    @Test //642 추가
    public void testRegisterWithImages() {

        log.info(boardService.getClass().getName());

        BoardDTO boardDTO = BoardDTO.builder()
                .title("File...Sample Title...")
                .content("Sample Content...")
                .writer("user00")
                .build();

        boardDTO.setFileNames(
                Arrays.asList(
                        UUID.randomUUID()+"_aaa.jpg",
                        UUID.randomUUID()+"_bbb.jpg",
                        UUID.randomUUID()+"_bbb.jpg"
                ));

        Long bno = boardService.register(boardDTO);

        log.info("bno: " + bno);

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
        //2024-04-15T13:18:58.020+09:00  INFO 7368 --- [    Test worker] o.zerock.b01.service.BoardServiceTests   : bno: 101
    }

    @Test
    public void testReadAll() {

        Long bno = 107L;
        //Long bno = 204L;

        BoardDTO boardDTO = boardService.readOne(bno);

        log.info(boardDTO);

        for (String fileName : boardDTO.getFileNames()) {
            log.info(fileName);
        }//end for

        //Hibernate:
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
        //    left join
        //        board_image i1_0
        //            on b1_0.bno=i1_0.board_bno
        //    where
        //        b1_0.bno=?
        //2024-04-15T13:34:13.421+09:00  INFO 8704 --- [    Test worker] o.zerock.b01.service.BoardServiceTests   : BoardDTO(bno=101, title=File...Sample Title..., content=Sample Content..., writer=user00, regDate=2024-04-15T13:18:57.832353, modDate=2024-04-15T13:18:57.832353, fileNames=[01d5dbe8-a9f2-4d5f-8e0f-49a9a8cb4091_aaa.jpg, 5a8c2bc6-7f75-4bde-8ca6-9fb58650a5c4_bbb.jpg, 12e8db1f-c346-4730-891b-07346c526d0c_bbb.jpg])
        //2024-04-15T13:34:13.432+09:00  INFO 8704 --- [    Test worker] o.zerock.b01.service.BoardServiceTests   : 01d5dbe8-a9f2-4d5f-8e0f-49a9a8cb4091_aaa.jpg
        //2024-04-15T13:34:13.433+09:00  INFO 8704 --- [    Test worker] o.zerock.b01.service.BoardServiceTests   : 5a8c2bc6-7f75-4bde-8ca6-9fb58650a5c4_bbb.jpg
        //2024-04-15T13:34:13.433+09:00  INFO 8704 --- [    Test worker] o.zerock.b01.service.BoardServiceTests   : 12e8db1f-c346-4730-891b-07346c526d0c_bbb.jpg

    }

    @Test
    public void testModify() { // 646 추가

        //변경에 필요한 데이터
        BoardDTO boardDTO = BoardDTO.builder()
                .bno(101L)
                .title("Updated....101")
                .content("Updated content 101...")
                .build();

        //첨부파일을 하나 추가
        boardDTO.setFileNames(Arrays.asList(UUID.randomUUID()+"_zzz.jpg"));

        boardService.modify(boardDTO);

        //Hibernate:
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
        //Hibernate:
        //    select
        //        b1_0.uuid,
        //        b2_0.bno,
        //        b2_0.content,
        //        b2_0.moddate,
        //        b2_0.regdate,
        //        b2_0.title,
        //        b2_0.writer,
        //        b1_0.file_name,
        //        b1_0.ord
        //    from
        //        board_image b1_0
        //    left join
        //        board b2_0
        //            on b2_0.bno=b1_0.board_bno
        //    where
        //        b1_0.uuid=?
        //Hibernate:
        //    insert
        //    into
        //        board_image
        //        (board_bno,file_name,ord,uuid)
        //    values
        //        (?,?,?,?)
        //Hibernate:
        //    update
        //        board
        //    set
        //        content=?,
        //        moddate=?,
        //        title=?,
        //        writer=?
        //    where
        //        bno=?
        //Hibernate:
        //    delete
        //    from
        //        board_image
        //    where
        //        uuid=?
        //Hibernate:
        //    delete
        //    from
        //        board_image
        //    where
        //        uuid=?
        //Hibernate:
        //    delete
        //    from
        //        board_image
        //    where
        //        uuid=?

    }

    @Test
    public void testRemoveAll() {  // 647 추가

        Long bno = 1L;

        boardService.remove(bno);
        // Hibernate:
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
        //Hibernate:
        //    delete
        //    from
        //        board_image
        //    where
        //        uuid=?
        //Hibernate:
        //    delete
        //    from
        //        board_image
        //    where
        //        uuid=?
        //Hibernate:
        //    delete
        //    from
        //        board_image
        //    where
        //        uuid=?
        //Hibernate:
        //    delete
        //    from
        //        board
        //    where
        //        bno=?
    }

    @Test
    public void testListWithAll() {  // 649 추가

        PageRequestDTO pageRequestDTO = PageRequestDTO.builder()
                .page(1)
                .size(10)
                .build();

        PageResponseDTO<BoardListAllDTO> responseDTO =
                boardService.listWithAll(pageRequestDTO);

        List<BoardListAllDTO> dtoList = responseDTO.getDtoList();

        dtoList.forEach(boardListAllDTO -> {
            log.info(boardListAllDTO.getBno()+":"+boardListAllDTO.getTitle());

            if(boardListAllDTO.getBoardImages() != null) {
                for (BoardImageDTO boardImage : boardListAllDTO.getBoardImages()) {
                    log.info(boardImage);
                }
            }

            log.info("-------------------------------");
        });
        //101:Updated....101
        //BoardImageDTO(uuid=2f95e161-498b-4f16-9fa4-5bad779c0278, fileName=zzz.jpg, ord=0)
        //-------------------------------
        //100:Title..100
        //-------------------------------
        //99:Title..99
        //BoardImageDTO(uuid=82c2b889-995b-48b2-9892-954f150443d8, fileName=99file0.jpg, ord=0)
        //BoardImageDTO(uuid=da7b4beb-7cc2-421f-b6f5-16570603b972, fileName=99file1.jpg, ord=1)
        //BoardImageDTO(uuid=1a67960b-b5cc-4c6a-9667-a6cfd6a858c2, fileName=99file2.jpg, ord=2)
        //-------------------------------
        //98:Title..98
        //BoardImageDTO(uuid=34a0a7ad-a77b-426f-8a8d-6f28b925b505, fileName=98file0.jpg, ord=0)
    }


}
