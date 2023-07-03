package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.MessageService;
import com.enterpriseintellijence.enterpriseintellijence.dto.ConversationDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.MessageCreateDTO;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

import static com.enterpriseintellijence.enterpriseintellijence.security.AppSecurityConfig.SECURITY_CONFIG_NAME;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/messages", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
@SecurityRequirement(name = SECURITY_CONFIG_NAME)
public class MessageController {
    // TODO: 16/05/23 Ciccio

    private final MessageService messageService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDTO createMessage(@Valid @RequestBody MessageCreateDTO messageCreateDTO) {
        return messageService.createMessage(messageCreateDTO);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public MessageDTO replaceMessage(@PathVariable("id") String id, @Valid @RequestBody MessageDTO messageDTO) throws IllegalAccessException {
        return messageService.replaceMessage(id, messageDTO);
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<MessageDTO> updateMessage(@PathVariable("id") String id, @Valid @RequestBody MessageDTO patch) throws IllegalAccessException {
        return ResponseEntity.ok(messageService.updateMessage(id, patch));
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteMessage(@PathVariable("id") String id) throws IllegalAccessException {
        messageService.deleteMessage(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<MessageDTO> getMessage(@PathVariable("id") String id) throws IllegalAccessException {
        return ResponseEntity.ok(messageService.getMessage(id));
    }

    @PostMapping(path = "/read")
    @ResponseStatus(HttpStatus.OK)
    public void setReadMessages(@RequestBody List<String> idList) {
        messageService.setReadMessages(idList);
    }



    @GetMapping("/conversations/{conversationId}")
    public ResponseEntity<Page<MessageDTO>> getConversation(
            @PathVariable("conversationId") String conversationId,
            @RequestParam(defaultValue = "0", required = false) int page,
            @RequestParam(defaultValue = "10", required = false) int sizePage) {

        return ResponseEntity.ok(messageService.getConversation(conversationId, page, sizePage));

    }


    @ApiResponses(value = {
        @ApiResponse(content = {
            @Content(
                mediaType = "application/json",
                array = @ArraySchema(schema = @Schema(implementation = ConversationDTO.class))
            )
        })
    })
    @GetMapping("/conversations")
    public ResponseEntity<Iterable<ConversationDTO>> getAllMyConversations() {

        return ResponseEntity.ok(messageService.getAllMyConversations());

    }

}
