package saturday.services;

import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface S3Service {
    public PutObjectResult upload(MultipartFile file, String uploadKey) throws IOException;
    public List<PutObjectResult> upload(MultipartFile[] multipartFiles);
    public List<S3ObjectSummary> list();
    public ResponseEntity<byte[]> download(String key) throws IOException;
}
