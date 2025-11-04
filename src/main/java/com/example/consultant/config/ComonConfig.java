package com.example.consultant.config;

import com.example.consultant.AiService.consultantService;
import dev.langchain4j.data.document.Document;

import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import dev.langchain4j.model.ollama.OllamaStreamingChatModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.model.ollama.OllamaChatModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import javassist.ClassPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class ComonConfig {

    //加这个会发生依赖循环的问题
    //因为直接在aiService中注入chat_model，会循环依赖
    /*@Autowired
    private OllamaChatModel chat_model;*/

    @Value("${ollama.baseUrl}")
    private String baseUrl;

    @Value("${ollama.model}")
    private String model;

    //流式调用要用streamingChatModel
    //还需要添加额外的依赖
    /*@Bean
    public StreamingChatModel streamingChatModel() {
        return OllamaStreamingChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(model)
                .logRequests(true)
                .logResponses(true)
                .build();
    }*/

    @Bean
    public ChatLanguageModel chatModel() {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(model)
                .build();
    }


    @Bean
    public ChatMemoryProvider chatMemoryProvider() {
        ChatMemoryProvider chatMemoryProvider = new ChatMemoryProvider() {
            @Override
            public ChatMemory get(Object memoryId) {
                return MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .maxMessages(5)
                        .build();
            }
        };
        return chatMemoryProvider;
    }

    @Bean
    public EmbeddingStore<TextSegment> store() {

        // 加载文档
        // 加载文档 - 使用文件系统方式加载rag目录
        List<Document> documents = FileSystemDocumentLoader.loadDocuments( Paths.get("src/main/resources/rag"));



        // 使用 TextSegment 泛型
        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();

        var embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl(baseUrl)
                .modelName("nomic-embed-text")
                .build();

        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(DocumentSplitters.recursive(500, 100))
                .embeddingModel(embeddingModel)
                .embeddingStore(store)
                .build();

        ingestor.ingest(documents);

        return store;
    }

    @Bean
    public ContentRetriever contentRetriever(EmbeddingStore<TextSegment> store) {

        var embeddingModel = OllamaEmbeddingModel.builder()
                .baseUrl(baseUrl)
                .modelName("nomic-embed-text")
                .build();

        return EmbeddingStoreContentRetriever.builder()
                .embeddingStore(store)
                .embeddingModel(embeddingModel)
                .maxResults(3)
                .minScore(0.7)
                .build();
    }




    //阻塞式调用
    /*@Bean
    public OllamaChatModel ollamaChatModel() {
        return OllamaChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(model)
                .logRequests(true) //打印请求和响应日志信息
                .logResponses(true)
                .build();
    }*/

    //在consultantService上注解了 @AiService标签 即可自动装配接口对象注入IOC
    //详细看consultantService接口！
    //以下为手动装配一个接口对象注入ioc 容器中
    /*@Bean
    public consultantService aiService(OllamaChatModel ollamaChatModel){
        consultantService service = AiServices.builder(consultantService.class)
                .chatModel(ollamaChatModel)
                .build();
        return service;
    }*/
}
