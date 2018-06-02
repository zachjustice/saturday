package saturday.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import saturday.domain.Entity;
import saturday.domain.Role;
import saturday.exceptions.BusinessLogicException;
import saturday.exceptions.ProcessingResourceException;
import saturday.repositories.EntityRepository;
import saturday.repositories.RoleRepository;
import saturday.utils.TokenAuthenticationUtils;

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
    public Entity findEntityByEmail(String email) {
        if(StringUtils.isEmpty(email)) {
            throw new BusinessLogicException("Empty email used while trying to find users by their email");
        }

        return entityRepository.findByEmail(email);
    }

    /**
     * Find entity by their id
     * @param id Id of the entity
     * @return The entity with the given id
     */
    @Override
    public Entity findEntityById(int id) {

        return entityRepository.findById(id);
    }

    @Override
    public Entity updateEntity(Entity updatedEntity) {
        return updateEntity(updatedEntity, false);
    }

    @Override
    public Entity updateEntity(Entity updatedEntity, boolean setPassword) {

        if(updatedEntity == null) {
            throw new ProcessingResourceException("Null entity argument while update entity.");
        }

        Entity currEntity = findEntityById(updatedEntity.getId());

        // allows users to change select fields on their entity
        String updatedName = updatedEntity.getName();
        String updatedEmail = updatedEntity.getEmail();
        Date updatedBirthday = updatedEntity.getBirthday();
        String updatedGender = updatedEntity.getGender();
        String updatedPassword = updatedEntity.getPassword();
        String updatedToken = updatedEntity.getToken();

        if(!StringUtils.isEmpty(updatedName)) {
            currEntity.setName(updatedName);
        }

        if(updatedBirthday != null) {
            currEntity.setBirthday(updatedBirthday);
        }

        if(!StringUtils.isEmpty(updatedGender)) {
            currEntity.setGender(updatedGender);
        }

        if(!StringUtils.isEmpty(updatedToken)) {
            // TODO multiple tokens
            currEntity.setToken(updatedToken);
        }

        if(!StringUtils.isEmpty(updatedPassword) && setPassword) {
            // TODO email verification
            currEntity.setPassword(bCryptPasswordEncoder.encode(updatedPassword));
        }

        if(!StringUtils.isEmpty(updatedEmail)) {
            currEntity.setEmail(updatedEmail);

            // Need a new token if the email was updated
            if(StringUtils.isEmpty(updatedToken)) {
                String token = TokenAuthenticationUtils.createToken(updatedEmail);
                currEntity.setToken(token);
            }
        }

        return entityRepository.save(currEntity);
    }

    public Entity saveEntity(Entity entity) {
        Entity entityWithSameEmail = findEntityByEmail(entity.getEmail());

        if(entityWithSameEmail != null) {
            throw new BusinessLogicException("An entity with the email '" + entity.getEmail() + "' already exists.");
        }

        if(StringUtils.isEmpty(entity.getPassword()) || entity.getPassword().length() < 8) {
            throw new BusinessLogicException("Password must be at least 8 characters.");
        }

        if(StringUtils.isEmpty(entity.getEmail())) {
            throw new BusinessLogicException("Email cannot be empty.");
        }

        if(StringUtils.isEmpty(entity.getName())) {
            throw new BusinessLogicException("Name cannot be empty.");
        }

        String token = TokenAuthenticationUtils.createToken(entity.getEmail());

        entity.setToken(token);
        entity.setIsEmailConfirmed(false);
        entity.setPassword(bCryptPasswordEncoder.encode(entity.getPassword()));

        Role authorRole = roleRepository.findByRole("USER");
        entity.setRoles(new HashSet<>(Collections.singletonList(authorRole)));

        return entityRepository.save(entity);
    }

    @Override
    public Entity getAuthenticatedEntity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Entity authenticatedEntity;

        try {
            authenticatedEntity = findEntityByEmail(email);
        } catch (BusinessLogicException e) {
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


