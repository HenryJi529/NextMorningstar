package com.morningstar.practice.repository;

import com.morningstar.practice.pojo.po.Person;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface PersonRepository extends Neo4jRepository<Person, Long> {
    List<Person> findByNameContainingIgnoreCase(String name);

    Page<Person> findByBornNotNull(Pageable pageable);

    void deleteByName(String name);

    List<Person> findByNameLike(String name);

    List<Person> findByNameRegex(String pattern);

    @Query("""
        MATCH (person1:Person) where id(person1) = $person1Id
        MATCH (person2:Person) where id(person2) = $person2Id
        MATCH path = shortestPath((person1)-[:ACTED_IN|DIRECTED*..100]-(person2))
        RETURN path
        ORDER BY length(path)
        LIMIT 1
    """)
    Optional<Object> findShortestPath(Long person1Id, Long person2Id);
}
