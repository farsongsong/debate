package com.example.portfolio.global.util;

import com.example.portfolio.global.exception.CustomException;
import com.example.portfolio.global.exception.ErrorCode;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

public class FileUtil {
    private static final String UPLOAD_DIR = "uploads/";
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final Set<String> ALLOWED_MIME_TYPES = Set.of(
            "image/jpeg", "image/png", "image/gif", "image/webp"
    );
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024; // 5MB

    public static String saveFile(MultipartFile file) throws IOException {
        validateImage(file);
        String ext = getExtension(file.getOriginalFilename());
        String filename = UUID.randomUUID() + "." + ext;
        Path path = Paths.get(UPLOAD_DIR + filename);
        Files.createDirectories(path.getParent());
        Files.write(path, file.getBytes());
        return "/uploads/" + filename;
    }

    private static void validateImage(MultipartFile file) {
        if (file == null || file.isEmpty()) throw new CustomException(ErrorCode.FILE_EMPTY);
        if (file.getSize() > MAX_FILE_SIZE) throw new CustomException(ErrorCode.FILE_TOO_LARGE);

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_MIME_TYPES.contains(contentType.toLowerCase())) {
            throw new CustomException(ErrorCode.FILE_INVALID_TYPE);
        }
        String ext = getExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(ext.toLowerCase())) {
            throw new CustomException(ErrorCode.FILE_INVALID_TYPE);
        }
    }

    private static String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) return "";
        return filename.substring(filename.lastIndexOf('.') + 1);
    }

    public static void deleteFile(String filePath) throws IOException {
        if (filePath != null && !filePath.isBlank()) {
            Files.deleteIfExists(Paths.get(filePath.replaceFirst("/", "")));
        }
    }
}
