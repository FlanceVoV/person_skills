package com.flance.framework.common.datasource.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@MappedSuperclass
public class BaseEntity {

    @Id
    @TableId(type = IdType.ASSIGN_ID)
    @Column(comment = "主键")
    private String id;

    @Column(comment = "创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @Column(comment = "更新时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;

    @Column(comment = "删除标记，0.正常 1.删除")
    private Integer deleted;

    @Column(comment = "创建人id")
    private String createUserId;

    @Column(comment = "创建人姓名（快照）")
    private String createUserName;

    @Column(comment = "更新人id")
    private String updateUserId;

    @Column(comment = "更新人姓名（快照）")
    private String updateUserName;

    public void preInsert(String createUserId, String createUserName) {
        this.createTime = LocalDateTime.now();
        this.updateTime = LocalDateTime.now();
        this.deleted = 0;
        this.createUserId = createUserId;
        this.createUserName = createUserName;
    }

    public void preUpdate(String updateUserId, String updateUserName) {
        this.updateTime = LocalDateTime.now();
        this.updateUserId = updateUserId;
        this.updateUserName = updateUserName;
    }

}
