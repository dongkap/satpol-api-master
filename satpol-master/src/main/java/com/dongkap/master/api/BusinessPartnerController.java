package com.dongkap.master.api;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.oauth2.provider.token.TokenStore;
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
import com.dongkap.dto.master.B2BDto;
import com.dongkap.dto.select.SelectResponseDto;
import com.dongkap.master.service.BusinessPartnerImplService;

@RestController
@RequestMapping(ResourceCode.MASTER_PATH)
public class BusinessPartnerController extends BaseControllerException {
	
	@Autowired
	private BusinessPartnerImplService businessPartnerService;

	@Autowired
	private TokenStore tokenStore;

	@RequestMapping(value = "/vw/auth/datatable/business-partner/v.1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<CommonResponseDto<B2BDto>> getDatatableBusinessPartner(Authentication authentication,
			@RequestBody(required = true) FilterDto filter) throws Exception {
		Map<String, Object> additionalInfo = this.getAdditionalInformation(authentication);
		return new ResponseEntity<CommonResponseDto<B2BDto>>(this.businessPartnerService.getDatatable(additionalInfo, filter), HttpStatus.OK);
	}

	@RequestMapping(value = "/vw/auth/select/business-partner/v.1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<SelectResponseDto> getSelectBusinessPartner(Authentication authentication,
														 @RequestHeader(name = HttpHeaders.ACCEPT_LANGUAGE, required = false) String locale,
														 @RequestBody(required = true) FilterDto filter) throws Exception {
		Map<String, Object> additionalInfo = this.getAdditionalInformation(authentication);
		return new ResponseEntity<SelectResponseDto>(this.businessPartnerService.getSelect(additionalInfo, filter), HttpStatus.OK);
	}

	@ResponseSuccess(SuccessCode.OK_DEFAULT)
	@RequestMapping(value = "/trx/auth/business-partner/v.1", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ApiBaseResponse> postBusinessPartner(Authentication authentication,
			@RequestBody(required = true) B2BDto data) throws Exception {
		Map<String, Object> additionalInfo = this.getAdditionalInformation(authentication);
		this.businessPartnerService.postBusinessPartner(additionalInfo, data);
		return new ResponseEntity<ApiBaseResponse>(new ApiBaseResponse(), HttpStatus.OK);
	}

	public Map<String, Object> getAdditionalInformation(Authentication auth) {
	    OAuth2AuthenticationDetails auth2AuthenticationDetails = (OAuth2AuthenticationDetails) auth.getDetails();
	    return tokenStore.readAccessToken(auth2AuthenticationDetails.getTokenValue()).getAdditionalInformation();
	}
	
}
