package ru.panyukovnn.rfopenllmbillingmanager.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateSessionRequest {

    @Size(min = 1, max = 255)
    private String title;

    @Size(min = 1, max = 255)
    private String model;

    private String systemPrompt;
}
