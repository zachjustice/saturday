package saturday.services;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicContent;
import saturday.domain.TopicContentRequest;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.repositories.TopicContentRepository;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service("topicContentService")
public class TopicContentServiceImpl implements TopicContentService {
    @Value("${saturday.s3.user-files-bucket}")
    private String bucketName;
    @Value("${saturday.s3.url.prefix}")
    private String s3urlPrefix;
    @Value("${saturday.s3.topic.content.key.prefix}")
    private String keyPrefix;
    @Value("${saturday.timestamp.format}")
    private String timestampFormat;

    private final TopicContentRepository topicContentRepository;
    private final TopicService topicService;
    private final EntityService entityService;
    private final S3Service s3Service;

    public TopicContentServiceImpl(
            TopicContentRepository topicContentRepository,
            TopicService topicService,
            EntityService entityService,
            S3Service s3Service
    ) {
        this.topicContentRepository = topicContentRepository;
        this.topicService = topicService;
        this.entityService = entityService;
        this.s3Service = s3Service;
    }

    /**
     * Find topic content by its primary key.
     * Topic Content is how user uploaded content is stored (relevant data in db and then actual file data in s3)
     * @param id The numeric primary key of the topic content to be retrieved
     * @return topic content
     */
    @Override
    public TopicContent findTopicContentById(int id) {
        TopicContent topicContent = topicContentRepository.findById(id);

        if(topicContent == null) {
            throw new ResourceNotFoundException("No topic content with " + id + " exists!");
        }

        return topicContent;
    }

    /**
     * All topic content belongs to a topic which is how users share content with one another.
     * @param id Primary key of the topic
     * @return all topic content for a topic
     */
    @Override
    public List<TopicContent> findTopicContentByTopicId(int id) {
        topicService.findTopicById(id); // throws Resource Not Found Exception
        return  topicContentRepository.findByTopicId(id);
    }

    @Override
    public TopicContent updateTopicContent(TopicContent oldTopicContent, TopicContent newTopicContent) {
        // can only update description and date taken for now
        oldTopicContent.setDescription(newTopicContent.getDescription());
        oldTopicContent.setDateTaken(newTopicContent.getDateTaken());

        return topicContentRepository.save(oldTopicContent);
    }

    @Override
    public TopicContent save(TopicContentRequest topicContentRequest) throws IOException {
        int topicId = topicContentRequest.getTopicId();
        int creatorId = topicContentRequest.getCreatorId();
        String description = topicContentRequest.getDescription();
        MultipartFile file = topicContentRequest.getFile();
        String data = topicContentRequest.getData();
        Date dateTaken = topicContentRequest.getDateTaken();

        Entity creator;
        Topic topic = topicService.findTopicById(topicId);

        // only let the user set the topic content's creator if they're an admin
        if(entityService.getAuthenticatedEntity().isAdmin()) {
            creator = entityService.findEntityById(creatorId);
        } else {
            creator = entityService.getAuthenticatedEntity();
        }

        if(topic == null) {
            throw new ResourceNotFoundException("The topic id, " + topicId + ", does not exist");
        }

        // Description can be null, but otherwise there's a length limit
        if(description != null && description.length() > 4000) {
            throw new BusinessLogicException("Topic Content Description cannot be more than 4000 characters");
        }

        // date taken associates the topic content with a particular calendar view for the user
        if(dateTaken == null) {
            dateTaken = new Date();
        }

        // upload after s3 validation.
        // then insert into db since we have the bucket name and s3 key
        String uuid = UUID.randomUUID().toString();
        String s3key = keyPrefix + uuid; // topic-content/{{GUID}}

        try {
            if(data != null) {
                // strip base64 data prefix
                int i = data.indexOf(",");

                if(i > -1) {
                    data = data.substring(i + 1);
                }

                // get input stream for upload
                byte[] bI = java.util.Base64.getDecoder().decode(data);
                InputStream fis = new ByteArrayInputStream(bI);

                // generate object metadata based on input stream
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(bI.length);
                metadata.setContentType("image/jpeg");

                s3Service.upload(fis, s3key, metadata);
            } else {
                // s3 url key is probably unique - should probably use GUID
                s3Service.upload(file, s3key);
            }
        } catch (IOException e){
            e.printStackTrace();
            throw new IOException("Failed to upload file: " + e.getMessage());
        }

        // Create topic content
        TopicContent topicContent = new TopicContent();
        topicContent.setDescription(description);
        topicContent.setCreator(creator);
        topicContent.setTopic(topic);
        topicContent.setS3bucket(bucketName);
        topicContent.setS3key(s3key);
        topicContent.setDateTaken(dateTaken);

        return topicContentRepository.save(topicContent);
    }

    /**
     * Get an entity's topic content based on the topics they're a part of.
     *
     * @param entityId the entity id for which to query topic content
     * @param page how far back to start grabbing topic content. (e.g. get the 100th to 150th topic content items)
     * @param pageSize how much topic content to get
     * @return A list of the entity's topic content
     */
    @Override
    public List<TopicContent> findByTopicMember(int entityId, int page, int pageSize) {
        entityService.findEntityById(entityId); //
        int offset = page * pageSize;
        int limit = offset + pageSize;
        return topicContentRepository.findByTopicMember(entityId, offset, limit);
    }

    @Override
    public void delete(TopicContent topicContent) {
        s3Service.delete(topicContent.getS3bucket(), topicContent.getS3key());
        topicContentRepository.delete(topicContent.getId());
    }
}
