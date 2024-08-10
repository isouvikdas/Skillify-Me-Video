package com.skillifyme.video.Skillify_Me_Video.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.IOException;

@Service
@Slf4j
public class MediaStreamLoaderImpl implements MediaStreamLoader {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${s3.bucket.name}")
    private String bucket;

    @Override
    public ResponseEntity<StreamingResponseBody> loadEntireMediaFile(String fileName) throws IOException {
        return loadPartialMediaFile(fileName, 0, getFileSize(fileName) - 1);
    }

    @Override
    public ResponseEntity<StreamingResponseBody> loadPartialMediaFile(String fileName, String rangeHeader) throws IOException {
        if (!StringUtils.hasText(rangeHeader)) {
            return loadEntireMediaFile(fileName);
        } else {
            long fileSize = getFileSize(fileName);
            String[] ranges = rangeHeader.replace("bytes=", "").split("-");
            long start = Long.parseLong(ranges[0]);
            long end = ranges.length > 1 ? Long.parseLong(ranges[1]) : fileSize - 1;
            return loadPartialMediaFile(fileName, start, end);
        }
    }

    private long getFileSize(String fileName) {
        ObjectMetadata metadata = amazonS3.getObjectMetadata(bucket, fileName);
        return metadata.getContentLength();
    }

    @Override
    public ResponseEntity<StreamingResponseBody> loadPartialMediaFile(String fileName, long fileStartPos, long fileEndPos) throws IOException {
        S3Object s3Object = amazonS3.getObject(new GetObjectRequest(bucket, fileName).withRange(fileStartPos, fileEndPos));
        S3ObjectInputStream inputStream = s3Object.getObjectContent();

        StreamingResponseBody responseStream = os -> {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.flush();
        };

        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", s3Object.getObjectMetadata().getContentType());
        responseHeaders.add("Content-Length", String.valueOf(fileEndPos - fileStartPos + 1));
        responseHeaders.add("Accept-Ranges", "bytes");
        responseHeaders.add("Content-Range", String.format("bytes %d-%d/%d", fileStartPos, fileEndPos, getFileSize(fileName)));

        return new ResponseEntity<>(responseStream, responseHeaders, HttpStatus.PARTIAL_CONTENT);
    }
}
