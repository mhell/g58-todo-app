package se.lexicon.g58todoapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.lexicon.g58todoapp.entity.Person;
import se.lexicon.g58todoapp.exception.PersonNotFoundException;
import se.lexicon.g58todoapp.repo.PersonRepository;
import se.lexicon.notify.model.Email;
import se.lexicon.notify.service.MessageService;

import java.util.List;

@Slf4j
@Service
public class PersonService {

    // TODO: Return dto

    PersonRepository personRepository;
    MessageService<Email> messageService;

    public PersonService(PersonRepository personRepository, MessageService<Email> messageService) {
        this.personRepository = personRepository;
        this.messageService = messageService;
    }

    public void createPerson(Person person) {
        person = personRepository.save(person);
        if (person.getId() != null){
            boolean sentMessage = messageService.sendMessage(new Email(
                    person.getEmail(),
                    "Welcome 🫡",
                            """
                            Hello, %s
                            Thank you for signing up to our App.
                            We hope you enjoy using it. 🎉
                            """.formatted(person.getName())));
            if (!sentMessage){
                log.error("Failed to send welcome email to: {}", person.getEmail());
            } else {
                log.info("Successfully sent welcome email to: {}",person.getEmail());
            }
        }
    }

    public void deletePerson(Long id) {
        personRepository.deleteById(id);
    }

    public Person updatePerson(Person person) {
        return personRepository.save(person);
    }

    public List<Person> findAll() {
        return personRepository.findAll();
    }

    public Person findById(Long id){
        return personRepository.findById(id).orElseThrow(()-> new PersonNotFoundException("Person not found"));
    }

    public Person findByEmail(String email) {
        return personRepository.findByEmail(email).orElseThrow(()-> new PersonNotFoundException("Person not found"));
    }
}
