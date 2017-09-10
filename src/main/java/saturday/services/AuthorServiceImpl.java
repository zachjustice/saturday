package saturday.services;

import java.util.Arrays;
import java.util.HashSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import saturday.domain.Entity;
import saturday.domain.Role;
import saturday.repositories.EntityRepository;
import saturday.repositories.RoleRepository;

@Service("authorService")
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private EntityRepository entityRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Entity findAuthorByEmail(String email) {
        return entityRepository.findByEmail(email);
    }

    @Override
    public Entity findAuthorById(int id) {
        return entityRepository.findById(id);
    }

    @Override
    public void saveAuthor(Entity entity) {
        entity.setPassword(bCryptPasswordEncoder.encode(entity.getPassword()));
        entity.setIsEnabled(true);
        Role authorRole = roleRepository.findByRole("USER");
        entity.setRoles(new HashSet<>(Arrays.asList(authorRole)));

        entityRepository.save(entity);
    }
}


