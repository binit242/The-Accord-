package com.scm.services.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.Map;

// import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.scm.helpers.AppConstants;
import com.scm.services.ProfilePicService;

@Service
public class ProfilePicServiceImpl implements ProfilePicService{

    private Cloudinary cloudinary;

    public ProfilePicServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

  @Override
public Map<String, Object> uploadProfilepic(MultipartFile profilePic, String filename) {
    try {
        byte[] data = new byte[profilePic.getInputStream().available()];
        profilePic.getInputStream().read(data);

        // Upload to Cloudinary with public_id
        @SuppressWarnings("unchecked")
        Map<String, Object> uploadResult = cloudinary.uploader().upload(data, ObjectUtils.asMap(
                "public_id", filename));

        return uploadResult;
    } catch (Exception e) {
        System.out.println("Profile picture upload skipped: " + e.getMessage());
        return saveLocally(profilePic, filename);
    }
}

private Map<String, Object> saveLocally(MultipartFile profilePic, String filename) {
    try {
        String extension = getExtension(profilePic.getOriginalFilename());
        String safeFilename = filename.replaceAll("[^a-zA-Z0-9._-]", "_") + extension;
        Path uploadDir = Path.of("uploads", "profile").toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);
        Path target = uploadDir.resolve(safeFilename).normalize();
        Files.copy(profilePic.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        Map<String, Object> result = new HashMap<>();
        result.put("secure_url", "/uploads/profile/" + safeFilename);
        result.put("public_id", safeFilename);
        return result;
    } catch (Exception localException) {
        System.out.println("Local profile picture save failed: " + localException.getMessage());
        return null;
    }
}

private String getExtension(String originalFilename) {
    if (originalFilename == null) {
        return "";
    }
    int dotIndex = originalFilename.lastIndexOf('.');
    if (dotIndex < 0 || dotIndex == originalFilename.length() - 1) {
        return "";
    }
    return originalFilename.substring(dotIndex);
}



   

    @Override
    public String getpicUrlFromPublicId(String publicId) {
         return cloudinary.url()
                .transformation(new Transformation() .width(AppConstants.CONTACT_IMAGE_WIDTH)
                                .height(AppConstants.CONTACT_IMAGE_HEIGHT)
                                .crop(AppConstants.CONTACT_IMAGE_CROP))
                .generate(publicId);
    }

}
