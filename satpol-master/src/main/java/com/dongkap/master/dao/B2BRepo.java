package com.dongkap.master.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dongkap.master.entity.B2BEntity;

public interface B2BRepo extends JpaRepository<B2BEntity, String>, JpaSpecificationExecutor<B2BEntity> {

	B2BEntity findByBusinessPartner_IdAndCorporate_CorporateCode(String businessPartnerId, String corporateCode);
	
	B2BEntity findByIdAndCorporate_CorporateCode(String b2bId, String corporateCode);
	
}