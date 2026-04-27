package com.flance.framework.modules.file.domain.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.flance.framework.common.datasource.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "FLANCE_FILE", schema = "FLANCE_FRAMEWORK", catalog = "FLANCE_FRAMEWORK", comment = "模型向量存储")
@TableName(value = "FLANCE_FILE", schema = "FLANCE_FRAMEWORK")
public class FlanceFile extends BaseEntity {

    @Column(comment = "文件名")
    private String fileName;

    @Column(comment = "文件路径")
    private String filePath;

    @Column(comment = "文件大小")
    private Long fileSize;

    @Column(comment = "文件大小（展示）")
    private String fileViewSize;

    @Column(comment = "文件后缀")
    private String fileSuffix;

    @Column(comment = "文件md5")
    private String fileMd5;

    @Column(comment = "文件url")
    private String fileUrl;

}
