package com.cookwe.domain.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LogAutoServiceDTO {

    private Long userId;

    private LocalDateTime timestamp;

    private BigDecimal pictureSize;

    private String apiResponse;

    private String exitCode;

    private String tokenCount;

    private Boolean isParseSuccess;

    private String exception;
}
