package storyboard.controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import storyboard.services.AuthorServiceImpl;
import storyboard.domain.Author;

/**
 * Created by zachjustice on 7/27/17.
 */
@RestController
public class AuthorController {
    @Autowired
    AuthorServiceImpl authorService;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/authors/{id}", method = RequestMethod.GET)
    public ResponseEntity<Author> getAuthor(@PathVariable(value="id") int id) {
        Author author = authorService.findAuthorById(id);
        author.setPassword("");

        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<Author> createAuthor(@RequestBody Author author) {
        authorService.saveAuthor(author);
        return new ResponseEntity<>(author, HttpStatus.OK);
    }

    @RequestMapping(value = "/test", method = RequestMethod.GET)
    public ResponseEntity<String> createAuthor() {
        return new ResponseEntity("Success", HttpStatus.OK);
    }
}
