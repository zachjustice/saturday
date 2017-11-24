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
import saturday.repositories.EntityRepository;
import saturday.repositories.TopicInviteRepository;
import saturday.repositories.TopicRepository;

import javax.xml.ws.Response;

@RestController()
public class TopicInviteController {
    private final TopicInviteRepository topicInviteRepository;
    private final EntityRepository entityRepository;
    private final TopicRepository topicRepository;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public TopicInviteController(TopicInviteRepository topicInviteRepository, EntityRepository entityRepository, TopicRepository topicRepository) {
        this.topicInviteRepository = topicInviteRepository;
        this.entityRepository = entityRepository;
        this.topicRepository = topicRepository;
    }

    @RequestMapping(value = "/topic_invites/{id}", method = RequestMethod.GET)
    public ResponseEntity<TopicInvite> getTopicInvite(@PathVariable int id) throws TopicInviteNotFoundException {
        if(id < 1) {
            throw new TopicInviteNotFoundException("Could not find topic invite with the id " + id);
        }

        TopicInvite topicInvite = this.topicInviteRepository.findById(id);
        return new ResponseEntity<>(topicInvite, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_invites", method = RequestMethod.PUT)
    public ResponseEntity<TopicInvite> saveTopicInvite(@RequestBody TopicInviteRequest topicInviteRequest) throws BadHttpRequest {
        Topic topic = topicRepository.findById(topicInviteRequest.getTopicId());
        if(topic == null) {
            throw new BadHttpRequest(new Exception("Invalid topic id " + topicInviteRequest.getTopicId()));
        }

        Entity invitee = entityRepository.findById(topicInviteRequest.getInviteeId());
        if(invitee == null) {
            throw new BadHttpRequest(new Exception("Invalid invitee id " + topicInviteRequest.getInviteeId()));
        }

        Entity inviter = entityRepository.findById(topicInviteRequest.getInviterId());
        if(inviter == null) {
            throw new BadHttpRequest(new Exception("Invalid inviter id " + topicInviteRequest.getInviterId()));
        }

        logger.info("TopicInviteRequest " + topicInviteRequest);

        TopicInvite topicInvite;
        if(topicInviteRequest.getId() > 0) {
            topicInvite = topicInviteRepository.findById(topicInviteRequest.getId());
        } else {
            topicInvite = new TopicInvite();
        }

        topicInvite.setTopic(topic);
        topicInvite.setInvitee(invitee);
        topicInvite.setInviter(inviter);

        logger.info("TopicInvite " + topicInvite);

        topicInvite = topicInviteRepository.save(topicInvite);
        return new ResponseEntity<>(topicInvite, HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_invites/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<TopicInvite> delete(@PathVariable(value = "id") int id) {
        topicInviteRepository.delete(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
