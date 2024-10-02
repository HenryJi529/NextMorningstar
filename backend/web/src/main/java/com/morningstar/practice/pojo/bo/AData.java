package com.morningstar.practice.pojo.bo;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class AData {
    private List<Double> values;
    private List<LocalDate> dates;
}