package se.lexicon.g58todoapp.repo;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import se.lexicon.g58todoapp.entity.Person;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class PersonRepositoryTest {

    @Autowired
    PersonRepository personRepository;

    @Test
    void findByEmail_exists_returnEntity() {
        // Arrange
        String email = "test@test.se";
        Person person = new Person("Name", email);
        personRepository.save(person);
        // Act
        Optional<Person> foundPerson = personRepository.findByEmail(email);
        // Assert
        assertFalse(foundPerson.isEmpty());
        assertEquals(email, foundPerson.get().getEmail());
    }

    @Test
    void existsByEmail_exists_returnTrue() {
        // Arrange
        String email = "test@test.se";
        Person person = new Person("Name", email);
        personRepository.save(person);
        // Act
        boolean exists = personRepository.existsByEmail(email);
        // Assert
        assertTrue(exists);
    }
}