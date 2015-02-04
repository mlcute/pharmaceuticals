package com.sanofi.in.mobcast;

import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;

class AsyncHttpPost extends AsyncTask<String, String, String> {

	ProgressDialog pDialog;
	private Activity activity;
	private HashMap<String, String> mData = null;// post data
	Context context = null;

	/**
	 * constructor
	 */

	public AsyncHttpPost(HashMap<String, String> data) {
		mData = data;
	}

	public AsyncHttpPost(HashMap<String, String> data, Activity activity) {

		this.activity = activity;
		context = activity;
		pDialog = new ProgressDialog(context);
		mData = data;
	}

	/**
	 * background
	 */

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if (context != null) {
			pDialog = new ProgressDialog(context);
			pDialog.setMessage(Html.fromHtml("Please Wait..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}
	}

	@Override
	protected String doInBackground(String... params) {
		byte[] result = null;
		String str = "";
		HttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(params[0]);// in this case, params[0] is
												// URL
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
			String body = bodyBuilder.toString();
			Log.v("AsyncPost", "Posting '" + body + "' to " + params[0]);
			StatusLine statusLine = response.getStatusLine();
			if (statusLine.getStatusCode() == HttpURLConnection.HTTP_OK) {
				result = EntityUtils.toByteArray(response.getEntity());
				str = new String(result, "UTF-8");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (Exception e) {
		}
		if (str != null)
			Log.d("response", str);
		return str;
	}

	/**
	 * on getting result
	 */
	@Override
	protected void onPostExecute(String result) {
		if (onPostExecuteListener != null) {
			onPostExecuteListener.onPostExecute(result);
		}
		// something...
		try {
			pDialog.dismiss();
			pDialog = null;
		} catch (Exception e) {
			// nothing
		}

	}

	OnPostExecuteListener onPostExecuteListener;

	public void setOnPostExecuteListener(
			OnPostExecuteListener onPostExecuteListener) {
		this.onPostExecuteListener = onPostExecuteListener;
	}

	public interface OnPostExecuteListener {
		public abstract void onPostExecute(String result);
	}
}