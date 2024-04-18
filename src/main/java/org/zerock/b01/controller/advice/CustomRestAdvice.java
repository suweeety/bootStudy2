package org.zerock.b01.controller.advice;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice  // 컨트롤러에서 발생하는 예외에 대해 json 과 같은 순수한 응답 메시지를 생성해서 보낼 수 있음.
@Log4j2
public class CustomRestAdvice {
    // Ajax와 같이 눈에 보이지 않는 방식으로 서버를 호출하고 결과를 전송하므로 에러가 발생하면 어디에서 어떤 에러가 발생 했는지 확인

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String>> handleBindException(BindException e) {
        // handleBindException() 컨트롤러에서 BindException이 발생할 경우 이를 이용해 json 메시지와 400(Bad Request) 전송
        log.error(e);

        Map<String, String> errorMap = new HashMap<>();

        if(e.hasErrors()){

            BindingResult bindingResult = e.getBindingResult();

            bindingResult.getFieldErrors().forEach(fieldError -> {
                errorMap.put(fieldError.getField(), fieldError.getCode());
            });
        }

        return ResponseEntity.badRequest().body(errorMap);
    }

    @ExceptionHandler(DataIntegrityViolationException.class) // 559 추가 (fk에 대한 사용자에게 예외을 전달함)
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String>> handleFKException(Exception e) {

        log.error(e);

        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("time", ""+System.currentTimeMillis());
        errorMap.put("msg",  "constraint fails");
        return ResponseEntity.badRequest().body(errorMap);
    }


//    @ExceptionHandler(NoSuchElementException.class) // 562 rno 댓글 조회 예외
//    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
//    public ResponseEntity<Map<String, String>> handleNoSuchElement(Exception e) {
//
//        log.error(e);
//
//        Map<String, String> errorMap = new HashMap<>();
//
//        errorMap.put("time", ""+System.currentTimeMillis());
//        errorMap.put("msg",  "No Such Element Exception");
//        return ResponseEntity.badRequest().body(errorMap);
//    }

    @ExceptionHandler({
            NoSuchElementException.class,
            EmptyResultDataAccessException.class }) // 564 추가 : 존재하지 않는 댓글 을 삭제할 때 처리
    @ResponseStatus(HttpStatus.EXPECTATION_FAILED)
    public ResponseEntity<Map<String, String>> handleNoSuchElement(Exception e) {

        log.error(e);

        Map<String, String> errorMap = new HashMap<>();

        errorMap.put("time", ""+System.currentTimeMillis());
        errorMap.put("msg",  "No Such Element Exception");
        return ResponseEntity.badRequest().body(errorMap);
    }

}
