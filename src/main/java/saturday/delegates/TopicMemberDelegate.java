package saturday.delegates;

import org.springframework.stereotype.Component;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.domain.topicMemberStatuses.TopicMemberStatus;
import saturday.domain.topicRoles.TopicUser;
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

    public TopicMemberDelegate(
            TopicMemberService topicMemberService,
            TopicService topicService,
            EntityService entityService,
            PermissionService permissionService
    ) {
        this.topicMemberService = topicMemberService;
        this.topicService = topicService;
        this.entityService = entityService;
        this.permissionService = permissionService;
    }


    public List<TopicMember> inviteByEmail(List<String> emails, int topicId) {
        if (emails == null) {
            throw new IllegalArgumentException("Emails cannot be null.");
        }

        return emails.stream().map(email -> inviteByEmail(email, topicId)).collect(Collectors.toList());
    }

    public TopicMember inviteByEmail(String email, int topicId) {
        Topic topic = topicService.findTopicById(topicId);
        if (topic == null) {
            throw new ResourceNotFoundException("No topic with the id " + topicId + " exists!");
        }

        if(!permissionService.canCreateTopicMember(topic)) {
            throw new AccessDeniedException();
        }

        Entity entity = entityService.findEntityByEmail(email);
        if (entity == null) {
            // TODO send an invite email
            throw new ResourceNotFoundException("No entity with the email " + email + " exists!");
        }

        TopicMember topicMember = new TopicMember();
        topicMember.setEntity(entity);
        topicMember.setTopic(topic);
        topicMember.setTopicRole(new TopicUser());

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
                        topicMember.getStatus().getId() == TopicMemberStatus.PENDING
                        || topicMember.getStatus().getId() == TopicMemberStatus.ACCEPTED)
                .collect(Collectors.toList());
    }
}
