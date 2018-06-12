package saturday.delegates;

import org.springframework.stereotype.Component;
import saturday.TopicRoleFactory;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.exceptions.AccessDeniedException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.services.EntityService;
import saturday.services.PermissionService;
import saturday.services.TopicMemberService;
import saturday.services.TopicService;

@Component
public class TopicMemberDelegate {
    private final TopicMemberService topicMemberService;
    private final TopicService topicService;
    private final EntityService entityService;
    private final PermissionService permissionService;

    private final TopicRoleFactory topicRoleFactory;

    public TopicMemberDelegate(
            TopicMemberService topicMemberService,
            TopicService topicService,
            EntityService entityService,
            PermissionService permissionService,
            TopicRoleFactory topicRoleFactory
    ) {
        this.topicMemberService = topicMemberService;
        this.topicService = topicService;
        this.entityService = entityService;
        this.permissionService = permissionService;
        this.topicRoleFactory = topicRoleFactory;
    }

    public TopicMember inviteByEmail(String email, int topicId) {
        Entity entity = entityService.findEntityByEmail(email);
        if (entity == null) {
            throw new ResourceNotFoundException("No entity with the email " + email + " exists!");
        }

        Topic topic = topicService.findTopicById(topicId);
        if (topic == null) {
            throw new ResourceNotFoundException("No topic with the id " + topicId + " exists!");
        }

        TopicMember topicMember = new TopicMember();
        topicMember.setEntity(entity);
        topicMember.setTopic(topic);
        topicMember.setTopicRole(topicRoleFactory.createUser());

        if(!permissionService.canCreate(topicMember)) {
            throw new AccessDeniedException();
        }

        return topicMemberService.save(topicMember);
    }

}
