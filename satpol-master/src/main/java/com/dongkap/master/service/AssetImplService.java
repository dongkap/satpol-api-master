package com.dongkap.master.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.dongkap.common.exceptions.SystemErrorException;
import com.dongkap.common.stream.PublishStream;
import com.dongkap.common.utils.ErrorCode;
import com.dongkap.common.utils.ParameterStatic;
import com.dongkap.common.utils.StreamKeyStatic;
import com.dongkap.dto.common.CommonResponseDto;
import com.dongkap.dto.common.FilterDto;
import com.dongkap.dto.master.AssetDto;
import com.dongkap.dto.master.BusinessPartnerDto;
import com.dongkap.dto.select.SelectDto;
import com.dongkap.dto.select.SelectResponseDto;
import com.dongkap.master.common.CommonService;
import com.dongkap.master.dao.AssetRepo;
import com.dongkap.master.dao.BusinessPartnerRepo;
import com.dongkap.master.dao.CorporateRepo;
import com.dongkap.master.dao.specification.AssetSpecification;
import com.dongkap.master.entity.AssetEntity;
import com.dongkap.master.entity.BusinessPartnerEntity;
import com.dongkap.master.entity.CorporateEntity;

@Service("assetService")
public class AssetImplService extends CommonService {

	protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AssetRepo assetRepo;

	@Autowired
	private CorporateRepo corporateRepo;

	@Autowired
	private BusinessPartnerRepo businessPartnerRepo;

	@Transactional
	public SelectResponseDto getSelect(Map<String, Object> additionalInfo, FilterDto filter) throws Exception {
		if(additionalInfo.get("corporate_code") == null) {
			throw new SystemErrorException(ErrorCode.ERR_SYS0001);
		}
		filter.getKeyword().put("corporateCode", additionalInfo.get("corporate_code"));
		Page<AssetEntity> asset = assetRepo.findAll(AssetSpecification.getSelect(filter.getKeyword()), page(filter.getOrder(), filter.getOffset(), filter.getLimit()));
		final SelectResponseDto response = new SelectResponseDto();
		response.setTotalFiltered(Long.valueOf(asset.getContent().size()));
		response.setTotalRecord(assetRepo.count(AssetSpecification.getSelect(filter.getKeyword())));
		asset.getContent().forEach(value -> {
			response.getData().add(new SelectDto(value.getAssetName(), value.getId(), !value.getActive(), null));
		});
		return response;
	}

	@Transactional
	public CommonResponseDto<AssetDto> getDatatable(Map<String, Object> additionalInfo, FilterDto filter) throws Exception {
		if(additionalInfo.get("corporate_code") == null) {
			throw new SystemErrorException(ErrorCode.ERR_SYS0001);
		}
		filter.getKeyword().put("corporateCode", additionalInfo.get("corporate_code"));
		Page<AssetEntity> asset = assetRepo.findAll(AssetSpecification.getDatatable(filter.getKeyword()), page(filter.getOrder(), filter.getOffset(), filter.getLimit()));
		final CommonResponseDto<AssetDto> response = new CommonResponseDto<AssetDto>();
		response.setTotalFiltered(Long.valueOf(asset.getContent().size()));
		response.setTotalRecord(assetRepo.count(AssetSpecification.getDatatable(filter.getKeyword())));
		asset.getContent().forEach(value -> {
			AssetDto temp = new AssetDto();
			temp.setId(value.getId());
			temp.setAssetName(value.getAssetName());
			temp.setAssetCondition(value.getAssetCondition());
			temp.setQuantity(value.getQuantity());
			temp.setDescription(value.getDescription());
			temp.setActive(value.getActive());
			temp.setVersion(value.getVersion());
			temp.setCreatedDate(value.getCreatedDate());
			temp.setCreatedBy(value.getCreatedBy());
			temp.setModifiedDate(value.getModifiedDate());
			temp.setModifiedBy(value.getModifiedBy());
			if(value.getBusinessPartner() != null) {
				BusinessPartnerDto businessPartnerTemp = new BusinessPartnerDto();
				businessPartnerTemp.setId(value.getBusinessPartner().getId());
				businessPartnerTemp.setBpName(value.getBusinessPartner().getBpName());
				temp.setBusinessPartner(businessPartnerTemp);
			}
			response.getData().add(temp);
		});
		return response;
	}
	
	@Transactional
	@PublishStream(key = StreamKeyStatic.ASSET, status = ParameterStatic.PERSIST_DATA)
	public List<AssetDto> postAsset(Map<String, Object> additionalInfo, AssetDto request) throws Exception {
		if(additionalInfo.get("corporate_code") == null) {
			throw new SystemErrorException(ErrorCode.ERR_SYS0001);
		}
		BusinessPartnerEntity businessPartner = null;
		if(request.getBusinessPartner() != null) {
			businessPartner = this.businessPartnerRepo.findById(request.getBusinessPartner().getId()).orElse(null);
		}
		AssetEntity asset = this.assetRepo.findByIdAndCorporate_CorporateCode(request.getId(), additionalInfo.get("corporate_code").toString());
		List<AssetDto> result = null;
		if (asset == null) {
			CorporateEntity corporate = corporateRepo.findByCorporateCode(additionalInfo.get("corporate_code").toString());
			if(corporate == null) {
				corporate = new CorporateEntity();
				corporate.setCorporateCode(additionalInfo.get("corporate_code").toString());
				corporate.setCorporateName(additionalInfo.get("corporate_name").toString());
			}

			asset = new AssetEntity();
			asset.setCorporate(corporate);
		} else {
			request.setId(asset.getId());
			result = new ArrayList<AssetDto>();
			result.add(request);
		}
		asset.setAssetName(request.getAssetName());
		asset.setAssetCondition(request.getAssetCondition());
		asset.setQuantity(request.getQuantity());
		asset.setDescription(request.getDescription());
		asset.setBusinessPartner(businessPartner);
		assetRepo.saveAndFlush(asset);
		return result;
	}

	@PublishStream(key = StreamKeyStatic.ASSET, status = ParameterStatic.DELETE_DATA)
	public void deleteAssets(List<String> ids) throws Exception {
		List<AssetEntity> assets = assetRepo.findByIdIn(ids);
		try {
			assetRepo.deleteInBatch(assets);			
		} catch (DataIntegrityViolationException e) {
			throw new SystemErrorException(ErrorCode.ERR_SCR0009);
		} catch (ConstraintViolationException e) {
			throw new SystemErrorException(ErrorCode.ERR_SCR0009);
		}
	}

}