package com.skillifyme.video.Skillify_Me_Video.repository;


import com.skillifyme.video.Skillify_Me_Video.model.Video;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VideoRepository extends MongoRepository<Video, ObjectId> {
    Optional<Video> findByFileName(String filename);
}
