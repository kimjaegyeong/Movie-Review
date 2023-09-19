package com.example.mreview.controller;

import com.example.mreview.dto.UploadResultDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@Slf4j
public class UploadController {
    @Value("${com.example.upload.path}")
    private String uploadPath;
    @PostMapping("/uploadAjax")

    public ResponseEntity<List<UploadResultDTO>> uploadFile(MultipartFile[] uploadFiles){
        List<UploadResultDTO> resultDTOList = new ArrayList<>();

        for(MultipartFile uploadFile : uploadFiles) {

            //이미지 파일만 업로드 가능
            if(uploadFile.getContentType().startsWith("image")==false){
                log.warn("this file is not image type");
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            //실제 파일 이름 IE나 Edge는 전체 경로가 들어오므로
            String originalName = uploadFile.getOriginalFilename();
            String fileName = originalName.substring(originalName.lastIndexOf("\\") +1);
            log.info("fileName: " + fileName);

            //날짜 폴더 생성
            String folderPath = makeFolder();

            //UUID
            String uuid= UUID.randomUUID().toString();
            //저장할 파일 이름 중간에 "_" 를 이용해서 구분
            String saveName = uploadPath + File.separator + folderPath + File.separator + uuid + "_" + fileName;
            //saveName = uploadPath\folderPath\ uuid_fileName
            Path savePath = Paths.get(saveName); // 상대 경로 정의
            try{
                uploadFile.transferTo(savePath); // multipartFile 객체의 transferTo 메서드, 업로드처리이다.
                resultDTOList.add(new UploadResultDTO(fileName,uuid,folderPath));
                System.out.println("getImageURL: " + resultDTOList.get(0).getImageURL()); //한국어를 encoding
            } catch(IOException e){
                e.printStackTrace();
            }
        } //end for
        return new ResponseEntity<>(resultDTOList, HttpStatus.OK);
    }

    @GetMapping("/display")
    public ResponseEntity<byte[]> getFile(String fileName){
        ResponseEntity<byte[]> result = null;

        try{
            String srcFileName= URLDecoder.decode(fileName,"UTF-8");
            log.info("fileName: "+ srcFileName);
            File file = new File(uploadPath + File.separator+srcFileName);
            log.info("file: "+ file);
            HttpHeaders header = new HttpHeaders();

            //MIME 타입 처리
            header.add("Content-Type", Files.probeContentType(file.toPath()));
            //파일 데이터 처리
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
        }catch(Exception e ){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }
        System.out.println(result);
        return result;
    }

    private String makeFolder() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("/", File.separator); // "/" 를 "\" 로 변경
        //make folder
        File uploadPathFolder = new File(uploadPath, folderPath); // c://upload/folderPath....
        if(uploadPathFolder.exists() == false){ //uploadPath에 folderPath가 존재하지 않는다면
            uploadPathFolder.mkdirs(); //만들어준다.
        }
        return folderPath;
    }
}
