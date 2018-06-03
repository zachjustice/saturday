package saturday.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import saturday.delegates.TopicRolePermissionDelegate;
import saturday.domain.TopicRolePermission;

@RestController()
public class TopicRolePermissionController {
    private final TopicRolePermissionDelegate topicRolePermissionDelegate;

    @Autowired
    public TopicRolePermissionController(
            TopicRolePermissionDelegate topicRolePermissionDelegate
    ) {
        this.topicRolePermissionDelegate = topicRolePermissionDelegate;
    }

    @RequestMapping(value = "/topic_role_permissions", method = RequestMethod.PUT)
    public ResponseEntity<TopicRolePermission> saveTopicRolePermission(
            @RequestBody TopicRolePermission topicRolePermission
    ) {
        topicRolePermission = this.topicRolePermissionDelegate.save(topicRolePermission);
        return new ResponseEntity<>(topicRolePermission, HttpStatus.OK);
    }
}
