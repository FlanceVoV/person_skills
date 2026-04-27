package com.flance.framework.modules.ai.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.flance.framework.common.datasource.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.Duration;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "FLANCE_AI_MODEL", schema = "FLANCE_FRAMEWORK", catalog = "FLANCE_FRAMEWORK", comment = "模型信息表")
@TableName(value = "FLANCE_AI_MODEL", schema = "FLANCE_FRAMEWORK")
public class FlanceAiModel extends BaseEntity {

    @Column(comment = "模型提供商")
    private String modelProvider;

    @Column(comment = "模型api风格")
    private String modelApi;

    @Column(comment = "模型显示名称")
    private String modelNameView;

    @Column(comment = "模型名称")
    private String modelName;

    @Column(comment = "模型路径")
    private String modelPath;

    @Column(comment = "密钥")
    private String apiKey;

    @Column(comment = "基础访问路径")
    private String baseUrl;

    @Column(comment = "超时时间，单位ms")
    private Long timeout;

    @Column(comment = "模型类型：embedding, rerank, llm")
    private String modelType;

    @Column(comment = "温度")
    private Double temperature;

}
