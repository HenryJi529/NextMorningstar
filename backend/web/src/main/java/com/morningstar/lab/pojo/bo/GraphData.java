package com.morningstar.lab.pojo.bo;

import com.morningstar.lab.pojo.po.Movie;
import com.morningstar.lab.pojo.po.Person;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GraphData {
    private List<Person> personList;

    private List<Movie> movieList;

    private List<PersonMoviePair> pairList;
}
