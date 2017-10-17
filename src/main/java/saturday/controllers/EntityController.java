package saturday.controllers;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saturday.domain.Entity;
import saturday.exception.EntityExistsException;
import saturday.services.EntityServiceImpl;
import saturday.utils.TokenAuthenticationUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * Created by zachjustice on 7/27/17.
 */
@RestController
public class EntityController {

    private final EntityServiceImpl entityService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EntityController(EntityServiceImpl entityService, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.entityService = entityService;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @RequestMapping(value = "/entities", method = RequestMethod.GET)
    public ResponseEntity<Entity> findEntityByEmail(@RequestParam(value="email") String email) {
        if(StringUtils.isEmpty(email)) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Entity entity = entityService.findEntityByEmail(email);

        if(entity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        entity.setPassword("");

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/entities/{id}", method = RequestMethod.GET)
    public ResponseEntity<Entity> getEntity(@PathVariable(value="id") int id) {
        Entity entity = entityService.findEntityById(id);
        if(entity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        entity.setPassword("");

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    // TODO auth check so only id-entity can update id-entity
    @RequestMapping(value = "/entities/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Entity> saveEntity(
            @PathVariable(value="id") int id,
            @RequestBody Entity updatedEntity
    ) {
        if(updatedEntity.getId() != id || updatedEntity.getId() == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        logger.info("New Entity: " + updatedEntity.toString());
        Entity currEntity = entityService.findEntityById(updatedEntity.getId());

        if(currEntity == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        logger.info("Old Entity: " + currEntity.toString());

        String updatedName = updatedEntity.getName();
        String updatedEmail = updatedEntity.getEmail();
        Date updatedBirthday = updatedEntity.getBirthday();
        String updatedGender = updatedEntity.getGender();
        String updatedPassword = updatedEntity.getPassword();
        String updatedPic = updatedEntity.getLocalPicture();

        if(!StringUtils.isEmpty(updatedName)) {
            currEntity.setName(updatedName);
        }

        if(!StringUtils.isEmpty(updatedEmail)) {
            currEntity.setEmail(updatedEmail);
        }

        if(updatedBirthday != null) {
            currEntity.setBirthday(updatedBirthday);
        }

        if(!StringUtils.isEmpty(updatedGender)) {
            currEntity.setGender(updatedGender);
        }

        if(!StringUtils.isEmpty(updatedPassword)) {
            currEntity.setPassword(bCryptPasswordEncoder.encode(updatedEntity.getPassword()));
        }

        if(!StringUtils.isEmpty(updatedPic)) {
            currEntity.setLocalPicture(updatedPic);
        }

        logger.info("Updated: " + currEntity);
        entityService.saveEntity(currEntity);
        return new ResponseEntity<>(currEntity, HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<Entity> createEntity(HttpServletResponse response, @RequestBody Entity entity) throws EntityExistsException {
        logger.info("Registered Entity: " + entity.toString());
        entity.setPassword(bCryptPasswordEncoder.encode(entity.getPassword()));
        Entity entityWithSameEmail = entityService.findEntityByEmail(entity.getEmail());

        if(entityWithSameEmail != null) {
            throw new EntityExistsException("An entity with the email '" + entity.getEmail() + "' already exists.");
        }

        // TODO validate fields before saving
        entity = entityService.saveEntity(entity);

        // Only add token if the preceding was successful to avoid adding Auth headers to errored requests
        TokenAuthenticationUtils.addAuthentication(response, entity.getEmail());

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }
}
