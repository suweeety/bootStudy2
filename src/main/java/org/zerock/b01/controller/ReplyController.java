package org.zerock.b01.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.PageRequestDTO;
import org.zerock.b01.dto.PageResponseDTO;
import org.zerock.b01.dto.ReplyDTO;
import org.zerock.b01.service.ReplyService;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/replies")
@Log4j2
@RequiredArgsConstructor // 의존성 주입 (초기화 되지않은 final 필드나, @NonNull 이 붙은 필드에 대해 생성자를 생성해 줍니다.)
// 새로운 필드를 추가할 때 다시 생성자를 만들어서 관리해야하는 번거로움을 없애준다. (@Autowired를 사용하지 않고 의존성 주입)
// @RequiredArgsConstructor어노테이션은 클래스에 선언된 final 변수들, 필드들을 매개변수로 하는 생성자를 자동으로 생성해주는 어노테이션입니다.
public class ReplyController {

    private final ReplyService replyService; // @RequiredArgsConstructor 생성자 자동 주입

    // 구형 swagger @ApiOperation(value = "Replies POST", notes = "POST 방식으로 댓글 등록")
    @Operation(summary = "Replies POST") // @ApiOperation 대신 사용
    @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
    // p525 변경 public ResponseEntity<Map<String,Long>> register(@RequestBody ReplyDTO replyDTO){
    public Map<String,Long> register(@Valid @RequestBody ReplyDTO replyDTO, BindingResult bindingResult) throws BindException {
        // @Valid 과정에서 문제가 발생하면 처리하도록 @RestControllerAdvice를 설계 함.
        // BindingResult를 파라미터로 추가하고 문제 있을 때는 BindException으로 throw
        // 메서드 리턴값에 문제가 있다면 @RestControllerAdvice가 처리

        log.info(replyDTO);

        if(bindingResult.hasErrors()){
            throw new BindException(bindingResult);
        }

        Map<String, Long> resultMap = new HashMap<>();

        Long rno = replyService.register(replyDTO);

        resultMap.put("rno",rno);
// 556제거   resultMap.put("rno",111L);

        return resultMap;
        //curl -X 'POST' \
        //  'http://localhost:8080/replies/' \
        //  -H 'accept: */*' \
        //  -H 'Content-Type: application/json' \
        //  -d '{
        //  "bno": 98,
        //  "replyText": "컨트롤러 테스트 댓글",
        //  "replyer": "replyer",
        //  "rno":0
        //}'

        //결과
        //{
        //  "rno": 5
        //}
    }

    @Operation(summary = "Replies of Board")
    @GetMapping(value = "/list/{bno}")  // 경로에 있는 값을 취해서 사용 -> 페이지 관련 정보는 일반 쿼리스트링으로 사용
    public PageResponseDTO<ReplyDTO> getList(@PathVariable("bno") Long bno,
                                             PageRequestDTO pageRequestDTO){
        // @PathVariable("bno") 호출하는 경로의 값을 직접 파라미터 변수로 처리
        PageResponseDTO<ReplyDTO> responseDTO = replyService.getListOfBoard(bno, pageRequestDTO);

        return responseDTO;
    }

    @Operation(summary = "GET 방식으로 특정 댓글 조회")
    @GetMapping("/{rno}")
    public ReplyDTO getReplyDTO( @PathVariable("rno") Long rno ){

        ReplyDTO replyDTO = replyService.read(rno);

        return replyDTO;
    }

    @Operation(summary =  "DELETE 방식으로 특정 댓글 삭제")
    @DeleteMapping("/{rno}")
    public Map<String,Long> remove( @PathVariable("rno") Long rno ){
        // rno를 받아 remove 처리 후 rno 를 전달함.
        replyService.remove(rno);

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("rno", rno);

        return resultMap;
    }



    @Operation(summary =  "PUT 방식으로 특정 댓글 수정")
    @PutMapping(value = "/{rno}", consumes = MediaType.APPLICATION_JSON_VALUE )
    public Map<String,Long> remove( @PathVariable("rno") Long rno, @RequestBody ReplyDTO replyDTO ){
        // rno를 받아 객체를 수정 후 rno를 전달
        replyDTO.setRno(rno); //번호를 일치시킴

        replyService.modify(replyDTO);

        Map<String, Long> resultMap = new HashMap<>();

        resultMap.put("rno", rno);

        return resultMap;
    }

}
