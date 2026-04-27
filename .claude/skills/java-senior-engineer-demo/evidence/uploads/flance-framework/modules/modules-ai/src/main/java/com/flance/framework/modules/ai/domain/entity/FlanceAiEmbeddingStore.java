package com.flance.framework.modules.ai.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.flance.framework.common.datasource.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "FLANCE_AI_EMBEDDING_STORE", schema = "FLANCE_FRAMEWORK", catalog = "FLANCE_FRAMEWORK", comment = "模型向量存储", indexes = {
        @Index(name = "UDX_FLANCE_AI_EMBEDDING_STORE_MODEL_ID", columnList = "MODEL_ID", unique = true)
})
@TableName(value = "FLANCE_AI_EMBEDDING_STORE", schema = "FLANCE_FRAMEWORK")
public class FlanceAiEmbeddingStore extends BaseEntity {

    @Column(comment = "模型id")
    private String modelId;

    @Column(comment = "向量维度")
    private Integer dimension;

    @Column(comment = "存储前缀")
    private String prefix;

    @Column(comment = "redis地址")
    private String host;

    @Column(comment = "redis端口")
    private Integer port;

    @Column(comment = "redis密码")
    private String password;

}
