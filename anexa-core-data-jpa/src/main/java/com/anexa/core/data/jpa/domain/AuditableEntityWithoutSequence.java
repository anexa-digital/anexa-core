package com.anexa.core.data.jpa.domain;

import javax.persistence.Column;
import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.anexa.core.domain.ObjectAuditableByUser;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@MappedSuperclass
@Getter
@Setter
@ToString(callSuper = true)
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class AuditableEntityWithoutSequence<ID> extends SimpleAuditableEntity<ID> implements ObjectAuditableByUser {

	@Column(name = "usuario_creacion", updatable = false)
	@CreatedBy
	private String creadoPor;

	@Column(name = "usuario_modificacion")
	@LastModifiedBy
	private String modificadoPor;
}
