package com.coolweather.app.activity;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.coolweather.app.R;

public class MapActivity extends Activity
{
	private MapView mapView=null;
	
	private BaiduMap baiduMap;
	
	private String provider;
	
	/**
	 * 变量isFirstLocate是为了防止多次调用animateMapStatus()方法
	 * 因为将地图移动到我们当前的位置只需要在程序第一次定位的时候调用就可以了
	 */
	private boolean isFirstLocate=true;
	
	private LocationManager locationManager;
	
	
	/**
	 * 反向地理编码
	 */
	private GeoCoder mSearch =null;
	
	/**
	 * 存储定位的位置地点
	 */
	private String myPosition=null;
	
	
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
		//开启才能显示位置
		baiduMap.setMyLocationEnabled(true);
		
		locationManager=(LocationManager) getSystemService(Context.LOCATION_SERVICE);
		//获取所有可能的位置提供器
		List<String> providerList=locationManager.getProviders(true);
		
		if(providerList.contains(LocationManager.NETWORK_PROVIDER))
		{
			provider=LocationManager.NETWORK_PROVIDER;
			
		}
		else if(providerList.contains(LocationManager.PASSIVE_PROVIDER))
		{
			provider=LocationManager.PASSIVE_PROVIDER;
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
		

		// 搜索初始化
		
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
			// 正向编码
			@Override
			public void onGetGeoCodeResult(GeoCodeResult result)
			{
				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR)
				{
					Toast.makeText(MapActivity.this, "抱歉，未能找到结果",
							Toast.LENGTH_LONG).show();
					return;
				}
				baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
						.getLocation()));
				// 定位
				String strInfo = String.format("纬度：%f 经度：%f",
						result.getLocation().latitude,
						result.getLocation().longitude);
				Toast.makeText(MapActivity.this, strInfo, Toast.LENGTH_LONG)
						.show();
				// result保存地理编码的结果 城市-->坐标

			}

			// 反向编码
			@Override
			public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result)
			{
				if (result == null
						|| result.error != SearchResult.ERRORNO.NO_ERROR)
				{
					Toast.makeText(MapActivity.this, "抱歉，未能找到结果",
							Toast.LENGTH_LONG).show();
					return;
				}
				baiduMap.setMapStatus(MapStatusUpdateFactory.newLatLng(result
						.getLocation()));
				// 定位
				//Toast.makeText(MapActivity.this, result.getAddress(),
				//		Toast.LENGTH_LONG).show();
				// result保存翻地理编码的结果 坐标-->城市
				myPosition=result.getAddress();
				showAlertDialog(myPosition+"?");
			}

			

			

		});
		
		if(location!=null)
		{
			
			navigateTo(location);
		}
		locationManager.requestLocationUpdates(provider, 10, 1, locationListener);
		
		
	}
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		super.onDestroy();
		//在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理 
		mapView.onDestroy();
		
		baiduMap.setMyLocationEnabled(false);
		
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
		if(isFirstLocate)
		{
			//设置定位纬经度
			LatLng ll=new LatLng(location.getLatitude(),location.getLongitude());
			MapStatusUpdate update=MapStatusUpdateFactory.newLatLng(ll);
			baiduMap.animateMapStatus(update);
			//设置放大比例
			update=MapStatusUpdateFactory.zoomTo(16f);
			baiduMap.animateMapStatus(update);
			isFirstLocate=false;
			
			if (mSearch != null)
			{
				mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(ll));
			}
		}
		
		MyLocationData.Builder locationBuilder=new MyLocationData.Builder();
		locationBuilder.latitude(location.getLatitude());
		locationBuilder.longitude(location.getLongitude());
		MyLocationData locationData=locationBuilder.build();
		baiduMap.setMyLocationData(locationData);
		
		String message="Latitude is "+location.getLatitude()+
				       "\n"+"Longitude is "+location.getLongitude();
		Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onBackPressed()
	{
		// TODO Auto-generated method stub
		//super.onBackPressed();
		/**
		 * 按返回键的处理，返回到weatherActivity
		 * 可以返回通过定位获得的地点信息，直接传回WeatherActivity进行处理跟数据的显示
		 * 
		 */
		CallBackMess();
	}
	
	
	
	private void showAlertDialog(String tips)
	{
		AlertDialog.Builder dialog=new AlertDialog.Builder(MapActivity.this);
		dialog.setTitle("This is a location dialog");
		dialog.setMessage(tips);
		dialog.setCancelable(false);
		dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				CallBackMess();
				
			}
		});
		dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				// TODO Auto-generated method stub
				
			}
		});
		dialog.show();
		
	}
	
	public void CallBackMess()
	{
		if (getIntent().getBooleanExtra("from_weather_to_map", false))
		{
			Intent intent = new Intent(this, WeatherActivity.class);
			intent.putExtra("self_position", myPosition);
			startActivity(intent);
		}
	}
}
