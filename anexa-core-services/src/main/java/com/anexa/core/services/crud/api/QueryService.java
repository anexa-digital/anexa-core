package com.anexa.core.services.crud.api;

import java.util.List;
import java.util.Optional;

import org.springframework.transaction.annotation.Transactional;

import com.anexa.core.domain.IdentifiedDomainObject;

@Transactional(readOnly = true)
public interface QueryService<M extends IdentifiedDomainObject<ID>, ID> {

	M findOneById(ID id);

	Optional<M> findById(ID id);

	List<M> findAllById(List<ID> ids);
}