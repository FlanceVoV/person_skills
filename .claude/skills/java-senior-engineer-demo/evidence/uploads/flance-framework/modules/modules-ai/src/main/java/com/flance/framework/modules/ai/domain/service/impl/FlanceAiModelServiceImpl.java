package com.flance.framework.modules.ai.domain.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flance.framework.modules.ai.domain.entity.FlanceAiModel;
import com.flance.framework.modules.ai.domain.mapper.FlanceAiModelMapper;
import com.flance.framework.modules.ai.domain.service.FlanceAiModelService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FlanceAiModelServiceImpl extends ServiceImpl<FlanceAiModelMapper, FlanceAiModel> implements FlanceAiModelService {

    @Override
    public List<FlanceAiModel> list() {
        LambdaQueryWrapper<FlanceAiModel> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(FlanceAiModel::getDeleted, 0);
        return list(queryWrapper);
    }

}
