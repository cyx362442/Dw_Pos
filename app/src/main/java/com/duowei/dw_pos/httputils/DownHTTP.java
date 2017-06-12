package com.duowei.dw_pos.httputils;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request.Method;
import com.android.volley.RequestQueue;
import com.duowei.dw_pos.tools.Base64;

import org.apache.http.util.ByteArrayBuffer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public final class DownHTTP {

    /**
     * 用开源项目Volley从服务器获取数据的工具，解决的4.x版本串行的问题
     * */

    /**
     * get请求方式调用方法，url：服务器中的地址，请求结果响应的事件调用的时候实现
     */
    public static void getVolley(String url, VolleyResultListener listener) {
        // 得到请求队列
        RequestQueue queue = MyVolley.getRequestQueue();
        // 创建http请求
        MyStringRequest myReq = new MyStringRequest(Method.GET, url, listener, listener);
        // 将http请求加入队列，volley库会开始执行请求
        queue.add(myReq);
    }

    /**
     * post请求方式调用的方法，Map类型参数需要在调用的时候传入，key值是服务器获取的字段名，values是对应字段的参数
     */
    public static void postVolley(String url, final Map<String, String> params, VolleyResultListener listener) {
        // 得到请求队列
        RequestQueue queue = MyVolley.getRequestQueue();
        // 创建http请求
        MyStringRequest myReq = new MyStringRequest(Method.POST, url, listener, listener) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return params;
            }
        };
        // 将http请求加入队列，volley库会开始执行请求
        queue.add(myReq);
    }

    /**
     * post请求方式调用的方法，Map类型参数需要在调用的时候传入，key值是服务器获取的字段名，values是对应字段的参数
     */
    public static void postVolley6(String url, String sql, VolleyResultListener listener) {
        final HashMap map = new HashMap<String, String>();
        String base64 = Base64.getBase64(sql).replaceAll("\n", "");
        map.put("State", "6");
        map.put("Ssql", base64);
        // 得到请求队列
        RequestQueue queue = MyVolley.getRequestQueue();
        // 创建http请求
        MyStringRequest myReq = new MyStringRequest(Method.POST, url, listener, listener) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                return map;
            }
        };
        // 将http请求加入队列，volley库会开始执行请求
        queue.add(myReq);
    }

	public static void postVolley7(String url, String sql,VolleyResultListener listener) {
		final HashMap map=new HashMap<String,String>();
		String base64 = Base64.getBase64(sql).replaceAll("\n", "");
		map.put("State","7");
		map.put("Ssql",base64);
		// 得到请求队列
		RequestQueue queue = MyVolley.getRequestQueue();
		// 创建http请求
		MyStringRequest myReq = new MyStringRequest(Method.POST, url, listener,listener)
		{
			protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
				return map;
			}
		};
		// 将http请求加入队列，volley库会开始执行请求
		queue.add(myReq);
	}

	/**
	 * 没用Volley的请求方式
	 * */

	private static final int TIMEOUT = 5000;

	public static String getResult(final Context context, String urlGet) {
		InputStream is = null;
		try {
			URL url = new URL(urlGet);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			// 超时时间
			conn.setConnectTimeout(TIMEOUT);
			conn.setReadTimeout(TIMEOUT);
			// 检查服务端的状态是否正确
			if (conn.getResponseCode()!= HttpURLConnection.HTTP_OK){
				// 服务器无法响应，作给客户端的结果的事件
				return "fail";
			}
			is = conn.getInputStream();
			byte[] buffer = new byte[4096];
			ByteArrayBuffer byteBuf = new ByteArrayBuffer(10000);
			int len = 0;
			while (-1 != (len  = is.read(buffer))){
				byteBuf.append(buffer, 0 , len);
			}
			return new String(byteBuf.buffer(), 0, byteBuf.length(),"utf-8");
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return "fail";
		} catch (IOException e) {
			e.printStackTrace();
			return "fail";
		}finally{
			if (is != null){
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
					return "fail";
				}
			}
		}
	}

	public static HashMap<String,String>hashMap;
	public static String postResult(String strUrlPath,String code,String sql){
		hashMap=new HashMap<String ,String>();
		hashMap.put("State",code);
		hashMap.put("Ssql",sql);
		byte[] data = getRequestData(hashMap).toString().getBytes();//获得请求体
		try {
			//String urlPath = "http://192.168.1.9:80/JJKSms/RecSms.php";
			URL url = new URL(strUrlPath);
			HttpURLConnection httpURLConnection = (HttpURLConnection)url.openConnection();
			httpURLConnection.setConnectTimeout(5000);     //设置连接超时时间
			httpURLConnection.setDoInput(true);                  //打开输入流，以便从服务器获取数据
			httpURLConnection.setDoOutput(true);                 //打开输出流，以便向服务器提交数据
			httpURLConnection.setRequestMethod("POST");     //设置以Post方式提交数据
			httpURLConnection.setUseCaches(false);               //使用Post方式不能使用缓存
			//设置请求体的类型是文本类型
			httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			//设置请求体的长度
			httpURLConnection.setRequestProperty("Content-Length", String.valueOf(data.length));
			//获得输出流，向服务器写入数据
			OutputStream outputStream = httpURLConnection.getOutputStream();
			outputStream.write(data);

			int response = httpURLConnection.getResponseCode();            //获得服务器的响应码
			if(response == HttpURLConnection.HTTP_OK) {
				InputStream inptStream = httpURLConnection.getInputStream();
				return dealResponseResult(inptStream);                     //处理服务器的响应结果
			}
		} catch (IOException e) {
			//e.printStackTrace();
			return "fail: " + e.getMessage().toString();
		}
		return "fail";
	}
	/*
 * Function  :   封装请求体信息
 * Param     :   params请求体内容，encode编码格式
 */
	public static StringBuffer getRequestData(Map<String, String> params) {
		StringBuffer stringBuffer = new StringBuffer();        //存储封装好的请求体信息
		try {
			for(Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey())
						.append("=")
						.append(URLEncoder.encode(entry.getValue(), "utf-8"))
						.append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1);    //删除最后的一个"&"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}
	/*
     * Function  :   处理服务器的响应结果（将输入流转化成字符串）
     * Param     :   inputStream服务器的响应输入流
     */
	public static String dealResponseResult(InputStream inputStream) {
		String resultData = null;      //存储处理结果
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		byte[] data = new byte[1024];
		int len = 0;
		try {
			while((len = inputStream.read(data)) != -1) {
				byteArrayOutputStream.write(data, 0, len);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		resultData = new String(byteArrayOutputStream.toByteArray());
		Log.e("resultData====",resultData);
		return resultData;
	}
}
