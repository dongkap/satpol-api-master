package com.dongkap.master.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dongkap.master.entity.BusinessPartnerEntity;

public interface BusinessPartnerRepo extends JpaRepository<BusinessPartnerEntity, String>, JpaSpecificationExecutor<BusinessPartnerEntity> {
	
}