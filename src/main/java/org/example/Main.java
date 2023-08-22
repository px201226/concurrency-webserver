package org.example;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.springframework.util.StopWatch;
import org.springframework.web.client.RestTemplate;

public class Main {

	public static void main(String[] args) throws InterruptedException {

		ExecutorService es = Executors.newFixedThreadPool(10);

		RestTemplate rt = new RestTemplate();
		String url = "http://localhost:8080/hello";

		StopWatch main = new StopWatch();
		main.start();

		for (int i = 0; i < 1000; i++) {
			es.execute(() -> {
				StopWatch sw = new StopWatch();
				sw.start();
				rt.getForObject(url, String.class);
				sw.stop();
			});
		}

		es.shutdown();
		// 지정된 시간이 타임아웃 걸리기 전이라면 대기작업이 진행될 때까지 기다린다.
		// (100초안에 작업이 끝날때까지 기다리거나, 100초가 초과되면 종료)
		es.awaitTermination(100, TimeUnit.SECONDS);
		main.stop();
	}
}