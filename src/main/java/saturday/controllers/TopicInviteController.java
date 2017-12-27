package saturday.controllers;

import javassist.tools.web.BadHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saturday.domain.TopicInvite;
import saturday.domain.TopicInviteRequest;
import saturday.domain.TopicInviteStatus;
import saturday.domain.TopicMember;
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.ProcessingResourceException;
import saturday.services.*;

@RestController()
public class TopicInviteController {
    private final TopicInviteService topicInviteService;
    private final EntityService entityService;
    private final TopicService topicService;
    private final PermissionService permissionService;
    private final TopicMemberService topicMemberService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${saturday.topic.invite.status.pending}")
    private int TOPIC_INVITE_PENDING;
    @Value("${saturday.topic.invite.status.rejected}")
    private int TOPIC_INVITE_REJECTED;
    @Value("${saturday.topic.invite.status.accepted}")
    private int TOPIC_INVITE_ACCEPTED;

    @Autowired
    public TopicInviteController(TopicInviteService topicInviteService, EntityService entityService, TopicService topicService, PermissionService permissionService, TopicMemberService topicMemberService) {
        this.topicInviteService = topicInviteService;
        this.entityService = entityService;
        this.topicService = topicService;
        this.permissionService = permissionService;
        this.topicMemberService = topicMemberService;
    }

    @RequestMapping(value = "/topic_invites/{id}", method = RequestMethod.GET)
    public ResponseEntity<TopicInvite> getTopicInvite(@PathVariable int id) throws AccessDeniedException {
        TopicInvite topicInvite = this.topicInviteService.findById(id);

        if (!permissionService.canView(topicInvite)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        return new ResponseEntity<>(topicInvite, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_invites/{id}/status", method = RequestMethod.PUT)
    public ResponseEntity<TopicInvite> acceptTopicInvite(@PathVariable int id, @RequestBody TopicInviteStatus newStatus) throws Exception {
        TopicInvite topicInvite = this.topicInviteService.findById(id);
        if (topicInvite.getStatus().getId() != TOPIC_INVITE_PENDING) {
            throw new Exception("Error updating topic invite status. This topic invite has status " + topicInvite.getStatus().getRole());
        }

        // TODO find better way to do this
        if (newStatus.getId() == TOPIC_INVITE_ACCEPTED) {
            if (!permissionService.canAcceptInvite(topicInvite)) {
                throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
            }

            TopicMember member = new TopicMember();
            member.setEntity(topicInvite.getInvitee());
            member.setTopic(topicInvite.getTopic());

            this.topicInviteService.saveStatus(topicInvite, newStatus);
            this.topicMemberService.save(member);

        } else if (newStatus.getId() == TOPIC_INVITE_REJECTED) {
            if (!permissionService.canRejectInvite(topicInvite)) {
                throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
            }

            this.topicInviteService.saveStatus(topicInvite, newStatus);
        } else {
            throw new Exception("New topic invite status must either be REJECTED or ACCEPTED");
        }

        return new ResponseEntity<>(topicInvite, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_invites", method = RequestMethod.POST)
    public ResponseEntity<TopicInvite> saveTopicInvite(@RequestBody TopicInviteRequest topicInviteRequest) throws BadHttpRequest, ProcessingResourceException, AccessDeniedException {

        if (!permissionService.canSendInvite(topicInviteRequest)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        TopicInvite topicInvite = topicInviteService.save(topicInviteRequest);
        return new ResponseEntity<>(topicInvite, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_invites/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<TopicInvite> delete(@PathVariable(value = "id") int id) throws AccessDeniedException {
        TopicInvite topicInvite = topicInviteService.findById(id);
        if (!permissionService.canDeleteInvite(topicInvite)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        topicInviteService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
