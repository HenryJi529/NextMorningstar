package com.morningstar.practice.pojo.bo;

import com.morningstar.practice.pojo.po.Movie;
import com.morningstar.practice.pojo.po.Person;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GraphData {
    private List<Person> personList;

    private List<Movie> movieList;

    private List<Pair> pairList;
}
