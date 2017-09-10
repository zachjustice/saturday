package storyboard.controllers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import storyboard.domain.Action;
import storyboard.domain.Author;
import storyboard.domain.Page;
import storyboard.services.ActionService;
import storyboard.services.AuthorService;
import storyboard.services.PageService;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zachjustice on 7/30/17.
 */
@RestController
@RequestMapping("/actions")
public class ActionController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${page.maxActionLength}")
    private int MAX_ACTION_LENGTH;
    @Value("${page.maxNumPageActions}")
    private int MAX_NUM_PAGE_ACTIONS;

    @Autowired
    private AuthorService authorService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private PageService pageService;
    @Autowired
    private EntityManager entityManager;

    @RequestMapping(value = "/{action_id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Action> getAction(@PathVariable(value="action_id") int id) throws Exception {
        Action action = actionService.findActionById(id);

        if(action == null) {
            throw new ResourceNotFoundException("How strange... It appears that action doesn't exist, yet");
        }

        return new ResponseEntity<>(action, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity<Action> createAction(
            @RequestBody(required = true) String newAction
    ) throws Exception {

        logger.info("Received json: " + newAction);

        // Validate JSON
        JsonObject newActionJson = new JsonParser().parse(newAction).getAsJsonObject();
        JsonElement descriptionElement = newActionJson.get("description");
        JsonElement authorIdElement = newActionJson.get("author_id");
        JsonElement prevPageElement = newActionJson.get("prevPage");
        JsonElement nextPageElement = newActionJson.get("nextPage");

        if(descriptionElement == null) {
            throw new Exception("Action description cannot be null");
        }

        if(authorIdElement == null) {
            throw new Exception("Author id cannot be null");
        }

        if(prevPageElement == null) {
            throw new Exception("prev page cannot be null");
        }

        // Validate and save action
        String description = descriptionElement.getAsString();
        int authorId = authorIdElement.getAsInt();
        int prevPageId = prevPageElement.getAsInt();

        validateDescription(description);

        Author author = authorService.findAuthorById(authorId);
        if(author == null) {
            throw new ResourceNotFoundException("How strange... It seems that author doesn't exist, yet.");
        }

        Page prevPage = pageService.findPageById(prevPageId);
        if(prevPage == null) {
            throw new ResourceNotFoundException("How strange... It seems that page doesn't exist, yet.");
        }

        if(prevPage.getActions().size() >= MAX_NUM_PAGE_ACTIONS) {
            throw new Exception("This page already has " + MAX_NUM_PAGE_ACTIONS + " prompts. Perhaps try writing a prompt for a different page.");
        }

        Action action = new Action();
        action.setDescription(description);
        action.setAuthor(author);
        action.setPrevPage(prevPage);

        // next page is optional
        if(nextPageElement != null) {
            int nextPageId = nextPageElement.getAsInt();
            Page nextPage = pageService.findPageById(nextPageId);

            if(nextPage == null) {
                throw new ResourceNotFoundException("How strange... It seems that the next page for this prompt doesn't exist, yet.");
            }

            action.setNextPage(nextPage);
        }

        action = actionService.saveAction(action);
        return new ResponseEntity<>(action, HttpStatus.OK);
    }

    @RequestMapping(value = "/{action_id}", method = RequestMethod.PATCH, produces = "application/json")
    public ResponseEntity<Action> updateAction(
            @PathVariable(value="action_id") int actionId,
            @RequestBody(required = true) String updatedAction
    ) throws Exception {

        logger.info(updatedAction);

        // Validate JSON
        JsonObject newAction = new JsonParser().parse(updatedAction).getAsJsonObject();
        JsonElement descriptionElement = newAction.get("description");
        JsonElement nextPageElement = newAction.get("nextPage");

        Action action = actionService.findActionById(actionId);
        if(action == null) {
            throw new ResourceNotFoundException("How strange... It seems that action doesn't exist, yet.");
        }

        if(descriptionElement != null) {
            String description = descriptionElement.getAsString();
            validateDescription(description);
            action.setDescription(description);
        }

        if(nextPageElement != null) {
            int nextPageId = nextPageElement.getAsInt();
            Page nextPage = pageService.findPageById(nextPageId);

            if(nextPage == null) {
                throw new ResourceNotFoundException("How strange, it appears the next page for this prompt doesn't exist");
            }

            action.setNextPage(nextPage);
        }

        action = actionService.saveAction(action);
        return new ResponseEntity<>(action, HttpStatus.OK);
    }

    private void validateDescription(String description) throws Exception {
        if(StringUtils.isEmpty(description)) {
            throw new Exception("No, no, don't leave the prompt empty. Try again, please.");
        }

        if(description.length() > MAX_ACTION_LENGTH) {
            throw new Exception("Well, don't try to fit the whole book on one page. Didn't you know an optimal prompt for a page is " + MAX_ACTION_LENGTH + " characters...");
        }
    }
}
