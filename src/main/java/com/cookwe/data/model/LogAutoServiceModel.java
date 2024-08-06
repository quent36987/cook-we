package com.cookwe.data.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Builder
@Table(name = "log_auto_service")
public class LogAutoServiceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "userid", nullable = false)
    private Long userId;

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @Column(name = "picture_size")
    private BigDecimal pictureSize;

    @Column(name = "api_response", columnDefinition = "TEXT")
    private String apiResponse;

    @Column(name = "exit_code", length = 50)
    private String exitCode;

    @Column(name = "token_count")
    private String tokenCount;
}
