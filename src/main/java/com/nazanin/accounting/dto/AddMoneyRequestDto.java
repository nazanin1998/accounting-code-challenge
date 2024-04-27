package com.nazanin.accounting.dto;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class AddMoneyRequestDto {
    @NotNull
    @JsonProperty("user_id")
    private Long userId;
    @NotNull
    @JsonProperty("amount")
    private Long amount;
}
