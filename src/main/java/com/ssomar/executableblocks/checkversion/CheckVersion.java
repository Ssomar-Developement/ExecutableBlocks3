package com.ssomar.executableblocks.checkversion;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;

public class CheckVersion {

	@SuppressWarnings("unchecked")
	public static String getVersion() {

		// JSON example
		String json;
		try {
			json = Jsoup.connect("https://api.spiget.org/v2/resources/93406/versions/latest").ignoreContentType(true).execute().body();

			JSONParser parser = new JSONParser();

			Object obj  = parser.parse(json);
			JSONArray array = new JSONArray();
			array.add(obj);

			for (Object o : array)
			{
				JSONObject version = (JSONObject) o;

				String versionStr = (String) version.get("name");
				if(versionStr.contains("[")){
					versionStr = versionStr.split("\\[")[0];
				}
				versionStr = versionStr.trim();

				return versionStr;
			}
		} catch (Exception e) {
			//e.printStackTrace();
		}
		return null;
	}

}
