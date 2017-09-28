package saturday.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import saturday.domain.Entity;
import saturday.domain.Role;
import saturday.repositories.EntityRepository;
import saturday.repositories.RoleRepository;

import java.util.Collections;
import java.util.HashSet;

@Service("entityService")
public class EntityServiceImpl implements EntityService {

    @Autowired
    private EntityRepository entityRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Entity findEntityByEmail(String email) {
        return entityRepository.findByEmail(email);
    }

    public Entity findEntityById(int id) {
        return entityRepository.findById(id);
    }

    public void saveEntity(Entity entity) {
        entity.setPassword(bCryptPasswordEncoder.encode(entity.getPassword()));
        entity.setIsEnabled(true);
        Role authorRole = roleRepository.findByRole("USER");
        entity.setRoles(new HashSet<>(Collections.singletonList(authorRole)));

        entityRepository.save(entity);
    }
}


