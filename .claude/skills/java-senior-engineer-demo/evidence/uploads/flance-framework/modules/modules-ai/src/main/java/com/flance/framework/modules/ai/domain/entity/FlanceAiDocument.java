package com.flance.framework.modules.ai.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.flance.framework.common.datasource.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "FLANCE_AI_DOCUMENT", schema = "FLANCE_FRAMEWORK", catalog = "FLANCE_FRAMEWORK", comment = "模型向量存储")
@TableName(value = "FLANCE_AI_DOCUMENT", schema = "FLANCE_FRAMEWORK")
public class FlanceAiDocument extends BaseEntity {

    private String fileId;

    private String modelId;

    private String fileName;

    private String fileSuffix;

    @Lob
    @Column(comment = "文件内容")
    private String fileContext;

}
