package se.lexicon.g58todoapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import se.lexicon.g58todoapp.entity.Person;
import se.lexicon.g58todoapp.repo.PersonRepository;

import java.util.List;

//localhost:8080/api/people for all the endpoint in this class.
@RequestMapping("/api/people")
@RestController
public class PersonController {

    private final PersonRepository personRepository;

    public PersonController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    //RequestParam = ?name=Simon
    //GET localhost:8080/api/hello?name=Simon
    @RequestMapping(value = "/hello")
    public String hello (@RequestParam(defaultValue = "from PersonController!") String name) {
        return "Hello, " + name;
    }

    //GET localhost:8080/api/people
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<Person> getPersons() {
        return personRepository.findAll();
    }

    //PathVariable = {id}
    //GET localhost:8080/api/people/1
    @GetMapping("/{id}")
    public Person getPersonById(@PathVariable Long id){
        return personRepository.findById(id).orElseThrow(RuntimeException::new);
    }

    //POST localhost:8080/api/people
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping
    public void createPerson(@RequestBody Person person){
        personRepository.save(person);
    }

    //DELETE Localhost:8080/api/people/1
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePerson(@PathVariable Long id){
        personRepository.deleteById(id);
    }





}
