package se.lexicon.g58todoapp.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import se.lexicon.g58todoapp.entity.Person;
import se.lexicon.g58todoapp.exception.PersonNotFoundException;
import se.lexicon.g58todoapp.repo.PersonRepository;
import se.lexicon.notify.model.Email;
import se.lexicon.notify.service.MessageService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class PersonServiceTest {
    @Mock
    PersonRepository personRepository;

    @Mock
    MessageService<Email> messageService;

    @InjectMocks
    PersonService personService;

    private Person testPerson;
    private final Long TEST_ID = 1L;
    private final String TEST_NAME = "Test Name";
    private final String TEST_EMAIL = "test@email.se";

    @BeforeEach
    void setUp() {
        testPerson = new Person(TEST_ID, TEST_NAME, TEST_EMAIL, LocalDate.parse("2026-01-01"), null);
    }

    @Test
    void createPerson_SavesAndSendsMessage() {
        // Arrange
        when(personRepository.save(any(Person.class))).thenReturn(testPerson);
        // Act
        personService.createPerson(testPerson);
        // Assert
        verify(personRepository).save(any(Person.class));
        verify(messageService).sendMessage(any(Email.class));
    }

    @Test
    void deletePerson_DeletesFromRepository() {
        // Arrange
        // Act
        personService.deletePerson(TEST_ID);
        // Assert
        verify(personRepository).deleteById(TEST_ID);
    }

    @Test
    void updatePerson_UpdatesAndReturnsPerson() {
        // Arrange
        when(personRepository.save(any(Person.class))).thenReturn(testPerson);
        // Act
        testPerson.setName("Updated name");
        Person updatedPerson = personService.updatePerson(testPerson);
        // Assert
        assertSame(testPerson, updatedPerson);
        verify(personRepository).save(any(Person.class));
    }

    @Test
    void updatePerson_Null_ThrowsException() {
        // Arrange
        when(personRepository.save(null)).thenThrow(IllegalArgumentException.class);
        // Act
        Executable result = () -> personService.updatePerson(null);
        // Assert
        assertThrows(IllegalArgumentException.class, result);
        verify(personRepository).save(null);
    }

    @Test
    void deletePerson_Null_ThrowsException() {
        // Arrange
        doThrow(IllegalArgumentException.class).when(personRepository).deleteById(null);
        // Act
        Executable result = () -> personService.deletePerson(null);
        // Assert
        assertThrows(IllegalArgumentException.class, result);
        verify(personRepository).deleteById(null);
    }

    @Test
    void findAll_ReturnsAllPersons() {
        // Arrange
        Person person2 = new Person( "person2", "person2@mail.se");
        List<Person> list= Arrays.asList(testPerson, person2);
        when(personRepository.findAll()).thenReturn(list); // if this method is called give this response ^ ( Mocking Data)
        // Act
        List<Person> result = personService.findAll();
        // Assert
        assertEquals(2, result.size());
        assertEquals(TEST_NAME, result.getFirst().getName());
        assertEquals(TEST_EMAIL, result.getFirst().getEmail());

        verify(personRepository).findAll(); // Verify that this method was actually called.
    }

    @Test
    void findById_ReturnsPerson() {
        // Arrange
        when(personRepository.findById(TEST_ID)).thenReturn(Optional.of(testPerson));
        // Act
        Person result = personService.findById(TEST_ID);
        // Assert
        assertNotNull(result);
        assertSame(testPerson, result);
        verify(personRepository).findById(anyLong());
    }

    @Test
    void findById_Null_ThrowsException() {
        // Arrange
        when(personRepository.findById(null)).thenThrow(IllegalArgumentException.class);
        // Act
        Executable result = () -> personService.findById(null);
        // Assert
        assertThrows(IllegalArgumentException.class, result);
        verify(personRepository).findById(null);
    }

    @Test
    void findByEmail_ReturnsPerson() {
        // Arrange
        when(personRepository.findByEmail(TEST_EMAIL)).thenReturn(Optional.of(testPerson));
        // Act
        Person result = personService.findByEmail(TEST_EMAIL);
        // Assert
        assertNotNull(result);
        assertSame(testPerson, result);
        verify(personRepository).findByEmail(anyString());
    }

    @Test
    void findByEmail_NonExistent_ThrowsException() {
        // Arrange
        when(personRepository.findByEmail(anyString())).thenReturn(Optional.empty());
        // Act
        Executable result = () -> personService.findByEmail("Non existent email");
        // Assert
        assertThrows(PersonNotFoundException.class, result);
        verify(personRepository).findByEmail(anyString());
    }
}