package com.dongkap.master.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.stereotype.Service;

import com.dongkap.common.exceptions.SystemErrorException;
import com.dongkap.common.utils.ErrorCode;
import com.dongkap.common.utils.ParameterStatic;
import com.dongkap.common.utils.StreamKeyStatic;
import com.dongkap.dto.common.CommonStreamMessageDto;
import com.dongkap.dto.common.FilterDto;
import com.dongkap.dto.master.ParameterI18nDto;
import com.dongkap.dto.master.ParameterRequestDto;
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
	
	@Autowired
	private ReactiveRedisOperations<String, CommonStreamMessageDto> reactiveRedisTemplate;

	@Value("${dongkap.locale}")
	private String locale;

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
			temp.setActive(value.isActive());
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
	public void postParameterI18n(ParameterRequestDto request, String username) throws Exception {
		if (request.getParameterValues() != null && request.getParameterCode() != null && request.getParameterGroupCode() != null) {
			ParameterGroupEntity paramGroup = parameterGroupRepo.findByParameterGroupCode(request.getParameterGroupCode());
			if (paramGroup != null) {
				ParameterEntity param = parameterRepo.findByParameterCode(request.getParameterCode());
				if (param == null) {
					param = new ParameterEntity();
					param.setParameterGroup(paramGroup);
					param.setParameterCode(request.getParameterCode());
					param.setCreatedBy(username);
					param.setCreatedDate(new Date());
					Set<ParameterI18nEntity> parameterI18ns = new HashSet<ParameterI18nEntity>();
					for(String localeCode: request.getParameterValues().keySet()) {
							ParameterI18nEntity paramI18n = new ParameterI18nEntity();
							paramI18n.setLocaleCode(localeCode);
							paramI18n.setParameterValue(request.getParameterValues().get(localeCode));
							paramI18n.setParameter(param);
							paramI18n.setCreatedBy(username);
							paramI18n.setCreatedDate(new Date());
							parameterI18ns.add(paramI18n);
					}
					param.setParameterI18n(parameterI18ns);
					param = parameterRepo.saveAndFlush(param);
				} else {
					CommonStreamMessageDto message = new CommonStreamMessageDto();
					message.setTopic(StreamKeyStatic.PARAMETER);
					message.setStatus(ParameterStatic.UPDATE_DATA);
					for(String localeCode: request.getParameterValues().keySet()) {
						ParameterI18nEntity paramI18n = parameterI18nRepo.findByParameter_ParameterCodeAndLocaleCode(request.getParameterCode(), localeCode);
						if (param == null) {
							paramI18n = new ParameterI18nEntity();
							paramI18n.setCreatedBy(username);
							paramI18n.setCreatedDate(new Date());
						} else {
							paramI18n.setModifiedBy(username);
							paramI18n.setModifiedDate(new Date());
						}
						paramI18n.setLocaleCode(localeCode);
						paramI18n.setParameterValue(request.getParameterValues().get(localeCode));
						paramI18n.setParameter(param);
						parameterI18nRepo.saveAndFlush(paramI18n);

						ParameterI18nDto param18n = new ParameterI18nDto(); 
						param18n.setParameterCode(param.getParameterCode());
						param18n.setParameterGroupCode(param.getParameterGroup().getParameterGroupCode());
						param18n.setParameterGroupName(param.getParameterGroup().getParameterGroupName());
						param18n.setParameterValue(paramI18n.getParameterValue());
						param18n.setLocale(paramI18n.getLocaleCode());
						message.getDatas().add(param18n);
					}
					ObjectRecord<String, CommonStreamMessageDto> record = StreamRecords.newRecord()
							.in(StreamKeyStatic.PARAMETER)
	                        .ofObject(message);
			        this.reactiveRedisTemplate
			                .opsForStream()
			                .add(record)
			                .subscribe();
				}
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
			temp.setActive(parameterI18n.isActive());
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
			response.getData().add(new SelectDto(value.getParameterValue(), value.getParameter().getParameterCode(), !value.getParameter().isActive(), null));
		});
		return response;
	}

}
