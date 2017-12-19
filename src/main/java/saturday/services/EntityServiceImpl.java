package saturday.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
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

    @Autowired
    public EntityServiceImpl(EntityRepository entityRepository, RoleRepository roleRepository) {
        this.entityRepository = entityRepository;
        this.roleRepository = roleRepository;
    }

    /**
     * Used by register route to make sure an entity with the same email doesn't exist,
     * so we don't throw an exception in this case if we can't find the entity
     * @param email The email by which to find an entity
     * @return The entity with this email
     */
    @Override
    public Entity findEntityByEmail(String email) {
        Entity entity = entityRepository.findByEmail(email);
        return entity;
    }

    public Entity findEntityById(int id) {
        Entity entity = entityRepository.findById(id);

        if(entity == null) {
            throw new ResourceNotFoundException("No entity with the id " + id + " exists!");
        }

        return entity;
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
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Entity authenticatedEntity;

        try {
            authenticatedEntity = findEntityByEmail(email);
        } catch (EmptyResultDataAccessException ex) {
            throw new RequestRejectedException("Couldn't find email for authenticated entity: '" + email + "'");
        }

        return authenticatedEntity;
    }
}


