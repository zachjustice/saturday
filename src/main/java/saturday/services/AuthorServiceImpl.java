package storyboard.services;

import java.util.Arrays;
import java.util.HashSet;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import storyboard.domain.Author;
import storyboard.domain.Role;
import storyboard.repositories.AuthorRepository;
import storyboard.repositories.RoleRepository;

@Service("authorService")
public class AuthorServiceImpl implements AuthorService {

    @Autowired
    private AuthorRepository authorRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    public Author findAuthorByEmail(String email) {
        return authorRepository.findByEmail(email);
    }

    @Override
    public Author findAuthorById(int id) {
        return authorRepository.findById(id);
    }

    @Override
    public void saveAuthor(Author author) {
        author.setPassword(bCryptPasswordEncoder.encode(author.getPassword()));
        author.setIsEnabled(true);
        Role authorRole = roleRepository.findByRole("USER");
        author.setRoles(new HashSet<>(Arrays.asList(authorRole)));

        authorRepository.save(author);
    }
}


