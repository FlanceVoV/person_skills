package com.flance.framework.modules.ai.langchain4j;

import com.flance.framework.modules.ai.domain.entity.FlanceAiEmbeddingStore;
import com.flance.framework.modules.ai.domain.service.FlanceAiEmbeddingStoreService;
import com.google.common.collect.Maps;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.Map;


@Component
public class DynamicAiRedisVectorFactory {

    private static final Map<String, EmbeddingStore<TextSegment>> EMBEDDING_STORE_MAP = Maps.newConcurrentMap();

    @Resource
    private FlanceAiEmbeddingStoreService flanceAiEmbeddingStoreService;

    public EmbeddingStore<TextSegment> getEmbeddingStore(String modelId, OpenAiEmbeddingModel embeddingModel) {

        EmbeddingStore<TextSegment> embeddingStore;
        synchronized (DynamicAiRedisVectorFactory.class) {
            FlanceAiEmbeddingStore flanceAiEmbeddingStore = getById(modelId, embeddingModel);
            embeddingStore = EMBEDDING_STORE_MAP.get(flanceAiEmbeddingStore.getId());
            if (null == embeddingStore) {
                embeddingStore = RedisEmbeddingStore.builder()
                        .host(flanceAiEmbeddingStore.getHost())
                        .port(flanceAiEmbeddingStore.getPort())
                        .password(flanceAiEmbeddingStore.getPassword())
                        .prefix(flanceAiEmbeddingStore.getPrefix())
                        .dimension(embeddingModel.dimension())
                        .build();
                EMBEDDING_STORE_MAP.put(flanceAiEmbeddingStore.getModelId(), embeddingStore);
            }
        }
        return embeddingStore;
    }

    private FlanceAiEmbeddingStore getById(String modelId, OpenAiEmbeddingModel embeddingModel) {
        FlanceAiEmbeddingStore flanceAiEmbeddingStore = flanceAiEmbeddingStoreService.getByModelId(modelId);
        if (null == flanceAiEmbeddingStore) {
            flanceAiEmbeddingStore = new FlanceAiEmbeddingStore();
            flanceAiEmbeddingStore.setModelId(modelId);
            flanceAiEmbeddingStore.setDimension(embeddingModel.dimension());
            flanceAiEmbeddingStore.setHost("127.0.0.1");
            flanceAiEmbeddingStore.setPassword("qwert@12345");
            flanceAiEmbeddingStore.setPort(6379);
            flanceAiEmbeddingStore.setPrefix("model:embedding:common:" + modelId + ":");
            flanceAiEmbeddingStore.setCreateUserId("system");
            flanceAiEmbeddingStore.setCreateUserName("system");
            flanceAiEmbeddingStore.preInsert("system", "system");
            flanceAiEmbeddingStore.preUpdate("system", "system");
            flanceAiEmbeddingStoreService.save(flanceAiEmbeddingStore);
        }
        return flanceAiEmbeddingStore;
    }

}
