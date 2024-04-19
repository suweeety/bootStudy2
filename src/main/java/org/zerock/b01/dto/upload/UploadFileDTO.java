package org.zerock.b01.dto.upload;


import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data  // 598 추가
public class UploadFileDTO {

    // 필드 선언
    private List<MultipartFile> files;
}

