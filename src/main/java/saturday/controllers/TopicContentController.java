package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import saturday.domain.TopicContent;
import saturday.domain.TopicContentRequest;
import saturday.exceptions.ProcessingResourceException;
import saturday.services.PermissionService;
import saturday.services.TopicContentService;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

@RestController
public class TopicContentController {

    private final TopicContentService topicContentService;
    private final PermissionService permissionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TopicContentController(TopicContentService topicContentService, PermissionService permissionService) {
        this.topicContentService = topicContentService;
        this.permissionService = permissionService;
    }

    @RequestMapping(value = "/topic_content", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<TopicContent> createTopicContent(
            @RequestBody TopicContentRequest topicContentRequest
    ) throws IOException, ProcessingResourceException {

        if(!permissionService.canCreate(topicContentRequest)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        TopicContent topicContent = topicContentService.save(topicContentRequest);
        logger.info("Created TopicContent: " + topicContentRequest.toString());

        return new ResponseEntity<>(topicContent, HttpStatus.OK);
    }


    @RequestMapping(value = "/topic_content", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TopicContent> createTopicContent(
            @RequestParam("creatorId")   Integer       creatorId,
            @RequestParam("topicId")     Integer       topicId,
            @RequestParam("description") String        description,
            @RequestParam("dateTaken")   String        dateTaken,
            @RequestParam("file")        MultipartFile file
    ) throws IOException, ProcessingResourceException {

        Calendar calDateTaken = javax.xml.bind.DatatypeConverter.parseDateTime(dateTaken);
        Date date = calDateTaken.getTime();

        TopicContentRequest topicContentRequest = new TopicContentRequest();
        topicContentRequest.setCreatorId(creatorId);
        topicContentRequest.setTopicId(topicId);
        topicContentRequest.setDescription(description);
        topicContentRequest.setDateTaken(date);
        topicContentRequest.setFile(file);

        if(!permissionService.canCreate(topicContentRequest)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        TopicContent topicContent = topicContentService.save(topicContentRequest);

        return new ResponseEntity<>(topicContent, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_content/{id}", method = RequestMethod.GET)
    public ResponseEntity<TopicContent> findTopicByName(@PathVariable(value="id") int id) throws ProcessingResourceException {

        TopicContent topicContent = topicContentService.findTopicContentById(id);

        if(!permissionService.canView(topicContent)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        return new ResponseEntity<>(topicContent, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_content/{id}", method = RequestMethod.PUT)
    public ResponseEntity<TopicContent> update(
            @PathVariable(value="id") int id,
            @RequestBody TopicContent newTopicContent
    ) {
        TopicContent topicContent = topicContentService.findTopicContentById(id);

        if(!permissionService.canModify(topicContent)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        topicContent = topicContentService.updateTopicContent(topicContent, newTopicContent);
        return new ResponseEntity<>(topicContent, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_content/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<String> update(
            @PathVariable(value="id") int id
    ) {
        TopicContent topicContent = topicContentService.findTopicContentById(id);
        if(!permissionService.canDelete(topicContent)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        topicContentService.delete(topicContent);
        return new ResponseEntity<>("Success", HttpStatus.OK);
    }
}
