package se.lexicon.g58todoapp.repo;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    private final String TEST_NAME = "testName";
    private final String TEST_EMAIL = "test@test.se";
    private Person testPerson;

    @BeforeEach
    void Setup() {
        testPerson = new Person(TEST_NAME,TEST_EMAIL);
    }

    @Test
    @DisplayName("Retrieve all persons from the repository")
    void findAll_twoPersisted_returnTwo() {
        //Arrange
        Person person2 = new Person("Dana", "dana@example.com");
        personRepository.save(testPerson);
        personRepository.save(person2);
        //Act
        var all = personRepository.findAll();
        //Assert
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Save and find by email")
    void findByEmail_exists_returnEntity() {
        // Arrange
        personRepository.save(testPerson);
        // Act
        Optional<Person> retrievedPerson = personRepository.findByEmail(TEST_EMAIL);
        // Assert
        assertTrue(retrievedPerson.isPresent());
        assertEquals(TEST_NAME, retrievedPerson.get().getName());
        assertEquals(TEST_EMAIL, retrievedPerson.get().getEmail());
    }

    @Test
    @DisplayName("Exists by email should return true")
    void existsByEmail_exists_returnTrue() {
        // Arrange
        personRepository.save(testPerson);
        // Act
        boolean exists = personRepository.existsByEmail(TEST_EMAIL);
        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("Exists by email should return false for unknown email")
    void existsByEmail_dontExists_returnTrue() {
        // Arrange
        personRepository.save(testPerson);
        // Act
        boolean exists = personRepository.existsByEmail("dontexist@mail.com");
        // Assert
        assertFalse(exists);
    }

    @Test
    @DisplayName("Delete a person by email and verify existence")
    void testDeletePersonByEmail() {
        // Arrange
        personRepository.save(testPerson);
        // Act
        personRepository.delete(testPerson);
        var retrievedPerson = personRepository.findById(testPerson.getId());
        // Assert
        assertTrue(retrievedPerson.isEmpty());
    }
}