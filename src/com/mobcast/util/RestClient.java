package com.mobcast.util;

/* HISTORY
 * CATEGORY 		:- ACTIVITY
 * DEVELOPER		:- VIKALP PATEL
 * AIM			    :- ADD IPD ACTIVITY
 * DESCRIPTION 		:- SAVE IPD
 * 
 * S - START E- END  C- COMMENTED  U -EDITED A -ADDED
 * --------------------------------------------------------------------------------------------------------------------
 * INDEX       DEVELOPER		DATE			FUNCTION		DESCRIPTION
 * --------------------------------------------------------------------------------------------------------------------
 * S0001       VIKALP PATEL    27/02/14         SECURITY        ADDED HASH IN JSON FILE UPLOAD
 * --------------------------------------------------------------------------------------------------------------------
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.FormBodyPart;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Base64;
import android.util.Log;

import com.sanofi.in.mobcast.ApplicationLoader;

public class RestClient {

	private static final String TAG = RestClient.class.getSimpleName();
	private static final int TIME_OUT = 30000;
			
	public static String postJSON(String url, JSONObject dataToSend)
			throws JSONException {
		// Create a new HttpClient and Post Header

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		String text = null;
		try {

			JSONArray postjson = new JSONArray();
			postjson.put(dataToSend);

			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Content-type", "application/json");
			httppost.setHeader(
					"Authorization",
					"Basic "
							+ Base64.encodeToString(
									"netdoersadmin:538f25fc32727".getBytes(),
									Base64.NO_WRAP));

			StringEntity se = new StringEntity(dataToSend.toString());
			httppost.setEntity(se);

			// Execute HTTP Post Request
			System.out.print(dataToSend);
			HttpResponse response = httpclient.execute(httppost);

			// for JSON:
			if (response != null) {
				InputStream is = response.getEntity().getContent();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();

				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				text = sb.toString();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch(Exception e){
			text = RequestBuilder.getPostFailMessage().toString();
		}
		if(BuildVars.debugRestClient){
			Log.i(TAG, dataToSend.toString());
			Log.i(TAG, url);	
			Log.i(TAG, ""+text);
		}
		if (text != null) {
			Log.e("----------> ", text);
		}

		return text;
	}

	public static String postData(String url, HashMap<String, String> postParam) {
		HashMap<String, String> mData = postParam;
		String str = "";
		byte[] result = null;

		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(url);// in this case, params[0]
											// is URL
		try {
			// set up post data
			ArrayList<NameValuePair> nameValuePair = new ArrayList<NameValuePair>();
			Iterator<String> it = mData.keySet().iterator();
			while (it.hasNext()) {
				String key = it.next();
				nameValuePair.add(new BasicNameValuePair(key, mData.get(key)));
			}

			post.setEntity(new UrlEncodedFormEntity(nameValuePair, "UTF-8"));
			HttpResponse response = client.execute(post);

			StringBuilder bodyBuilder = new StringBuilder();
			Iterator<Entry<String, String>> iterator = mData.entrySet()
					.iterator();
			// constructs the POST body using the parameters
			while (iterator.hasNext()) {
				Entry<String, String> param = iterator.next();
				bodyBuilder.append(param.getKey()).append('=')
						.append(param.getValue());
				if (iterator.hasNext()) {
					bodyBuilder.append('&');
				}
			}
			String body = bodyBuilder.toString()+"->"+url;
			Log.d(TAG, body);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
				result = EntityUtils.toByteArray(response.getEntity());
				str = new String(result, "iso-8859-1");
				Log.d("response", str);

			}

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
		}
		return str;
	}

	public static String postString(String url, String dataToSend)
			throws JSONException {
		// Create a new HttpClient and Post Header

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(url);

		String text = null;
		try {
			httppost.setHeader("Accept", "application/json");
			httppost.setHeader("Content-type", "application/json");
			httppost.setHeader(
					"Authorization",
					"Basic "
							+ Base64.encodeToString(
									"netdoersadmin:538f25fc32727".getBytes(),
									Base64.NO_WRAP));

			StringEntity se = new StringEntity(dataToSend.toString());
			httppost.setEntity(se);

			// Execute HTTP Post Request
			System.out.print(dataToSend);
			HttpResponse response = httpclient.execute(httppost);

			// for JSON:
			if (response != null) {
				InputStream is = response.getEntity().getContent();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();

				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				text = sb.toString();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (text != null) {
			Log.e("----------> ", text);
		}

		return text;
	}

	public static String putJSON(String url, JSONObject dataToSend)
			throws JSONException {
		// Create a new HttpClient and Post Header

		HttpClient httpclient = new DefaultHttpClient();
		HttpPut httpput = new HttpPut(url);

		String text = null;
		try {

			JSONArray postjson = new JSONArray();
			postjson.put(dataToSend);

			httpput.setHeader("Accept", "application/json");
			httpput.setHeader("Content-type", "application/json");
			httpput.setHeader(
					"Authorization",
					"Basic "
							+ Base64.encodeToString(
									"netdoersadmin:538f25fc32727".getBytes(),
									Base64.NO_WRAP));

			StringEntity se = new StringEntity(dataToSend.toString());
			httpput.setEntity(se);

			// Execute HTTP Post Request
			System.out.print(dataToSend);
			HttpResponse response = httpclient.execute(httpput);

			// for JSON:
			if (response != null) {
				InputStream is = response.getEntity().getContent();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();

				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				text = sb.toString();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (text != null) {
			Log.e("----------> ", text);
		}

		return text;
	}

	public static String getJSON(String url) throws JSONException {
		// Create a new HttpClient and Post Header

		HttpClient httpclient = new DefaultHttpClient();
		HttpGet httpget = new HttpGet(url);

		String text = null;
		try {

			httpget.setHeader("Accept", "application/json");
			httpget.setHeader("Content-type", "application/json");
			httpget.setHeader(
					"Authorization",
					"Basic "
							+ Base64.encodeToString(
									"netdoersadmin:538f25fc32727".getBytes(),
									Base64.NO_WRAP));

			// StringEntity se = new StringEntity(dataToSend.toString());
			// httpget.setEntity(se);

			// Execute HTTP Get Request
			HttpResponse response = httpclient.execute(httpget);

			// for JSON:
			if (response != null) {
				InputStream is = response.getEntity().getContent();

				BufferedReader reader = new BufferedReader(
						new InputStreamReader(is));
				StringBuilder sb = new StringBuilder();

				String line = null;
				try {
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						is.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				text = sb.toString();
			}

		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (text != null) {
			Log.e("----------> ", text);
		}

		return text;
	}

	public static String uploadFile(String type, File file, String url)
			throws UnsupportedEncodingException, ClientProtocolException,
			IOException {
		String fileName = file.getName();
		FileInputStream fileInputStream = new FileInputStream(file);
		int bytesAvailable = fileInputStream.available();

		byte[] buffer = new byte[bytesAvailable];
		// read file and write it into form...
		long bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(url);
		postRequest.setHeader(
				"Authorization",
				"Basic "
						+ Base64.encodeToString(
								"netdoersadmin:538f25fc32727".getBytes(),
								Base64.NO_WRAP));
		ByteArrayBody bab = new ByteArrayBody(buffer, fileName);
		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		reqEntity.addPart(type, bab);

		String myVal = ApplicationLoader.getPreferences().getApiKey();
		FormBodyPart bodyPart = new FormBodyPart("api_key", new StringBody(
				myVal));

		reqEntity.addPart(bodyPart);

		postRequest.setEntity(reqEntity);
		HttpResponse response = httpClient.execute(postRequest);
		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = null;
		StringBuffer stringResponse = new StringBuffer();
		while ((line = in.readLine()) != null) {
			stringResponse.append(line);
			Log.e("uploadFile", line);
		}
		response = null;
		in.close();
		in = null;
		reqEntity = null;
		postRequest = null;

		return stringResponse.toString();
	}

	public static String uploadMediaFile(String type, String post_id,
			File file, String url) throws UnsupportedEncodingException,
			ClientProtocolException, IOException {
		String fileName = file.getName();
		FileInputStream fileInputStream = new FileInputStream(file);
		int bytesAvailable = fileInputStream.available();

		byte[] buffer = new byte[bytesAvailable];
		// read file and write it into form...
		long bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(url);
		postRequest.setHeader(
				"Authorization",
				"Basic "
						+ Base64.encodeToString(
								"netdoersadmin:538f25fc32727".getBytes(),
								Base64.NO_WRAP));
		ByteArrayBody bab = new ByteArrayBody(buffer, fileName);
		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		reqEntity.addPart(type, bab);

		String myVal = ApplicationLoader.getPreferences().getApiKey();
		FormBodyPart bodyPart = new FormBodyPart("api_key", new StringBody(
				myVal));
		FormBodyPart bodyPart1 = new FormBodyPart("post_id", new StringBody(
				post_id));

		reqEntity.addPart(bodyPart);
		reqEntity.addPart(bodyPart1);

		postRequest.setEntity(reqEntity);
		HttpResponse response = httpClient.execute(postRequest);
		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = null;
		StringBuffer stringResponse = new StringBuffer();
		while ((line = in.readLine()) != null) {
			stringResponse.append(line);
			Log.e("uploadFile", line);
		}
		response = null;
		in.close();
		in = null;
		reqEntity = null;
		postRequest = null;

		return stringResponse.toString();
	}

	public static String uploadGroupMediaFile(String type, String group_id,
			File file, String url) throws UnsupportedEncodingException,
			ClientProtocolException, IOException {
		String fileName = file.getName();
		FileInputStream fileInputStream = new FileInputStream(file);
		int bytesAvailable = fileInputStream.available();

		byte[] buffer = new byte[bytesAvailable];
		// read file and write it into form...
		long bytesRead = fileInputStream.read(buffer, 0, bytesAvailable);

		HttpClient httpClient = new DefaultHttpClient();
		HttpPost postRequest = new HttpPost(url);
		postRequest.setHeader(
				"Authorization",
				"Basic "
						+ Base64.encodeToString(
								"netdoersadmin:538f25fc32727".getBytes(),
								Base64.NO_WRAP));
		ByteArrayBody bab = new ByteArrayBody(buffer, fileName);
		MultipartEntity reqEntity = new MultipartEntity(
				HttpMultipartMode.BROWSER_COMPATIBLE);
		reqEntity.addPart(type, bab);

		String myVal = ApplicationLoader.getPreferences().getApiKey();
		FormBodyPart bodyPart = new FormBodyPart("api_key", new StringBody(
				myVal));
		FormBodyPart bodyPart1 = new FormBodyPart("group_id", new StringBody(
				group_id));

		reqEntity.addPart(bodyPart);
		reqEntity.addPart(bodyPart1);

		postRequest.setEntity(reqEntity);
		HttpResponse response = httpClient.execute(postRequest);
		BufferedReader in = new BufferedReader(new InputStreamReader(response
				.getEntity().getContent()));
		String line = null;
		StringBuffer stringResponse = new StringBuffer();
		while ((line = in.readLine()) != null) {
			stringResponse.append(line);
			Log.e("uploadFile", line);
		}
		response = null;
		in.close();
		in = null;
		reqEntity = null;
		postRequest = null;

		return stringResponse.toString();
	}
}
