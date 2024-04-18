package org.zerock.b01.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReplyDTO {

    private Long rno;

    @NotNull // Null 만 허용하지 않음. "", " " 은 허용
    private Long bno;

    @NotEmpty // 는 null 과 "" 둘 다 허용하지 않게 함, " " 은 허용
    private String replyText;

    // @NotBlank 는 null 과 "" 과 " " 모두 허용하지 않습니다.

    @NotEmpty
    private String replyer;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss") // 581 추가
    private LocalDateTime regDate;

    @JsonIgnore
    private LocalDateTime modDate;

    // @JsonIgnore, @JsonIgnoreProperties, @JsonIgnoreType 이러한 주석은 JSON 직렬화, 역직렬화에서 속성을 무시하는데 사용됩니다.
    // 직렬화란??  : 객체의 직렬화는 객체의 내용을 바이트 단위로 변환하여
    // 파일 또는 네트워크를 통해서 스트림(송수신)이 가능하도록 하는 것을 의미

    // @JsonIgnore 어노테이션은 클래스의 속성(필드, 멤버변수) 수준에서 사용되고
    // @JsonIgnoreProperties 어노테이션은 클래스 수준(클래스 선언 바로 위에)에 사용됩니다.
    // @JsonIgnoreType 어노테이션은 클래스 수준에서 사용되며 전체 클래스를 무시합니다.

}
