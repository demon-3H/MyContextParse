package org.example.controller;

import org.example.service.TranslationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * OpenAIController
 *
 */
@RestController
public class OpenAIController {
    @Autowired
    private TranslationService translationService;

    @GetMapping("/chat/{str}")
    public String chat(@PathVariable("str") String str) throws Exception {
        return translationService.translationIdentification(str);
    }

}
