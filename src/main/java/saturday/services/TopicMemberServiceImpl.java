package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.domain.topicMemberStatuses.TopicMemberStatus;
import saturday.domain.topicRoles.TopicUser;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.repositories.TopicMemberRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("topicMemberService")
public class TopicMemberServiceImpl implements TopicMemberService {
    private final TopicMemberRepository topicMemberRepository;
    private final EntityService entityService;

    @Value("${saturday.topic.invite.status.pending}")
    private int TOPIC_MEMBER_STATUS_PENDING;
    @Value("${saturday.topic.role.user}")
    private int TOPIC_ROLE_USER;

    @Autowired
    TopicMemberServiceImpl(TopicMemberRepository topicMemberRepository, EntityService entityService) {
        this.topicMemberRepository = topicMemberRepository;
        this.entityService = entityService;
    }

    /**
     * Topic members can send requests to other users.
     * @param topicMember the new topic member with an initial status of pending
     * @return The created topic member
     */
    @Override
    public TopicMember save(TopicMember topicMember) throws BusinessLogicException {

        if(topicMember.getTopic() == null) {
            throw new IllegalArgumentException("Failed to create topic member. Null topic.");
        }

        if(topicMember.getEntity() == null) {
            throw new IllegalArgumentException("Failed to create topic member. Null entity.");
        }

        // check if the invitee is already a topic member
        TopicMember existingTopicMember = topicMemberRepository.findByEntityAndTopic(topicMember.getEntity(), topicMember.getTopic());
        if (existingTopicMember != null) {
            return existingTopicMember;
        }

        if(topicMember.getStatus() == null) {
            // new topic members have a default status of pending if no status is provided
            TopicMemberStatus pendingStatus = new TopicMemberStatus();
            pendingStatus.setId(TOPIC_MEMBER_STATUS_PENDING);
            topicMember.setStatus(pendingStatus);
        }

        if (topicMember.getTopicRole() == null) {
            // new topic members default to the USER role
            TopicUser userTopicRole = new TopicUser();
            topicMember.setTopicRole(userTopicRole);
        }

        // The default value for the creator of a topic member is the current user
        // unless an admin set a creator
        Entity currentEntity = entityService.getAuthenticatedEntity();
        if(currentEntity.isAdmin() && topicMember.getCreator() != null) {
            topicMember.setCreator(topicMember.getCreator());
        } else {
            topicMember.setCreator(currentEntity);
        }

        return topicMemberRepository.save(topicMember);
    }

    /**
     * The inviter or invitee can update the topic member request to have the invitee join/leave/whatever the topic.
     * @param newTopicMember The new topic member with the updated fields
     * @return The updated topic member
     */
    @Override
    public TopicMember update(TopicMember newTopicMember) {

        if (newTopicMember == null) {
            throw new IllegalArgumentException("Error updating topic member. Null topicMember.");
        }

        TopicMember currentTopicMember = topicMemberRepository.findById(newTopicMember.getId());

        if (currentTopicMember == null) {
            throw new ResourceNotFoundException("No topic member with the id " + newTopicMember.getId() + " exists!");
        }

        if(newTopicMember.getTopicRole() != null) {
            currentTopicMember.setTopicRole(newTopicMember.getTopicRole());
        }

        if(newTopicMember.getStatus() != null) {
            currentTopicMember.setStatus(newTopicMember.getStatus());
        }

        Entity authenticatedEntity = entityService.getAuthenticatedEntity();
        currentTopicMember.setModifier(authenticatedEntity);

        return topicMemberRepository.save(currentTopicMember);
    }

    @Override
    public void deleteById(int id) {
        try {
            topicMemberRepository.deleteById(id);
        } catch(EmptyResultDataAccessException ex) {
            throw new ResourceNotFoundException("No topic member with id " + id + " exists!");
        }
    }

    @Override
    public TopicMember findById(int id) {
        TopicMember topicMember =  topicMemberRepository.findById(id);
        if(topicMember == null) {
            throw new ResourceNotFoundException("No topic member with an id " + id + " exists!");
        }

        return topicMember;
    }

    @Override
    public List<TopicMember> findByTopicId(int id) {
        return topicMemberRepository.findByTopicId(id);
    }

    @Override
    public TopicMember findByEntityAndTopic(Entity entity, Topic topic) {
        return topicMemberRepository.findByEntityAndTopic(entity, topic);
    }

    @Override
    public TopicMember findByEntityIdAndTopicId(int entityId, int topicId) {
        return topicMemberRepository.findByEntityIdAndTopicId(entityId, topicId);
    }

    @Override
    public TopicMember findByEntityAndTopicAndStatus(Entity entity, Topic topic, TopicMemberStatus status) {
        return topicMemberRepository.findByEntityAndTopicAndStatus(entity, topic, status);
    }

    /**
     * Retrieve get and received topic invites (topic members with a pending status)
     * @param involvedParty The party which sent or received the topic invite
     * @return Map of the form {sent: [TopicMembers], received: [TopicMembers]}
     */
    @Override
    public Map<String, List<TopicMember>> getSentAndReceivedTopicInvites(Entity involvedParty) {

        List<TopicMember> topicMembers = topicMemberRepository.findAllByCreatorOrEntityAndStatus(
                involvedParty.getId(),
                involvedParty.getId(),
                TOPIC_MEMBER_STATUS_PENDING
        );

        Map<String, List<TopicMember>> sentAndReceivedTopicInvites = topicMembers
                .stream()
                .collect(
                        Collectors.groupingBy(topicMember -> {
                            if(topicMember.getCreator().getId() == involvedParty.getId()) {
                                return "sent";
                            } else {
                                return "received";
                            }
                        })
                );

        // The returned map should default to having empty sent/received keys
        if(!sentAndReceivedTopicInvites.containsKey("sent")) {
            sentAndReceivedTopicInvites.put("sent", new ArrayList<>());
        }

        if(!sentAndReceivedTopicInvites.containsKey("received")) {
            sentAndReceivedTopicInvites.put("received", new ArrayList<>());
        }

        return sentAndReceivedTopicInvites;
    }

    @Override
    public List<TopicMember> findByEntityIdAndTopicRoleId(int entityId, int topicRoleId) {
        try {
            return topicMemberRepository.findByEntityIdAndTopicRoleId(entityId, topicRoleId);
        } catch (org.springframework.data.rest.webmvc.ResourceNotFoundException ex) {
            return new ArrayList<>();
        }
    }
}
