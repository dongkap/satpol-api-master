package com.dongkap.master.service.listener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.ObjectRecord;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.stereotype.Service;
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
    @Transactional
	public void onMessage(ObjectRecord<String, CommonStreamMessageDto> message) {
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
	}
	
	public void persist(CorporateDto request) {
		CorporateEntity corporate = new CorporateEntity(); 
		corporate.setId(request.getId());
		corporate.setCorporateCode(request.getCorporateCode());
		corporate.setCorporateName(request.getCorporateName());
		try {
    		corporateRepo.saveAndFlush(corporate);
		} catch (Exception e) {
			LOGGER.warn("Stream Persist : {}", e.getMessage());
		}
	}

	public void delete(CorporateDto request) {
		CorporateEntity corporate = corporateRepo.findById(request.getId()).orElse(null);
		try {
			if(corporate != null) {
				corporate.setActive(false);
				corporateRepo.save(corporate);	
			}
		} catch (Exception e) {
			LOGGER.warn("Stream Delete : {}", e.getMessage());
		}
	}
}
