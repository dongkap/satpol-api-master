package com.dongkap.master.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.dongkap.dto.master.B2BDto;
import com.dongkap.dto.master.BusinessPartnerDto;
import com.dongkap.dto.security.CorporateDto;
import com.dongkap.dto.select.SelectDto;
import com.dongkap.dto.select.SelectResponseDto;
import com.dongkap.master.common.CommonService;
import com.dongkap.master.dao.B2BRepo;
import com.dongkap.master.dao.BusinessPartnerRepo;
import com.dongkap.master.dao.CorporateRepo;
import com.dongkap.master.dao.specification.B2BSpecification;
import com.dongkap.master.entity.B2BEntity;
import com.dongkap.master.entity.BusinessPartnerEntity;
import com.dongkap.master.entity.CorporateEntity;

@Service("businessPartnerService")
public class BusinessPartnerImplService extends CommonService {

	protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private B2BRepo b2bRepo;

	@Autowired
	private BusinessPartnerRepo businessPartnerRepo;

	@Autowired
	private CorporateRepo corporateRepo;

	@Transactional
	public SelectResponseDto getSelect(Map<String, Object> additionalInfo, FilterDto filter) throws Exception {
		if(additionalInfo.get("corporate_code") == null) {
			throw new SystemErrorException(ErrorCode.ERR_SYS0001);
		}
		filter.getKeyword().put("corporateCode", additionalInfo.get("corporate_code"));
		Page<B2BEntity> b2b = b2bRepo.findAll(B2BSpecification.getSelect(filter.getKeyword()), page(filter.getOrder(), filter.getOffset(), filter.getLimit()));
		final SelectResponseDto response = new SelectResponseDto();
		response.setTotalFiltered(Long.valueOf(b2b.getContent().size()));
		response.setTotalRecord(b2bRepo.count(B2BSpecification.getSelect(filter.getKeyword())));
		b2b.getContent().forEach(value -> {
			response.getData().add(new SelectDto(value.getBusinessPartner().getBpName(), value.getBusinessPartner().getId(), !value.getActive(), null));
		});
		return response;
	}

	@Transactional
	public CommonResponseDto<B2BDto> getDatatable(Map<String, Object> additionalInfo, FilterDto filter) throws Exception {
		Page<B2BEntity> b2b = b2bRepo.findAll(B2BSpecification.getDatatable(filter.getKeyword()), page(filter.getOrder(), filter.getOffset(), filter.getLimit()));
		final CommonResponseDto<B2BDto> response = new CommonResponseDto<B2BDto>();
		response.setTotalFiltered(Long.valueOf(b2b.getContent().size()));
		response.setTotalRecord(b2bRepo.count(B2BSpecification.getDatatable(filter.getKeyword())));
		b2b.getContent().forEach(value -> {
			B2BDto b2bTemp = new B2BDto();
			b2bTemp.setId(value.getId());
			b2bTemp.setB2bNonExpired(value.getB2bNonExpired());
			b2bTemp.setExpiredTime(value.getExpiredTime());
			b2bTemp.setActive(value.getActive());
			b2bTemp.setVersion(value.getVersion());
			b2bTemp.setCreatedDate(value.getCreatedDate());
			b2bTemp.setCreatedBy(value.getCreatedBy());
			b2bTemp.setModifiedDate(value.getModifiedDate());
			b2bTemp.setModifiedBy(value.getModifiedBy());
			BusinessPartnerDto businessPartnerTemp = new BusinessPartnerDto();
			businessPartnerTemp.setId(value.getBusinessPartner().getId());
			businessPartnerTemp.setBpName(value.getBusinessPartner().getBpName());
			businessPartnerTemp.setEmail(value.getBusinessPartner().getEmail());
			businessPartnerTemp.setAddress(value.getBusinessPartner().getAddress());
			businessPartnerTemp.setTelpNumber(value.getBusinessPartner().getTelpNumber());
			businessPartnerTemp.setFaxNumber(value.getBusinessPartner().getFaxNumber());
			b2bTemp.setBusinessPartner(businessPartnerTemp);
			CorporateDto corporateTemp = new CorporateDto();
			corporateTemp.setId(value.getCorporate().getId());
			corporateTemp.setCorporateCode(value.getCorporate().getCorporateCode());
			corporateTemp.setCorporateName(value.getCorporate().getCorporateName());
			b2bTemp.setCorporate(corporateTemp);
			response.getData().add(b2bTemp);
		});
		return response;
	}

