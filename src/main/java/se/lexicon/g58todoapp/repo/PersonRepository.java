package se.lexicon.g58todoapp.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import se.lexicon.g58todoapp.entity.Person;

import java.util.Optional;

public interface PersonRepository extends JpaRepository<Person, Long> {
    // Find person with a email
    Optional<Person> findByEmail(String email);

    // Is there a person with a specific email?
    boolean findByEmailExists(String email);
}