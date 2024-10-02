package com.morningstar.practice.dao;

import com.morningstar.practice.pojo.po.Movie;
import com.morningstar.practice.repository.MovieRepository;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
public class MovieRepositoryTest {
    @Resource
    private MovieRepository movieRepository;

    @Test
    public void testFindByTitleContaining() {
        List<Movie> movies = movieRepository.findByTitleContaining("The Matrix");
        System.out.println(Arrays.toString(movies.toArray()));
    }

    @Test
    public void testFindAll() {
        System.out.println("总的电影数: " + movieRepository.findAll().size());
    }
}
