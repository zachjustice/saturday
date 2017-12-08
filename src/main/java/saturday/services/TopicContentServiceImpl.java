package saturday.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicContent;
import saturday.domain.TopicContentRequest;
import saturday.exceptions.ProcessingResourceException;
import saturday.repositories.TopicContentRepository;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Service("topicContentService")
public class TopicContentServiceImpl implements TopicContentService {
    @Value("${saturday.s3.bucket}")
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

        if(description != null && description.length() > 4000) {
            throw new ProcessingResourceException("Topic Content Description cannot be more than 4000 characters");
        }

        // date taken associates the topic content with a particular calendar view for the user
        if(dateTaken == null) {
            dateTaken = new Date();
        }

        // upload after s3 validation.
        // then insert into db since we have the bucket name and s3 key
        String s3url;
        try {
            // s3 url key is probably unique - should probably use GUID
            String now = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").format(new Date());
            String uploadKey = keyPrefix + creatorId + "-" + now;
            s3url  = s3urlPrefix + bucketName + "/" + uploadKey;
            s3Service.upload(file, uploadKey);
        } catch (IOException e){
            e.printStackTrace();
            throw new IOException("Failed to upload file: " + e.getMessage());
        }

        // Create topic content
        TopicContent topicContent = new TopicContent();
        topicContent.setDescription(description);
        topicContent.setCreator(creator);
        topicContent.setTopic(topic);
        topicContent.setS3url(s3url);
        topicContent.setDateTaken(dateTaken);

        return topicContentRepository.save(topicContent);
    }

    @Override
    public List<TopicContent> findByTopicMember(int entityId) {
        entityService.findEntityById(entityId); // throws Resource Not Found Exception
        return topicContentRepository.findByTopicMember(entityId);
    }

    @Override
    public void delete(TopicContent topicContent) {
        // TODO store the bucketname and s3 key instead of the s3 url so we can delete by that
        // s3Service.delete(topicContent.bucketName, topicContent.s3key);
        topicContentRepository.delete(topicContent.getId());
    }
}
