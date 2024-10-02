package com.morningstar.practice.repository;

import com.morningstar.practice.pojo.po.Movie;
import org.springframework.data.neo4j.repository.Neo4jRepository;

import java.util.List;

public interface MovieRepository extends Neo4jRepository<Movie, Long> {
    List<Movie> findByTitleContaining(String title);
}
