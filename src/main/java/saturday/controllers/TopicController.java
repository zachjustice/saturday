package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saturday.domain.Topic;
import saturday.domain.TopicContent;
import saturday.domain.TopicMember;
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.ProcessingResourceException;
import saturday.services.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by zachjustice on 7/27/17.
 */
@RestController
public class TopicController {

    private final TopicMemberService topicMemberService;
    private final TopicService topicService;
    private final EntityService entityService;
    private final TopicContentService topicContentService;
    private final PermissionService permissionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TopicController(TopicMemberService topicMemberService, TopicService topicService, EntityService entityService, TopicContentService topicContentService, PermissionService permissionService) {
        this.topicMemberService = topicMemberService;
        this.topicService = topicService;
        this.entityService = entityService;
        this.topicContentService = topicContentService;
        this.permissionService = permissionService;
    }

    @RequestMapping(value = "/topics", method = RequestMethod.POST)
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) throws ProcessingResourceException {

        // TODO constraints around topics created per minute, max amount of topics per week / month?
        //      similar to how aws api gateway does rate limiting?
        topic = topicService.saveTopic(topic);

        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    @RequestMapping(value = "/topics", method = RequestMethod.GET)
    public ResponseEntity<List<Topic>> findTopicByName(@RequestParam(value = "name") String name) throws ProcessingResourceException, AccessDeniedException {
        List<Topic> topics = topicService.findTopicByName(name);

        topics = topics.stream().filter(permissionService::canView).collect(Collectors.toList());

        return new ResponseEntity<>(topics, HttpStatus.OK);
    }

    @RequestMapping(value = "/topics/{id}", method = RequestMethod.GET)
    public ResponseEntity<Topic> getTopic(@PathVariable(value = "id") int id) throws AccessDeniedException {
        Topic topic = topicService.findTopicById(id);

        if(!permissionService.canView(topic)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions");
        }

        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    // TODO auth check so only owner/admin can update id-topic
    @RequestMapping(value = "/topics/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Topic> saveTopic(
            @PathVariable(value = "id") int id,
            @RequestBody Topic topic
    ) throws ProcessingResourceException, AccessDeniedException {
        topic = topicService.findTopicById(topic.getId());

        if(!permissionService.canModify(topic)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions");
        }

        topic = topicService.saveTopic(topic);
        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    @RequestMapping(value = "/topics/{id}/topic_content", method = RequestMethod.GET)
    public ResponseEntity<List<TopicContent>> getTopicContentByTopic(@PathVariable(value = "id") int id) throws AccessDeniedException {
        Topic topic = topicService.findTopicById(id);

        if(!permissionService.canView(topic)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions");
        }

        List<TopicContent> topicContentList = topicContentService.findTopicContentByTopicId(id);
        return new ResponseEntity<>(topicContentList, HttpStatus.OK);
    }

    @RequestMapping(value = "topics/{id}/topic_members", method = RequestMethod.GET)
    public ResponseEntity<List<TopicMember>> getTopicTopicMember(@PathVariable(value = "id") int id) throws AccessDeniedException {
        Topic topic = topicService.findTopicById(id);

        if (!permissionService.canView(topic)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        List<TopicMember> topicMembers = topicMemberService.findByTopicId(id);
        return new ResponseEntity<>(topicMembers, HttpStatus.OK);
    }

    @RequestMapping(value = "topics/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Topic> delete(@PathVariable(value = "id") int id) throws AccessDeniedException {
        Topic topic = topicService.findTopicById(id);

        if (!permissionService.canDelete(topic)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        topicService.delete(topic);
        return new ResponseEntity<>(topic, HttpStatus.OK);
    }
}
