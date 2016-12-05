package com.hji.okapi;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class OkapiApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(OkapiApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(OkapiApplication.class, args);
	}

	private static final String OKAPI_URL = "http://localhost:9130";
	// private static final String OKAPI_URL =
	// "http://hji-dev-1a.static-ebscohost.com:9130";

	@Override
	public void run(String... args) throws Exception {
		addTenant();
		declareModule();
		registerModule();
		enableModuleForClient();
		verifyModuleForClient();
	}

	private void addTenant() throws Exception {
		StringBuilder sb = new StringBuilder();
		Files.lines(Paths.get(ClassLoader.getSystemResource("TenantDescriptor.json").toURI()), StandardCharsets.UTF_8)
				.forEach(s -> sb.append(s));

		logger.info("TenantDescriptor: " + sb.toString());

		RestTemplate rs = new RestTemplate();
		String url = rs.postForLocation(OKAPI_URL + "/_/proxy/tenants", sb.toString()).toString();
		logger.info(url);

	}

	private void declareModule() throws Exception {
		StringBuilder sb = new StringBuilder();
		Files.lines(Paths.get(ClassLoader.getSystemResource("ModuleDescriptor.json").toURI()), StandardCharsets.UTF_8)
				.forEach(s -> sb.append(s));
		logger.info("ModuleDescriptor: " + sb.toString());

		RestTemplate rs = new RestTemplate();
		String url = rs.postForLocation(OKAPI_URL + "/_/proxy/modules", sb.toString()).toString();
		logger.info(url);

	}

	private void registerModule() throws Exception {
		StringBuilder sb = new StringBuilder();
		Files.lines(Paths.get(ClassLoader.getSystemResource("DeploymentDescriptor.json").toURI()),
				StandardCharsets.UTF_8).forEach(s -> sb.append(s));
		logger.info("DeploymentDescriptor: " + sb.toString());

		RestTemplate rs = new RestTemplate();
		String url = rs.postForLocation(OKAPI_URL + "/_/discovery/modules", sb.toString()).toString();
		logger.info(url);

	}

	private void enableModuleForClient() throws Exception {
		StringBuilder sb = new StringBuilder();
		Files.lines(Paths.get(ClassLoader.getSystemResource("TenantModuleDescriptor.json").toURI()),
				StandardCharsets.UTF_8).forEach(s -> sb.append(s));
		logger.info("TenantModuleDescriptor: " + sb.toString());

		RestTemplate rs = new RestTemplate();
		String resp = rs.postForObject(OKAPI_URL + "/_/proxy/tenants/mylib/modules", sb.toString(), String.class);
		logger.info(resp);

	}

	private void verifyModuleForClient() throws Exception {
		HttpHeaders headers = new HttpHeaders();
		headers.set("Accept", MediaType.APPLICATION_JSON_VALUE);
		headers.add("X-Okapi-Tenant", "mylib");

		HttpEntity<?> entity = new HttpEntity<>(headers);

		RestTemplate rs = new RestTemplate();
		HttpEntity<String> resp = rs.exchange(OKAPI_URL + "/hello", HttpMethod.GET, entity,
				String.class);
		logger.info(resp.getBody());
	}

}
