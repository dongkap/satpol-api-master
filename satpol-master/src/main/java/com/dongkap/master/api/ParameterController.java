package com.dongkap.master.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dongkap.common.exceptions.BaseControllerException;
import com.dongkap.common.utils.ResourceCode;
import com.dongkap.dto.common.CommonResponseDto;
import com.dongkap.dto.common.FilterDto;
import com.dongkap.dto.master.ParameterDto;
import com.dongkap.master.service.ParameterImplService;

@RestController
@RequestMapping(ResourceCode.MASTER_PATH)
public class ParameterController extends BaseControllerException {

	@Autowired
	private ParameterImplService parameterService;

	@RequestMapping(value = "/vw/auth/datatable/parameter/v.1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommonResponseDto<ParameterDto>> getDatatableParameter(Authentication authentication,
			@RequestBody(required = true) FilterDto filter) throws Exception {
		return new ResponseEntity<CommonResponseDto<ParameterDto>>(this.parameterService.getDatatableParameter(filter), HttpStatus.OK);
	}
	
}
