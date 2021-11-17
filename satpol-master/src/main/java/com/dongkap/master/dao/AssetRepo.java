package com.dongkap.master.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.dongkap.master.entity.AssetEntity;

public interface AssetRepo extends JpaRepository<AssetEntity, String>, JpaSpecificationExecutor<AssetEntity> {
	
}