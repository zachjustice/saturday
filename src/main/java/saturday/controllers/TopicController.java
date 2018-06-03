package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saturday.domain.*;
import saturday.exceptions.AccessDeniedException;
import saturday.services.*;

import java.util.ArrayList;
import java.util.List;

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

    @Value("${saturday.topic.invite.status.accepted}")
    private int TOPIC_MEMBER_STATUS_ACCEPTED;
    @Value("${saturday.topic.role.admin}")
    private int TOPIC_ROLE_ADMIN;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TopicController(TopicMemberService topicMemberService, TopicService topicService, EntityService entityService, TopicContentService topicContentService, PermissionService permissionService) {
        this.topicMemberService = topicMemberService;
        this.topicService = topicService;
        this.entityService = entityService;
        this.topicContentService = topicContentService;
        this.permissionService = permissionService;
    }

    @RequestMapping(value = "/topics", method = RequestMethod.POST)
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {

        // TODO AWS API Gateway style rate limiting for topics?
        // Create a topic and set the current user as the only member
        topic = topicService.saveTopic(topic);

        Entity currentEntity = entityService.getAuthenticatedEntity();

        TopicMemberStatus acceptedStatus = new TopicMemberStatus();
        acceptedStatus.setId(TOPIC_MEMBER_STATUS_ACCEPTED);

        TopicRole adminTopicRole = new TopicRole();
        adminTopicRole.setId(TOPIC_ROLE_ADMIN);

        TopicMember topicMember = new TopicMember();
        topicMember.setTopic(topic);
        topicMember.setCreator(currentEntity);
        topicMember.setEntity(currentEntity);
        topicMember.setTopicRole(adminTopicRole);

        topicMember.setStatus(acceptedStatus);
        topicMemberService.save(topicMember);

        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    @RequestMapping(value = "/topics", method = RequestMethod.GET)
    public ResponseEntity<List<Topic>> findTopicByName(@RequestParam(value = "name") String name) {
        List<Topic> matchingTopics = topicService.findTopicByName(name);
        List<Topic> topics = new ArrayList<>(matchingTopics.size());

        // for topic search by name make sure only permissable topics are shown
        for (Topic topic : matchingTopics) {
            if (permissionService.canView(topic)) {
                topics.add(topic);
            }
        }

        return new ResponseEntity<>(topics, HttpStatus.OK);
    }

    @RequestMapping(value = "/topics/{id}", method = RequestMethod.GET)
    public ResponseEntity<Topic> getTopic(@PathVariable(value = "id") int id) {
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
    ) {
        topic = topicService.findTopicById(topic.getId());

        if(!permissionService.canModify(topic)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions");
        }

        topic = topicService.saveTopic(topic);
        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    @RequestMapping(value = "topics/{id}/topic_members", method = RequestMethod.GET)
    public ResponseEntity<List<TopicMember>> getTopicTopicMember(@PathVariable(value = "id") int id) {
        Topic topic = topicService.findTopicById(id);

        if (!permissionService.canView(topic)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        List<TopicMember> topicMembers = topicMemberService.findByTopicId(id);
        return new ResponseEntity<>(topicMembers, HttpStatus.OK);
    }

    @RequestMapping(value = "topics/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Topic> delete(@PathVariable(value = "id") int id) {
        Topic topic = topicService.findTopicById(id);

        if (!permissionService.canDelete(topic)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        topicService.delete(topic);
        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    /**
     * Get a list of an entity's topics
     * @param id The entity for which to retrieve topics
     * @return A list of topics
     */
    @RequestMapping(value = "/entities/{id}/topics", method = RequestMethod.GET)
    public ResponseEntity<List<Topic>> getEntityTopics(
            @PathVariable(value="id") int id
    ) {
        Entity entity = entityService.findEntityById(id);

        if(!permissionService.canAccess(entity)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        return new ResponseEntity<>(entity.getTopics(), HttpStatus.OK);
    }
}
