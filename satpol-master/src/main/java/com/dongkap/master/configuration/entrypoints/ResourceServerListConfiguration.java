package com.dongkap.master.configuration.entrypoints;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfiguration;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.dongkap.common.utils.PrecedenceOrder;

@Configuration
public class ResourceServerListConfiguration {
	
    @Autowired
    private AccessDeniedHandler accessDeniedHandler;
	
    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

	@Bean
	protected ResourceServerConfiguration masterResources() {
		ResourceServerConfiguration resource = new ResourceServerConfiguration() {
			public void setConfigurers(List<ResourceServerConfigurer> configurers) {
				super.setConfigurers(configurers);
			}
		};
		resource.setConfigurers(Arrays.<ResourceServerConfigurer> asList(new ResourceServerMasterAdapter(accessDeniedHandler, authenticationEntryPoint)));
		resource.setOrder(PrecedenceOrder.MASTER);
		return resource;
	}
	
}
