package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saturday.domain.TopicMember;
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ResourceNotFoundException;
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
    public ResponseEntity<TopicMember> getTopicMember(@PathVariable int id) throws ResourceNotFoundException, AccessDeniedException, BusinessLogicException {
        TopicMember topicMember = this.topicMemberService.findById(id);

        if(!permissionService.canView(topicMember)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        return new ResponseEntity<>(topicMember, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_members", method = RequestMethod.POST)
    public ResponseEntity<TopicMember> saveTopicMember(@RequestBody TopicMember topicMember) throws BusinessLogicException, AccessDeniedException {
        if(!permissionService.canCreate(topicMember)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        topicMember = topicMemberService.save(topicMember);
        return new ResponseEntity<>(topicMember, HttpStatus.OK);
    }

    @RequestMapping(value = "topic_members/{id}", method = RequestMethod.PUT)
    public ResponseEntity<TopicMember> update(
            @PathVariable(value = "id") int id,
            @RequestBody TopicMember newTopicMember
    ) throws AccessDeniedException, BusinessLogicException, ResourceNotFoundException {
        TopicMember oldTopicMember = topicMemberService.findById(newTopicMember.getId());
        if(!permissionService.canModify(oldTopicMember, newTopicMember)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        newTopicMember = topicMemberService.update(oldTopicMember, newTopicMember);
        return new ResponseEntity<>(newTopicMember, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_members/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<TopicMember> delete(@PathVariable(value = "id") int id) throws AccessDeniedException, TopicMemberNotFoundException {

        // Instead of deleting topic members, move the status to "rescinded" or "left_topic"
        // so we can avoid repeatedly sending invites
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
