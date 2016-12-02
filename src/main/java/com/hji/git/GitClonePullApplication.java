package com.hji.git;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootApplication
public class GitClonePullApplication implements CommandLineRunner {

	private static final Logger logger = LoggerFactory.getLogger(GitClonePullApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(GitClonePullApplication.class, args);
	}

	private static final String LOCAL_DIR = "C:/dev/work/github/folio/";

	@Override
	public void run(String... args) throws Exception {
		String url = "https://api.github.com/orgs/folio-org/repos?per_page=1000";
		RestTemplate rs = new RestTemplate();
		String s = rs.getForObject(url, String.class);

		ObjectMapper om = new ObjectMapper();
		@SuppressWarnings("unchecked")
		List<Map<String, String>> list = om.readValue(s, List.class);
		List<Process> plist = new ArrayList<>();
		for (Map<String, String> map : list) {
			String clone_url = map.get("clone_url");
			String dir = LOCAL_DIR + clone_url.substring(clone_url.lastIndexOf("/") + 1, clone_url.lastIndexOf(".git"));
			if (new File(dir).exists()) {
				logger.info("updating " + clone_url);
				String cmd = "git pull --recurse-submodules";
				plist.add(Runtime.getRuntime().exec(cmd, null, new File(dir)));
			} else {
				logger.info("cloning " + clone_url);
				String cmd = "git clone --recursive " + clone_url + " " + dir;
				plist.add(Runtime.getRuntime().exec(cmd));
			}
		}

		for (Process p : plist) {
			p.waitFor();
		}

		logger.info("git is done");
	}
}
