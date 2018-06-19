package saturday.services;

import saturday.domain.topicMemberStatuses.TopicMember;

public interface NotificationService {
    void send(TopicMember inviteEvent);
}
