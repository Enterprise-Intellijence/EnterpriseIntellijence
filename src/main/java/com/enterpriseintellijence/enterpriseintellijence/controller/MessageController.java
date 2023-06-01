package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.MessageService;
import com.enterpriseintellijence.enterpriseintellijence.dto.ConversationDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.basics.ProductBasicDTO;
import com.enterpriseintellijence.enterpriseintellijence.dto.creation.MessageCreateDTO;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/messages", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MessageController {
    // TODO: 16/05/23 Ciccio

    private final MessageService messageService;

    private final Bandwidth limit = Bandwidth.classic(20, Refill.greedy(25, Duration.ofMinutes(1)));
    private final Bucket bucket = Bucket.builder().addLimit(limit).build();

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

    @PostMapping(path="/read")
    @ResponseStatus(HttpStatus.OK)
    public void setReadMessages(@RequestBody List<String> idList){
        messageService.setReadMessages(idList);
    }

    @GetMapping("/conversations/{user}")
    public ResponseEntity<Page<MessageDTO>> getConversation(
            @PathVariable("user") String user,
            @RequestParam(name="prodId",required = false) String prodId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int sizePage){

        return ResponseEntity.ok(messageService.getConversation(user,prodId,page,sizePage));

    }

    @GetMapping("/conversations")
    public ResponseEntity<Iterable<ConversationDTO>> getAllMyConversations(){

        return ResponseEntity.ok(messageService.getAllMyConversations());

    }
}
