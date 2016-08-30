package com.coolweather.app.activity;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.coolweather.app.R;

public class MapActivity extends Activity
{
	private MapView mapView=null;
	
	private BaiduMap baiduMap;
	
	private String provider;
	
	private boolean isFirstLocate=true;
	
	private LocationManager locationManager;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		 //在使用SDK各组件之前初始化context信息，传入ApplicationContext  
        //注意该方法要再setContentView方法之前实现  
		SDKInitializer.initialize(getApplicationContext());
		setContentView(R.layout.map_layout);
		mapView=(MapView) findViewById(R.id.bmapView);
		baiduMap=mapView.getMap();
		
		/*
		LatLng ll=new LatLng(118.085897,24.634831);
		MapStatusUpdate update=MapStatusUpdateFactory.newLatLng(ll);
		baiduMap.animateMapStatus(update);*/
		
		
		
		locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//获取所有可能的位置提供器
		List<String> providerList=locationManager.getProviders(true);
		
		if(providerList.contains(LocationManager.NETWORK_PROVIDER))
		{
			provider=LocationManager.NETWORK_PROVIDER;
			
		}
		else if(providerList.contains(LocationManager.GPS_PROVIDER))
		{
			provider=LocationManager.GPS_PROVIDER;
		}
		
		else
		{
			//当前没有可以使用的位置提供器，弹出Toast提示用户
			Toast.makeText(this, "No location provider to use", 
					Toast.LENGTH_SHORT).show();
		}
		
		Location location=locationManager.getLastKnownLocation(provider);
		
		if(location!=null)
		{
			
			navigateTo(location);
		}
		locationManager.requestLocationUpdates(provider, 5000, 1, locationListener);
		
		
	}
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理 
		mapView.onDestroy();
		
		if(locationManager!=null)
		{
			locationManager.removeUpdates(locationListener);
		}
	}
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		//在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理 
		mapView.onPause();
		
	}
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		//在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理 
		mapView.onResume();
	}
	
	LocationListener locationListener=new LocationListener()
												{

													@Override
													public void onLocationChanged(
															Location location)
													{
														// TODO Auto-generated method stub
														if(location!=null)
															navigateTo(location);
													}

													@Override
													public void onProviderDisabled(
															String provider)
													{
														// TODO Auto-generated method stub
														
													}

													@Override
													public void onProviderEnabled(
															String provider)
													{
														// TODO Auto-generated method stub
														
													}

													@Override
													public void onStatusChanged(
															String provider,
															int status,
															Bundle extras)
													{
														// TODO Auto-generated method stub
														
													}
													
												};
	private  void navigateTo(Location location)
	{
		/*
		MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
		locationBuilder.latitude(location.getLatitude());
		locationBuilder.longitude(location.getLongitude());
		MyLocationData locationData=locationBuilder.build();
		baiduMap.setMyLocationData(locationData);
		baiduMap.setMyLocationEnabled(true);*/
		/*
		String currentPositon="Latitude is "+location.getLatitude()+"\n"
		                      +"Longitude is "+location.getLongitude();
		Toast.makeText(this, currentPositon, Toast.LENGTH_SHORT).show();
		*/
		if(isFirstLocate)
		{
			LatLng ll=new LatLng(location.getLatitude(),location.getLongitude());
			MapStatusUpdate update=MapStatusUpdateFactory.newLatLng(ll);
			baiduMap.animateMapStatus(update);
			update=MapStatusUpdateFactory.zoomTo(16f);
			baiduMap.animateMapStatus(update);
			//isFirstLocate=false;
		}
	}
	
}
