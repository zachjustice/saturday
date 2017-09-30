package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saturday.domain.Entity;
import saturday.services.EntityServiceImpl;

/**
 * Created by zachjustice on 7/27/17.
 */
@RestController
public class EntityController {
    @Autowired
    EntityServiceImpl entityService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/entities/{id}", method = RequestMethod.GET)
    public ResponseEntity<Entity> getEntity(@PathVariable(value="id") int id) {
        Entity entity = entityService.findEntityById(id);
        entity.setPassword("");

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<Entity> createEntity(@RequestBody Entity entity) {
        logger.info("Entity Controller: " + entity.toString());
        entityService.saveEntity(entity);
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity<String> createEntity() {
        return new ResponseEntity("Success", HttpStatus.OK);
    }
}
