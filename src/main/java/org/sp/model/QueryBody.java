package org.sp.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

@Jacksonized
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class QueryBody {
    public List<String> subjects;
    public List<String> properties;
}
