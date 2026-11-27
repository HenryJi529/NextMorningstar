package com.morningstar.lab.service;

import com.morningstar.lab.pojo.bo.GraphData;
import com.morningstar.lab.pojo.bo.PersonMoviePair;
import com.morningstar.lab.pojo.po.Movie;
import com.morningstar.lab.pojo.po.Person;
import com.morningstar.lab.repository.PersonRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.neo4j.driver.Value;
import org.neo4j.driver.types.Node;
import org.neo4j.driver.types.Path;
import org.neo4j.driver.types.Relationship;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class SmallWorldNetworkService {
    private final PersonRepository personRepository;

    public GraphData getShortestPath(String person1Id, String person2Id) {
        Object rawPath = personRepository.findShortestPath(person1Id, person2Id).orElse(null);
        if (rawPath == null) {
            return null;
        }
        Path path = ((Value) rawPath).asPath();

        Map<String, Person> persons = new LinkedHashMap<>();
        Map<String, Movie> movies = new LinkedHashMap<>();
        for (Node node : path.nodes()) {
            String elementId = node.elementId();
            if (node.hasLabel("Person")) {
                persons.put(elementId, Person.builder()
                        .id(elementId)
                        .born(node.get("born").asInt())
                        .name(node.get("name").asString())
                        .build());
            } else {
                movies.put(elementId, Movie.builder()
                        .id(elementId)
                        .tagline(node.get("tagline").asString())
                        .title(node.get("title").asString())
                        .released(node.get("released").asInt())
                        .build());
            }
        }

        List<PersonMoviePair> pairs = new ArrayList<>();
        for (Relationship relationship : path.relationships()) {
            pairs.add(PersonMoviePair.builder()
                    .personId(relationship.startNodeElementId())
                    .movieId(relationship.endNodeElementId())
                    .type(relationship.type())
                    .build());
        }

        return GraphData.builder()
                .personList(List.copyOf(persons.values()))
                .movieList(List.copyOf(movies.values()))
                .pairList(pairs)
                .build();
    }

    public List<Person> findByNameContainingIgnoreCase(String name) {
        return personRepository.findByNameContainingIgnoreCase(name);
    }
}
