package org.zerock.b01.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import lombok.extern.log4j.Log4j2;
import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.b01.dto.upload.UploadFileDTO;
import org.zerock.b01.dto.upload.UploadResultDTO;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;


@RestController
@Log4j2
public class UpDownController {

    @Value("${org.zerock.upload.path}")// import 시에 springframework으로 시작하는 Value
    private String uploadPath; // application.properties에 경로 등록 필수

//    @Operation(summary =  "POST 방식으로 파일 등록") 605 제거 하단으로 교체
//    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
//    public String upload(UploadFileDTO uploadFileDTO){
//        log.info(uploadFileDTO);
//
//        // 600 추가
//        if(uploadFileDTO.getFiles() != null) { // uploadFileDTO에 값이 null이 아니면
//
//            uploadFileDTO.getFiles().forEach(multipartFile -> {
//                log.info(multipartFile.getOriginalFilename()); // 원본 파일명 출력
//                String originalName = multipartFile.getOriginalFilename();
//                String uuid = UUID.randomUUID().toString();  // 랜덤 uuid 생성
//
//                Path savePath = Paths.get(uploadPath, uuid+"_"+originalName);
//
//                try {
//                    multipartFile.transferTo(savePath); // 실제 파일 저장
//
//                    // 파일이 이미지 종류라면 섬네일 처리 함.
//                    if(Files.probeContentType(savePath).startsWith("image")){
//
//                        File thumbFile = new File(uploadPath, "s_"+uuid+"_"+originalName);
//                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200,200);
//                        // 섬네일 생성 가로200X세로200
//                    }
//                }catch (IOException e){
//                    e.printStackTrace();
//                }
//            }); // for문 종료
//
//        } // if문 종료
//        return null;
//    }

    @Operation(summary =  "POST 방식으로 파일 등록")
    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public List<UploadResultDTO> upload(
            @Parameter(
                    description = "Files to be uploaded",
                    content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )  // 파일로 업로드 안되 추가
            UploadFileDTO uploadFileDTO){

        log.info(uploadFileDTO);

        if(uploadFileDTO.getFiles() != null){

            final List<UploadResultDTO> list = new ArrayList<>();

            uploadFileDTO.getFiles().forEach(multipartFile -> {

                String originalName = multipartFile.getOriginalFilename();
                log.info(originalName);

                String uuid = UUID.randomUUID().toString();

                Path savePath = Paths.get(uploadPath, uuid+"_"+ originalName);

                boolean image = false;

                try {
                    multipartFile.transferTo(savePath);

                    //이미지 파일의 종류라면
                    if(Files.probeContentType(savePath).startsWith("image")){

                        image = true;

                        File thumbFile = new File(uploadPath, "s_" + uuid+"_"+ originalName);

                        Thumbnailator.createThumbnail(savePath.toFile(), thumbFile, 200,200);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }

                list.add(UploadResultDTO.builder()
                        .uuid(uuid)
                        .fileName(originalName)
                        .img(image).build()
                );

            });//end each

            return list;
        }//end if

        return null;
    }


    @Operation(summary =  "GET방식으로 첨부파일 조회")
    @GetMapping("/view/{fileName}") // 608 추가
    public ResponseEntity<Resource> viewFileGET(@PathVariable String fileName){

        Resource resource = new FileSystemResource(uploadPath+File.separator + fileName);
        String resourceName = resource.getFilename();
        HttpHeaders headers = new HttpHeaders();

        try{
            headers.add("Content-Type", Files.probeContentType( resource.getFile().toPath() ));
        } catch(Exception e){
            return ResponseEntity.internalServerError().build();
        }
        return ResponseEntity.ok().headers(headers).body(resource);
    }

    @Operation(summary = "DELETE 방식으로 파일 삭제")
    @DeleteMapping("/remove/{fileName}")  // 609 추가
    public Map<String,Boolean> removeFile(@PathVariable String fileName){

        Resource resource = new FileSystemResource(uploadPath+File.separator + fileName);
        String resourceName = resource.getFilename();

        Map<String, Boolean> resultMap = new HashMap<>();
        boolean removed = false;

        try {
            String contentType = Files.probeContentType(resource.getFile().toPath());
            removed = resource.getFile().delete();

            //섬네일이 존재한다면
            if(contentType.startsWith("image")){
                File thumbnailFile = new File(uploadPath+File.separator +"s_" + fileName);
                thumbnailFile.delete();
            }

        } catch (Exception e) {
            log.error(e.getMessage());
        }

        resultMap.put("result", removed);

        return resultMap;
    }

}
