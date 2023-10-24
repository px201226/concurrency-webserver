package org.example;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

public class Main {

	public static void main(String[] args) throws InterruptedException {

		int port = Integer.parseInt(args[0]);
		int thread = Integer.parseInt(args[1]);
		String url = "http://localhost:" + port;

		ExecutorService es = Executors.newFixedThreadPool(thread);
		final var a = StringUtils.capitalize("a");
		RestTemplate restTemplate = new RestTemplate();

		for (int i = 0; i < thread; i++) {

			final int finalI = i;
			es.execute(() -> {
				try {
					final var forObject = restTemplate.getForObject(url, String.class);
					System.out.println(forObject);

				} catch (Exception e) {
					System.out.println(e.getMessage());

				}
			});
			Thread.sleep(500L);
		}

		es.shutdown();
		es.awaitTermination(100, TimeUnit.SECONDS);
	}
}