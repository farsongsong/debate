package com.example.portfolio.infra.file;

import com.example.portfolio.global.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

@Slf4j @Service
public class FileService {
    public String upload(MultipartFile file) {
        try { return FileUtil.saveFile(file); }
        catch (IOException e) { log.error("File upload failed", e); throw new RuntimeException("파일 업로드에 실패했습니다.", e); }
    }
    public void delete(String path) {
        try { FileUtil.deleteFile(path); } catch (IOException e) { log.warn("File delete failed: {}", path); }
    }
}
