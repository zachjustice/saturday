package saturday.services;

import com.amazonaws.services.s3.model.ObjectMetadata;
import com.drew.imaging.ImageProcessingException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicContent;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.repositories.TopicContentRepository;
import saturday.utils.FileUtils;
import saturday.utils.MimeTypes;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Service()
public class TopicContentService {
    @Value("${saturday.page-size-limit}")
    private int PAGE_SIZE_LIMIT;
    @Value("${saturday.s3.user-files-bucket}")
    private String bucketName;
    @Value("${saturday.s3.url.prefix}")
    private String s3urlPrefix;
    @Value("${saturday.s3.topic.content.key.prefix}")
    private String keyPrefix;
    @Value("${saturday.timestamp.format}")
    private String timestampFormat;
    @Value("${saturday.date.format}")
    private String dateFormat;

    private final TopicContentRepository topicContentRepository;
    private final TopicService topicService;
    private final EntityService entityService;
    private final S3Service s3Service;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TopicContentService(
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
     *
     * @param id The numeric primary key of the topic content to be retrieved
     * @return topic content
     */

    public TopicContent findTopicContentById(int id) {
        TopicContent topicContent = topicContentRepository.findById(id);

        if (topicContent == null) {
            throw new ResourceNotFoundException("No topic content with " + id + " exists!");
        }

        return topicContent;
    }

    /**
     * Returns a map where the key is a date and the value is a list of topic content taken on that date
     * { dateTaken = [TopicContent...], ... }
     *
     * @return all topic content for a topic
     */

    public Map<String, List<TopicContent>> findTopicContentByTopicIdGroupedByDateTaken(Date start, Date end, int topicId) {

        Map<DateTime, List<TopicContent>> topicContentByDateTaken = topicContentRepository
                .findTopicContentByTopicIdAndDateTakenBetweenOrderByDateTakenDesc(topicId, start, end)
                .stream()
                .collect(Collectors.groupingBy((topicContent) -> {
                    DateTime dateTime = new DateTime(topicContent.getDateTaken().getTime());
                    return dateTime.withTimeAtStartOfDay();
                }));

        LinkedHashMap<String, List<TopicContent>> orderedTopicContentByDate = new LinkedHashMap<>();

        // first date in the map should be the earliest. (we start getting photos in January and end getting photos
        // February- currDate/feb is after startDate/jan)
        for (DateTime currDate = new DateTime(end); currDate.isAfter(start.getTime()); currDate = currDate.plusDays(-1)) {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat);
            String dateFormatted = format.format(currDate.toDate());

            List<TopicContent> topicContentForDay = topicContentByDateTaken.get(currDate);
            if (topicContentForDay == null) {
                topicContentForDay = new ArrayList<>();
            }

            orderedTopicContentByDate.put(dateFormatted, topicContentForDay);
        }

        return orderedTopicContentByDate;
    }

    /**
     * All topic content belongs to a topic which is how users share content with one another.
     *
     * @return all topic content for a topic
     */
    public Page<TopicContent> findTopicContentByTopicId(Pageable page, int topicId) {
        if (page.getPageSize() > PAGE_SIZE_LIMIT) {
            throw new IllegalArgumentException("Page size is larger than " + PAGE_SIZE_LIMIT);
        }

        return topicContentRepository.findAllByTopicId(page, topicId);
    }

    public TopicContent update(TopicContent newTopicContent) {
        TopicContent topicContent = findTopicContentById(newTopicContent.getId());

        if (topicContent == null) {
            throw new ResourceNotFoundException("No topic content with the id " + newTopicContent.getId() + " exists!");
        }

        topicContent.setDescription(newTopicContent.getDescription());
        topicContent.setDateTaken(newTopicContent.getDateTaken());
        topicContent.setTopic(newTopicContent.getTopic());

        return topicContentRepository.save(topicContent);
    }

