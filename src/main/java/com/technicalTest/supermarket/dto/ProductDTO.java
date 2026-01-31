package com.technicalTest.supermarket.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductDTO {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank(message = "The name is required")
    @Size(min = 3, max = 50, message = "The name must be between 3 and 50 letters long")
    private String name;
    @NotNull(message = "The price can't be a null value")
    @Positive(message = "The price must be a positive value")
    private BigDecimal price;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime registrationDate;
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime modificationDate;
}
