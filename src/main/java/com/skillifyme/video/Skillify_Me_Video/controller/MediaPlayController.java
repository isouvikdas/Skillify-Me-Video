package com.skillifyme.video.Skillify_Me_Video.controller;

import com.skillifyme.video.Skillify_Me_Video.service.FileService;
import com.skillifyme.video.Skillify_Me_Video.service.MediaStreamLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;

@Slf4j
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("media")
public class MediaPlayController {

    private static final String MESSAGE_1 = "Uploaded the file successfully";
    private static final String FILE_NAME = "fileName";

    @Autowired
    private FileService fileService;

    @Autowired
    private MediaStreamLoader mediaLoaderService;

    @Value("${s3.bucket.name}")
    private String bucket;

    @PostMapping("/upload")
    public ResponseEntity<Object> save(@RequestParam("file") MultipartFile multipartFile) {
        fileService.save(multipartFile);
        return new ResponseEntity<>(MESSAGE_1, HttpStatus.OK);
    }

    @GetMapping("/download")
    public ResponseEntity<Object> findByName(@RequestBody(required = false) Map<String, String> params) {
        return ResponseEntity
                .ok()
                .cacheControl(CacheControl.noCache())
                .header("Content-type", "application/octet-stream")
                .header("Content-disposition", "attachment; filename=\"" + params.get(FILE_NAME) + "\"")
                .body(new InputStreamResource(fileService.findByName(params.get(FILE_NAME))));
    }

    @GetMapping(value = "/stream/{vid_id}")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> streamMedia(
            @PathVariable("vid_id") String videoId,
            @RequestHeader(value = "Range", required = false) String rangeHeader) {
        try {
            ResponseEntity<StreamingResponseBody> response =
                    mediaLoaderService.loadPartialMediaFile(videoId, rangeHeader);

            return response;
        } catch (FileNotFoundException e) {
            log.error("File not found: {}", videoId, e);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            log.error("Error streaming file: {}", videoId, e);
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
