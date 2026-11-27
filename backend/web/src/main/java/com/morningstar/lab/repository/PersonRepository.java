package com.morningstar.lab.repository;

import com.morningstar.lab.pojo.po.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends Neo4jRepository<Person, String> {
    List<Person> findByNameContainingIgnoreCase(String name);

    Page<Person> findByBornNotNull(Pageable pageable);

    void deleteByName(String name);

    List<Person> findByNameLike(String name);

    List<Person> findByNameRegex(String pattern);

    @Query("""
            MATCH (person1:Person) WHERE elementId(person1) = $person1Id
            MATCH (person2:Person) WHERE elementId(person2) = $person2Id
            MATCH path = shortestPath((person1)-[:ACTED_IN|DIRECTED*..100]-(person2))
            RETURN path
            ORDER BY length(path)
            LIMIT 1
            """)
    Optional<Object> findShortestPath(String person1Id, String person2Id);
}
