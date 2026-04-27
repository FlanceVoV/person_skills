package com.flance.framework.modules.ai.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.flance.framework.common.datasource.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "FLANCE_AI_RAG", schema = "FLANCE_FRAMEWORK", catalog = "FLANCE_FRAMEWORK", comment = "RAG表")
@TableName(value = "FLANCE_AI_RAG", schema = "FLANCE_FRAMEWORK")
public class FlanceAiRag extends BaseEntity {



}
