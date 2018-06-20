package saturday.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import saturday.domain.Topic;
import saturday.domain.TopicMember;
import saturday.domain.oneSignal.*;
import saturday.utils.HTTPUtils;


@Service()
public class OneSignalService implements NotificationService {

    private final RestTemplate restTemplate;
    @Value("${saturday.one-signal.rest.api.url}")
    private String oneSignalUrl;
    @Value("${saturday.one-signal.app.id}")
    private String oneSignalAppId;
    @Value("${saturday.one-signal.rest.api.key}")
    private String oneSignalRestApiKey;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public OneSignalService(
            RestTemplate restTemplate
    ) {
        this.restTemplate = restTemplate;
    }

    @Override
    public void send(TopicMember invitedTopicMember) {
        OneSignalNotification notification = buildNotification(invitedTopicMember);
        HttpHeaders headers = HTTPUtils.createHeaders(oneSignalRestApiKey);
        HttpEntity<OneSignalNotification> request = new HttpEntity<>(notification, headers);

        try {
            restTemplate.exchange(oneSignalUrl, HttpMethod.POST, request, OneSignalResponse.class);
        } catch (RestClientException ex) {
            logger.error(ex.getLocalizedMessage());
        }
    }

    private OneSignalNotification buildNotification(TopicMember invitedTopicMember) {

        if (invitedTopicMember == null) {
            throw new IllegalArgumentException("Null argument: invitedTopicMember.");
        }

        if (invitedTopicMember.getEntity() == null) {
            throw new IllegalArgumentException("TopicMember.entity is null.");
        }

        OneSignalFilter filter = new OneSignalFilter();
        filter.setField("tag");
        filter.setKey("saturday_entity_id");
        filter.setRelation("=");
        filter.setValue(String.valueOf(invitedTopicMember.getEntity().getId()));

        Topic topic = invitedTopicMember.getTopic();
        String message = "You've been invited to join a new group!";

        if (topic != null && !StringUtils.isEmpty(topic.getName())) {
            String topicName = topic.getName();
            message = "You've been invited to join " + topicName + "!";
        }

        OneSignalContents contents = new OneSignalContents();
        contents.setEnglishMessage(message);

        OneSignalNotification notification = new OneSignalNotification();
        notification.setAppId(oneSignalAppId);
        notification.setContents(contents);
        notification.getFilters().add(filter);

        return notification;
    }
}
