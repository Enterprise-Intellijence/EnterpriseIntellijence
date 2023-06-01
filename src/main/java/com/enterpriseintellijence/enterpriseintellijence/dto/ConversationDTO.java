package com.enterpriseintellijence.enterpriseintellijence.dto;

import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.UserBasicDTO;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ConversationDTO {
    private ProductBasicDTO productBasicDTO;
    @NotNull
    private UserBasicDTO otherUser;
    @NotNull
    private MessageDTO lastMessage;
    @NotNull
    private int unreadMessagesCount;
}
