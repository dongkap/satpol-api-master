package com.dongkap.master.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.dongkap.common.aspect.ResponseSuccess;
import com.dongkap.common.exceptions.BaseControllerException;
import com.dongkap.common.utils.ResourceCode;
import com.dongkap.common.utils.SuccessCode;
import com.dongkap.dto.common.ApiBaseResponse;
import com.dongkap.dto.common.CommonResponseDto;
import com.dongkap.dto.common.FilterDto;
import com.dongkap.dto.master.ParameterI18nDto;
import com.dongkap.dto.master.ParameterRequestDto;
import com.dongkap.dto.radio.RadioDto;
import com.dongkap.dto.select.SelectResponseDto;
import com.dongkap.master.service.ParameterI18nImplService;

@RestController
@RequestMapping(ResourceCode.MASTER_PATH)
public class ParameterI18nController extends BaseControllerException {

	@Autowired
	private ParameterI18nImplService parameterI18nService;

	@RequestMapping(value = "/vw/auth/datatable/parameter-i18n/v.1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommonResponseDto<ParameterI18nDto>> getDatatableParameter(Authentication authentication,
			@RequestBody(required = true) FilterDto filter) throws Exception {
		return new ResponseEntity<CommonResponseDto<ParameterI18nDto>>(this.parameterI18nService.getDatatableParameterI18n(filter), HttpStatus.OK);
	}

	@RequestMapping(value = "/vw/post/select/parameter-i18n/v.1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SelectResponseDto> getSelectParameter(Authentication authentication,
			@RequestBody(required = true) FilterDto filter,
			@RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, required = false) String locale) throws Exception {
		return new ResponseEntity<SelectResponseDto>(this.parameterI18nService.getSelect(filter, locale), HttpStatus.OK);
	}

	@RequestMapping(value = "/vw/post/radio/parameter-i18n/v.1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<RadioDto>> getRadioParameter(Authentication authentication,
			@RequestBody(required = true) FilterDto filter,
			@RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, required = false) String locale) throws Exception {
		return new ResponseEntity<List<RadioDto>>(this.parameterI18nService.getRadio(filter, locale), HttpStatus.OK);
	}

	@RequestMapping(value = "/vw/auth/all/parameter-i18n/v.1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<ParameterI18nDto>> getParameterCode(Authentication authentication,
			@RequestBody(required = true) Map<String, Object> filter) throws Exception {
		return new ResponseEntity<List<ParameterI18nDto>>(this.parameterI18nService.getParameterCode(filter), HttpStatus.OK);
	}

	@RequestMapping(value = "/vw/post/parameter-i18n/v.1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ParameterI18nDto> getParameterCode(Authentication authentication,
			@RequestBody(required = true) Map<String, Object> param,
			@RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, required = false) String locale) throws Exception {
		return new ResponseEntity<ParameterI18nDto>(this.parameterI18nService.getParameter(param, locale), HttpStatus.OK);
	}
	
	@ResponseSuccess(SuccessCode.OK_SCR009)
	@RequestMapping(value = "/trx/auth/parameter-i18n/v.1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiBaseResponse> postParameterCode(Authentication authentication,
			@RequestBody(required = true) ParameterRequestDto data) throws Exception {
		String username = authentication.getName();
		this.parameterI18nService.postParameterI18n(data, username);
		return new ResponseEntity<ApiBaseResponse>(new ApiBaseResponse(), HttpStatus.OK);
	}
	
}
