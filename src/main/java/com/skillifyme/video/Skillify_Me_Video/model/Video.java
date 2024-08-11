package com.skillifyme.video.Skillify_Me_Video.model;

import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "lectures")
@Data
public class Video {
    private ObjectId id;
    private String fileName;
    private String s3Key;
    private long fileSize;
    private String contentType;
}
