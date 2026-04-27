package com.flance.framework.application.webflux;

import com.flance.framework.modules.ai.domain.service.FlanceAiModelService;
import com.flance.framework.modules.ai.langchain4j.DynamicAiModelFactory;
import com.flance.framework.modules.ai.langchain4j.StreamingSimpleResponse;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.List;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM_VALUE;

@RestController
@RequestMapping("/assistant")
public class AssistantController {

    @Resource
    DynamicAiModelFactory dynamicAiModelFactory;

    @Resource
    FlanceAiModelService flanceAiModelService;

    @RequestMapping(value = "/stream", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam String message) {
        return Flux.interval(Duration.ofSeconds(1))
                .map(index -> "assistant -> " + message + " -> index:" + index)
                .take(10);
    }

    @RequestMapping(value = "/chatStream", produces = TEXT_EVENT_STREAM_VALUE)
    public Flux<String> chatStream(@RequestParam String message, @RequestParam String modelId) {
        OpenAiStreamingChatModel streamingChatModel = dynamicAiModelFactory.getStreamingChatModel(flanceAiModelService.getById(modelId));
        return Flux.create(emitter -> {
            streamingChatModel.chat(List.of(UserMessage.from(message)), new StreamingSimpleResponse(emitter));
        });
    }


}