    /**
     * Upload user content as base 64 data to a given topic
     * TODO This and save(file) are very similar. Deduplicate the code.
     *
     * @param data        Base64 encoded file to upload
     * @param topicId     The topic to upload the file to
     * @param creatorId   The creator of the topic content. The authorized user by default.
     * @param description A description of the uploaded content
     * @return The saved topic content record
     * @throws IOException If s3 upload fails to read the file
     */
    public TopicContent save(String data, Integer topicId, Integer creatorId, String description) throws IOException {
        Entity creator;
        Topic topic = topicService.findTopicById(topicId);

        // only let the user set the topic content's creator if they're an admin
        if (entityService.getAuthenticatedEntity().isAdmin()) {
            creator = entityService.findEntityById(creatorId);
        } else {
            creator = entityService.getAuthenticatedEntity();
        }

        if (topic == null) {
            throw new ResourceNotFoundException("The topic id, " + topicId + ", does not exist");
        }

        // Description can be null, but otherwise there's a length limit
        if (description != null && description.length() > 4000) {
            throw new BusinessLogicException("Topic Content Description cannot be more than 4000 characters");
        }

        // upload after s3 validation.
        // then insert into db since we have the bucket name and s3 key
        String uuid = UUID.randomUUID().toString();
        String s3key = keyPrefix + uuid; // topic-content/{{GUID}}

        // TODO move upload stuff to s3
        byte[] bI = FileUtils.decodeBase64(data);
        InputStream fis = new ByteArrayInputStream(bI);

        // generate object metadata based on input stream
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(bI.length);
        metadata.setContentType("image/jpeg");

        s3Service.upload(fis, s3key, metadata);

        // Create topic content
        TopicContent topicContent = new TopicContent();
        topicContent.setDescription(description);
        topicContent.setCreator(creator);
        topicContent.setTopic(topic);
        topicContent.setS3bucket(bucketName);
        topicContent.setS3key(s3key);

        // get origin date of the photo if its available
        Date dateTaken;

        try {
            dateTaken = FileUtils.getDate(new BufferedInputStream(fis));

            // log failures to parse metadata/exif data
            if (dateTaken == null) {
                logger.error("Failed to retrieve date from exif data for " + s3key + ".");
                dateTaken = new Date();
            }
        } catch (ImageProcessingException e) {
            dateTaken = new Date();
            logger.error("Failed to retrieve date from exif data for " + s3key + ". " + e.getMessage());
        }

        return topicContentRepository.save(topicContent);
    }

    /**
     * Upload user content to a given topic
     *
     * @param file        The file to upload
     * @param topicId     The topic to upload the file to
     * @param creatorId   The creator of the topic content. The authorized user by default.
     * @param description A description of the uploaded content
     * @param dateTaken
     * @return The saved topic content record
     * @throws IOException If s3 upload fails to read the file
     */
    public TopicContent save(MultipartFile file, Integer topicId, Integer creatorId, String description, Date dateTaken) throws IOException {


        Entity creator;
        // only let the user set the topic content's creator if they're an admin
        if (entityService.getAuthenticatedEntity().isAdmin()) {
            creator = entityService.findEntityById(creatorId);
        } else {
            creator = entityService.getAuthenticatedEntity();
        }

        Topic topic = topicService.findTopicById(topicId);
        if (topic == null) {
            throw new ResourceNotFoundException("The topic id, " + topicId + ", does not exist");
        }

        // Description can be null, but otherwise there's a length limit
        if (description != null && description.length() > 4000) {
            throw new BusinessLogicException("Topic Content Description cannot be more than 4000 characters");
        }

        // upload after s3 validation.
        // then insert into db since we have the bucket name and s3 key
        String uuid = UUID.randomUUID().toString();
        String fileExtension = MimeTypes.getFileExtention(file.getContentType());
        String s3key = keyPrefix + uuid + "." + fileExtension; // topic-content/{{GUID}}

        try {
            // s3 url key is probably unique - should probably use GUID
            s3Service.upload(file, s3key);
        } catch (IOException e) {
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

        // get origin date of the photo if its available
        if (dateTaken == null) {
            try {
                dateTaken = FileUtils.getDate(new BufferedInputStream(file.getInputStream()));

                // log failures to parse metadata/exif data
                if (dateTaken == null) {
                    logger.error("Failed to retrieve date from exif data for " + s3key + ".");
                    dateTaken = new Date();
                }
            } catch (ImageProcessingException e) {
                dateTaken = new Date();
                logger.error("Failed to retrieve date from exif data for " + s3key + ". " + e.getMessage());
            }
        }

        topicContent.setDateTaken(dateTaken);
        return topicContentRepository.save(topicContent);
    }

    /**
     * Get an entity's topic content based on the topics they're a part of.
     *
     * @param entityId the entity id for which to query topic content
     * @param page     how far back to start grabbing topic content. (e.g. get the 100th to 150th topic content items)
     * @param pageSize how much topic content to get
     * @return A list of the entity's topic content
     */
    public List<TopicContent> findByTopicMember(int entityId, int page, int pageSize) {
        entityService.findEntityById(entityId); //
        int offset = page * pageSize;
        int limit = offset + pageSize;
        return topicContentRepository.findByTopicMember(entityId, offset, limit);
    }

    public void delete(TopicContent topicContent) {
        s3Service.delete(topicContent.getS3bucket(), topicContent.getS3key());
        topicContentRepository.deleteById(topicContent.getId());
    }
}
