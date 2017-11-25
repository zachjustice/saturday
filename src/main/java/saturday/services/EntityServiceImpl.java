package saturday.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;
import saturday.domain.Entity;
import saturday.domain.Role;
import saturday.repositories.EntityRepository;
import saturday.repositories.RoleRepository;

import java.util.Collections;
import java.util.HashSet;

@Service("entityService")
public class EntityServiceImpl implements EntityService {

    private final EntityRepository entityRepository;
    private final RoleRepository roleRepository;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private Entity authenticatedEntity;

    @Autowired
    public EntityServiceImpl(EntityRepository entityRepository, RoleRepository roleRepository) {
        this.entityRepository = entityRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Entity findEntityByEmail(String email) {
        return entityRepository.findByEmail(email);
    }

    public Entity findEntityById(int id) {
        return entityRepository.findById(id);
    }

    public Entity saveEntity(Entity entity) {
        entity.setIsEnabled(true);
        Role authorRole = roleRepository.findByRole("USER");
        entity.setRoles(new HashSet<>(Collections.singletonList(authorRole)));
        logger.info("Saving entity " + entity.toString());

        return entityRepository.save(entity);
    }

    @Override
    public Entity getAuthenticatedEntity() {
        if(authenticatedEntity == null) {
            String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

            try {
                authenticatedEntity = findEntityByEmail(email);
            } catch (EmptyResultDataAccessException ex) {
                throw new RequestRejectedException("Couldn't find email for authenticated entity: '" + email + "'");
            }
        }

        return authenticatedEntity;
    }
}


