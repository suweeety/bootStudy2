package org.zerock.b01.repository;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.b01.domain.Board;
import org.zerock.b01.domain.Reply;

@SpringBootTest
@Log4j2
public class ReplyRepositoryTests {
    @Autowired
    private ReplyRepository replyRepository;

    @Test
    public void testInsert() {

        //실제 DB에 있는 bno
        Long bno  = 100L;

        Board board = Board.builder().bno(bno).build();

        Reply reply = Reply.builder()
                .board(board)
                .replyText("댓글.....")
                .replyer("replyer1")
                .build();

        replyRepository.save(reply);
        //insert
        //    into
        //        reply
        //        (board_bno,moddate,regdate,reply_text,replyer)
        //    values
        //        (?,?,?,?,?)

    }

    @Transactional  // 2번의 쿼리로 동작할 때 no Session 이 뜸
    @Test
    public void testBoardReplies() {

        Long bno = 100L;

        Pageable pageable = PageRequest.of(0,10, Sort.by("rno").descending());

        Page<Reply> result = replyRepository.listOfBoard(bno, pageable);

        result.getContent().forEach(reply -> {
            log.info(reply);
        });
        //Hibernate:
        //    select
        //        r1_0.rno,
        //        r1_0.board_bno,
        //        r1_0.moddate,
        //        r1_0.regdate,
        //        r1_0.reply_text,
        //        r1_0.replyer
        //    from
        //        reply r1_0
        //    where
        //        r1_0.board_bno=?
        //    order by
        //        r1_0.rno desc limit ?,
        //        ?
        //2024-04-12T13:18:20.255+09:00  INFO 8284 --- [    Test worker] o.z.b01.repository.ReplyRepositoryTests  : Reply(rno=3, replyText=댓글....., replyer=replyer1)
        //2024-04-12T13:18:20.257+09:00  INFO 8284 --- [    Test worker] o.z.b01.repository.ReplyRepositoryTests  : Reply(rno=2, replyText=댓글....., replyer=replyer1)
    }


}
