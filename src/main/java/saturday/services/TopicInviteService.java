package saturday.services;

import javassist.tools.web.BadHttpRequest;
import saturday.domain.*;
import saturday.exceptions.ProcessingResourceException;

import java.util.List;

/**
 * Created by zachjustice on 7/26/17.
 */
public interface TopicInviteService {
    TopicInvite save(TopicInviteRequest topicInvite) throws BadHttpRequest, ProcessingResourceException;

    TopicInvite saveStatus(TopicInvite topicInvite, TopicInviteStatus newStatus) throws Exception;
    void delete(int id);

    TopicInvite findById(int id);
    List<TopicInvite> findByTopicId(int id);
    List<TopicInvite> findTopicInvitesByInvitee(Entity invitee);
    List<TopicInvite> findTopicInvitesByInviter(Entity inviter);
    TopicInvite findTopicInviteByInviteeAndTopic(Entity invitee, Topic topic);
    List<TopicInvite> findTopicInvitesByInviteeOrInviter(Entity invitee);
}
