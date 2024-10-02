package com.morningstar.kill.pojo.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserSeasonRank {
    public Integer identity;

    public Integer revert;

    public Integer nation;

    public Integer solo;

    public Integer doubles;

    public Integer triples;
}
