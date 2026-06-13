package com.scm.services.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.Transformation;
import com.cloudinary.utils.ObjectUtils;
import com.scm.helpers.AppConstants;
import com.scm.services.ImageService;

@Service
public class ImageServiceImpl implements ImageService {

    private Cloudinary cloudinary;

    public ImageServiceImpl(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    @Override
    public String uploadImage(MultipartFile contactImage, String filename) {

        // code likhnaa hia jo image ko upload kar rha ho

        try {
            byte[] data = new byte[contactImage.getInputStream().available()];
            contactImage.getInputStream().read(data);
            cloudinary.uploader().upload(data, ObjectUtils.asMap(
                    "public_id", filename));

            return this.getUrlFromPublicId(filename);
        } catch (Exception e) {
            System.out.println("Image upload skipped: " + e.getMessage());
            return saveLocally(contactImage, filename);
        }

        // and return raha hoga : url

    }

    @Override
    public String getUrlFromPublicId(String publicId) {

        return cloudinary
                .url()
                .transformation(
                        new Transformation<>()
                                .width(AppConstants.CONTACT_IMAGE_WIDTH)
                                .height(AppConstants.CONTACT_IMAGE_HEIGHT)
                                .crop(AppConstants.CONTACT_IMAGE_CROP))
                .generate(publicId);

    }

    private String saveLocally(MultipartFile contactImage, String filename) {
        try {
            String extension = getExtension(contactImage.getOriginalFilename());
            String safeFilename = filename.replaceAll("[^a-zA-Z0-9._-]", "_") + extension;
            Path uploadDir = Path.of("uploads", "contacts").toAbsolutePath().normalize();
            Files.createDirectories(uploadDir);
            Path target = uploadDir.resolve(safeFilename).normalize();
            Files.copy(contactImage.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/contacts/" + safeFilename;
        } catch (Exception localException) {
            System.out.println("Local contact image save failed: " + localException.getMessage());
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

}
