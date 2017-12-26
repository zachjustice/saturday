package saturday.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import saturday.domain.Entity;
import saturday.domain.Role;
import saturday.exceptions.ProcessingResourceException;
import saturday.repositories.EntityRepository;
import saturday.repositories.RoleRepository;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

@Service("entityService")
public class EntityServiceImpl implements EntityService {

    private final EntityRepository entityRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EntityServiceImpl(EntityRepository entityRepository, RoleRepository roleRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.entityRepository = entityRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    /**
     * Used by register route to make sure an entity with the same email doesn't exist,
     * so we don't throw an exception in this case if we can't find the entity
     * @param email The email by which to find an entity
     * @return The entity with this email
     */
    @Override
    public Entity findEntityByEmail(String email) throws ProcessingResourceException {
        if(StringUtils.isEmpty(email)) {
            throw new ProcessingResourceException("Empty email used while trying to find users by their email");
        }

        return entityRepository.findByEmail(email);
    }

    public Entity findEntityById(int id) {
        Entity entity = entityRepository.findById(id);

        if(entity == null) {
            throw new ResourceNotFoundException("No entity with the id " + id + " exists!");
        }

        return entity;
    }

    public Entity updateEntity(Entity currEntity, Entity updatedEntity) throws ProcessingResourceException {
        if(currEntity == null || updatedEntity == null) {
            throw new ProcessingResourceException("Null entity argument while update entity.");
        }

        if(updatedEntity.getId() != currEntity.getId()) {
            throw new ProcessingResourceException("Updated entity's id does not match current entity's id.");
        }

        // only users to change select fields on their entity
        String updatedName = updatedEntity.getName();
        String updatedEmail = updatedEntity.getEmail();
        Date updatedBirthday = updatedEntity.getBirthday();
        String updatedGender = updatedEntity.getGender();
        String updatedPassword = updatedEntity.getPassword();

        if(!StringUtils.isEmpty(updatedName)) {
            currEntity.setName(updatedName);
        }

        if(!StringUtils.isEmpty(updatedEmail)) {
            currEntity.setEmail(updatedEmail);
        }

        if(updatedBirthday != null) {
            currEntity.setBirthday(updatedBirthday);
        }

        if(!StringUtils.isEmpty(updatedGender)) {
            currEntity.setGender(updatedGender);
        }

        if(!StringUtils.isEmpty(updatedPassword)) {
            // TODO email verification
            currEntity.setPassword(bCryptPasswordEncoder.encode(updatedEntity.getPassword()));
        }

        return entityRepository.save(currEntity);
    }

    public Entity saveEntity(Entity entity) throws ProcessingResourceException {
        Entity entityWithSameEmail = findEntityByEmail(entity.getEmail());

        if(entityWithSameEmail != null) {
            throw new ProcessingResourceException("An entity with the email '" + entity.getEmail() + "' already exists.");
        }

        if(StringUtils.isEmpty(entity.getPassword()) || entity.getPassword().length() < 8) {
            throw new ProcessingResourceException("Password must be at least 8 characters.");
        }

        if(StringUtils.isEmpty(entity.getEmail())) {
            throw new ProcessingResourceException("Email cannot be empty.");
        }

        if(StringUtils.isEmpty(entity.getName())) {
            throw new ProcessingResourceException("Name cannot be empty.");
        }

        entity.setPassword(bCryptPasswordEncoder.encode(entity.getPassword()));
        entity.setIsEnabled(true);
        Role authorRole = roleRepository.findByRole("USER");
        entity.setRoles(new HashSet<>(Collections.singletonList(authorRole)));

        return entityRepository.save(entity);
    }

    @Override
    public Entity getAuthenticatedEntity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Entity authenticatedEntity;

        logger.info("AUTHED AS " + email);

        try {
            authenticatedEntity = findEntityByEmail(email);
        } catch (ProcessingResourceException e) {
            throw new RequestRejectedException("Could not retrieve entity by email for the authenticated entity. " +
                    "The email for the authenticated entity might be null");
        } catch (EmptyResultDataAccessException ex) {
            throw new RequestRejectedException("Couldn't find email for authenticated entity: '" + email + "'");
        }

        if(authenticatedEntity == null) {
            throw new RequestRejectedException("Error authenticating entity.");
        }

        return authenticatedEntity;
    }
}


