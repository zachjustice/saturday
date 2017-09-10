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
 * Created by zachjustice on 7/29/17.
 */
@RestController
@RequestMapping("/pages")
public class PageController {

    @Value("${page.maxPageLength}")
    private int MAX_PAGE_LENGTH;

    @Autowired
    private PageService pageService;
    @Autowired
    private ActionService actionService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private EntityManager entityManager;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @RequestMapping(value = "/{page_id}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Page> getPage(@PathVariable(value="page_id") int id) throws Exception {
        Page page = pageService.findPageById(id);

        if(page == null) {
            throw new ResourceNotFoundException("How strange... It seems that page doesn't exist, yet.");
        }

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Page> createPage(
            @RequestBody(required = true) String newPage
            ) throws Exception {

        logger.info(newPage);

        // Validate JSON response
        JsonObject newPageJson = new JsonParser().parse(newPage).getAsJsonObject();
        JsonElement contentElement = newPageJson.get("content");
        JsonElement authorIdElement = newPageJson.get("author_id");
        JsonElement previousActionIdElement = newPageJson.get("previous_action_id");

        if(contentElement == null) {
            throw new Exception("Page content cannot be null");
        }

        if(authorIdElement == null) {
            throw new Exception("Author id cannot be null");
        }

        if(previousActionIdElement == null) {
            throw new Exception("Previous action id cannot be null");
        }

        // Validate and save page
        Author author;
        Action prevAction;

        String content = contentElement.getAsString();
        int authorId = authorIdElement.getAsInt();
        int previousActionId = previousActionIdElement.getAsInt();

        validatePageContent(content);

        prevAction = actionService.findActionById(previousActionId);
        if(prevAction == null) {
            throw new ResourceNotFoundException("How strange... It seems that action doesn't exist, yet.");
        }

        if(prevAction.getNextPage() != null) {
            throw new Exception("This action already points to a pagee.");
        }

        author = authorService.findAuthorById(authorId);
        if(author == null) {
            throw new ResourceNotFoundException("How strange... It seems that author doesn't exist, yet.");
        }

        Page page = new Page();
        page.setContent(content);
        page.setAuthor(author);

        page = pageService.savePage(page);

        prevAction.setNextPage(page);
        actionService.saveAction(prevAction);

        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    @RequestMapping(value = "/{page_id}", method = RequestMethod.PATCH, produces = "application/json")
    public ResponseEntity<Page> updatePage(
            @PathVariable(value="page_id") int pageId,
            @RequestBody(required = true) String updatedPage
        ) throws Exception {

        logger.info(updatedPage);

        // Validate JSON
        JsonObject newPageJson = new JsonParser().parse(updatedPage).getAsJsonObject();
        JsonElement contentElement = newPageJson.get("content");

        if(contentElement == null) {
            throw new Exception("Page content cannot be null");
        }

        // Validate and update page
        String content = contentElement.getAsString();
        validatePageContent(content);

        Page page = pageService.findPageById(pageId);
        if(page == null) {
            throw new ResourceNotFoundException("How strange... It appears that page doesn't exist, yet");
        }

        page.setContent(content);

        page = pageService.savePage(page);
        return new ResponseEntity<>(page, HttpStatus.OK);
    }

    private void validatePageContent(String content) throws Exception {
        if(StringUtils.isEmpty(content)) {
            throw new Exception("No, no, don't leave the page empty. Try again, please.");
        }

        if(content.length() > MAX_PAGE_LENGTH) {
            throw new Exception("Well, don't try to fit the whole book on one page. Didn't you know an optimal prompt for a page is " + MAX_PAGE_LENGTH + " characters...");
        }
    }


}
