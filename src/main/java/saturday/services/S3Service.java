package saturday.services;

import org.springframework.web.multipart.MultipartFile;

public interface S3Service {
    public void downloadFile(String keyName);
    public void uploadFile(String keyName, MultipartFile file);
}
