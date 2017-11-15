package saturday.controllers;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.hibernate.validator.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import saturday.domain.Entity;
import saturday.domain.NewTopicContent;
import saturday.domain.Topic;
import saturday.domain.TopicContent;
import saturday.exceptions.TopicNotFoundException;
import saturday.services.EntityService;
import saturday.services.S3Service;
import saturday.services.TopicContentService;
import saturday.services.TopicService;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class TopicContentController {

    @Value("${saturday.s3.bucket}")
    private String bucketName;
    @Value("${saturday.s3.url.prefix}")
    private String s3urlPrefix;
    @Value("${saturday.s3.topic.content.key.prefix}")
    private String keyPrefix;
    @Value("${saturday.timestamp.format}")
    private String timestampFormat;

    private final TopicContentService topicContentService;
    private final TopicService topicService;
    private final EntityService entityService;
    private final S3Service s3Service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TopicContentController(TopicContentService topicContentService, TopicService topicService, EntityService entityService, S3Service s3Service) {
        this.topicContentService = topicContentService;
        this.topicService = topicService;
        this.entityService = entityService;
        this.s3Service = s3Service;
    }

    @RequestMapping(value = "/topic_content", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<TopicContent> createTopicContent(
            @RequestBody NewTopicContent newTopicContent
    ) {
        // Validate
        if(newTopicContent == null || newTopicContent.getCreator() <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        // Get the creator
        Integer creatorId = newTopicContent.getCreator();
        Entity creator = entityService.findEntityById(creatorId);

        if(creator == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String now = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss").format(new Date());

        // TODO validate new topic content
        String data = newTopicContent.getData();
        String description = newTopicContent.getDescription();
        Integer topicId = newTopicContent.getTopic();
        String uploadKey = keyPrefix + creatorId + "-" + now + ".jpeg";
        String s3url  = s3urlPrefix + bucketName + "/" + uploadKey;

        // Upload to s3 first to avoid creating db row without matching s3 media
        try {

            // strip base64 data prefix
            int i = data.indexOf(",");

            if(i > -1) {
                data = data.substring(i + 1);
            }

            byte[] bI = java.util.Base64.getDecoder().decode(data);
            InputStream fis = new ByteArrayInputStream(bI);

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bI.length);
            metadata.setContentType("image/jpeg");

            s3Service.upload(fis, uploadKey, metadata);
        } catch (IOException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Create topic content
        TopicContent topicContent = new TopicContent();
        topicContent.setDescription(description);
        topicContent.setCreator(creator);
        topicContent.setTopic(topicService.findTopicById(topicId));
        topicContent.setS3url(s3url);

        topicContent = topicContentService.saveTopicContent(topicContent);
        logger.info("Created TopicContent: " + newTopicContent.toString());

        return new ResponseEntity<>(topicContent, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_content", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TopicContent> createTopicContent(
            @RequestParam("creatorId")   Integer creatorId,
            @RequestParam("topicId")     Integer topicId,
            @RequestParam("description") String description,
            @RequestParam("file")        MultipartFile file
    ) throws TopicNotFoundException {
        // Get the creator
        Entity creator = entityService.findEntityById(creatorId);
        Topic topic = topicService.findTopicById(topicId);

        if(creator == null) {
            throw new EntityNotFoundException();
        }

        if(topic == null) {
            throw new TopicNotFoundException("The topic id, " + topicId + ", does not exist");
        }

        String now = new SimpleDateFormat("yyyy-MM-ddTHH:mm:ss").format(new Date());

        // TODO validate new topic content
        String uploadKey = keyPrefix + creatorId + "-" + now;
        String s3url  = s3urlPrefix + bucketName + "/" + uploadKey;

        // Upload to s3 first to avoid creating db row without matching s3 media
        try {
            s3Service.upload(file, uploadKey);
        } catch (IOException e){
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Create topic content
        TopicContent topicContent = new TopicContent();
        topicContent.setDescription(description);
        topicContent.setCreator(creator);
        topicContent.setTopic(topic);
        topicContent.setS3url(s3url);

        topicContent = topicContentService.saveTopicContent(topicContent);
        logger.info("Created TopicContent: " + topicContent.toString());

        return new ResponseEntity<>(topicContent, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_content/{id}", method = RequestMethod.GET)
    public ResponseEntity<TopicContent> findTopicByName(@PathVariable(value="id") int id) {
        TopicContent topicContent = topicContentService.findTopicContentById(id);

        if(topicContent == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(topicContent, HttpStatus.OK);
    }
}
