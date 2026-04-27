package com.flance.framework.modules.ai.domain.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.flance.framework.modules.ai.domain.entity.FlanceAiRag;
import com.flance.framework.modules.ai.domain.mapper.FlanceAiRagMapper;
import com.flance.framework.modules.ai.domain.service.FlanceAiRagService;
import org.springframework.stereotype.Service;

@Service
public class FlanceAiRagServiceImpl extends ServiceImpl<FlanceAiRagMapper, FlanceAiRag> implements FlanceAiRagService {
}
