package com.enterpriseintellijence.enterpriseintellijence.dto.creation;

import com.enterpriseintellijence.enterpriseintellijence.dto.enums.EntertainmentLanguage;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@NoArgsConstructor
@ToString
@Builder
@AllArgsConstructor
public class EntertainmentCreateDTO extends ProductCreateDTO {
    @NotNull
    private EntertainmentLanguage entertainmentLanguage;
}
