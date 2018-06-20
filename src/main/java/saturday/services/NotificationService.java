package saturday.services;

import saturday.domain.TopicMember;

public interface NotificationService {
    void send(TopicMember inviteEvent);
}
