package com.koreanguide.koreanguidebackend.domain.gpt.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.koreanguide.koreanguidebackend.domain.gpt.data.AssistantRequestDto;
import com.koreanguide.koreanguidebackend.domain.gpt.service.GptService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/gpt")
public class GptController {
    private final GptService gptService;

    @Autowired
    public GptController(GptService gptService) {
        this.gptService = gptService;
    }

    @PostMapping("/")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "X-AUTH-TOKEN", required = true,
                    dataType = "String", paramType = "header")
    })
    public ResponseEntity<?> getAssistantMsg(@RequestBody AssistantRequestDto assistantRequestDto)
            throws JsonProcessingException {
        return gptService.getAssistantMsg(assistantRequestDto);
    }
}
