/*
 * Copyright 2017 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.cloud.dataflow.server.local.security.support;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.integration.IntegrationAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.OAuth2AuthorizationServerConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cloud.dataflow.autoconfigure.local.LocalDataFlowServerAutoConfiguration;
import org.springframework.cloud.dataflow.configuration.metadata.ApplicationConfigurationMetadataResolverAutoConfiguration;
import org.springframework.cloud.deployer.spi.local.LocalDeployerAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.security.oauth2.provider.token.AccessTokenConverter;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.util.SocketUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Gunnar Hillert
 */
@RestController
@SpringBootApplication(
	excludeName={"org.springframework.cloud.dataflow.shell.autoconfigure.BaseShellAutoConfiguration"},
	exclude={
		DataSourceAutoConfiguration.class,
		DataSourceTransactionManagerAutoConfiguration.class,
		JmxAutoConfiguration.class,
		LocalDataFlowServerAutoConfiguration.class,
		ApplicationConfigurationMetadataResolverAutoConfiguration.class,
		LocalDeployerAutoConfiguration.class,
		IntegrationAutoConfiguration.class})
public class OAuth2TestServer {

	public static void main(String[] args) {
		new SpringApplicationBuilder(OAuth2TestServer.class)
				.properties("server.port:" + SocketUtils.findAvailableTcpPort()).build()
				.run("--debug --spring.config.location=classpath:/org/springframework/cloud/dataflow/server/local"
						+ "/security/support/oauth2TestServerConfig.yml");
	}

	@RequestMapping({ "/user", "/me" })
	public Map<String, String> user(Principal principal) {
		return Collections.singletonMap("name", principal.getName());
	}

	@Configuration
	@EnableAuthorizationServer
	protected static class MyOAuth2AuthorizationServerConfiguration extends OAuth2AuthorizationServerConfiguration {
		public MyOAuth2AuthorizationServerConfiguration(BaseClientDetails details,
				AuthenticationManager authenticationManager, ObjectProvider<TokenStore> tokenStore,
				ObjectProvider<AccessTokenConverter> tokenConverter, AuthorizationServerProperties properties) {
			super(details, authenticationManager, tokenStore, tokenConverter, properties);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
			super.configure(security);
			security.allowFormAuthenticationForClients();
		}
	}

	@Configuration
	@EnableResourceServer
	protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {
		@Override
		public void configure(HttpSecurity http) throws Exception {
			http.antMatcher("/me").authorizeRequests().anyRequest().authenticated();
		}
	}

}
