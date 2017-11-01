package saturday.services;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface S3Service {
    PutObjectResult upload(MultipartFile file, String uploadKey) throws IOException;
    PutObjectResult upload(InputStream ioStream, String uploadKey, ObjectMetadata metadata) throws IOException;
    List<PutObjectResult> upload(MultipartFile[] multipartFiles);
    List<S3ObjectSummary> list();
    ResponseEntity<byte[]> download(String key) throws IOException;
}
