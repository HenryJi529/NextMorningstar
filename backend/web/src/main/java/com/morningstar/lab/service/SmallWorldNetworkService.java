package com.morningstar.lab.service;

import com.morningstar.lab.pojo.bo.GraphData;
import com.morningstar.lab.pojo.bo.PersonMoviePair;
import com.morningstar.lab.pojo.po.Movie;
import com.morningstar.lab.pojo.po.Person;
import com.morningstar.lab.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.internal.value.PathValue;
import org.neo4j.driver.types.Path;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmallWorldNetworkService {
    private final PersonRepository personRepository;

    public GraphData getShortestPath(Long person1Id, Long person2Id) {
        List<Person> personList = new ArrayList<>();
        List<Movie> movieList = new ArrayList<>();
        List<PersonMoviePair> personMoviePairList = new ArrayList<>();
        PathValue pathValue = (PathValue) personRepository.findShortestPath(person1Id, person2Id).orElse(null);
        if (pathValue == null) {
            return null;
        }
        Path path = pathValue.asPath();
        path.nodes().forEach((node) -> {
            if (node.hasLabel("Person")) {
                Person person = Person.builder().id(node.id()).born(node.get("born").asInt()).name(node.get("name").asString()).build();
                personList.add(person);
            } else {
                Movie movie = Movie.builder().id(node.id()).tagline(node.get("tagline").asString()).title(node.get("title").asString()).released(node.get("released").asInt()).build();
                movieList.add(movie);
            }
        });
        path.relationships().forEach((relationship) -> {
            Person person = personList.stream().filter(node -> node.getId().equals(relationship.startNodeId())).toList().get(0);
            Movie movie = movieList.stream().filter(node -> node.getId().equals(relationship.endNodeId())).toList().get(0);

            personMoviePairList.add(PersonMoviePair.builder().personId(person.getId()).movieId(movie.getId()).type(relationship.type()).build());
        });
        return GraphData.builder().personList(personList).movieList(movieList).pairList(personMoviePairList).build();
    }

    public List<Person> findByNameContainingIgnoreCase(String name) {
        return personRepository.findByNameContainingIgnoreCase(name);
    }
}
