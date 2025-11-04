package com.example.consultant.controller;

import com.example.consultant.AiService.consultantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.concurrent.Flow;

@RestController
public class ChatController {

    @Autowired
    private consultantService consultantservice;

    /*@RequestMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public Flux<String> chat(String memoryId, String message){
        Flux<String> result = consultantservice.chat(memoryId, message);
        return result;
    }*/


    //阻塞式调用
    @RequestMapping(value = "/chat", produces = "text/html;charset=utf-8")
    public String chat(String memoryId, String message){
        String result = consultantservice.chat(memoryId, message);
        return result;
    }

}
