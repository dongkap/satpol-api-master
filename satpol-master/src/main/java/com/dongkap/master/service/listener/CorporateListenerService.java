package com.dongkap.master.service.listener;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.dongkap.common.stream.CommonStreamListener;
import com.dongkap.common.utils.ParameterStatic;
import com.dongkap.common.utils.StreamKeyStatic;
import com.dongkap.dto.common.CommonStreamMessageDto;
import com.dongkap.dto.security.CorporateDto;
import com.dongkap.master.dao.CorporateRepo;
import com.dongkap.master.entity.CorporateEntity;

import lombok.SneakyThrows;

@Service
public class CorporateListenerService extends CommonStreamListener<CommonStreamMessageDto> {

	@Autowired
	private CorporateRepo corporateRepo;

    public CorporateListenerService(
    		@Value("${spring.application.name}") String appName,
    		@Value("${spring.application.name}") String groupId) {
		super(appName, groupId, StreamKeyStatic.CORPORATE, CommonStreamMessageDto.class);
	}
	
	@Override
    @SneakyThrows
	@Transactional(noRollbackFor = { ConstraintViolationException.class }, propagation = Propagation.REQUIRES_NEW)
	public void onMessage(ObjectRecord<String, CommonStreamMessageDto> message) {
		try {
	        String stream = message.getStream();
	        RecordId id = message.getId();
			LOGGER.info("A message was received stream: [{}], id: [{}]", stream, id);
	        CommonStreamMessageDto value = message.getValue();
	        if(value != null) {
	        	for(Object data: value.getDatas()) {
		        	if(data instanceof CorporateDto) {
		        		CorporateDto request = (CorporateDto) data;
		        		if(value.getStatus().equalsIgnoreCase(ParameterStatic.PERSIST_DATA)) {
		        			this.persist(request);
		        		}
		        		if(value.getStatus().equalsIgnoreCase(ParameterStatic.DELETE_DATA)) {
			        		this.delete(request);
		        		}
		        	}
		        }
	        }			
		} catch (Exception e) {
			LOGGER.warn("Stream On Message : {}", e.getMessage());
		}
	}
	
	public void persist(CorporateDto request) {
		try {
			CorporateEntity corporate = corporateRepo.findByCorporateCode(request.getCorporateCode());
			if(corporate != null) {
				if(!corporate.getId().equals(request.getId())) {
					corporateRepo.delete(corporate);
				}
			} else {
				corporate = new CorporateEntity();	
			}
			corporate.setId(request.getId());
			corporate.setCorporateCode(request.getCorporateCode());
			corporate.setCorporateName(request.getCorporateName());
    		corporateRepo.saveAndFlush(corporate);
		} catch (DataIntegrityViolationException e) {
			LOGGER.warn("Stream Persist : {}", e.getMessage());
		} catch (ConstraintViolationException e) {
			LOGGER.warn("Stream Persist : {}", e.getMessage());
		} catch (Exception e) {
			LOGGER.warn("Stream Persist : {}", e.getMessage());
		}
	}

	public void delete(CorporateDto request) {
		try {
			CorporateEntity corporate = corporateRepo.findByCorporateCode(request.getCorporateCode());
			if(corporate != null) {
				corporateRepo.delete(corporate);
			}
		} catch (DataIntegrityViolationException e) {
			LOGGER.warn("Stream Delete : {}", e.getMessage());
		} catch (ConstraintViolationException e) {
			LOGGER.warn("Stream Delete : {}", e.getMessage());
		} catch (Exception e) {
			LOGGER.warn("Stream Delete : {}", e.getMessage());
		}
	}
}
