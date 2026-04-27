package com.flance.framework.modules.ai.domain.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.flance.framework.modules.ai.domain.entity.FlanceAiEmbeddingStore;

public interface FlanceAiEmbeddingStoreService extends IService<FlanceAiEmbeddingStore> {

    FlanceAiEmbeddingStore getByModelId(String modelId);

}
