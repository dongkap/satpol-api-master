package com.dongkap.master.dao.specification;

import java.util.Map;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.jpa.domain.Specification;

import com.dongkap.master.entity.BusinessPartnerEntity;


public class BusinessPartnerSpecification {
	
	private static final String IS_ACTIVE = "active";

	public static Specification<BusinessPartnerEntity> getSelect(final Map<String, Object> keyword) {
		return new Specification<BusinessPartnerEntity>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -637621292944403277L;

			@Override
			public Predicate toPredicate(Root<BusinessPartnerEntity> root, CriteriaQuery<?> criteria, CriteriaBuilder builder) {
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
									predicate.getExpressions().add(builder.like(builder.upper(root.<String>get("bpName")), String.format("%%%s%%", value.toString().toUpperCase())));
									break;
								case "id" :
									predicate.getExpressions().add(builder.equal(root.get("id"), value));
									break;
								case "corporateCode" :
									predicate.getExpressions().add(builder.equal(root.join("b2bs").join("corporate").<String>get("corporateCode"), value.toString()));
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

	public static Specification<BusinessPartnerEntity> getDatatable(final Map<String, Object> keyword) {
		return new Specification<BusinessPartnerEntity>() {

			/**
			 * 
			 */
			private static final long serialVersionUID = -637621292944403277L;

			@Override
			public Predicate toPredicate(Root<BusinessPartnerEntity> root, CriteriaQuery<?> criteria, CriteriaBuilder builder) {
				Predicate predicate = builder.conjunction();
				if (!keyword.isEmpty()) {
					for(Map.Entry<String, Object> filter : keyword.entrySet()) {
						String key = filter.getKey();
						Object value = filter.getValue();
						if (value != null) {
							switch (key) {
								case "bpName" :
									// builder.upper for PostgreSQL
									predicate.getExpressions().add(builder.like(builder.upper(root.<String>get("bpName")), String.format("%%%s%%", value.toString().toUpperCase())));
									break;
								case "corporateCode" :
									predicate.getExpressions().add(builder.equal(root.join("b2bs").join("corporate").<String>get("corporateCode"), value.toString()));
									break;
								case "_all" :
									predicate.getExpressions().add(builder.like(builder.upper(root.<String>get("bpName")), String.format("%%%s%%", value.toString().toUpperCase())));
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
