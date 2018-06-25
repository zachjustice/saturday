package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import saturday.delegates.EntityDelegate;
import saturday.domain.Entity;
import saturday.exceptions.AccessDeniedException;
import saturday.publishers.SaturdayEventPublisher;
import saturday.services.ConfirmEmailService;
import saturday.services.EntityService;
import saturday.services.PermissionService;
import saturday.services.S3Service;
import saturday.utils.HTTPUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by zachjustice on 7/27/17.
 */
@RestController
public class EntityController {

    private final EntityService entityService;
    private final S3Service s3Service;
    private final ConfirmEmailService confirmEmailService;
    private final PermissionService permissionService;
    private final SaturdayEventPublisher saturdayEventPublisher;
    private final EntityDelegate entityDelegate;

    @Value("${saturday.s3.user-files-bucket}")
    private String bucketName;
    @Value("${saturday.s3.url.prefix}")
    private String s3UrlPrefix;
    @Value("${saturday.s3.entity.profile_picture.key.prefix}")
    private String entityProfilePictureKeyPrefix;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EntityController(
            EntityService entityService,
            PermissionService permissionService,
            S3Service s3Service,
            ConfirmEmailService confirmEmailService,
            SaturdayEventPublisher saturdayEventPublisher,
            EntityDelegate entityDelegate
    ) {
        this.entityService = entityService;
        this.permissionService = permissionService;
        this.s3Service = s3Service;
        this.confirmEmailService = confirmEmailService;
        this.saturdayEventPublisher = saturdayEventPublisher;
        this.entityDelegate = entityDelegate;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<Entity> createEntity(HttpServletResponse response, @RequestBody Entity entity) {

        String token = entityDelegate.save(entity);

        // Only add token if the preceding was successful to avoid adding Auth headers to error'ed requests
        HTTPUtils.addAuthenticationHeader(response, token);

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Entity> saveEntity(
            @PathVariable(value = "id") int id,
            @RequestBody Entity updatedEntity
    ) {

        Entity currEntity = entityService.findEntityById(updatedEntity.getId());

        if (!permissionService.canAccess(currEntity)) {
            throw new AccessDeniedException();
        }

        currEntity = entityService.updateEntity(updatedEntity, true);

        return new ResponseEntity<>(currEntity, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities", method = RequestMethod.GET)
    public ResponseEntity<Entity> findEntityByEmail(@RequestParam(value = "email") String email) {

        Entity entity = entityService.findEntityByEmail(email);

        if (entity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities/{id}", method = RequestMethod.GET)
    public ResponseEntity<Entity> getEntity(@PathVariable(value = "id") int id) {
        Entity entity = entityService.findEntityById(id);

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities/{id}/profile_picture", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<Entity> uploadProfilePicture(
            @PathVariable(value = "id") int id,
            @RequestParam("picture") MultipartFile picture) throws IOException {

        Entity entity = entityService.findEntityById(id);

        if (!permissionService.canAccess(entity)) {
            throw new AccessDeniedException();
        }

        // TODO do this better
        String uuid = UUID.randomUUID().toString();

        String uploadKey = entityProfilePictureKeyPrefix + uuid; // s3 file url
        String fileUrl = s3UrlPrefix + bucketName + "/" + uploadKey;

        s3Service.upload(picture, uploadKey);

        entity.setPictureUrl(fileUrl);
        entityService.updateEntity(entity);// do this better especially

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }



    /**
     * Resend the email confirmation email
     *
     * @param id The id of the entity to send the confirmation email to
     * @return Success or throw a failure
     */
    @RequestMapping(value = "entities/{id}/resend_confirmation", method = RequestMethod.POST)
    public ResponseEntity<String> resendEmailConfirmationEmail(
            @PathVariable(value = "id") int id
    ) {

        Entity entity = entityService.findEntityById(id);
        if (!permissionService.canAccess(entity)) {
            throw new AccessDeniedException();
        }

        confirmEmailService.sendEmail(entity);

        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }

    /**
     * Emails the provided entity with a "Forgot Password" email
     *
     * @param email The id of the entity to send the confirmation email to
     * @return Success or throw a failure
     */
    @RequestMapping(value = "/send_reset_password_email", method = RequestMethod.POST)
    public ResponseEntity<String> sendForgotPasswordEmail(
            @RequestParam(value = "email") String email
    ) {
        Entity entity = entityService.findEntityByEmail(email);
        if (entity == null) {
            // No need to tell people what emails exist and which don't (?)
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }

        // send reset password email
        saturdayEventPublisher.publishResetPasswordEvent(entity);

        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }
}
