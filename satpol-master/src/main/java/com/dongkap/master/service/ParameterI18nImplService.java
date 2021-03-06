package com.dongkap.master.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import com.dongkap.common.exceptions.SystemErrorException;
import com.dongkap.common.stream.PublishStream;
import com.dongkap.common.utils.ErrorCode;
import com.dongkap.common.utils.ParameterStatic;
import com.dongkap.common.utils.StreamKeyStatic;
import com.dongkap.dto.checkbox.CheckboxDto;
import com.dongkap.dto.common.CommonResponseDto;
import com.dongkap.dto.common.FilterDto;
import com.dongkap.dto.master.ParameterI18nDto;
import com.dongkap.dto.master.ParameterRequestDto;
import com.dongkap.dto.radio.RadioDto;
import com.dongkap.dto.select.SelectDto;
import com.dongkap.dto.select.SelectResponseDto;
import com.dongkap.master.common.CommonService;
import com.dongkap.master.dao.ParameterGroupRepo;
import com.dongkap.master.dao.ParameterI18nRepo;
import com.dongkap.master.dao.ParameterRepo;
import com.dongkap.master.dao.specification.ParameterI18nSpecification;
import com.dongkap.master.entity.ParameterEntity;
import com.dongkap.master.entity.ParameterGroupEntity;
import com.dongkap.master.entity.ParameterI18nEntity;

@Service("parameterI18nService")
public class ParameterI18nImplService extends CommonService {

	protected Logger LOGGER = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ParameterGroupRepo parameterGroupRepo;

	@Autowired
	private ParameterI18nRepo parameterI18nRepo;
	
	@Autowired
	private ParameterRepo parameterRepo;

	@Value("${dongkap.locale}")
	private String locale;

	public CommonResponseDto<ParameterI18nDto> getDatatableParameterI18n(FilterDto filter) throws Exception {
		Page<ParameterI18nEntity> param = parameterI18nRepo.findAll(ParameterI18nSpecification.getDatatable(filter.getKeyword()), page(filter.getOrder(), filter.getOffset(), filter.getLimit()));
		final CommonResponseDto<ParameterI18nDto> response = new CommonResponseDto<ParameterI18nDto>();
		response.setTotalFiltered(Long.valueOf(param.getContent().size()));
		response.setTotalRecord(parameterI18nRepo.count(ParameterI18nSpecification.getDatatable(filter.getKeyword())));
		param.getContent().forEach(value -> {
			ParameterI18nDto temp = new ParameterI18nDto();
			temp.setParameterValue(value.getParameterValue());
			temp.setLocale(value.getLocaleCode());
			temp.setParameterCode(value.getParameter().getParameterCode());
			temp.setParameterGroupCode(value.getParameter().getParameterGroup().getParameterGroupCode());
			temp.setParameterGroupName(value.getParameter().getParameterGroup().getParameterGroupName());
			temp.setActive(value.getActive());
			temp.setVersion(value.getVersion());
			temp.setCreatedDate(value.getCreatedDate());
			temp.setCreatedBy(value.getCreatedBy());
			temp.setModifiedDate(value.getModifiedDate());
			temp.setModifiedBy(value.getModifiedBy());
			response.getData().add(temp);
		});
		return response;
	}

	public List<ParameterI18nDto> getParameterCode(Map<String, Object> filter) throws Exception {
		List<ParameterI18nEntity> param = parameterI18nRepo.findByParameter_ParameterCode(filter.get("parameterCode").toString());
		final List<ParameterI18nDto> response = new ArrayList<ParameterI18nDto>();
		param.forEach(value -> {
			ParameterI18nDto temp = new ParameterI18nDto();
			temp.setParameterCode(value.getParameter().getParameterCode());
			temp.setParameterGroupCode(value.getParameter().getParameterGroup().getParameterGroupCode());
			temp.setParameterGroupName(value.getParameter().getParameterGroup().getParameterGroupName());
			temp.setParameterValue(value.getParameterValue());
			temp.setLocale(value.getLocaleCode());
			temp.setActive(value.getActive());
			temp.setVersion(value.getVersion());
			temp.setCreatedDate(value.getCreatedDate());
			temp.setCreatedBy(value.getCreatedBy());
			temp.setModifiedDate(value.getModifiedDate());
			temp.setModifiedBy(value.getModifiedBy());
			response.add(temp);
		});
		return response;
	}
	
