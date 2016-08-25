package com.coolweather.app.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/*
 * 全国所有省市县的数据都是从服务器端获取的。
 * 因此这里和服务器的交互式必不可少的，所以可以在util包里面先增加一个HttpUtil类。
 */
public class HttpUtil
{
	public static void sendHttpRequest(final String address,
			                           final HttpCallbackListener listener)
	{
		new Thread(new Runnable()
		{

			public void run()
			{
				// TODO Auto-generated method stub
				HttpURLConnection connection=null;
				try
				{
					URL url=new URL(address);
					connection=(HttpURLConnection)url.openConnection();
					connection.setRequestMethod("GET");
					connection.setReadTimeout(8000);
					connection.setConnectTimeout(8000);
					//从输入流中逐行读取数据，并保存到StringBuilder中去
					InputStream in=connection.getInputStream();
					BufferedReader reader=new BufferedReader(new InputStreamReader(in));
					StringBuilder response=new StringBuilder();
					String line;
					while((line=reader.readLine())!=null)
					{
						response.append(line);
					}
					if(listener!=null)
					{
						//回调onFinish()方法
						listener.onFinish(response.toString());
					}
					
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					//e.printStackTrace();
					if(listener!=null)
					{
						//回调onError()方法
						listener.onError(e);
					}
				}
				finally
				{
					if(connection!=null)
						connection.disconnect();
				}	
			}
			
		}).start();
	}
}
