package com.github.jvalsesia.pcms.domain.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "ErrorResponse", description = "Schema to hold error response information")
public class ErrorResponse {
    @Schema(description = "Error message representing when error happened")
    private LocalDateTime timestamp;
    @Schema(description = "Error message representing the error status")
    private int status;
    @Schema(description = "Error message representing the type of the error")
    private String error;
    @Schema(description = "Error message representing the message itself")
    private String message;
    @Schema(description = "Error message representing the map of errors")
    private Map<String, String> validationErrors;

}