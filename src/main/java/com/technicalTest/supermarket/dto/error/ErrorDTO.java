package com.technicalTest.supermarket.dto.error;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorDTO(LocalDateTime timestamp, String message) {
}
