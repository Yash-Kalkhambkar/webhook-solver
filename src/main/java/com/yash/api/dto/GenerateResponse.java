package com.yash.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GenerateResponse {
    @JsonProperty("webhook")
    private String webhook;
    private String accessToken;
}
