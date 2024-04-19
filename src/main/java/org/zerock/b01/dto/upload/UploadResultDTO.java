package org.zerock.b01.dto.upload;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDTO { // 여러개의 파일이 업로드되면 결과도 여러개 발생 하게 되고 여러 정보로 반환해야 함
    // 별도의 dto를 구성하여 객체로 반환 처리 용

    private String uuid;

    private String fileName;

    private boolean img;

    public String getLink(){
        // 차후에 json 처리 될 때 link라는 속성으로 자동 처리
        if(img){
            return "s_"+ uuid +"_"+fileName; //이미지인 경우 섬네일
        }else {
            return uuid+"_"+fileName;
        }
    }
}
