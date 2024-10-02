package com.morningstar.practice.pojo.po;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.neo4j.core.schema.GeneratedValue;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node("Movie")
@Data
@Builder
public class Movie {
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private Integer released;
    private String tagline;
}
