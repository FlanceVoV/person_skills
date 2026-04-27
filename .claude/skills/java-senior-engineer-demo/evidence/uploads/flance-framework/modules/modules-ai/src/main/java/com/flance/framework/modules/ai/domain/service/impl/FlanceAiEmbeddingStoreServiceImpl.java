package com.flance.framework.modules.ai.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flance.framework.modules.ai.domain.entity.FlanceAiEmbeddingStore;
import com.flance.framework.modules.ai.domain.mapper.FlanceAiEmbeddingStoreMapper;
import com.flance.framework.modules.ai.domain.service.FlanceAiEmbeddingStoreService;
import org.springframework.stereotype.Service;

@Service
public class FlanceAiEmbeddingStoreServiceImpl extends ServiceImpl<FlanceAiEmbeddingStoreMapper, FlanceAiEmbeddingStore> implements FlanceAiEmbeddingStoreService {

    @Override
    public FlanceAiEmbeddingStore getByModelId(String modelId) {
        LambdaQueryWrapper<FlanceAiEmbeddingStore> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlanceAiEmbeddingStore::getModelId, modelId);
        queryWrapper.eq(FlanceAiEmbeddingStore::getDeleted, 0);
        return getOne(queryWrapper);
    }
}
