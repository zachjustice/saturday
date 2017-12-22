package saturday.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import saturday.domain.*;
import saturday.exceptions.ProcessingResourceException;
import saturday.repositories.TopicInviteRepository;
import saturday.repositories.TopicMemberRepository;

import java.util.List;

@Service("topicInviteService")
public class TopicInviteServiceImpl implements TopicInviteService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Value("${saturday.topic.invite.status.pending}")
    private int TOPIC_INVITE_PENDING;

    private final TopicInviteRepository topicInviteRepository;
    private final TopicMemberRepository topicMemberRepository;
    private final TopicService topicService;
    private final EntityService entityService;

    @Autowired
    TopicInviteServiceImpl(TopicInviteRepository topicInviteRepository, TopicMemberRepository topicMemberRepository, TopicService topicService, EntityService entityService) {
        this.topicInviteRepository = topicInviteRepository;
        this.topicMemberRepository = topicMemberRepository;
        this.topicService = topicService;
        this.entityService = entityService;
    }

    @Override
    public TopicInvite saveStatus(TopicInvite topicInvite, TopicInviteStatus newStatus) throws Exception {
        if (topicInvite.getStatus().getId() != TOPIC_INVITE_PENDING) {
            throw new ProcessingResourceException("Error updating topic invite status. This topic invite has already been " + topicInvite.getStatus().getRole());
        }

        topicInvite.setStatus(newStatus);
        return topicInvite;
    }

    /**
     * Find a topic invite by its primary key.
     * A topic invite represents a request from one user (the inviter) to another (the invitee)
     * to join a topic that the invitee is a part of.
     * Topic invites have 3 statuses: PENDING, ACCEPTED, and REJECTED
     *
     * @param id the primary key of the topic invite
     * @return The topic invite
     */
    @Override
    public TopicInvite findById(int id) {
        TopicInvite topicInvite = topicInviteRepository.findById(id);

        if (topicInvite == null) {
            throw new ResourceNotFoundException("No topic invite content with " + id + " exists!");
        }

        return topicInvite;
    }

    @Override
    public List<TopicInvite> findByTopicId(int id) {
        return topicInviteRepository.findByTopicId(id);
    }

    @Override
    public List<TopicInvite> findTopicInvitesByInvitee(Entity invitee) {
        return topicInviteRepository.findTopicInvitesByInvitee(invitee);
    }

    @Override
    public List<TopicInvite> findTopicInvitesByInviter(Entity inviter) {
        return topicInviteRepository.findTopicInvitesByInviter(inviter);
    }

    @Override
    public TopicInvite findTopicInviteByInviteeAndTopic(Entity invitee, Topic topic) {
        return topicInviteRepository.findTopicInviteByInviteeAndTopic(invitee, topic);
    }

    @Override
    public List<TopicInvite> findTopicInvitesByInviteeOrInviter(Entity entity) {
        List<TopicInvite> list = topicInviteRepository.findTopicInvitesByInviteeOrInviter(entity.getId());
        return list;
    }

    /**
     * Creators/moderators of topics can invite other users to their topics
     * @param topicInviteRequest The data for the topic invite to be created
     * @return The new topic invite
     */
    @Override
    public TopicInvite save(TopicInviteRequest topicInviteRequest) throws ProcessingResourceException {
        // Validate inviter and invitee
        if (topicInviteRequest.getInviteeId() == topicInviteRequest.getInviterId()) {
            throw new ProcessingResourceException("Invitee and inviter cannot be the same.");
        }

        Topic topic = topicService.findTopicById(topicInviteRequest.getTopicId());
        if (topic == null) {
            throw new ProcessingResourceException("Invalid topic id " + topicInviteRequest.getTopicId());
        }

        Entity invitee = entityService.findEntityById(topicInviteRequest.getInviteeId());
        if (invitee == null) {
            throw new ProcessingResourceException("Invalid invitee id " + topicInviteRequest.getInviteeId());
        }

        // check if the invitee is already a topic member
        TopicMember existingTopicMember = topicMemberRepository.findByEntityAndTopic(invitee, topic);
        if (existingTopicMember != null) {
            throw new ProcessingResourceException("Entity is already a member of this topic.");
        }

        // Check if a topic invite for the invited entity and topic already exists
        // to avoid duplicates.
        TopicInvite existingTopicInvite = this.findTopicInviteByInviteeAndTopic(invitee, topic);
        if (existingTopicInvite != null) {
            // TODO return 204?
            return existingTopicInvite;
        }

        // Only allow admins to set the Inviter id. Otherwise the inviter is the authenticated user
        Entity inviter;
        if (entityService.getAuthenticatedEntity().isAdmin()) {
            inviter = entityService.findEntityById(topicInviteRequest.getInviterId());
        } else {
            inviter = entityService.getAuthenticatedEntity();
        }

        if (inviter == null) {
            throw new ProcessingResourceException("Invalid inviter id " + topicInviteRequest.getInviterId());
        }

        TopicInviteStatus pendingStatus = new TopicInviteStatus();
        pendingStatus.setId(TOPIC_INVITE_PENDING);

        TopicInvite topicInvite = new TopicInvite();
        topicInvite.setInviter(inviter);
        topicInvite.setInvitee(invitee);
        topicInvite.setTopic(topic);
        topicInvite.setStatus(pendingStatus);

        return topicInviteRepository.save(topicInvite);
    }

    @Override
    public void delete(int id) {
        topicInviteRepository.delete(id);
    }
}
