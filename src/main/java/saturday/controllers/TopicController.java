package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saturday.delegates.TopicDelegate;
import saturday.delegates.TopicRolePermissionDelegate;
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
    private final PermissionService permissionService;
    private final TopicRolePermissionDelegate topicRolePermissionDelegate;
    private final TopicDelegate topicDelegate;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TopicController(TopicMemberService topicMemberService, TopicService topicService, EntityService entityService, PermissionService permissionService, TopicRolePermissionDelegate topicRolePermissionDelegate, TopicDelegate topicDelegate) {
        this.topicMemberService = topicMemberService;
        this.topicService = topicService;
        this.entityService = entityService;
        this.permissionService = permissionService;
        this.topicRolePermissionDelegate = topicRolePermissionDelegate;
        this.topicDelegate = topicDelegate;
    }

    @RequestMapping(value = "/topics", method = RequestMethod.POST)
    public ResponseEntity<Topic> createTopic(@RequestBody Topic topic) {

        // TODO AWS API Gateway style rate limiting for topics?
        // Create a topic and set the current user as the only member
        topic = topicDelegate.save(topic);

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
        topic = topicDelegate.update(topic);
        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    @RequestMapping(value = "topics/{id}/topic_members", method = RequestMethod.GET)
    public ResponseEntity<List<TopicMember>> getTopicTopicMember(@PathVariable(value = "id") int id) {
        Topic topic = topicService.findTopicById(id);

        if (!permissionService.canView(topic)) {
            throw new AccessDeniedException();
        }

        List<TopicMember> topicMembers = topicMemberService.findByTopicId(id);
        return new ResponseEntity<>(topicMembers, HttpStatus.OK);
    }

    @RequestMapping(value = "topics/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Topic> delete(@PathVariable(value = "id") int id) {
        Topic topic = topicService.findTopicById(id);

        if (!permissionService.canDelete(topic)) {
            throw new AccessDeniedException();
        }

        topicService.delete(topic);
        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    /**
     * Get permissions settings for a topic
     * @param id A Topic Id
     * @return Permissions for the topic
     */
    @RequestMapping(value = "/topics/{id}/permissions", method = RequestMethod.GET)
    public ResponseEntity<List<TopicRolePermission>> getTopicRolePermission(
            @PathVariable int id
    ) {
        List<TopicRolePermission> topicRolePermissions = this.topicRolePermissionDelegate.getPermissions(id);
        return new ResponseEntity<>(topicRolePermissions, HttpStatus.OK);
    }

    /**
     * Get a list of an entity's topics
     * @param entityId The entity for which to retrieve topics
     * @return A list of topics
     */
    @RequestMapping(value = "/entities/{id}/topics", method = RequestMethod.GET)
    public ResponseEntity<List<Topic>> getEntityTopics(
            @PathVariable(value="id") int entityId
    ) {
        List<Topic> entityTopics = this.topicDelegate.getEntityTopics(entityId);
        return new ResponseEntity<>(entityTopics, HttpStatus.OK);
    }
}
