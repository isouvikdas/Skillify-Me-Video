package com.skillifyme.video.Skillify_Me_Video.service;

import org.bson.types.ObjectId;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "skillifymecourse")
public interface CourseServiceClient {

    @PostMapping("/lessons/{lessonId}/video")
    boolean updateLessonVideo(@PathVariable("lessonId") ObjectId lessonId, @RequestParam("videoId") ObjectId videoId);
}
