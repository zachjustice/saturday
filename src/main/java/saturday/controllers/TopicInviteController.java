package saturday.controllers;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import saturday.domain.*;
import saturday.exceptions.TopicInviteNotFoundException;
import saturday.exceptions.TopicNotFoundException;
import saturday.repositories.TopicInviteRepository;
import saturday.services.EntityService;
import saturday.services.S3Service;
import saturday.services.TopicContentService;
import saturday.services.TopicService;

import javax.persistence.EntityNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController(value = "/topic_invites")
public class TopicInviteController {
    private final TopicInviteRepository topicInviteRepository;

    @Autowired
    public TopicInviteController(TopicInviteRepository topicInviteRepository) {
        this.topicInviteRepository = topicInviteRepository;
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public ResponseEntity<TopicInvite> getTopicInvite(@PathVariable int id) throws TopicInviteNotFoundException {
        if(id < 1) {
            throw new TopicInviteNotFoundException("Could not find topic invite with the id " + id);
        }
        TopicInvite topicInvite = this.topicInviteRepository.findById(id);
        return new ResponseEntity<>(topicInvite, HttpStatus.OK);
    }
}
