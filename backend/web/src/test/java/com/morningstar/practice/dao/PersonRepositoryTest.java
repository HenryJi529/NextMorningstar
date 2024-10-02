package com.morningstar.practice.dao;

import com.morningstar.practice.pojo.po.Person;
import com.morningstar.practice.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PersonRepositoryTest {
    private final PersonRepository personRepository;
    @Test
    public void testFindByName() {
        personRepository.findByNameContainingIgnoreCase("Tom").forEach(System.out::println);
        personRepository.findByNameLike("Tom").forEach(System.out::println);
        personRepository.findByNameRegex("Tom .*").forEach(System.out::println);
    }

    @Test
    public void testFindAll() {
        System.out.println("总的人员数: " + personRepository.findAll().size());
    }

    @Test
    public void testCreateAndDelete() {
        if(!personRepository.findByNameContainingIgnoreCase("Henry Ji").isEmpty()){
            personRepository.deleteByName("Henry Ji");
        }

        Person person = Person.builder().name("Henry Ji").born(1999).build();
        personRepository.save(person);
        System.out.println(person);

        System.out.println(personRepository.findById(person.getId()).orElse(null));
    }

    @Test
    public void testPage() {
        PageRequest pageRequest = PageRequest.of(0, 2, Sort.by(Sort.Order.desc("born")));
        Page<Person> page = personRepository.findByBornNotNull(pageRequest);
        page.getContent().forEach(System.out::println);
    }
}
