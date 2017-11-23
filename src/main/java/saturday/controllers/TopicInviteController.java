package saturday.controllers;

import com.amazonaws.services.s3.model.ObjectMetadata;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import saturday.domain.Entity;
import saturday.domain.NewTopicContent;
import saturday.domain.Topic;
import saturday.domain.TopicContent;
import saturday.exceptions.TopicNotFoundException;
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

@RestController
public class TopicInviteController {
}
