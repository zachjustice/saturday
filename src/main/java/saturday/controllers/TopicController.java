package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import saturday.domain.Entity;
import saturday.domain.NewTopic;
import saturday.domain.Topic;
import saturday.domain.TopicContent;
import saturday.services.EntityService;
import saturday.services.TopicContentService;
import saturday.services.TopicService;

import java.util.List;

/**
 * Created by zachjustice on 7/27/17.
 */
@RestController
public class TopicController {

    private final TopicService topicService;
    private final EntityService entityService;
    private final TopicContentService topicContentService;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public TopicController(TopicService topicService, EntityService entityService, TopicContentService topicContentService) {
        this.topicService = topicService;
        this.entityService = entityService;
        this.topicContentService = topicContentService;
    }

    @RequestMapping(value = "/topics", method = RequestMethod.POST)
    public ResponseEntity<Topic> createTopic(@RequestBody NewTopic newTopic) {

        if(StringUtils.isEmpty(newTopic.getName()) || newTopic.getCreator() <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        int creatorId = newTopic.getCreator();
        Entity creator = entityService.findEntityById(creatorId);

        if(creator == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String name = newTopic.getName();
        String description = newTopic.getDescription();

        Topic topic = new Topic();
        topic.setName(name);
        topic.setDescription(description);
        topic.setCreator(creator);

        topic = topicService.saveTopic(topic);
        logger.info("Created Topic: " + newTopic.toString());

        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    @RequestMapping(value = "/topics", method = RequestMethod.GET)
    public ResponseEntity<Topic> findTopicByName(@RequestParam(value="name") String name) {
        if(StringUtils.isEmpty(name)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Topic topic = topicService.findTopicByName(name);

        if(topic == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    @RequestMapping(value = "/topics/{id}/topic_content", method = RequestMethod.GET)
    public ResponseEntity<List<TopicContent>> getTopicContentByTopic(@PathVariable(value="id") int id) {
        Topic topic = topicService.findTopicById(id);

        if(topic == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        List<TopicContent> topicContentList = topicContentService.findTopicContentByTopicId(id);

        return new ResponseEntity<>(topicContentList , HttpStatus.OK);
    }

    @RequestMapping(value = "/topics/{id}", method = RequestMethod.GET)
    public ResponseEntity<Topic> getTopic(@PathVariable(value="id") int id) {
        Topic topic = topicService.findTopicById(id);

        if(topic == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(topic, HttpStatus.OK);
    }

    // TODO auth check so only owner/admin can update id-topic
    @RequestMapping(value = "/topics/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Topic> saveTopic(
            @PathVariable(value="id") int id,
            @RequestBody Topic newTopic
    ) {
        if(newTopic.getId() != id || newTopic.getId() == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("New Topic: " + newTopic.toString());
        Topic currTopic = topicService.findTopicById(newTopic.getId());

        if(currTopic == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Old Topic: " + currTopic.toString());

        // Validate Topic
        String newName = newTopic.getName();
        String newDescription = newTopic.getDescription();

        if(!StringUtils.isEmpty(newName)) {
            currTopic.setName(newName);
        }

        if(!StringUtils.isEmpty(newDescription)) {
            currTopic.setDescription(newDescription);
        }

        logger.info("Updated: " + currTopic);
        topicService.saveTopic(currTopic);
        return new ResponseEntity<>(currTopic, HttpStatus.OK);
    }
}
