package com.github.jvalsesia.pcms.infrastructure.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.*;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuditEntity {
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
    @Column(name = "created_by", updatable = false)
    private String createdBy;
    @Column(name = "updated_at", insertable = false)
    private LocalDateTime updatedAt;
    @Column(name = "updated_by", insertable = false)
    private String updatedBy;
}
