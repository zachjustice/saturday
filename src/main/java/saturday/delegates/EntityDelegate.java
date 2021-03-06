package saturday.delegates;

import org.apache.http.util.TextUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import saturday.domain.Entity;
import saturday.exceptions.AccessDeniedException;
import saturday.publishers.SaturdayEventPublisher;
import saturday.services.EmailService;
import saturday.services.EntityService;
import saturday.services.PermissionService;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityDelegate {
    @Value("${saturday.ses.from-email}")
    private String FROM_EMAIL;

    private final SaturdayEventPublisher saturdayEventPublisher;
    private final EntityService entityService;
    private final AccessTokenDelegate accessTokenDelegate;
    private final PermissionService permissionService;
    private final EmailService emailService;

    @Autowired
    public EntityDelegate(
            SaturdayEventPublisher saturdayEventPublisher,
            EntityService entityService,
            AccessTokenDelegate accessTokenDelegate,
            PermissionService permissionService,
            EmailService emailService) {
        this.saturdayEventPublisher = saturdayEventPublisher;
        this.entityService = entityService;
        this.accessTokenDelegate = accessTokenDelegate;
        this.permissionService = permissionService;
        this.emailService = emailService;
    }

    /**
     * Creates an entity. Publishes a registration event upon successful creation
     * and adds a new access token for the user. This token is returned so that
     * the entity controller can add the token to the response http headers.
     * @param entity The entity to create
     * @return The base64 encoded token consisting of the raw access token and the user's email.
     */
    public String save(Entity entity) {
        entity = entityService.saveEntity(entity);

        String base64EncodedToken = accessTokenDelegate.saveBearerToken(entity);

        saturdayEventPublisher.publishRegistrationEvent(entity);
        return base64EncodedToken;
    }

    public List<Entity> search(String searchTerm) {
        List<Entity> entities = entityService.search(searchTerm);

        return entities.stream()
                .filter(permissionService::canView)
                .collect(Collectors.toList());
    }

    public Entity findByEmail(String searchTerm) {
        Entity entity = entityService.findEntityByEmail(searchTerm);

        if (entity == null) {
            return null;
        }

        if (!permissionService.canView(entity)) {
            throw new AccessDeniedException();
        }

        return entity;
    }

    public void processInvite(String email, String inviteLink) {
        if (TextUtils.isBlank(email)) {
            throw new IllegalArgumentException("Email cannot be blank.");
        }

        this.emailService.sendEmail(
                "MomDiary Invite",
                "justice@zachsolutions.com",
                FROM_EMAIL,
                "Hey Zach, \nInvite '" + email + "' to momdiary.\nTheir invite link is '" + inviteLink + "'"
        );
    }
}
