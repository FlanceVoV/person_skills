package com.flance.framework.modules.ai.init;

import com.flance.framework.modules.ai.domain.entity.FlanceAiModel;
import com.flance.framework.modules.ai.domain.service.FlanceAiModelService;
import com.flance.framework.modules.ai.langchain4j.DynamicAiModelFactory;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class Init {

    @Resource
    FlanceAiModelService flanceAiModelService;

    @Resource
    DynamicAiModelFactory dynamicAiModelFactory;

    @PostConstruct
    public void init() {
        List<FlanceAiModel> models = flanceAiModelService.list();
        models.forEach(dynamicAiModelFactory::createModel);
    }

}
