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
import com.dongkap.dto.master.BusinessPartnerDto;
import com.dongkap.dto.select.SelectDto;
import com.dongkap.dto.select.SelectResponseDto;
import com.dongkap.master.common.CommonService;
import com.dongkap.master.dao.BusinessPartnerRepo;
import com.dongkap.master.dao.specification.BusinessPartnerSpecification;
import com.dongkap.master.entity.BusinessPartnerEntity;

@Service("businessPartnerService")
public class BusinessPartnerImplService extends CommonService {

	protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BusinessPartnerRepo businessPartnerRepo;

	@Transactional
	public SelectResponseDto getSelect(Map<String, Object> additionalInfo, FilterDto filter) throws Exception {
		if(additionalInfo.get("corporate_code") == null) {
			throw new SystemErrorException(ErrorCode.ERR_SYS0001);
		}
		filter.getKeyword().put("corporateCode", additionalInfo.get("corporate_code"));
		Page<BusinessPartnerEntity> businessPartner = businessPartnerRepo.findAll(BusinessPartnerSpecification.getSelect(filter.getKeyword()), page(filter.getOrder(), filter.getOffset(), filter.getLimit()));
		final SelectResponseDto response = new SelectResponseDto();
		response.setTotalFiltered(Long.valueOf(businessPartner.getContent().size()));
		response.setTotalRecord(businessPartnerRepo.count(BusinessPartnerSpecification.getSelect(filter.getKeyword())));
		businessPartner.getContent().forEach(value -> {
			response.getData().add(new SelectDto(value.getBpName(), value.getId(), !value.isActive(), null));
		});
		return response;
	}

	@Transactional
	public CommonResponseDto<BusinessPartnerDto> getDatatable(Map<String, Object> additionalInfo, FilterDto filter) throws Exception {
		Page<BusinessPartnerEntity> businessPartner = businessPartnerRepo.findAll(BusinessPartnerSpecification.getDatatable(filter.getKeyword()), page(filter.getOrder(), filter.getOffset(), filter.getLimit()));
		final CommonResponseDto<BusinessPartnerDto> response = new CommonResponseDto<BusinessPartnerDto>();
		response.setTotalFiltered(Long.valueOf(businessPartner.getContent().size()));
		response.setTotalRecord(businessPartnerRepo.count(BusinessPartnerSpecification.getDatatable(filter.getKeyword())));
		businessPartner.getContent().forEach(value -> {
			BusinessPartnerDto temp = new BusinessPartnerDto();
			temp.setBpName(value.getBpName());
			temp.setEmail(value.getEmail());
			temp.setAddress(value.getAddress());
			temp.setTelpNumber(value.getTelpNumber());
			temp.setFaxNumber(value.getFaxNumber());
			temp.setActive(value.isActive());
			temp.setVersion(value.getVersion());
			temp.setCreatedDate(value.getCreatedDate());
			temp.setCreatedBy(value.getCreatedBy());
			temp.setModifiedDate(value.getModifiedDate());
			temp.setModifiedBy(value.getModifiedBy());
			response.getData().add(temp);
		});
		return response;
	}
	
	@Transactional
	@PublishStream(key = StreamKeyStatic.BUSINESS_PARTNER, status = ParameterStatic.UPDATE_DATA)
	public List<BusinessPartnerDto> postBusinessPartner(Map<String, Object> additionalInfo, BusinessPartnerDto request) throws Exception {
		BusinessPartnerEntity businessPartner = this.businessPartnerRepo.findById(request.getId()).orElse(null);
		List<BusinessPartnerDto> result = null;
		if (businessPartner == null) {
			businessPartner = new BusinessPartnerEntity();
		} else {
			request.setId(businessPartner.getId());
			result = new ArrayList<BusinessPartnerDto>();
			result.add(request);
		}
		businessPartner.setBpName(request.getBpName());
		businessPartner.setEmail(request.getEmail());
		businessPartner.setAddress(request.getAddress());
		businessPartner.setTelpNumber(request.getTelpNumber());
		businessPartner.setFaxNumber(request.getFaxNumber());
		businessPartnerRepo.saveAndFlush(businessPartner);
		return result;
	}

	public void deleteBusinessPartners(List<String> ids) throws Exception {
		List<BusinessPartnerEntity> businessPartners = businessPartnerRepo.findByIdIn(ids);
		try {
			businessPartnerRepo.deleteInBatch(businessPartners);			
		} catch (DataIntegrityViolationException e) {
			throw new SystemErrorException(ErrorCode.ERR_SCR0009);
		} catch (ConstraintViolationException e) {
			throw new SystemErrorException(ErrorCode.ERR_SCR0009);
		}
	}

}
