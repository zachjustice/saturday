package saturday.controllers;

import javassist.tools.web.BadHttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saturday.domain.*;
import saturday.exceptions.TopicInviteNotFoundException;
import saturday.services.EntityService;
import saturday.services.TopicInviteService;
import saturday.services.TopicService;

@RestController()
public class TopicInviteController {
    private final TopicInviteService topicInviteService;
    private final EntityService entityService;
    private final TopicService topicService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TopicInviteController(TopicInviteService topicInviteService, EntityService entityService, TopicService topicService) {
        this.topicInviteService = topicInviteService;
        this.entityService = entityService;
        this.topicService = topicService;
    }

    @RequestMapping(value = "/topic_invites/{id}", method = RequestMethod.GET)
    public ResponseEntity<TopicInvite> getTopicInvite(@PathVariable int id) throws TopicInviteNotFoundException {
        if(id < 1) {
            throw new TopicInviteNotFoundException("Could not find topic invite with the id " + id);
        }

        TopicInvite topicInvite = this.topicInviteService.findById(id);
        return new ResponseEntity<>(topicInvite, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_invites", method = RequestMethod.PUT)
    public ResponseEntity<TopicInvite> saveTopicInvite(@RequestBody TopicInviteRequest topicInviteRequest) throws BadHttpRequest {
        Topic topic = topicService.findTopicById(topicInviteRequest.getTopicId());
        if(topic == null) {
            throw new BadHttpRequest(new Exception("Invalid topic id " + topicInviteRequest.getTopicId()));
        }

        Entity invitee = entityService.findEntityById(topicInviteRequest.getInviteeId());
        if(invitee == null) {
            throw new BadHttpRequest(new Exception("Invalid invitee id " + topicInviteRequest.getInviteeId()));
        }

        Entity inviter = entityService.findEntityById(topicInviteRequest.getInviterId());
        if(inviter == null) {
            throw new BadHttpRequest(new Exception("Invalid inviter id " + topicInviteRequest.getInviterId()));
        }

        logger.info("TopicInviteRequest " + topicInviteRequest);

        TopicInvite topicInvite;
        if(topicInviteRequest.getId() > 0) {
            topicInvite = topicInviteService.findById(topicInviteRequest.getId());
        } else {
            topicInvite = new TopicInvite();
        }

        topicInvite.setTopic(topic);
        topicInvite.setInvitee(invitee);
        topicInvite.setInviter(inviter);

        logger.info("TopicInvite " + topicInvite);

        topicInvite = topicInviteService.save(topicInvite);
        return new ResponseEntity<>(topicInvite, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_invites/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<TopicInvite> delete(@PathVariable(value = "id") int id) {
        topicInviteService.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
