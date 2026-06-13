package com.scm.services;

import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface ProfilePicService {

Map<String, Object> uploadProfilepic(MultipartFile profilePic, String filename);


    String getpicUrlFromPublicId(String publicId);

}
