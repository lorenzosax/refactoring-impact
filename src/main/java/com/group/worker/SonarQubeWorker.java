package com.group.worker;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.group.pojo.sonar.Analysis;

public class SonarQubeWorker {

	private static final String USER_AGENT = "Mozilla/5.0";

	private static final String uriApi = "http://localhost:9000/api/measures/component_tree?component=moneytransfer45864560&metricKeys=sqale_index&qualifiers=FIL&ps=500&pageIndex=";

	private static void sendGET() throws IOException {
		URL obj = new URL(uriApi);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.setRequestProperty("User-Agent", USER_AGENT);
		int responseCode = con.getResponseCode();
		System.out.println("GET Response Code :: " + responseCode);

		if (responseCode == HttpURLConnection.HTTP_OK) { // success
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			Analysis an = new Gson().fromJson(response.toString(), Analysis.class);
			
			System.out.println(an.toString());
		} else {
			System.out.println("GET request not worked");
		}

	}

}