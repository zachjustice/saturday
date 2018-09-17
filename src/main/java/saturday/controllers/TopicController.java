package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import saturday.delegates.TopicDelegate;
import saturday.delegates.TopicRolePermissionDelegate;
import saturday.domain.CreateTopicRequest;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.domain.TopicRolePermission;
import saturday.exceptions.AccessDeniedException;
import saturday.services.EntityService;
import saturday.services.PermissionService;
import saturday.services.TopicMemberService;
import saturday.services.TopicService;

import java.util.ArrayList;
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
    public ResponseEntity<Topic> createTopic(@RequestBody CreateTopicRequest createTopicRequest) {

        // TODO AWS API Gateway style rate limiting for topics?
        // Create a topic and set the current user as the only member
        Topic topic = topicDelegate.save(createTopicRequest);

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
     * @param id An entity Id
     * @return Permissions for the topic
     */
    @RequestMapping(value = "/entities/{id}/topic_permissions", method = RequestMethod.GET)
    public ResponseEntity<List<TopicRolePermission>> getEntityTopicRolePermission(
            @PathVariable int id
    ) {
        List<TopicRolePermission> topicRolePermissions = this.topicRolePermissionDelegate.getEntityPermissions(id);
        return new ResponseEntity<>(topicRolePermissions, HttpStatus.OK);
    }

    /**
     * Get a list of an entity's topics
     * @param entityId The entity for which to retrieve topics
     * @return A list of topics
     */
    @RequestMapping(value = "/entities/{id}/topic_member_status_id/{status_id}", method = RequestMethod.GET)
    public ResponseEntity<List<Topic>> getTopicByEntityAndTopicMemberStatusId(
            @PathVariable(value = "id") int entityId,
            @PathVariable("status_id") int statusId
    ) {
        List<Topic> entityTopics = this.topicDelegate.getTopicsByEntityAndTopicMemberStatusId(entityId, statusId);
        return new ResponseEntity<>(entityTopics, HttpStatus.OK);
    }

    /**
     * Get topics for which the given entity is of the given topic role
     * @param entityId Entity Id
     * @param topicRoleId Topic Role Id
     * @return List of Topic matching the search criteria
     */
    @RequestMapping(value = "/entities/{entity_id}/topic_roles/{topic_role_id}", method = RequestMethod.GET)
    public ResponseEntity<List<Topic>> getTopicsEntityIsAdminOf(
            @PathVariable(value = "entity_id") int entityId,
            @PathVariable(value = "topic_role_id") int topicRoleId
    ) {
        Entity entity = entityService.findEntityById(entityId);

        if (!permissionService.canAccess(entity)) {
            throw new AccessDeniedException();
        }

        List<Topic> topicsEntityIsAdminOf = topicMemberService.findByEntityIdAndTopicRoleId(entityId, topicRoleId)
                .stream()
                .map(TopicMember::getTopic)
                .collect(Collectors.toList());

        return new ResponseEntity<>(topicsEntityIsAdminOf, HttpStatus.OK);
    }


    /**
     * Get topic info for a shareable topic
     * @param shareLink Unique id for the group for sharing
     */
    @RequestMapping(value = "/share/{share_link}", method = RequestMethod.GET)
    public ResponseEntity<Topic> getTopicsEntityIsAdminOf(
            @PathVariable(value = "share_link") String shareLink
    ) {
        Topic topic = topicService.findEntityByInviteLink(shareLink);

        if (!permissionService.canShare(topic)) {
            throw new AccessDeniedException();
        }

        return new ResponseEntity<>(topic, HttpStatus.OK);
    }
}
