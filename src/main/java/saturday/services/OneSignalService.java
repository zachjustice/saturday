package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import saturday.domain.oneSignal.OneSignalNotification;
import saturday.domain.TopicMember;
import saturday.domain.oneSignal.OneSignalResponse;


@Service()
public class OneSignalService implements NotificationService {

    private final RestTemplate restTemplate;
    @Value("saturday.oneSignal.url")
    private String oneSignalUrl;

    @Autowired
    public OneSignalService(
            RestTemplate restTemplate
    ) {

        this.restTemplate = restTemplate;
    }

    @Override
    public void send(TopicMember invitedTopicMember) {
        OneSignalNotification request = new OneSignalNotification();

        // TODO Auth
        restTemplate.postForObject(oneSignalUrl, request, OneSignalResponse.class);
    }
}
