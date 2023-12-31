package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDateTime;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

	public static void main(String[] args) throws IOException {
		log.info("start");
		final var counter = new Counter();
		try (var socket = new ServerSocket(8080)) {

			while (true) {
				final var clientSocket = socket.accept();
				log.info("accept client");
				new Thread(new WorkerRunnable(clientSocket, counter)).start();
			}
		}


	}

	static class Counter {

		private Integer count = 0;


		public synchronized Integer increase() {
			return count++;
		}
	}

	static class WorkerRunnable implements Runnable {

		private final Socket clientSocket;
		private final Counter counter;

		public WorkerRunnable(Socket clientSocket, Counter counter) {
			this.clientSocket = clientSocket;
			this.counter = counter;
		}

		@Override
		public void run() {
			try {
				BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				OutputStream output = clientSocket.getOutputStream();
				final var time = LocalDateTime.now();
				String readLine;
				String req = "";
				while ((readLine = input.readLine()) != null) {
					if (req.equals("")) {
						req = readLine;
					}
					if (readLine.equals("")) { // If the end of the http request, stop
						break;
					}
				}

				if (req == null || req.equals("") || req.contains("favicon.ico")) {
					return;
				}
				final var increase = counter.increase();

				output.write(
						("HTTP/1.1 200 OK\n\nWorkerRunnable: " +
								increase + " - " + time + "").getBytes()
				);
				output.flush();
				output.close();
				input.close();
				log.info("Request processed: {}", increase);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}

		}
	}
}