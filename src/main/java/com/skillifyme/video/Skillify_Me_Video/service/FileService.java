package com.skillifyme.video.Skillify_Me_Video.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.skillifyme.video.Skillify_Me_Video.model.Video;
import com.skillifyme.video.Skillify_Me_Video.repository.VideoRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@Service
public class FileService {

    @Autowired
    private AmazonS3 amazonS3;

    @Value("${s3.bucket.name}")
    private String bucket;

    @Autowired
    private VideoRepository videoRepository;

    private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
        final File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (final FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        } catch (IOException e) {
            log.error("Error {} occurred while converting the multipart file", e.getLocalizedMessage());
        }
        return file;
    }

    @Async
    public S3ObjectInputStream findByName(String fileName) {
        log.info("Downloading file with name {}", fileName);
        Video video = videoRepository.findByFileName(fileName)
                .orElseThrow(() -> new IllegalArgumentException("No video found with this filename: " + fileName));
        return amazonS3.getObject(bucket, video.getS3Key()).getObjectContent();
    }

    @Async
    public Video save(final MultipartFile multipartFile) {
        try {
            final File file = convertMultiPartFileToFile(multipartFile);
            final String fileName = LocalDateTime.now() + "_" + file.getName();
            log.info("Uploading file with name {}", fileName);
            final PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, fileName, file);
            amazonS3.putObject(putObjectRequest);

            Video video = new Video();
            video.setFileName(fileName);
            video.setS3Key(fileName);
            video.setFileSize(file.length());
            video.setContentType(Files.probeContentType(file.toPath()));
            Video savedVideo = videoRepository.save(video);

            Files.delete(file.toPath());
            return savedVideo;
        } catch (AmazonServiceException e) {
            log.error("Error {} occurred while uploading file", e.getLocalizedMessage());
            return null;
        } catch (IOException ex) {
            log.error("Error {} occurred while deleting temporary file", ex.getLocalizedMessage());
            return null;
        }
    }

    public Video getVideoDetails(ObjectId videoId) {
        return videoRepository.findById(videoId)
                .orElseThrow(() -> new IllegalArgumentException("video not found with this id: " + videoId));
    }

}
