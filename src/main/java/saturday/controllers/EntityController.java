package saturday.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import saturday.domain.Entity;
import saturday.services.AuthorServiceImpl;

/**
 * Created by zachjustice on 7/27/17.
 */
@RestController
public class EntityController {
    @Autowired
    AuthorServiceImpl authorService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/authors/{id}", method = RequestMethod.GET)
    public ResponseEntity<Entity> getAuthor(@PathVariable(value="id") int id) {
        Entity entity = authorService.findAuthorById(id);
        entity.setPassword("");

        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<Entity> createAuthor(@RequestBody Entity entity) {
        authorService.saveAuthor(entity);
        return new ResponseEntity<>(entity, HttpStatus.OK);
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity<String> createAuthor() {
        return new ResponseEntity("Success", HttpStatus.OK);
    }
}
