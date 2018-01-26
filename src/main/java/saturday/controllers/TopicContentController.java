package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicContent;
import saturday.domain.TopicContentRequest;
import saturday.exceptions.AccessDeniedException;
import saturday.services.EntityService;
import saturday.services.PermissionService;
import saturday.services.TopicContentService;
import saturday.services.TopicService;

import java.io.IOException;
import java.util.List;

@RestController
public class TopicContentController {

    private final TopicContentService topicContentService;
    private final PermissionService permissionService;
    private final EntityService entityService;
    private final TopicService topicService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TopicContentController(TopicContentService topicContentService, PermissionService permissionService, EntityService entityService, TopicService topicService) {
        this.topicContentService = topicContentService;
        this.permissionService = permissionService;
        this.entityService = entityService;
        this.topicService = topicService;
    }

    @RequestMapping(value = "/topic_content", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<TopicContent> createTopicContent(
            @RequestBody TopicContentRequest topicContentRequest
    ) throws IOException {

        if (!permissionService.canCreate(topicContentRequest)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        TopicContent topicContent = topicContentService.save(
                topicContentRequest.getData(),
                topicContentRequest.getTopicId(),
                topicContentRequest.getCreatorId(),
                topicContentRequest.getDescription()
        );
        logger.info("Created TopicContent: " + topicContentRequest.toString());

        return new ResponseEntity<>(topicContent, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_content", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TopicContent> createTopicContent(
            @RequestParam("creatorId") Integer creatorId,
            @RequestParam("topicId") Integer topicId,
            @RequestParam("description") String description,
            @RequestParam("file") MultipartFile file
    ) throws IOException {

        // TODO get date taken from photo exif
        TopicContentRequest topicContentRequest = new TopicContentRequest();
        topicContentRequest.setCreatorId(creatorId);
        topicContentRequest.setTopicId(topicId);
        topicContentRequest.setDescription(description);
        topicContentRequest.setFile(file);

        if (!permissionService.canCreate(topicContentRequest)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        TopicContent topicContent = topicContentService.save(file, topicId, creatorId, description);

        return new ResponseEntity<>(topicContent, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_content/{id}", method = RequestMethod.GET)
    public ResponseEntity<TopicContent> findTopicByName(@PathVariable(value = "id") int id) {

        TopicContent topicContent = topicContentService.findTopicContentById(id);

        if (!permissionService.canView(topicContent)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        return new ResponseEntity<>(topicContent, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_content/{id}", method = RequestMethod.PUT)
    public ResponseEntity<TopicContent> update(
            @PathVariable(value = "id") int id,
            @RequestBody TopicContent newTopicContent
    ) {
        TopicContent topicContent = topicContentService.findTopicContentById(id);

        if (!permissionService.canModify(topicContent)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        topicContent = topicContentService.updateTopicContent(topicContent, newTopicContent);
        return new ResponseEntity<>(topicContent, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_content/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> delete(
            @PathVariable(value = "id") int id
    ) {
        TopicContent topicContent = topicContentService.findTopicContentById(id);
        if (!permissionService.canDelete(topicContent)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        topicContentService.delete(topicContent);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }

    /**
     * Get entity's topic content
     *
     * @param id       The entity to retrieve topic content for
     * @param page     when page of results to return
     * @param pageSize how large of a page to use
     * @return A list of topic content
     */
    @RequestMapping(value = "/entities/{id}/topic_content", method = RequestMethod.GET)
    public ResponseEntity<List<TopicContent>> getEntityTopicContent(
            @PathVariable(value = "id") int id,
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "page_size", defaultValue = "30") int pageSize
    ) {

        Entity entity = entityService.findEntityById(id);

        if (!permissionService.canAccess(entity)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        List<TopicContent> entityTopicContent = topicContentService.findByTopicMember(id, page, pageSize);

        return new ResponseEntity<>(entityTopicContent, HttpStatus.OK);
    }

    /**
     * Returns a paginated list of topic content for a topic
     *
     * @param pageable paging and sorting information for the request
     * @return list of topic content
     */
    @RequestMapping(value = "/topics/{id}/topic_content", method = RequestMethod.GET)
    public ResponseEntity<Page<TopicContent>> getTopicContentByTopic(
            @PathVariable(value = "id") int topicId,
            Pageable pageable
    ) {
        Topic topic = topicService.findTopicById(topicId);

        if (!permissionService.canView(topic)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions");
        }

        Page<TopicContent> topicContentList = topicContentService.findTopicContentByTopicId(pageable, topicId);
        return new ResponseEntity<>(topicContentList, HttpStatus.OK);
    }
}
