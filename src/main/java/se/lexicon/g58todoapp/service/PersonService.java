package se.lexicon.g58todoapp.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import se.lexicon.g58todoapp.entity.Person;
import se.lexicon.g58todoapp.repo.PersonRepository;
import se.lexicon.notify.model.Email;
import se.lexicon.notify.service.MessageService;

@Slf4j
@Service
public class PersonService {

    // TODO: Expand Service Layer methods

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
}
