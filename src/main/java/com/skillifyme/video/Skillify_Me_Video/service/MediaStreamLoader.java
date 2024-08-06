package com.skillifyme.video.Skillify_Me_Video.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;


public interface MediaStreamLoader {

    ResponseEntity<StreamingResponseBody>
    loadEntireMediaFile(String localMediaFilePath) throws IOException;

    ResponseEntity<StreamingResponseBody> loadPartialMediaFile
            (String localMediaFilePath, String rangeValues) throws IOException;

    ResponseEntity<StreamingResponseBody> loadPartialMediaFile
            (String localMediaFilePath, long fileStartPos, long fileEndPos) throws IOException;
}
