package com.anexa.core.services.crud.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.OptimisticLockException;

import org.springframework.transaction.annotation.Transactional;

import com.anexa.core.data.jpa.repository.IdentifiedDomainObjectRepository;
import com.anexa.core.domain.IdentifiedDomainObject;
import com.anexa.core.domain.VersionableObject;
import com.anexa.core.services.crud.api.CrudService;

import lombok.val;

@Transactional(readOnly = true)
abstract public class CrudServiceImpl<E extends IdentifiedDomainObject<ID>, M extends IdentifiedDomainObject<ID>, ID>
		extends QueryServiceImpl<E, M, ID> implements CrudService<M, ID> {

	@Override
	protected abstract IdentifiedDomainObjectRepository<E, ID> getRepository();

	// -----------------------------------------------------------------------------------------------------------------------
	// CREATE
	// -----------------------------------------------------------------------------------------------------------------------
	@Transactional
	@Override
	public M create(M model) {
		E entity = newEntity();

		entity = asEntity(model, entity);
		entity = beforeCreate(entity);
		entity = saveAndFlush(entity);

		val result = asModel(entity);
		return result;
	}

	@Transactional
	@Override
	public List<M> create(List<M> models) {
		val result = new ArrayList<M>();

		for (M m : models) {
			result.add(create(m));
		}

		return result;
	}

	protected E beforeCreate(E entity) {
		return entity;
	}

	// -----------------------------------------------------------------------------------------------------------------------
	// UPDATE
	// -----------------------------------------------------------------------------------------------------------------------
	@Transactional
	@Override
	public M update(M model) {
		E entity = findOneEntityById(model.getId());

		entity = asEntity(model, entity);
		entity = beforeUpdate(entity);

		if (entity instanceof VersionableObject && model instanceof VersionableObject) {
			val e = (VersionableObject) entity;
			val m = (VersionableObject) model;
			e.setVersion(m.getVersion());
		}

		entity = saveAndFlush(entity);

		M result = asModel(entity);
		return result;
	}

	@Transactional
	@Override
	public List<M> update(List<M> models) {
		val result = new ArrayList<M>();

		for (M m : models) {
			result.add(update(m));
		}

		return result;
	}

	protected E beforeUpdate(E entity) {
		return entity;
	}

	// -----------------------------------------------------------------------------------------------------------------------
	// DELETE
	// -----------------------------------------------------------------------------------------------------------------------
	@Transactional
	@Override
	public void delete(ID id) {
		E entity = findOneEntityById(id);

		if (entity instanceof VersionableObject) {
			throw new UnsupportedOperationException(
					"La entidad implenta la interfaz VersionableObject y debe ser eliminada por medio del metodo delete(ID id, int version)");
		}

		entity = beforeDelete(entity);
		deleteEntity(entity);
	}

	@Transactional
	@Override
	public void delete(List<ID> ids) {
		for (val e : ids) {
			delete(e);
		}
	}

	@Transactional
	@Override
	public void delete(ID id, int version) {
		E entity = findOneEntityById(id);

		if (!(entity instanceof VersionableObject)) {
			throw new UnsupportedOperationException(
					"La entidad NO implenta la interfaz VersionableObject y debe ser eliminada por medio del metodo delete(ID id)");
		}

		val e = (VersionableObject) entity;
		if (e.getVersion() != version) {
			throw new OptimisticLockException();
		}

		entity = beforeDelete(entity);
		deleteEntity(entity);
	}

	@Transactional
	@Override
	public void delete(Map<ID, Integer> models) {
		for (val e : models.entrySet()) {
			delete(e.getKey(), e.getValue());
		}
	}

	protected E beforeDelete(E entity) {
		return entity;
	}

	// -----------------------------------------------------------------------------------------------------------------------
	// Entities
	// -----------------------------------------------------------------------------------------------------------------------
	abstract protected E newEntity();

	protected abstract E asEntity(M model, E entity);

	protected E saveAndFlush(E entity) {
		val result = getRepository().saveAndFlush(entity);
		return result;
	}

	protected void deleteEntity(E entity) {
		getRepository().delete(entity);
	}
}
