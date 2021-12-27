package com.dongkap.master.dao.specification;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.dongkap.master.entity.B2BEntity;


public class B2BSpecification {
	
	private static final String IS_ACTIVE = "active";

	public static Specification<B2BEntity> getSelect(final Map<String, Object> keyword) {
		return new Specification<B2BEntity>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -637621292944403277L;

			@Override
			public Predicate toPredicate(Root<B2BEntity> root, CriteriaQuery<?> criteria, CriteriaBuilder builder) {
				Predicate predicate = builder.conjunction();
				if (!keyword.isEmpty()) {
					for(Map.Entry<String, Object> filter : keyword.entrySet()) {
						String key = filter.getKey();
						Object value = filter.getValue();
						if (value != null) {
							switch (key) {
								case "_label" :
								case "bpName" :
									// builder.upper for PostgreSQL
									predicate.getExpressions().add(builder.like(root.join("businessPartner").<String>get("bpName"), String.format("%%%s%%", value.toString().toUpperCase())));
									break;
								case "id" :
									predicate.getExpressions().add(builder.equal(root.join("businessPartner").<String>get("id"), value));
									break;
								case "corporateCode" :
									predicate.getExpressions().add(builder.equal(root.join("corporate").<String>get("corporateCode"), value.toString()));
									break;
							}
						}
					}
				}
				predicate = builder.and(predicate, builder.equal(root.get(IS_ACTIVE), true));
				return predicate;
			}
		};
	}

	public static Specification<B2BEntity> getDatatable(final Map<String, Object> keyword) {
		return new Specification<B2BEntity>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -637621292944403277L;

			@Override
			public Predicate toPredicate(Root<B2BEntity> root, CriteriaQuery<?> criteria, CriteriaBuilder builder) {
				Predicate predicate = builder.conjunction();
				if (!keyword.isEmpty()) {
					for(Map.Entry<String, Object> filter : keyword.entrySet()) {
						String key = filter.getKey();
						Object value = filter.getValue();
						if (value != null) {
							switch (key) {
								case "bpName" :
									// builder.upper for PostgreSQL
									predicate.getExpressions().add(builder.like(root.join("businessPartner").<String>get("bpName"), String.format("%%%s%%", value.toString().toUpperCase())));
									break;
								case "corporateCode" :
									predicate.getExpressions().add(builder.equal(root.join("corporate").<String>get("corporateCode"), value.toString()));
									break;
								case "_all" :
									predicate.getExpressions().add(builder.like(root.join("businessPartner").<String>get("bpName"), String.format("%%%s%%", value.toString().toUpperCase())));
									break;
								default :
									break;
							}	
						}
					}
				}
				predicate = builder.and(predicate, builder.equal(root.get(IS_ACTIVE), true));
				return predicate;
			}
		};
	}

}
