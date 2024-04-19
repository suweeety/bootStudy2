package org.zerock.b01.repository.search;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.zerock.b01.domain.Board;
import org.zerock.b01.dto.BoardListAllDTO;
import org.zerock.b01.dto.BoardListReplyCountDTO;

public interface BoardSearch {

    Page<Board> search1(Pageable pageable);

    Page<Board> searchAll(String[] types, String keyword, Pageable pageable);

    //댓글 개수 처리용 리스트 (Querydsl용)
    Page<BoardListReplyCountDTO> searchWithReplyCount(String[] types,  // 검색조건
                                                      String keyword,   // 검색어
                                                      Pageable pageable); // 페이징

//    Page<BoardListReplyCountDTO> searchWithAll(String[] types, // 634 교체 BoardListAllDTO
//                                        String keyword,
//                                        Pageable pageable);  // 628 추가


    Page<BoardListAllDTO> searchWithAll(String[] types,
                                            String keyword,
                                            Pageable pageable);  // 628 추가
}
