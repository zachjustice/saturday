package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import saturday.domain.TopicContent;
import saturday.services.PermissionService;
import saturday.services.TopicContentService;

import java.io.IOException;

@RestController
public class TopicContentController {

    private final TopicContentService topicContentService;
    private final PermissionService permissionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TopicContentController(TopicContentService topicContentService, PermissionService permissionService) {
        this.topicContentService = topicContentService;
        this.permissionService = permissionService;
    }

    @RequestMapping(value = "/topic_content", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<TopicContent> createTopicContent(
            @RequestParam("creatorId")   Integer creatorId,
            @RequestParam("topicId")     Integer topicId,
            @RequestParam("description") String description,
            @RequestParam("file")        MultipartFile file
    ) throws IOException {

        TopicContent topicContent = topicContentService.save(file, creatorId, topicId, description);
        return new ResponseEntity<>(topicContent, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_content/{id}", method = RequestMethod.GET)
    public ResponseEntity<TopicContent> findTopicByName(@PathVariable(value="id") int id) {

        TopicContent topicContent = topicContentService.findTopicContentById(id);

        if(!permissionService.canAccess(topicContent)) {
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