	@Transactional
	@PublishStream(key = StreamKeyStatic.PARAMETER, status = ParameterStatic.PERSIST_DATA)
	public List<ParameterI18nDto> postParameterI18n(ParameterRequestDto request, String username) throws Exception {
		if (request.getParameterValues() != null && request.getParameterCode() != null && request.getParameterGroupCode() != null) {
			ParameterGroupEntity paramGroup = parameterGroupRepo.findByParameterGroupCode(request.getParameterGroupCode());
			if (paramGroup != null) {
				List<ParameterI18nDto> publishDto = new ArrayList<ParameterI18nDto>();
				List<ParameterI18nEntity> listI18n = new ArrayList<ParameterI18nEntity>();
				ParameterEntity param = parameterRepo.findByParameterCode(request.getParameterCode());
				if (param == null) {
					param = new ParameterEntity();
					param.setParameterGroup(paramGroup);
					param.setParameterCode(request.getParameterCode());
					for(String localeCode: request.getParameterValues().keySet()) {
						ParameterI18nEntity paramI18n = new ParameterI18nEntity();
						paramI18n.setLocaleCode(localeCode);
						paramI18n.setParameterValue(request.getParameterValues().get(localeCode));
						param.getParameterI18n().add(paramI18n);
						paramI18n.setParameter(param);
					}
					/**
					 * one flush for multiple language
					 */
					param = parameterRepo.saveAndFlush(param);
					listI18n = param.getParameterI18n().stream().collect(Collectors.toList());
				} else {
					listI18n = param.getParameterI18n().stream().collect(Collectors.toList());
					for(ParameterI18nEntity paramI18n : listI18n) {
						if(!paramI18n.getParameterValue().equals(request.getParameterValues().get(paramI18n.getLocaleCode()))) {
							paramI18n.getParameter().setModifiedBy(username);
							paramI18n.getParameter().setModifiedDate(new Date());	
						}
						paramI18n.setParameterValue(request.getParameterValues().get(paramI18n.getLocaleCode()));
						paramI18n = parameterI18nRepo.saveAndFlush(paramI18n);
					}
				}
				for(ParameterI18nEntity paramI18n : listI18n) {
					ParameterI18nDto param18nDto = new ParameterI18nDto();
					param18nDto.setParameterId(paramI18n.getParameter().getId());
					param18nDto.setParameterCode(paramI18n.getParameter().getParameterCode());
					param18nDto.setParameterGroupCode(paramI18n.getParameter().getParameterGroup().getParameterGroupCode());
					param18nDto.setParameterGroupName(paramI18n.getParameter().getParameterGroup().getParameterGroupName());
					param18nDto.setParameterI18nId(paramI18n.getId());
					param18nDto.setParameterValue(paramI18n.getParameterValue());
					param18nDto.setLocale(paramI18n.getLocaleCode());
					publishDto.add(param18nDto);
				}
				return publishDto;
			} else {
				throw new SystemErrorException(ErrorCode.ERR_SYS0404);
			}
		} else {
			throw new SystemErrorException(ErrorCode.ERR_SYS0404);
		}
	}
	
