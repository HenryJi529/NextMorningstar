package com.morningstar.kill.pojo.bo;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.morningstar.kill.serializer.SeasonStatsSerializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSeasonStats {
    @JsonSerialize(using = SeasonStatsSerializer.class)
    public Double identity;

    @JsonSerialize(using = SeasonStatsSerializer.class)
    public Double revert;

    @JsonSerialize(using = SeasonStatsSerializer.class)
    public Double nation;

    @JsonSerialize(using = SeasonStatsSerializer.class)
    public Double solo;

    @JsonSerialize(using = SeasonStatsSerializer.class)
    public Double doubles;

    @JsonSerialize(using = SeasonStatsSerializer.class)
    public Double triples;
}
