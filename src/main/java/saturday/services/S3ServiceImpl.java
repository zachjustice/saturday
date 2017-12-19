package saturday.services;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service("s3Service")
public class S3ServiceImpl implements S3Service {

    @Value("${saturday.s3.user-files-bucket}")
    private String bucketName;

    private final AmazonS3 s3Client;

    private Logger logger = LoggerFactory.getLogger(S3ServiceImpl.class);

    @Autowired
    public S3ServiceImpl(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    /**
     * Upload multiple files to s3
     * @param multipartFiles the array of files to upload
     * @return s3 PutObjectResults which describe the metadata for uploaded object
     */
    public List<PutObjectResult> upload(MultipartFile[] multipartFiles) {
        List<PutObjectResult> putObjectResults = new ArrayList<>();

        Arrays.stream(multipartFiles)
                .filter(multipartFile -> !StringUtils.isEmpty(multipartFile.getOriginalFilename()))
                .forEach(multipartFile -> {
                    try {
                        putObjectResults.add(upload(multipartFile, multipartFile.getOriginalFilename()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });

        return putObjectResults;
    }

    /**
     * Converts a multipartfile into an input stream for s3 and uploads to s3.
     * Also forms the minimum amount of metadata for the s3 object.
     * @param multipartFile The file to upload
     * @param uploadKey The s3 key for the file
     * @return PutObjectResult, s3 object describing metadata of the uploaded object
     * @throws IOException If we fail to get the input stream from the multipart file
     */
    public PutObjectResult upload(MultipartFile multipartFile, String uploadKey) throws IOException {
        InputStream inputStream = multipartFile.getInputStream();

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(multipartFile.getSize());
        metadata.setContentType(multipartFile.getContentType());

        return upload(inputStream, uploadKey, metadata);
    }

    /**
     * Takes an input stream, upload key, and object metadata to upload an object to s3.
     * @param inputStream File to be uploaded
     * @param uploadKey s3 key for the file
     * @param metadata Metadata for the file
     * @return PutObjectResult, s3 object of uploaded object's metadata
     * @throws IOException if we fail to close the input stream
     */
   public PutObjectResult upload(InputStream inputStream, String uploadKey, ObjectMetadata metadata) throws IOException {
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, uploadKey, inputStream, metadata);

        putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);

        PutObjectResult putObjectResult = s3Client.putObject(putObjectRequest);

        IOUtils.closeQuietly(inputStream);

        return putObjectResult;
    }

    /**
     * Download an s3 image by key
     * @param key the key for the s3 object
     * @return The s3 image as a byte array
     * @throws IOException if IOUtils fails to interpret data
     */
    public ResponseEntity<byte[]> download(String key) throws IOException {
        GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, key);

        S3Object s3Object = s3Client.getObject(getObjectRequest);

        S3ObjectInputStream objectInputStream = s3Object.getObjectContent();

        byte[] bytes = IOUtils.toByteArray(objectInputStream);

        String fileName = URLEncoder.encode(key, "UTF-8").replaceAll("\\+", "%20");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpHeaders.setContentLength(bytes.length);
        httpHeaders.setContentDispositionFormData("attachment", fileName);

        return new ResponseEntity<>(bytes, httpHeaders, HttpStatus.OK);
    }

    /**
     * Lists the contents for a bucket
     * @return list of object summaries
     */
    public List<S3ObjectSummary> list() {
        ObjectListing objectListing = s3Client.listObjects(new ListObjectsRequest().withBucketName(bucketName));

        return objectListing.getObjectSummaries();
    }

    /**
     * Delete an object from s3
     */
    public void delete(String bucketName, String s3key) {
        s3Client.deleteObject(bucketName, s3key);
    }
}
