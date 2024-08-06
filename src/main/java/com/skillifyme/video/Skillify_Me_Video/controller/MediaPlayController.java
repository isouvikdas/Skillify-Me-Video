package com.skillifyme.video.Skillify_Me_Video.controller;

import com.skillifyme.video.Skillify_Me_Video.service.MediaStreamLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.FileNotFoundException;
import java.io.IOException;

@RestController
public class MediaPlayController {

    @Autowired
    private MediaStreamLoader mediaStreamLoader;

    @GetMapping(value = "/play/media/v02/{vid_id}")
    @ResponseBody
    public ResponseEntity<StreamingResponseBody> playMediaV02(
            @PathVariable("vid_id")
            String video_id,
            @RequestHeader(value = "Range", required = false)
            String rangeHeader) {
        try {
            String filePathString = "<Place full path to your video file here.>";

            ResponseEntity<StreamingResponseBody> retVal =
                    mediaStreamLoader.loadPartialMediaFile(filePathString, rangeHeader);

            return retVal;
        } catch (FileNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IOException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
