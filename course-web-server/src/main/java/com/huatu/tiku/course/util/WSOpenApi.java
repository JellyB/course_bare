package com.huatu.tiku.course.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.SimpleTimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.AbstractHttpMessage;
import org.apache.http.util.EntityUtils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * cdn工具类
 * 
 * @author zhangchong
 *
 */
public class WSOpenApi {

	// 网宿用户名
	private static final String USER_NAME = "ht_wx";
	// apikey
	private static final String API_KEY = "ht_wx@123.com";

	// 查询刷新记录
	public static String QUERY_OPERATION_URL = "/ccm/purge/ItemIdQuery";
	// 刷新缓存
	public static String PURGE_CACHE_URL = "/ccm/purge/ItemIdReceiver";
	// 网宿api接口地址
	public static String BASE_API_URL = "https://open.chinanetcenter.com";

	/**
	 * GET
	 */
	public static void get(String url, Map<String, String> header) throws URISyntaxException {
		HttpGet get = new HttpGet();
		get.setURI(new URI(url));
		call(get, header);
	}

	/**
	 * POST
	 */
	public static String post(String url, Map<String, String> header, byte[] body) throws URISyntaxException {
		HttpPost post = new HttpPost();
		post.setURI(new URI(url));
		if (body != null) {
			ByteArrayEntity se = new ByteArrayEntity(body);
			post.setEntity(se);
		}
		String ret = call(post, header);
		return ret;
	}

	/**
	 * PUT
	 */
	public static void put(String url, Map<String, String> header) throws URISyntaxException {
		HttpPut get = new HttpPut();
		get.setURI(new URI(url));
		call(get, header);
	}

	/**
	 * DELETE
	 */
	public static void delete(String url, Map<String, String> header) throws URISyntaxException {
		HttpDelete get = new HttpDelete();
		get.setURI(new URI(url));
		call(get, header);
	}

	/**
	 * http call
	 */
	private static String call(HttpRequestBase method, Map<String, String> header) {
		DefaultHttpClient client = null;
		try {
			client = new DefaultHttpClient();
			addHeader(method, USER_NAME, API_KEY, header);
			HttpResponse response = client.execute(method);
			if (response.getStatusLine().getStatusCode() != 200) {
				method.abort();
			}
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				String str = EntityUtils.toString(entity);
				return str;
			}
		} catch (Exception e) {
			method.abort();
			e.printStackTrace();
		}
		return null;
	}

	private static void addHeader(AbstractHttpMessage client, String username, String apikey,
			Map<String, String> headers) throws Exception {
		if (headers != null) {
			for (Iterator<Entry<String, String>> it = headers.entrySet().iterator(); it.hasNext();) {
				Entry<String, String> entry = it.next();
				client.addHeader(entry.getKey(), entry.getValue());
			}
		}
		Date date = new Date();
		String dateString = getDate(date);
		String authoriztion = encode(dateString, username, apikey);
		client.addHeader("Date", dateString);
		client.addHeader("Authorization", "Basic " + authoriztion);
	}

	private static String getDate(Date date) {
		SimpleDateFormat rfc822DateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
		rfc822DateFormat.setTimeZone(new SimpleTimeZone(0, "GMT"));
		String dateString = rfc822DateFormat.format(date);
		return dateString;
	}

	private static String encode(String dateString, String username, String apikey) throws Exception {
		String signature = signAndBase64Encode(dateString.getBytes(), apikey);
		String userAndPwd = username + ":" + signature;
		return new String(Base64.encodeBase64(userAndPwd.getBytes("UTF-8")));
	}

	private static String signAndBase64Encode(byte[] data, String apikey) throws Exception {
		Mac mac = Mac.getInstance("HmacSHA256");// 如果使用sha256则参数是：HmacSHA256
		mac.init(new SecretKeySpec(apikey.getBytes(), "HmacSHA256"));// 如果使用sha256则参数是：HmacSHA256
		byte[] signature = mac.doFinal(data);
		return new String(Base64.encodeBase64(signature));
	}

	/**
	 * 更新cdn缓存
	 * 
	 * @param purgeUrl
	 * @return
	 */
	public static String purge(String purgeUrl) {
		try {
			Map<String, String> header = new HashMap<String, String>();
			header.put("Accept", "application/json");
			header.put("Content-Type", "application/json");

			JSONArray ja = new JSONArray();
			ja.add(purgeUrl);
			JSONObject json = new JSONObject();
			json.put("urls", ja);
			String body = json.toJSONString();
			String postRet = WSOpenApi.post(BASE_API_URL + PURGE_CACHE_URL, header, body.getBytes());
			return postRet;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 查询缓存更新进度
	 * 
	 * @param queryUrl
	 * @return
	 */
	public static String queryTask(String queryUrl) {
		try {
			Map<String, String> header = new HashMap<String, String>();
			header.put("Accept", "application/json");
			header.put("Content-Type", "application/json");

			JSONObject queryBody = new JSONObject();
			
			DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
			queryBody.put("startTime", "2019-05-22 06:33:26");
			queryBody.put("endTime", LocalDateTime.now().format(ofPattern));
			queryBody.put("url", "https://ns.huatu.com/s/v1/search/course/keywords");

			String postRet = WSOpenApi.post(BASE_API_URL + QUERY_OPERATION_URL, header, queryBody.toJSONString().getBytes());
			return postRet;
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) throws Exception {
//		String url = "https://ns.huatu.com/s/v1/search/course/keywords";
//		String ret = WSOpenApi.queryTask(url);
//		System.out.println(ret);
//		String purge = WSOpenApi.purge(url);
//		System.out.println(purge);
		System.out.println(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
	}
}
