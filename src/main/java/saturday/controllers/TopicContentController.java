package saturday.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import saturday.domain.Entity;
import saturday.domain.NewTopicContent;
import saturday.domain.TopicContent;
import saturday.services.TopicContentService;

@RestController
public class TopicContentController {
    public final TopicContentService topicContentService;

    public TopicContentController(TopicContentService topicContentService) {
        this.topicContentService = topicContentService;
    }

    @RequestMapping(value = "/topic_content", method = RequestMethod.POST)
    public ResponseEntity<TopicContent> createTopicContent(@RequestBody NewTopicContent newTopicContent) {

        /*
        if(StringUtils.isEmpty(newTopicContent.getName()) || newTopicContent.getCreator() <= 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        int creatorId = newTopicContent.getCreator();
        Entity creator = entityService.findEntityById(creatorId);

        if(creator == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String name = newTopicContent.getName();
        String description = newTopicContent.getDescription();

        TopicContent topic = new TopicContent();
        topic.setName(name);
        topic.setDescription(description);
        topic.setCreator(creator);

        topicContent = topicContentService.saveTopicContent(topic);
        logger.info("Created TopicContent: " + newTopicContent.toString());

        return new ResponseEntity<>(topicContent, HttpStatus.OK);
        */
        return new ResponseEntity<>(new TopicContent(), HttpStatus.OK);
    }

    @RequestMapping(value = "/topic_content/{id}", method = RequestMethod.GET)
    public ResponseEntity<TopicContent> findTopicByName(@PathVariable(value="id") int id) {
        TopicContent topicContent = topicContentService.findTopicContentById(id);

        if(topicContent == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(topicContent, HttpStatus.OK);
    }
}
