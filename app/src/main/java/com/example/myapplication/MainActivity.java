package com.example.myapplication;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.TileOverlayOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationSource, AMapLocationListener {

    private MapView mMapView;
    private AMap aMap;
    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private OnLocationChangedListener mListener;
    private Spinner mapTypeSpinner;

    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private static final String[] REQUIRED_PERMISSIONS = new String[] {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private boolean isFirstLocation = true;

    private EditText searchBox;
    private RecyclerView searchResults;
    private SearchAdapter searchAdapter;

    // 用于存储上一个 Marker
    private Marker lastClickedMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 设置全屏模式（可选）
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        // 修改状态栏颜色
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.primary_color));
        }

        setContentView(R.layout.activity_main);

        // 初始化高德地图SDK
        MapsInitializer.updatePrivacyShow(this, true, true);
        MapsInitializer.updatePrivacyAgree(this, true);

        // 获取地图控件引用
        mMapView = findViewById(R.id.map);
        mMapView.onCreate(savedInstanceState);

        // 获取 Spinner 控件
        mapTypeSpinner = findViewById(R.id.map_type_spinner);

        // 设置 Spinner 适配器
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, new String[]{"普通地图", "卫星地图", "夜景地图"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mapTypeSpinner.setAdapter(adapter);

        // 检查并请求权限
        if (allPermissionsGranted()) {
            initializeMap();
            initLocation();
            initSearchComponents(); // 初始化搜索组件
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }
    }

    private void initializeMap() {
        if (mMapView != null) {
            aMap = mMapView.getMap();
            initMapFeatures();
        }
    }

    private void initMapFeatures() {
        if (aMap == null) {
            return;
        }

        // 设置定位样式
        MyLocationStyle myLocationStyle = new MyLocationStyle();
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_SHOW);
        myLocationStyle.interval(2000);
        aMap.setMyLocationStyle(myLocationStyle);
        aMap.getUiSettings().setMyLocationButtonEnabled(true);
        aMap.setMyLocationEnabled(true);
        aMap.setLocationSource(this);

        // 自定义瓦片地图
        MyTileProvider tileProvider = new MyTileProvider(MainActivity.this);
        aMap.addTileOverlay(new TileOverlayOptions()
                .tileProvider(tileProvider)
                .zIndex(1111)
                .diskCacheDir("/storage/amap/cache").diskCacheEnabled(true)
                .diskCacheSize(100));

        // 初始化地图视角为云南
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(24.8801, 102.8329), 10));

        // 设置地图类型切换
        mapTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                switch (position) {
                    case 0:
                        aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                        Toast.makeText(MainActivity.this, "切换到普通地图", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                        Toast.makeText(MainActivity.this, "切换到卫星地图", Toast.LENGTH_SHORT).show();
                        break;
                    case 2:
                        aMap.setMapType(AMap.MAP_TYPE_NIGHT);
                        Toast.makeText(MainActivity.this, "切换到夜景地图", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private boolean allPermissionsGranted() {
        for (String permission : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED) {
                return false;
            }
        }
        return true;
    }

    private void initLocation() {
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(false); // 连续定位
        mLocationClient.setLocationOption(mLocationOption);
        mLocationClient.setLocationListener(this);
        activate(mListener); // 启动定位服务
    }

    private void initSearchComponents() {
        // 初始化搜索组件
        searchBox = findViewById(R.id.search_box);
        searchResults = findViewById(R.id.search_results);
        searchAdapter = new SearchAdapter(new ArrayList<>());
        searchResults.setLayoutManager(new LinearLayoutManager(this));
        searchResults.setAdapter(searchAdapter);

        // 添加文本变化监听事件
        searchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                performSearch(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) searchAdapter.updateData(new ArrayList<>());
            }
        });

        searchAdapter.setOnItemClickListener((position, item) -> {
            LatLng latLng = new LatLng(item.latitude, item.longitude);
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 5));

            // 如果上一个 Marker 存在，则移除它
            if (lastClickedMarker != null) {
                lastClickedMarker.remove();
            }

            Marker marker = aMap.addMarker(new MarkerOptions().position(latLng).title(item.name));
            String info = "纬度: " + item.latitude + "\n经度: " + item.longitude + "\n介绍: " + item.message;
            marker.setSnippet(info);
            marker.showInfoWindow();

            // 更新上一个 Marker 为当前 Marker
            lastClickedMarker = marker;
        });
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            searchAdapter.updateData(new ArrayList<>());
            return;
        }

        OkHttpClient okHttpClient = new OkHttpClient();
        FormBody formBody = new FormBody.Builder()
                .add("name", query)
                .build();

        Request request = new Request.Builder()
                .post(formBody)
                .url("http://192.168.1.106:8090/getpoints")
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("TAG", "onFailure: 查询失败");
                showErrorMessage("查询失败，请稍后重试");
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    Log.e("TAG", "onResponse: 查询失败");
                    showErrorMessage("查询失败，请稍后重试");
                    return;
                }

                String responseData = response.body().string();
                List<NewPoints> pointsList = parseJson(responseData);
                List<NewPoints> sortedResults = sortResults(pointsList, query);

                new Handler(Looper.getMainLooper()).post(() -> searchAdapter.updateData(sortedResults));
            }
        });
    }

    private List<NewPoints> parseJson(String json) {
        Gson gson = new Gson();
        Type type = new TypeToken<List<NewPoints>>() {}.getType();
        return gson.fromJson(json, type);
    }

    private List<NewPoints> sortResults(List<NewPoints> pointsList, String query) {
        List<NewPoints> exactMatches = new ArrayList<>();
        List<NewPoints> partialMatches = new ArrayList<>();
        List<NewPoints> nonMatches = new ArrayList<>();

        for (NewPoints point : pointsList) {
            if (point.name.equals(query)) {
                exactMatches.add(point);
            } else if (point.name.contains(query)) {
                partialMatches.add(point);
            } else {
                nonMatches.add(point);
            }
        }

        List<NewPoints> sortedResults = new ArrayList<>();
        sortedResults.addAll(exactMatches);
        sortedResults.addAll(partialMatches);
        sortedResults.addAll(nonMatches);

        return sortedResults;
    }

    private void showErrorMessage(String message) {
        new Handler(Looper.getMainLooper()).post(() -> Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show());
    }

    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mLocationClient != null) {
            mLocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mLocationClient != null) {
            mLocationClient.stopLocation();
            mLocationClient.onDestroy();
        }
        mLocationClient = null;
    }

    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (amapLocation != null && amapLocation.getErrorCode() == 0) {
            if (isFirstLocation) {
                LatLng latLng = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
                isFirstLocation = false;
            }
            if (mListener != null) {
                mListener.onLocationChanged(amapLocation);
            }
        } else {
            String errText = "定位失败," + amapLocation.getErrorInfo();
            Log.e("AmapErr", errText);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                initializeMap();
                initLocation();
                initSearchComponents();
            } else {
                Toast.makeText(this, "权限申请被拒绝", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
        if (null != mLocationClient) {
            mLocationClient.onDestroy();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }
}
