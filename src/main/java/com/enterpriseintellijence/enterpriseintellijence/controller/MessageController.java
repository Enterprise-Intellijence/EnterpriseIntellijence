package com.enterpriseintellijence.enterpriseintellijence.controller;

import com.enterpriseintellijence.enterpriseintellijence.data.services.MessageService;
import com.enterpriseintellijence.enterpriseintellijence.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/messages", produces = "application/json")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MessageController {

    private final MessageService messageService;

    @PostMapping(consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDTO createMessage(@RequestBody MessageDTO messageDTO) {
        return messageService.createMessage(messageDTO);
    }

    @PutMapping(path = "/{id}", consumes = "application/json")
    public MessageDTO replaceMessage(@PathVariable("id") String id, @RequestBody MessageDTO messageDTO) {
        return messageService.replaceMessage(messageDTO).getBody();
    }

    @PatchMapping(path = "/{id}", consumes = "application/json")
    public ResponseEntity<MessageDTO> updateMessage(@PathVariable("id") String id, @RequestBody MessageDTO messageDTO) {
        return messageService.updateMessage(messageDTO);
    }

    @DeleteMapping(path = "/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<MessageDTO> deleteMessage(@PathVariable("id") String id) {
        return messageService.deleteMessage(id);
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<MessageDTO> getMessage(@PathVariable("id") String id) {
        return messageService.getMessage(id);
    }
}
