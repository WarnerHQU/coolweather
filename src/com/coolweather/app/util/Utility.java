package com.coolweather.app.util;

import android.text.TextUtils;

import com.coolweather.app.model.City;
import com.coolweather.app.model.CoolWeatherDB;
import com.coolweather.app.model.County;
import com.coolweather.app.model.Province;

/*
 * 由于服务器返回的省市县数据都是以“代号|城市，代号|城市”
 * 这种格式的，所以需要一个工具类来解析和处理这种数据。
 */
public class Utility
{
	/*
	 * 解析和处理服务器返回的省级数据,加锁
	 */
	public synchronized static boolean handleProvincesResponse(CoolWeatherDB coolWeatherDB,String response)
	{
		if(!TextUtils.isEmpty(response))
		{
			String[] allProvinces=response.split(",");
			if(allProvinces!=null&&allProvinces.length>0)
			{
				for(String p:allProvinces)
				{
					//为何要用\\|不用|?????
					String[] array=p.split("\\|");
					Province province=new Province();
					province.setProvinceCode(array[0]);
					province.setProvinceName(array[1]);
					
					//将解析出来的数据存储到province表中
					coolWeatherDB.saveProvince(province);
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * 解析和处理服务器返回的市级数据
	 */
	public synchronized static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId)
	{
		if(!TextUtils.isEmpty(response))
		{
			String[] allCities=response.split(",");
			if(allCities!=null&&allCities.length>0)
			{
				for(String c:allCities)
				{
					String[] array=c.split("\\|");
					City city=new City();
					city.setCityCode(array[0]);
					city.setCityName(array[1]);
					city.setProvinceId(provinceId);
					
					//将解析出来的数据存储City类中去
					coolWeatherDB.saveCity(city);
					
				}
				return true;
			}
		}
		return false;
	}
	
	/*
	 * 解析和返回服务器返回的县级数据
	 */
	public synchronized static boolean handleCountiesResponse(CoolWeatherDB coolWeatherDB,String response,int cityId)
	{
		if(!TextUtils.isEmpty(response))
		{
			String[] allCounties=response.split(",");
			if(allCounties!=null&&allCounties.length>0)
			{
				for(String c:allCounties)
				{
					String[] array=c.split("\\|");
					County county=new County();
					county.setCountyCode(array[0]);
					county.setCountyName(array[1]);
					county.setCityId(cityId);
					
					//将解析出来的数据存储到County表中去
					coolWeatherDB.saveCounty(county);
				}
				return true;
			}
		}
		
		return false;
	}
	
}