	@Transactional
	public BusinessPartnerDto getBusinessPartner(Map<String, Object> additionalInfo, Map<String, Object> data) throws Exception {
		if(data == null || data.isEmpty())
			throw new SystemErrorException(ErrorCode.ERR_SYS0404);
		String bpId = data.get("id").toString();		
		BusinessPartnerEntity bp = businessPartnerRepo.findById(bpId).orElse(null);
		BusinessPartnerDto response = new BusinessPartnerDto();
		response.setId(bp.getId());
		response.setBpName(bp.getBpName());
		response.setEmail(bp.getEmail());
		response.setAddress(bp.getAddress());
		response.setTelpNumber(bp.getTelpNumber());
		response.setFaxNumber(bp.getFaxNumber());
		response.setActive(bp.getActive());
		response.setVersion(bp.getVersion());
		response.setCreatedDate(bp.getCreatedDate());
		response.setCreatedBy(bp.getCreatedBy());
		response.setModifiedDate(bp.getModifiedDate());
		response.setModifiedBy(bp.getModifiedBy());
		return response;
	}
	
	@Transactional
	@PublishStream(key = StreamKeyStatic.BUSINESS_PARTNER, status = ParameterStatic.PERSIST_DATA)
	public List<B2BDto> postBusinessPartner(Map<String, Object> additionalInfo, B2BDto request) throws Exception {
		if(additionalInfo.get("corporate_code") == null) {
			throw new SystemErrorException(ErrorCode.ERR_SYS0001);
		}
		B2BEntity b2b = this.b2bRepo.findByIdAndCorporate_CorporateCode(request.getId(), additionalInfo.get("corporate_code").toString());
		BusinessPartnerEntity businessPartner = new BusinessPartnerEntity();
		if (b2b == null) {
			CorporateEntity corporate = corporateRepo.findByCorporateCode(additionalInfo.get("corporate_code").toString());
			if(corporate == null) {
				corporate = new CorporateEntity();
				corporate.setId(additionalInfo.get("corporate_uuid").toString());
				corporate.setCorporateCode(additionalInfo.get("corporate_code").toString());
				corporate.setCorporateName(additionalInfo.get("corporate_name").toString());
			}
			b2b = new B2BEntity();
			b2b.setCorporate(corporate);
		} else {
			businessPartner = b2b.getBusinessPartner();
		}
		b2b.setB2bNonExpired(request.getB2bNonExpired());
		b2b.setExpiredTime(request.getExpiredTime());
		businessPartner.setBpName(request.getBusinessPartner().getBpName());
		businessPartner.setEmail(request.getBusinessPartner().getEmail());
		businessPartner.setAddress(request.getBusinessPartner().getAddress());
		businessPartner.setTelpNumber(request.getBusinessPartner().getTelpNumber());
		businessPartner.setFaxNumber(request.getBusinessPartner().getFaxNumber());
		b2b.setBusinessPartner(businessPartner);
		b2b = b2bRepo.saveAndFlush(b2b);

		request.setId(b2b.getId());
		BusinessPartnerDto businessPartnerDto = new BusinessPartnerDto();
		businessPartnerDto.setId(b2b.getBusinessPartner().getId());
		businessPartnerDto.setBpName(b2b.getBusinessPartner().getBpName());
		request.setBusinessPartner(businessPartnerDto);
		CorporateDto corporateDto = new CorporateDto();
		corporateDto.setId(b2b.getCorporate().getId());
		corporateDto.setCorporateCode(b2b.getCorporate().getCorporateCode());
		corporateDto.setCorporateName(b2b.getCorporate().getCorporateName());
		request.setCorporate(corporateDto);
		List<B2BDto> publishDto = new ArrayList<B2BDto>();
		publishDto.add(request);
		return publishDto;
	}

}