	public ParameterI18nDto getParameter(Map<String, Object> param, String locale) throws Exception {
		if(locale == null) {
    		locale = this.locale;
		} else {
			final Locale i18n = Locale.forLanguageTag(locale);
	    	if(i18n.getDisplayLanguage().isEmpty()) {
	    		locale = this.locale;
	    	}	
		}
		ParameterI18nEntity parameterI18n = parameterI18nRepo.findByParameter_ParameterCodeAndLocaleCode(param.get("parameterCode").toString(), locale);
		if(parameterI18n != null) {
			ParameterI18nDto temp = new ParameterI18nDto();
			temp.setParameterCode(parameterI18n.getParameter().getParameterCode());
			temp.setParameterGroupCode(parameterI18n.getParameter().getParameterGroup().getParameterGroupCode());
			temp.setParameterGroupName(parameterI18n.getParameter().getParameterGroup().getParameterGroupName());
			temp.setParameterValue(parameterI18n.getParameterValue());
			temp.setLocale(parameterI18n.getLocaleCode());
			temp.setActive(parameterI18n.getActive());
			temp.setVersion(parameterI18n.getVersion());
			temp.setCreatedDate(parameterI18n.getCreatedDate());
			temp.setCreatedBy(parameterI18n.getCreatedBy());
			temp.setModifiedDate(parameterI18n.getModifiedDate());
			temp.setModifiedBy(parameterI18n.getModifiedBy());
			return temp;
		} else {
			return null;
		}
	}

	public SelectResponseDto getSelect(FilterDto filter, String locale) throws Exception {
		if(locale == null) {
    		locale = this.locale;
		} else {
	    	final Locale i18n = Locale.forLanguageTag(locale);
	    	if(i18n.getDisplayLanguage().isEmpty()) {
	    		locale = this.locale;
	    	}	
		}
    	filter.getKeyword().put("localeCode", locale);
		Page<ParameterI18nEntity> parameter = parameterI18nRepo.findAll(ParameterI18nSpecification.getSelect(filter.getKeyword()), page(filter.getOrder(), filter.getOffset(), filter.getLimit()));
		final SelectResponseDto response = new SelectResponseDto();
		response.setTotalFiltered(Long.valueOf(parameter.getContent().size()));
		response.setTotalRecord(parameterI18nRepo.count(ParameterI18nSpecification.getSelect(filter.getKeyword())));
		parameter.getContent().forEach(value -> {
			response.getData().add(new SelectDto(value.getParameterValue(), value.getParameter().getParameterCode(), !value.getParameter().getActive(), null));
		});
		return response;
	}

	public List<RadioDto> getRadio(FilterDto filter, String locale) throws Exception {
		if(locale == null) {
    		locale = this.locale;
		} else {
	    	final Locale i18n = Locale.forLanguageTag(locale);
	    	if(i18n.getDisplayLanguage().isEmpty()) {
	    		locale = this.locale;
	    	}	
		}
    	filter.getKeyword().put("localeCode", locale);
		Page<ParameterI18nEntity> parameter = parameterI18nRepo.findAll(ParameterI18nSpecification.getSelect(filter.getKeyword()), page(filter.getOrder(), filter.getOffset(), filter.getLimit()));
		final List<RadioDto> response = new ArrayList<RadioDto>();
		parameter.getContent().forEach(value -> {
			response.add(new RadioDto(value.getParameterValue(), value.getParameter().getParameterCode(), !value.getParameter().getActive(), null));
		});
		return response;
	}

	public List<CheckboxDto> getCheckbox(FilterDto filter, String locale) throws Exception {
		if(locale == null) {
    		locale = this.locale;
		} else {
	    	final Locale i18n = Locale.forLanguageTag(locale);
	    	if(i18n.getDisplayLanguage().isEmpty()) {
	    		locale = this.locale;
	    	}	
		}
    	filter.getKeyword().put("localeCode", locale);
		Page<ParameterI18nEntity> parameter = parameterI18nRepo.findAll(ParameterI18nSpecification.getSelect(filter.getKeyword()), page(filter.getOrder(), filter.getOffset(), filter.getLimit()));
		final List<CheckboxDto> response = new ArrayList<CheckboxDto>();
		parameter.getContent().forEach(value -> {
			response.add(new CheckboxDto(value.getParameter().getParameterCode(), value.getParameterValue(), !value.getParameter().getActive(), null));
		});
		return response;
	}

}
