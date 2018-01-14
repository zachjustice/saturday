package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saturday.domain.Entity;
import saturday.domain.TopicMember;
import saturday.exceptions.AccessDeniedException;
import saturday.services.EntityService;
import saturday.services.PermissionService;
import saturday.services.TopicMemberService;
import saturday.services.TopicService;

import java.util.List;
import java.util.Map;

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

    /**
     * Retrieve topic invites where the given entity is either the sender or receiver
     * @param involvedPartyId  The entity which sent or received the invites
     * @return HashMap of the sent and received topic invites involving the given user
     */
    @RequestMapping(value = "/topic_members", method = RequestMethod.GET)
    public ResponseEntity<Map<String, List<TopicMember>>> getTopicMemberByInvolvedParty(
            @RequestParam("involved_party") int involvedPartyId
    ) {
        Entity involvedParty = entityService.findEntityById(involvedPartyId);

        if(!permissionService.canAccess(involvedParty)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        Map<String, List<TopicMember>> sentAndReceivedTopicInvites = topicMemberService.getSentAndReceivedTopicInvites(involvedParty);
        return new ResponseEntity<>(sentAndReceivedTopicInvites, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_members/{id}", method = RequestMethod.GET)
    public ResponseEntity<TopicMember> getTopicMember(@PathVariable int id) {
        TopicMember topicMember = this.topicMemberService.findById(id);

        if(!permissionService.canView(topicMember)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        return new ResponseEntity<>(topicMember, HttpStatus.OK);
    }

    /**
     * Create a topic member.
     * @param topicMember The topic member to create
     * @return The created topic member
     */
    @RequestMapping(value = "/topic_members", method = RequestMethod.POST)
    public ResponseEntity<TopicMember> saveTopicMember(@RequestBody TopicMember topicMember) {
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
    ) {
        TopicMember oldTopicMember = topicMemberService.findById(newTopicMember.getId());
        if(!permissionService.canModify(oldTopicMember, newTopicMember)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        newTopicMember = topicMemberService.update(oldTopicMember, newTopicMember);
        return new ResponseEntity<>(newTopicMember, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_members/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<TopicMember> delete(@PathVariable(value = "id") int id) {

        // Instead of deleting topic members, move the status to "rescinded" or "left_topic"
        // so we can avoid repeatedly sending invites
        if(!entityService.getAuthenticatedEntity().isAdmin()) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        topicMemberService.delete(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
