package saturday.delegates;

import org.springframework.beans.factory.annotation.Value;
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

import java.util.List;
import java.util.stream.Collectors;

@Component
public class TopicMemberDelegate {
    private final TopicMemberService topicMemberService;
    private final TopicService topicService;
    private final EntityService entityService;
    private final PermissionService permissionService;

    private final TopicRoleFactory topicRoleFactory;

    @Value("${saturday.topic.invite.status.accepted}")
    private int TOPIC_MEMBER_STATUS_ACCEPTED;
    @Value("${saturday.topic.invite.status.pending}")
    private int TOPIC_MEMBER_STATUS_PENDING;

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

    public List<TopicMember> getPendingAndAcceptedTopicMembersByTopic(int topicId) {
        Topic topic = topicService.findTopicById(topicId);

        if (!permissionService.canView(topic)) {
            throw new AccessDeniedException();
        }

        return topicMemberService.findByTopicId(topicId)
                .stream()
                .filter(topicMember ->
                        topicMember.getStatus().getId() == TOPIC_MEMBER_STATUS_PENDING
                        || topicMember.getStatus().getId() == TOPIC_MEMBER_STATUS_ACCEPTED)
                .collect(Collectors.toList());
    }
}
