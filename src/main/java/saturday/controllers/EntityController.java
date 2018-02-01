package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import saturday.domain.Entity;
import saturday.exceptions.AccessDeniedException;
import saturday.publishers.SaturdayEventPublisher;
import saturday.services.EntityServiceImpl;
import saturday.services.PermissionService;
import saturday.services.RegistrationConfirmationService;
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

    private final EntityServiceImpl entityService;
    private final S3Service s3Service;
    private final RegistrationConfirmationService registrationConfirmationService;
    private final PermissionService permissionService;
    private final SaturdayEventPublisher saturdayEventPublisher;

    @Value("${saturday.s3.user-files-bucket}")
    private String bucketName;
    @Value("${saturday.s3.url.prefix}")
    private String s3UrlPrefix;
    @Value("${saturday.s3.entity.profile_picture.key.prefix}")
    private String entityProfilePictureKeyPrefix;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EntityController(
            EntityServiceImpl entityService,
            PermissionService permissionService,
            S3Service s3Service,
            RegistrationConfirmationService registrationConfirmationService,
            SaturdayEventPublisher saturdayEventPublisher) {
        this.entityService = entityService;
        this.permissionService = permissionService;
        this.s3Service = s3Service;
        this.registrationConfirmationService = registrationConfirmationService;
        this.saturdayEventPublisher = saturdayEventPublisher;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<Entity> createEntity(HttpServletResponse response, @RequestBody Entity entity) {

        entity = entityService.saveEntity(entity);
        saturdayEventPublisher.publishRegistrationEvent(entity);

        // Only add token if the preceding was successful to avoid adding Auth headers to error'ed requests
        HTTPUtils.addAuthenticationHeader(response, entity.getToken());

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Entity> saveEntity(
            HttpServletResponse response,
            @PathVariable(value = "id") int id,
            @RequestBody Entity updatedEntity
    ) {

        Entity currEntity = entityService.findEntityById(updatedEntity.getId());

        if (!permissionService.canAccess(currEntity)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        currEntity = entityService.updateEntity(updatedEntity);
        HTTPUtils.addAuthenticationHeader(response, currEntity.getToken());

        return new ResponseEntity<>(currEntity, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities", method = RequestMethod.GET)
    public ResponseEntity<Entity> findEntityByEmail(@RequestParam(value = "email") String email) {

        Entity entity = entityService.findEntityByEmail(email);

        if (entity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        // TODO better way to do this
        entity.setPassword("");
        entity.setToken("");

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities/{id}", method = RequestMethod.GET)
    public ResponseEntity<Entity> getEntity(@PathVariable(value = "id") int id) {
        Entity entity = entityService.findEntityById(id);

        // TODO better way to do this
        entity.setPassword("");
        entity.setToken("");

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities/{id}/profile_picture", method = RequestMethod.POST, consumes = "multipart/form-data")
    public ResponseEntity<Entity> uploadProfilePicture(
            @PathVariable(value = "id") int id,
            @RequestParam("picture") MultipartFile picture) throws IOException {

        Entity entity = entityService.findEntityById(id);

        if (!permissionService.canAccess(entity)) {
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
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
            throw new AccessDeniedException("Authenticated entity does not have sufficient permissions.");
        }

        registrationConfirmationService.sendEmail(entity);

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
            // No need to tell people what emails exist and which don't
            return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
        }

        // send reset password email
        logger.info("begin publish");
        saturdayEventPublisher.publishResetPasswordEvent(entity);
        logger.info("end publish");

        return new ResponseEntity<>("SUCCESS", HttpStatus.OK);
    }
}
