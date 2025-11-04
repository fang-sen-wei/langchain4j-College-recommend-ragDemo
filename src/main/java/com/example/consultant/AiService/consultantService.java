package com.example.consultant.AiService;


import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;

import java.util.concurrent.Flow;


@AiService
public interface consultantService {


    //根据userId 支持记忆保存对话
    //Flux<String> chat(@MemoryId String userId, @UserMessage String message);

    //阻塞式调用
    @SystemMessage("你是广东省高考填报专家，熟知各大学在广东省的最低分数线,并给出填报意见")
    String chat(@MemoryId String userId, @UserMessage String message);

}
