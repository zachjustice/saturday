package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicContent;
import saturday.domain.TopicInvite;
import saturday.exceptions.EntityExistsException;
import saturday.exceptions.ProcessingResourceException;
import saturday.services.*;
import saturday.utils.TokenAuthenticationUtils;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Created by zachjustice on 7/27/17.
 */
@RestController
public class EntityController {

    private final EntityServiceImpl entityService;
    private final TopicContentService topicContentService;
    private final TopicInviteService topicInviteService;
    private final PermissionService permissionService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final S3Service s3Service;

    @Value("${saturday.s3.user-files-bucket}")
    private String bucketName;
    @Value("${saturday.s3.url.prefix}")
    private String s3UrlPrefix;
    @Value("${saturday.s3.entity.profile_picture.key.prefix}")
    private String entityProfilePictureKeyPrefix;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EntityController(EntityServiceImpl entityService, TopicContentService topicContentService, TopicInviteService topicInviteService, PermissionService permissionService, BCryptPasswordEncoder bCryptPasswordEncoder, S3Service s3Service) {
        this.entityService = entityService;
        this.topicContentService = topicContentService;
        this.topicInviteService = topicInviteService;
        this.permissionService = permissionService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.s3Service = s3Service;
    }

    @RequestMapping(value = "/entities", method = RequestMethod.GET)
    public ResponseEntity<Entity> findEntityByEmail(@RequestParam(value="email") String email) throws ProcessingResourceException {

        Entity entity = entityService.findEntityByEmail(email);

        if(entity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // TODO better way to do this
        entity.setPassword("");
        entity.setToken("");

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities/{id}", method = RequestMethod.GET)
    public ResponseEntity<Entity> getEntity(@PathVariable(value="id") int id) {
        Entity entity = entityService.findEntityById(id);

        // TODO better way to do this
        entity.setPassword("");
        entity.setToken("");

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Entity> saveEntity(
            @PathVariable(value="id") int id,
            @RequestBody Entity updatedEntity
    ) throws ProcessingResourceException {

        Entity currEntity = entityService.findEntityById(updatedEntity.getId());

        if(!permissionService.canView(currEntity)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        currEntity = entityService.updateEntity(currEntity, updatedEntity);
        return new ResponseEntity<>(currEntity, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities/{id}/profile_picture", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<Entity> uploadProfilePicture(
            @PathVariable(value="id") int id,
            @RequestParam("picture") MultipartFile picture) throws EntityExistsException, IOException, ProcessingResourceException {

        Entity entity = entityService.findEntityById(id);

        if(!permissionService.canView(entity)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        String uuid = UUID.randomUUID().toString();

        String uploadKey = entityProfilePictureKeyPrefix + uuid; // s3 file url
        String fileUrl = s3UrlPrefix + bucketName + "/" + uploadKey;

        s3Service.upload(picture, uploadKey);

        entity.setPictureUrl(fileUrl);
        entityService.saveEntity(entity);

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<Entity> createEntity(HttpServletResponse response, @RequestBody Entity entity) throws EntityExistsException, ProcessingResourceException {
        logger.info("Registered Entity: " + entity.toString());

        // TODO validate fields before saving
        entity = entityService.saveEntity(entity);

        // Only add token if the preceding was successful to avoid adding Auth headers to errored requests
        String token = TokenAuthenticationUtils.addAuthentication(response, entity.getEmail());
        entity.setToken(token);

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities/{id}/topic_invites", method = RequestMethod.GET)
    public ResponseEntity<List<TopicInvite>> getEntityReceivedTopicInvites(
            @PathVariable(value="id") int id,
            @RequestParam(value="getReceived", required = false) Boolean getReceived
    ) {
        Entity entity = entityService.findEntityById(id);

        if(entity == null) {
            throw new EntityNotFoundException("No entity with id " + id + " exists!");
        }

        if(!permissionService.canView(entity)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        List<TopicInvite> topicInvites;
        if(getReceived == null) {
            topicInvites = topicInviteService.findTopicInvitesByInviteeOrInviter(entity);
        } else if(getReceived) {
            topicInvites = topicInviteService.findTopicInvitesByInvitee(entity);
        } else {
            topicInvites = topicInviteService.findTopicInvitesByInviter(entity);
        }

        return new ResponseEntity<>(topicInvites, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities/{id}/topics", method = RequestMethod.GET)
    public ResponseEntity<List<Topic>> getEntityTopics(
            @PathVariable(value="id") int id
    ) throws AccessDeniedException {
        Entity entity = entityService.findEntityById(id);

        if(entity == null) {
            throw new EntityNotFoundException("No entity with id " + id + " exists!");
        }

        if(!permissionService.canView(entity)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        return new ResponseEntity<>(entity.getTopics(), HttpStatus.OK);
    }

    @RequestMapping(value = "/entities/{id}/topic_content", method = RequestMethod.GET)
    public ResponseEntity<List<TopicContent>> getEntityTopicContent(
            @PathVariable(value="id") int id
    ) throws AccessDeniedException {
        Entity entity = entityService.findEntityById(id);

        if(entity == null) {
            throw new EntityNotFoundException("No entity with id " + id + " exists!");
        }

        if(!permissionService.canView(entity)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        List<TopicContent> entityTopicContent = topicContentService.findByTopicMember(id);

        return new ResponseEntity<>(entityTopicContent, HttpStatus.OK);
    }

}
