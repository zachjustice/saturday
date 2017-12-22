package saturday.controllers;

import javassist.tools.web.BadHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.*;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.domain.TopicMemberRequest;
import saturday.exceptions.ProcessingResourceException;
import saturday.exceptions.TopicMemberNotFoundException;
import saturday.services.EntityService;
import saturday.services.PermissionService;
import saturday.services.TopicMemberService;
import saturday.services.TopicService;

@RestController()
public class TopicMemberController {
    private final TopicMemberService topicMemberService;
    private final EntityService entityService;
    private final TopicService topicService;
    private final PermissionService permissionService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TopicMemberController(TopicMemberService topicMemberService, EntityService entityService, TopicService topicService, PermissionService permissionService) {
        this.topicMemberService = topicMemberService;
        this.entityService = entityService;
        this.topicService = topicService;
        this.permissionService = permissionService;
    }

    @RequestMapping(value = "/topic_members/{id}", method = RequestMethod.GET)
    public ResponseEntity<TopicMember> getTopicMember(@PathVariable int id) throws TopicMemberNotFoundException {
        TopicMember topicMember = this.topicMemberService.findById(id);
        if(topicMember == null) {
            throw new TopicMemberNotFoundException("Couldn't find topic member with id " + id);
        }

        if(!permissionService.canAccess(topicMember)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        return new ResponseEntity<>(topicMember, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_members", method = RequestMethod.POST)
    public ResponseEntity<TopicMember> saveTopicMember(@RequestBody TopicMemberRequest topicMemberRequest) throws BadHttpRequest, ProcessingResourceException {
        if(!permissionService.canCreate(topicMemberRequest)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        Topic topic = topicService.findTopicById(topicMemberRequest.getTopicId());
        if(topic == null) {
            throw new BadHttpRequest(new Exception("Invalid topic id " + topicMemberRequest.getTopicId()));
        }

        Entity entity = entityService.findEntityById(topicMemberRequest.getEntityId());
        if(entity == null) {
            throw new BadHttpRequest(new Exception("Invalid entity id " + topicMemberRequest.getEntityId()));
        }

        TopicMember existing = topicMemberService.findByEntityAndTopic(entity, topic);
        if(existing != null) {
            // Entity is already a member of the topic
            return new ResponseEntity<>(existing, HttpStatus.OK);
        }

        TopicMember topicMember = new TopicMember();
        topicMember.setEntity(entity);
        topicMember.setTopic(topic);

        logger.info("TopicMember " + topicMember);

        topicMember = topicMemberService.save(topicMember);
        return new ResponseEntity<>(topicMember, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_members/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<TopicMember> delete(@PathVariable(value = "id") int id) {
        if(!entityService.getAuthenticatedEntity().isAdmin()) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        try {
            topicMemberService.delete(id);
        } catch(EmptyResultDataAccessException ex) {
            throw new TopicMemberNotFoundException("No topic member with id " + id + " exists!");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
