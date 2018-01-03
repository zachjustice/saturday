package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import saturday.domain.Entity;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.domain.TopicMemberStatus;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ResourceNotFoundException;
import saturday.repositories.TopicMemberRepository;

import java.util.List;

@Service("topicMemberService")
public class TopicMemberServiceImpl implements TopicMemberService {
    private final TopicMemberRepository topicMemberRepository;
    private final EntityService entityService;

    @Value("${saturday.topic.invite.status.pending}")
    private int TOPIC_MEMBER_STATUS_PENDING;

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
            throw new BusinessLogicException("Failed to create topic member. Null topic.");
        }

        if(topicMember.getEntity() == null) {
            throw new BusinessLogicException("Failed to create topic member. Null entity.");
        }

        // check if the invitee is already a topic member
        TopicMember existingTopicMember = topicMemberRepository.findByEntityAndTopic(topicMember.getEntity(), topicMember.getTopic());
        if (existingTopicMember != null) {
            return existingTopicMember;
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
     * @param oldTopicMember The old topic member. We pass fields from the new topic member to the old one
     * @param newTopicMember The new topic member with the updated fields
     * @return The updated topic member
     * @     */
    @Override
    public TopicMember update(TopicMember oldTopicMember, TopicMember newTopicMember) {

        if(oldTopicMember.getStatus() == null) {
            throw new BusinessLogicException("Failed to modify topic member. Null topic member status.");
        }

        if(newTopicMember.getStatus() == null) {
            throw new BusinessLogicException("Failed to modify topic member. Null topic member status.");
        }

        if(oldTopicMember.getCreator() == null) {
            throw new BusinessLogicException("Failed to modify topic member. Null creator.");
        }

        if(oldTopicMember.getEntity() == null) {
            throw new BusinessLogicException("Failed to modify topic member. Null topic member entity.");
        }

        // Status and the modifier are currently the only thing we need to update on topic members
        oldTopicMember.setStatus(newTopicMember.getStatus());
        oldTopicMember.setModifier(newTopicMember.getModifier());

        return topicMemberRepository.save(oldTopicMember);
    }

    @Override
    public void delete(int id) {
        try {
            topicMemberRepository.delete(id);
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
    public TopicMember findByEntityAndTopicAndStatus(Entity entity, Topic topic, TopicMemberStatus status) {
        return topicMemberRepository.findByEntityAndTopicAndStatus(entity, topic, status);
    }
}
