package org.zerock.b01.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardListReplyCountDTO {
    // 게시글과 댓들의 관계 처럼 엔티티가 조금씩 확장되면 가장 문제가 생기는 부분이 list 페이지 임
    // 댓들의 개수를 추가하는 객체
    private Long bno;

    private String title;

    private String writer;

    private LocalDateTime regDate;

    private Long replyCount;  // 댓글 수 출력용

}
