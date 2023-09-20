package com.example.mreview.controller;

import com.example.mreview.dto.UploadResultDTO;
import lombok.extern.slf4j.Slf4j;
import net.coobird.thumbnailator.Thumbnailator;
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
import java.io.UnsupportedEncodingException;
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
            //Path 는 경로 정보를 캡슐화한 것이다.
            try{
                uploadFile.transferTo(savePath); // multipartFile 객체의 transferTo 메서드, 업로드처리이다.
                resultDTOList.add(new UploadResultDTO(fileName,uuid,folderPath));
                /* 추가되는 부분 */
                //섬네일 생성
                //섬네일 파일 이름은 중간에 s_로 시작하도록
                String thumbnailSaveName = uploadPath + File.separator + folderPath + File.separator
                        + "s_" + uuid + "_" + fileName;
                File thumbnailFile= new File(thumbnailSaveName); //파일을 실제로 로컬 저장소에 쓰기 위해 파일 객체를 준비한다는 느낌
                //섬네일 생성
                Thumbnailator.createThumbnail(savePath.toFile(),thumbnailFile,100,100);
                /* 끝 */

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
            //uploadPath = c:/upload , srcFileName = 년/월/일/파일이름 <- UploadResultDTO 객체로부터 받음
            log.info("file: "+ file);
            HttpHeaders header = new HttpHeaders();

            //MIME 타입 처리
            header.add("Content-Type", Files.probeContentType(file.toPath()));
            //파일 데이터 처리
            result = new ResponseEntity<>(FileCopyUtils.copyToByteArray(file), header, HttpStatus.OK);
            //FileCopyUtils는 파일 및 스트림 복사를 위한 간단한 유틸리티 메소드의 집합이다.
        }catch(Exception e ){
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);

        }
        System.out.println(result);
        return result;
    }

    @PostMapping("/removeFile")
    public ResponseEntity<Boolean> removeFile(String fileName){
        String srcFileName = null;
        try{
            srcFileName = URLDecoder.decode(fileName,"UTF-8");
            File file= new File(uploadPath + File.separator + srcFileName);
            //파일 저장할 때는 파일 이름으로만 저장했지만, 지금 매개변수로 들어오는 fileName은 UploadResultDTO 객체이기 때문에 년/월/일 정보도 함께 가지고있다.
            System.out.println("srcFileName : "+srcFileName);
            boolean result = file.delete();

            System.out.println("parent and child");
            System.out.println(file.getParent()); //c:/upload/년/월/일
            System.out.println(file.getName()); //파일이름

            File thumbnail = new File(file.getParent(), "s_" + file.getName());
            result = thumbnail.delete();

            return new ResponseEntity<>(result, HttpStatus.OK);
        }catch (UnsupportedEncodingException e){
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String makeFolder() {
        String str = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        String folderPath = str.replace("/", File.separator); // ""yyyy/MM/dd에서 "/" 를 "\" 로 변경
        //make folder
        File uploadPathFolder = new File(uploadPath, folderPath); // c://upload/folderPath....
        if(uploadPathFolder.exists() == false){ //uploadPath에 folderPath가 존재하지 않는다면
            uploadPathFolder.mkdirs(); //만들어준다.
        }
        return folderPath;
    }
}
