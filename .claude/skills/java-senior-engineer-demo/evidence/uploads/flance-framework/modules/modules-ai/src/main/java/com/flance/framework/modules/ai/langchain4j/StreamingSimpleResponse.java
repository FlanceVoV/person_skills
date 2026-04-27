package com.flance.framework.modules.ai.langchain4j;

import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.chat.response.StreamingChatResponseHandler;
import reactor.core.publisher.FluxSink;

public class StreamingSimpleResponse implements StreamingChatResponseHandler {

    private final FluxSink<String> emitter;

    public StreamingSimpleResponse(FluxSink<String> emitter) {
        this.emitter = emitter;
    }

    @Override
    public void onPartialResponse(String partialResponse) {
        emitter.next("data:" + partialResponse + "\n\n");
    }

    @Override
    public void onCompleteResponse(ChatResponse chatResponse) {
        emitter.next("data:" + chatResponse.aiMessage().text() + "\n\n");
        emitter.complete();
    }

    @Override
    public void onError(Throwable throwable) {

    }
}
