package com.morningstar.dev.pojo.bo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class StructuredAiScanOutput {
    private List<AiIssue> issues;
}
