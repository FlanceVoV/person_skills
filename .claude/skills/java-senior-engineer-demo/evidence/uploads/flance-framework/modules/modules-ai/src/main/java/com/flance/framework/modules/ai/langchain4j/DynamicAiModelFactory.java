package com.flance.framework.modules.ai.langchain4j;

import com.flance.framework.modules.ai.domain.entity.FlanceAiModel;
import com.flance.framework.modules.ai.utils.DurationUtil;
import com.google.common.collect.Maps;
import dev.langchain4j.model.cohere.CohereScoringModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.model.openai.OpenAiStreamingChatModel;
import dev.langchain4j.model.scoring.ScoringModel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class DynamicAiModelFactory {

    private static final Map<String, OpenAiChatModel> MODEL_MAP = Maps.newConcurrentMap();

    private static final Map<String, OpenAiStreamingChatModel> STREAMING_MODEL_MAP = Maps.newConcurrentMap();

    private static final Map<String, OpenAiEmbeddingModel> EMBEDDING_MODEL_MAP = Maps.newConcurrentMap();

    private static final Map<String, ScoringModel> SCORING_MODEL_MAP = Maps.newConcurrentMap();

    @Resource
    private DynamicAiRedisVectorFactory dynamicAiRedisVectorFactory;


    public void createModel(FlanceAiModel model) {
        if (model.getModelType().equals("llm")) {
            getChatModel(model);
            getStreamingChatModel(model);
        }
        if (model.getModelType().equals("embedding")) {
            getEmbeddingModel(model);
        }
        if (model.getModelType().equals("rerank")) {
            getRerankModel(model);
        }
        log.info("【FLANCE AI】 create MODEL [{}] [{}] [{}]", model.getModelProvider(), model.getModelType(), model.getModelNameView());
    }

    public OpenAiChatModel getChatModel(FlanceAiModel model) {
        OpenAiChatModel chatModel;
        synchronized (DynamicAiModelFactory.class) {
            chatModel = MODEL_MAP.get(model.getId());
            if (null == chatModel) {
                chatModel = OpenAiChatModel.builder()
                        .baseUrl(model.getBaseUrl())
                        .apiKey(model.getApiKey())
                        .modelName(model.getModelName())
                        .timeout(DurationUtil.toDuration(model.getTimeout()))
                        .temperature(model.getTemperature())
                        .logRequests(true)
                        .build();
                MODEL_MAP.put(model.getId(), chatModel);
            }
        }
        return chatModel;
    }

    public OpenAiStreamingChatModel getStreamingChatModel(FlanceAiModel model) {
        OpenAiStreamingChatModel chatModel;
        synchronized (DynamicAiModelFactory.class) {
            chatModel = STREAMING_MODEL_MAP.get(model.getId());
            if (null == chatModel) {
                chatModel = OpenAiStreamingChatModel.builder()
                        .baseUrl(model.getBaseUrl())
                        .apiKey(model.getApiKey())
                        .modelName(model.getModelName())
                        .timeout(DurationUtil.toDuration(model.getTimeout()))
                        .temperature(model.getTemperature())
                        .logRequests(true)
                        .build();
                STREAMING_MODEL_MAP.put(model.getId(), chatModel);
            }
        }
        return chatModel;
    }

    public OpenAiEmbeddingModel getEmbeddingModel(FlanceAiModel model) {
        OpenAiEmbeddingModel embeddingModel;
        synchronized (DynamicAiModelFactory.class) {
            embeddingModel = EMBEDDING_MODEL_MAP.get(model.getId());
            if (null == embeddingModel) {
                embeddingModel = OpenAiEmbeddingModel.builder()
                        .baseUrl(model.getBaseUrl())
                        .apiKey(model.getApiKey())
                        .modelName(model.getModelName())
                        .timeout(DurationUtil.toDuration(model.getTimeout()))
                        .logRequests(true)
                        .build();
                EMBEDDING_MODEL_MAP.put(model.getId(), embeddingModel);

            }
        }
        dynamicAiRedisVectorFactory.getEmbeddingStore(model.getId(), embeddingModel);
        return embeddingModel;
    }

    public ScoringModel getRerankModel(FlanceAiModel model) {
        ScoringModel scoringModel;
        synchronized (DynamicAiModelFactory.class) {
            scoringModel = SCORING_MODEL_MAP.get(model.getId());
            if (scoringModel == null) {
                scoringModel = CohereScoringModel.builder()
                        .baseUrl(model.getBaseUrl())
                        .apiKey(model.getApiKey())
                        .modelName(model.getModelName())
                        .timeout(DurationUtil.toDuration(model.getTimeout()))
                        .logRequests(true)
                        .build();
                SCORING_MODEL_MAP.put(model.getId(), scoringModel);
            }
        }
        return scoringModel;
    }

}
