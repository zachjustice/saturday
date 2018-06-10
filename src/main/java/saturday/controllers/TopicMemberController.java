package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saturday.domain.Entity;
import saturday.domain.TopicMember;
import saturday.domain.TopicMemberStatus;
import saturday.exceptions.AccessDeniedException;
import saturday.publishers.SaturdayEventPublisher;
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
    private final PermissionService permissionService;
    private final SaturdayEventPublisher saturdayEventPublisher;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TopicMemberController(
            TopicMemberService topicMemberService,
            EntityService entityService,
            PermissionService permissionService,
            SaturdayEventPublisher saturdayEventPublisher
    ) {
        this.topicMemberService = topicMemberService;
        this.entityService = entityService;
        this.permissionService = permissionService;
        this.saturdayEventPublisher = saturdayEventPublisher;
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
        this.saturdayEventPublisher.publishInviteEvent(topicMember);
        return new ResponseEntity<>(topicMember, HttpStatus.OK);
    }

    @RequestMapping(value = "topic_members/{id}", method = RequestMethod.PUT)
    public ResponseEntity<TopicMember> update(
            @PathVariable(value = "id") int id,
            @RequestBody TopicMember newTopicMember
    ) {
        if(!permissionService.canModify(newTopicMember)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        newTopicMember = topicMemberService.update(newTopicMember);
        return new ResponseEntity<>(newTopicMember, HttpStatus.OK);
    }

    @RequestMapping(
            value = "entities/{entity_id}/topics/{topic_id}/topic_member_status/{topic_member_status_id}",
            method = RequestMethod.PUT
    )
    public ResponseEntity<TopicMember> update(
            @PathVariable(value = "entity_id") int entityId,
            @PathVariable(value = "topic_id") int topicId,
            @PathVariable(value = "topic_member_status_id") int topicMemberStatusId
    ) {
        TopicMemberStatus topicMemberStatus = new TopicMemberStatus();
        topicMemberStatus.setId(topicMemberStatusId);

        TopicMember topicMember = topicMemberService.findByEntityIdAndTopicId(entityId, topicId);
        topicMember.setStatus(topicMemberStatus);

        if(!permissionService.canModify(topicMember )) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        topicMember = topicMemberService.update(topicMember);
        return new ResponseEntity<>(topicMember, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_members/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<TopicMember> delete(@PathVariable(value = "id") int id) {

        // Instead of deleting topic members, move the status to "rescinded" or "left_topic"
        // so we can avoid repeatedly sending invites
        if(!entityService.getAuthenticatedEntity().isAdmin()) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        topicMemberService.deleteById(id);

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
