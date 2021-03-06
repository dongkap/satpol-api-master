package com.dongkap.master.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dongkap.master.entity.LanguageEntity;

public interface LanguageRepo extends JpaRepository<LanguageEntity, String>, JpaSpecificationExecutor<LanguageEntity> {
	
}