package com.example.lyzy.ldbg;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.esri.android.map.FeatureLayer;
import com.esri.android.map.GraphicsLayer;
import com.esri.android.map.MapOnTouchListener;
import com.esri.android.map.MapView;
import com.esri.android.map.RasterLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.map.event.OnZoomListener;
import com.esri.core.geodatabase.Geodatabase;
import com.esri.core.geodatabase.GeodatabaseFeature;
import com.esri.core.geodatabase.GeodatabaseFeatureTable;
import com.esri.core.geodatabase.ShapefileFeatureTable;
import com.esri.core.geometry.Geometry;
import com.esri.core.geometry.GeometryEngine;
import com.esri.core.geometry.MultiPath;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.Polygon;
import com.esri.core.geometry.Polyline;
import com.esri.core.geometry.SpatialReference;
import com.esri.core.map.Feature;
import com.esri.core.map.FeatureTemplate;
import com.esri.core.map.Graphic;
import com.esri.core.raster.FileRasterSource;
import com.esri.core.renderer.Renderer;
import com.esri.core.renderer.SimpleRenderer;
import com.esri.core.symbol.SimpleFillSymbol;
import com.esri.core.symbol.SimpleLineSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.table.TableException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Main extends Activity
{

    private LocationListener locationListener = new LocationListener()
    {
        /**
         * 位置信息变化时触发
         */
        public void onLocationChanged(Location location)
        {
            markLocation(location);
        }

        /**
         * 状态改变时调用
         */
        public void onStatusChanged(String provider, int status, Bundle extras)
        {
            switch (status)
            {
                //GPS状态为可见时
                case LocationProvider.AVAILABLE:

                    Log.i("TAG", "当前GPS状态为可见状态");
                    break;
                //GPS状态为服务区外时
                case LocationProvider.OUT_OF_SERVICE:

                    Log.i("TAG", "当前GPS状态为服务区外状态");
                    break;
                //GPS状态为暂停服务时
                case LocationProvider.TEMPORARILY_UNAVAILABLE:

                    Log.i("TAG", "当前GPS状态为暂停服务状态");
                    break;
            }
        }

        /**
         * GPS开启时触发
         */
        public void onProviderEnabled(String provider)
        {
            showToast("GPS打开");
            Location location = locMag.getLastKnownLocation(provider);
            markLocation(location);
        }

        /**
         * GPS禁用时触发
         */
        public void onProviderDisabled(String provider)
        {
            showToast("GPS已关闭");
        }
    };


    ImageView imageView;
    TextView tbilichi;
    TextView tvceliang;
    TextView tvjinweidu;

    boolean startbianji = false;
    boolean startxuanze = false;
    boolean startceliang = false;
    boolean startceliangmian = false;
    boolean startceliangxian = false;
    boolean startceliangdandian = false;
    boolean startcelianglianxu = false;
    boolean startxiubian = false;
    boolean startdingwei = false;

    ImageButton btnbianji;
    ImageButton btnxuanze;
    ImageButton btnfengge;
    ImageButton btnxiubian;
    ImageButton btnceliang;
    ImageButton btnceliangmian;
    ImageButton btnceliangxian;
    ImageButton btnceliangdandian;
    ImageButton btnceliangliangxu;
    ImageButton btndingwei;

    Spinner sdimaoming;
    Spinner sxiangming;
    Spinner spoxiangming;
    Spinner spoweiming;
    Spinner spoduming;

    LinearLayout lbianji;
    LinearLayout linearLayout;
    LinearLayout lgongju;
    LinearLayout lbiaoti;
    LinearLayout lceliang;
    LinearLayout lceliangshuzhi;

    RasterLayer rasterLayer;
    String rasterPath = Environment.getExternalStorageDirectory().getPath() + "/林地变更/tif/tuanchang.tif";
    private static final String TAG = null;
    FileRasterSource rasterSource = null;
    MapView mapView = null;
    //     方向传感器管理器
    private SensorManager orientationmanager;
    private SensorListener lorientation;
    Geodatabase geodatabase;
    public static final String GEO_FILENAME = "/林地变更/gdb/offlinedata.geodatabase";
    String gdbfile = Environment.getExternalStorageDirectory().getAbsolutePath() + GEO_FILENAME;
    GeodatabaseFeatureTable geodatabaseFeatureTable;
    FeatureLayer featureLayer;
    //    监听
    MyTouchListener listenerTouch;
    Point startPoint = null;
    Point currentPoint = null;
    Polygon multiPath = null;
    GraphicsLayer graphicsLayeredit;
    GraphicsLayer graphicsLayerPoint;
    int uid;
    int pointid;
    long[] ids = null;
    long fid;
    boolean startshouhui = false;
    ImageButton btnshouhui;
    ArrayList<Point> points;
    Graphic graphicp;

    Geometry.Type drawtype = null;

    Polyline multiPathLine = null;
    boolean startfengge = false;
    Geometry[] geometries;
    Polyline line = null;
    Polygon gon = null;

    Point celiangstartPoint = null;
    MultiPath celiangmultiPath = null;
    int celianguid = 0;
    Geometry.Type celiangtype = null;

    String chinashapefilePath = Environment.getExternalStorageDirectory().getPath() + "/林地变更/shp/china.shp";
    FeatureLayer cfeatureLayer;
    GraphicsLayer gLayerGps;
    double locx;
    double locy;
    Toast toast;
    Point gpsPoint;
    Polyline gpsLine;
    LocationManager locMag;
    Point gpsStartPoint;
    SpatialReference spatiaR;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageView);
        btnbianji = (ImageButton) findViewById(R.id.btnbianji);
        btnxuanze = (ImageButton) findViewById(R.id.btnxuanzhe);
        btnshouhui = (ImageButton) findViewById(R.id.btnshouhui);
        btnfengge = (ImageButton) findViewById(R.id.btnfenge);
        btnxiubian = (ImageButton) findViewById(R.id.btnxiubian);
        btnceliang = (ImageButton) findViewById(R.id.btnceliang);
        btnceliangmian = (ImageButton) findViewById(R.id.btnceliangmian);
        btnceliangxian = (ImageButton) findViewById(R.id.btnceliangxian);
        btnceliangdandian = (ImageButton) findViewById(R.id.btnceliangdandian);
        btnceliangliangxu = (ImageButton) findViewById(R.id.btncelianglianxu);
        btndingwei = (ImageButton) findViewById(R.id.btndingwei);

        tbilichi = (TextView) findViewById(R.id.tbilichi);
        tvceliang = (TextView) findViewById(R.id.tvceliang);
        tvjinweidu = (TextView) findViewById(R.id.tvjinweidu);

        lbiaoti = (LinearLayout) findViewById(R.id.lbiaoti);
        lbiaoti.setBackgroundColor(Color.rgb(240,240,240));
        lbianji = (LinearLayout) findViewById(R.id.lbianji);
        lbianji.setVisibility(View.INVISIBLE);
        lbianji.setBackgroundColor(Color.rgb(240, 228, 228));
        lgongju = (LinearLayout) findViewById(R.id.lgongju);
        lgongju.setVisibility(View.INVISIBLE);
        lgongju.setBackgroundColor(Color.rgb(228, 228, 228));
        linearLayout = (LinearLayout) findViewById(R.id.linearLayout);
        linearLayout.setBackgroundColor(Color.rgb(228, 228, 228));
        lceliang = (LinearLayout) findViewById(R.id.lceliang);
        lceliang.setVisibility(View.INVISIBLE);
        lceliang.setBackgroundColor(Color.rgb(228, 228, 228));
        lceliangshuzhi = (LinearLayout) findViewById(R.id.lceliangshuzhi);
        lceliangshuzhi.setVisibility(View.INVISIBLE);
        lceliangshuzhi.setBackgroundColor(Color.rgb(255, 255, 255));

        mapView = (MapView) findViewById(R.id.map);
        mapView.setMaxScale(500);

        // 初始化方向监听
        lorientation = new SensorListener();
        //获取系统服务（SENSOR_SERVICE)返回一个SensorManager 对象
        orientationmanager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        //中国地图
        try {

            ShapefileFeatureTable shapefileFeatureTable=new ShapefileFeatureTable(chinashapefilePath);

            cfeatureLayer = new FeatureLayer(shapefileFeatureTable);
            SimpleFillSymbol simpleFillSymbolC = new SimpleFillSymbol(Color.rgb(255,255,198));
            simpleFillSymbolC.setOutline(new SimpleLineSymbol(Color.rgb(215,158,158),1));
            Renderer rendererC = new SimpleRenderer(simpleFillSymbolC);
            cfeatureLayer.setRenderer(rendererC);

            mapView.addLayer(cfeatureLayer);
        } catch (FileNotFoundException e) {

            e.printStackTrace();

        }

        //加载影像

        try
        {
            rasterSource = new FileRasterSource(rasterPath);
        } catch (IllegalArgumentException ie)
        {
            Log.d(TAG, "null or empty path");
        } catch (FileNotFoundException fe)
        {
            Log.d(TAG, "raster file doesn't exist");
        } catch (RuntimeException re)
        {
            Log.d(TAG, "raster file can't be opened");
        }
        rasterLayer = new RasterLayer(rasterSource);
        mapView.addLayer(rasterLayer);

        //        加载数据库
        try
        {
            geodatabase = new Geodatabase(gdbfile);

        } catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        geodatabaseFeatureTable = geodatabase.getGeodatabaseFeatureTableByLayerId(1);
        featureLayer = new FeatureLayer(geodatabaseFeatureTable);
        SimpleFillSymbol simpleFillSymbol = new SimpleFillSymbol(Color.GREEN);
        simpleFillSymbol.setOutline(new SimpleLineSymbol(Color.BLACK, 1));
        simpleFillSymbol.setAlpha(30);
        Renderer renderer=new SimpleRenderer(simpleFillSymbol);
        featureLayer.setRenderer(renderer);
        featureLayer.setSelectionColor(Color.BLUE);
        featureLayer.setSelectionColorWidth(2);
        mapView.addLayer(featureLayer);

        graphicsLayeredit = new GraphicsLayer();
        mapView.addLayer(graphicsLayeredit);

        graphicsLayerPoint = new GraphicsLayer();
        mapView.addLayer(graphicsLayerPoint);


        //      地图启动时比例尺
        mapView.setOnStatusChangedListener(new OnStatusChangedListener()
        {
            private static final long serialVersionUID = 1L;

            public void onStatusChanged(Object source, STATUS status)
            {
                if (OnStatusChangedListener.STATUS.INITIALIZED == status && source == mapView)
                {
                    double scalec = mapView.getScale();
                    long scalelc = Math.round(scalec);
                    tbilichi.setText("比例尺 = 1 : " + scalelc);
                }
            }
        });
        //        实时比例尺
        mapView.setOnZoomListener(new OnZoomListener()
        {
            @Override
            public void preAction(float v, float v1, double v2)
            {
            }

            @Override
            public void postAction(float v, float v1, double v2)
            {
                double scale = mapView.getScale();
                long scalelong = Math.round(scale);
                tbilichi.setText("比例尺 = 1 : " + scalelong);
            }

        });

        listenerTouch = new MyTouchListener(this, mapView);
        mapView.setOnTouchListener(listenerTouch);

//        注册位置管理
        locMag = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        gLayerGps = new GraphicsLayer();
        mapView.addLayer(gLayerGps);

        spatiaR = mapView.getSpatialReference();

    }

    //状态监听
    GpsStatus.Listener gpslistener = new GpsStatus.Listener() {
        public void onGpsStatusChanged(int event) {
            switch (event) {
                //第一次定位
                case GpsStatus.GPS_EVENT_FIRST_FIX:
                    Log.i("TAG", "第一次定位");
                    break;
                //卫星状态改变
                case GpsStatus.GPS_EVENT_SATELLITE_STATUS:
                    Log.i("TAG", "卫星状态改变");
                    //获取当前状态
                    GpsStatus gpsStatus=locMag.getGpsStatus(null);
                    //获取卫星颗数的默认最大值
                    int maxSatellites = gpsStatus.getMaxSatellites();
                    //创建一个迭代器保存所有卫星
                    Iterator<GpsSatellite> iters = gpsStatus.getSatellites().iterator();
                    int count = 0;
                    while (iters.hasNext() && count <= maxSatellites) {
                        GpsSatellite s = iters.next();
                        count++;
                    }
                    System.out.println("搜索到："+count+"颗卫星");
                    break;
                //定位启动
                case GpsStatus.GPS_EVENT_STARTED:
                    Log.i("TAG", "定位启动");
                    break;
                //定位结束
                case GpsStatus.GPS_EVENT_STOPPED:
                    Log.i("TAG", "定位结束");
                    break;
            }
        }
    };




    public class MyTouchListener extends MapOnTouchListener
    {

        public MyTouchListener(Context context, MapView view)
        {
        //引用父类方法
            super(context, view);
        }

        @Override
        public boolean onSingleTap(MotionEvent e)
        {
            if (startxuanze)
            {
                float x = e.getX();
                float y = e.getY();
                // gets the first 1 features at the clicked point on the map, within 5 pixels
                ids = featureLayer.getFeatureIDs(x, y, 5, 1);

                if (ids.length > 0)
                {
                    Feature featureselected = featureLayer.getFeature(ids[0]);
                    featureLayer.selectFeatures(ids,true);
                    fid = featureselected.getId();
                    String stringfid = String.valueOf(featureselected.getId());
                    Toast.makeText(getApplicationContext(),
                            "FID=" + stringfid, Toast.LENGTH_SHORT).show();

                } else
                { //提示无选中要素
                    Log.i("", "无选中要素");
                }
            }

            if(startceliangdandian){
                if (celiangtype == Geometry.Type.POLYGON){
                    Point celiangCurrentPoint = mapView.toMapPoint(e.getX(), e.getY());
                    Graphic celiangGraphic = new Graphic(celiangCurrentPoint, new SimpleMarkerSymbol(
                            Color.RED, 6, SimpleMarkerSymbol.STYLE.CIRCLE));
                    int celiangpointid = graphicsLayerPoint.addGraphic(celiangGraphic);

                    if (celiangstartPoint == null)
                    {
                        celiangmultiPath = new Polygon();
                        celiangstartPoint = mapView.toMapPoint(e.getX(), e.getY());
                        //将第一个点存入multiPath
                        celiangmultiPath.startPath((float) celiangstartPoint.getX(), (float) celiangstartPoint.getY());
                        celianguid = graphicsLayeredit.addGraphic(new Graphic(celiangmultiPath, new SimpleLineSymbol(Color.BLUE, 2)));
                    }
                    celiangmultiPath.lineTo((float) celiangCurrentPoint.getX(), (float) celiangCurrentPoint.getY());
                    graphicsLayeredit.updateGraphic(celianguid, celiangmultiPath);
                    caculateceliangmultiPath();
                }

                if (celiangtype == Geometry.Type.POLYLINE){
                    Point celiangCurrentPoint = mapView.toMapPoint(e.getX(), e.getY());
                    Graphic celiangGraphic = new Graphic(celiangCurrentPoint, new SimpleMarkerSymbol(
                            Color.GREEN, 4, SimpleMarkerSymbol.STYLE.CIRCLE));
                    int celiangpointid = graphicsLayerPoint.addGraphic(celiangGraphic);

                    if (celiangstartPoint == null)
                    {
                        celiangmultiPath = new Polyline();
                        celiangstartPoint = mapView.toMapPoint(e.getX(), e.getY());
                        //将第一个点存入multiPath
                        celiangmultiPath.startPath((float) celiangstartPoint.getX(), (float) celiangstartPoint.getY());
                        celianguid = graphicsLayeredit.addGraphic(new Graphic(celiangmultiPath, new SimpleLineSymbol(Color.BLUE, 2)));
                    }
                    celiangmultiPath.lineTo((float) celiangCurrentPoint.getX(), (float) celiangCurrentPoint.getY());
                    graphicsLayeredit.updateGraphic(celianguid, celiangmultiPath);
                    caculateceliangmultiPath();
                }
            }
            return false;
        }


        //    挡在屏幕上滑动时，将滑动生成的点逐步加入poly变量中；
        @Override
        public boolean onDragPointerMove(MotionEvent from, MotionEvent to)
        {

            if (startshouhui)
            {
                /*面绘制*/
                if(drawtype == Geometry.Type.POLYGON)
                {

                    currentPoint = mapView.toMapPoint(to.getX(), to.getY());
                    if (startPoint == null)
                    {

                        multiPath = new Polygon();
                        startPoint = mapView.toMapPoint(from.getX(), from.getY());
                        //将第一个点存入multiPath
                        multiPath.startPath((float) startPoint.getX(), (float) startPoint.getY());
                        uid = graphicsLayeredit.addGraphic(new Graphic(multiPath, new SimpleLineSymbol(Color.BLUE, 2)));
                    }

                    multiPath.lineTo((float) currentPoint.getX(), (float) currentPoint.getY());
                    //                SimpleLineSymbol simpleLineSymbol = new SimpleLineSymbol(Color.BLUE, 2);
                    //增加线点
                    graphicsLayeredit.updateGraphic(uid, multiPath);

                    graphicsLayerPoint.removeAll();
                    points = new ArrayList<Point>();

                    for (int i = 0; i < multiPath.getPointCount(); i++)
                    {
                        points.add(multiPath.getPoint(i));
                    }

                    for (Point pt : points)
                    {
                        graphicp = new Graphic(pt, new SimpleMarkerSymbol(
                                Color.GREEN, 4, SimpleMarkerSymbol.STYLE.CIRCLE));
                        pointid = graphicsLayerPoint.addGraphic(graphicp);
                    }
                    if (startceliang){
                        caculatemultiPath();
                    }
                }
                /*线绘制*/
                if(drawtype == Geometry.Type.POLYLINE){
                    Point currentPoint = mapView.toMapPoint(to.getX(), to.getY());
                    if (startPoint == null)
                    {//判断是否已经存在第一个点
                        multiPathLine = new Polyline();
                        startPoint = mapView.toMapPoint(from.getX(), from.getY());
                        multiPathLine.startPath((float) startPoint.getX(), (float) startPoint.getY());
                        uid = graphicsLayeredit.addGraphic(new Graphic(multiPathLine, new SimpleLineSymbol(Color.BLUE, 2)));
                    }
                    multiPathLine.lineTo((float) currentPoint.getX(), (float) currentPoint.getY());
                    //增加线点
                    graphicsLayeredit.updateGraphic(uid, multiPathLine);

                    graphicsLayerPoint.removeAll();
                    points = new ArrayList<Point>();

                    for (int i = 0; i < multiPathLine.getPointCount(); i++)
                    {
                        points.add(multiPathLine.getPoint(i));
                    }

                    for (Point pt : points)
                    {
                        graphicp = new Graphic(pt, new SimpleMarkerSymbol(
                                Color.RED, 6, SimpleMarkerSymbol.STYLE.CIRCLE));
                        pointid = graphicsLayerPoint.addGraphic(graphicp);
                    }

                    if (startceliang){
                        caculatemultiPath();
                    }
                }

            }
            return false;
        }

        @Override
        public boolean onDragPointerUp(MotionEvent from, MotionEvent to){
            if(startxiubian)
            {
                int i;
                long[] selectid = featureLayer.getSelectionIDs();
                line = (Polyline) graphicsLayeredit.getGraphic(uid).getGeometry();
                gon = (Polygon) featureLayer.getFeature(selectid[0]).getGeometry();
                boolean crosses = CutPolygonL.Crosses(gon, line);
                if (crosses)
                {
                    ArrayList<Polygon> _result = CutPolygonL.Cut(gon, line);
                    geometries = new Geometry[_result.size()];

                    for (i = 0; i < _result.size(); i++)
                    {
                        geometries[i] = _result.get(i);
                    }
                    if (GeometryEngine.simplify(geometries[0], mapView.getSpatialReference()).calculateArea2D() >
                            GeometryEngine.simplify(geometries[1], mapView.getSpatialReference()).calculateArea2D())
                    {
                        graphicsLayeredit.removeAll();
                        graphicsLayerPoint.removeAll();
                        try
                        {
                            geodatabaseFeatureTable.updateFeature(selectid[0],
                                    GeometryEngine.simplify(geometries[0], mapView.getSpatialReference()));
                        } catch (TableException e)
                        {
                            e.printStackTrace();
                        }
                    } else
                    {
                        graphicsLayeredit.removeAll();
                        graphicsLayerPoint.removeAll();
                        try
                        {
                            geodatabaseFeatureTable.updateFeature(selectid[0],
                                    GeometryEngine.simplify(geometries[1], mapView.getSpatialReference()));
                        } catch (TableException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }else {
                    graphicsLayeredit.removeAll();
                    graphicsLayerPoint.removeAll();
                }
                    multiPathLine = null;
                    startPoint = null;
                    currentPoint = null;
                    points = null;
                    gon = null;
                    line = null;

            }
            return false;
        }

    }



    public void btnbianji(View view)
    {
        if (!startxuanze && !startceliang)
        {
            if (!startbianji)
            {
                startbianji = true;
                btnbianji.setSelected(true);
                lbianji.setVisibility(View.VISIBLE);
            } else
            {
                startbianji = false;
                btnbianji.setSelected(false);
                lbianji.setVisibility(View.INVISIBLE);
            }
        }
    }

    public void btnxuanze(View view){
        if (!startbianji && !startceliang)
        {
            if (!startxuanze)
            {
                startxuanze = true;
                btnxuanze.setSelected(true);
                lgongju.setVisibility(View.VISIBLE);
            } else
            {
                startxuanze = false;
                btnxuanze.setSelected(false);
                lgongju.setVisibility(View.INVISIBLE);
                featureLayer.clearSelection();
                startfengge = false;
                startshouhui = false;
                startxiubian = false;
                drawtype = null;
                btnfengge.setSelected(false);
                btnxiubian.setSelected(false);
            }
        }
    }

    public void btnshuxing(View view){
        if (startxuanze && featureLayer.getSelectionIDs() != null
                && featureLayer.getSelectionIDs().length == 1){
            shuxingView();
        }

    }

    public void btnhuitui(View view){
        if(startshouhui && multiPath != null && multiPath.getPointCount()>1)
        {
            int pointCount = multiPath.getPointCount();
            multiPath.removePoint(pointCount - 1);
            graphicsLayerPoint.removeAll();
            points = new ArrayList<Point>();

            for (int i = 0; i < multiPath.getPointCount(); i++) {
                points.add(multiPath.getPoint(i));
            }

            for (Point pt : points)
            {
                graphicp = new Graphic(pt, new SimpleMarkerSymbol(
                        Color.RED, 6, SimpleMarkerSymbol.STYLE.CIRCLE));
                pointid = graphicsLayerPoint.addGraphic(graphicp);
            }
            graphicsLayeredit.updateGraphic(uid, multiPath);
        }
        if (startxuanze && featureLayer.getSelectionIDs() != null
                && featureLayer.getSelectionIDs().length > 0
                && !startxiubian && !startfengge ){
            long[] selectid = featureLayer.getSelectionIDs();
            featureLayer.unselectFeature(selectid[featureLayer.getSelectionIDs().length - 1]);
        }

    }

    public void btnshanchu(View view) throws TableException
    {
        if(startshouhui){
            graphicsLayeredit.removeAll();
            graphicsLayerPoint.removeAll();
            startPoint = null;
            multiPath = null;
            currentPoint = null;
        }
        if (startxuanze && !startfengge && !startxiubian &&
                featureLayer.getSelectionIDs() != null){
            long[] deleteids = featureLayer.getSelectionIDs();
            geodatabaseFeatureTable.deleteFeatures(deleteids);
            startxuanze = false;
            btnxuanze.setSelected(false);
            lgongju.setVisibility(View.INVISIBLE);
        }
        if(startfengge ){
            graphicsLayerPoint.removeAll();
            graphicsLayeredit.removeAll();

        }
    }

    public void btnfangda(View view){
        mapView.zoomin();

    }

    public void btnsuoxiao(View view){
        mapView.zoomout();

    }

    public void btnquantu(View view){
        mapView.zoomToScale(mapView.getCenter(), mapView.getMinScale());
        double scaleq = mapView.getScale();
        long scalelongq = Math.round(scaleq);
        tbilichi.setText("比例尺 = 1 : " + scalelongq);
    }








    public void btnshouhui(View view){
        if(!startshouhui){
            drawtype = Geometry.Type.POLYGON;
            startshouhui = true;
            btnshouhui.setSelected(true);

        }else {
            startshouhui = false;
            btnshouhui.setSelected(false);
            drawtype = null;
        }

    }



    public void btnwancheng(View view) throws TableException
    {
        if(startshouhui && multiPath != null && multiPath.getPointCount()>3){
            List<FeatureTemplate> featureTemplateList= geodatabaseFeatureTable.getFeatureTemplates();
            FeatureTemplate featureTemplate = featureTemplateList.get(0);
            GeodatabaseFeature geodatabaseFeature = geodatabaseFeatureTable.createFeatureWithTemplate(featureTemplate, multiPath);
            long addfid = geodatabaseFeatureTable.addFeature(geodatabaseFeature);
            graphicsLayerPoint.removeAll();
            graphicsLayeredit.removeAll();
            multiPath = null;
            startPoint = null;
            currentPoint = null;
            startshouhui = false;
            btnshouhui.setSelected(false);
            drawtype = null;
            Toast.makeText(getApplicationContext(),
                    "FID=" + addfid, Toast.LENGTH_SHORT).show();

        }

    }

    public void btnfenge(View view) throws TableException
    {
        long[] selectid = featureLayer.getSelectionIDs();
        if(!startfengge && startxuanze && selectid != null && selectid.length == 1){
            drawtype = Geometry.Type.POLYLINE;
            startshouhui = true;
            startfengge = true;
            btnfengge.setSelected(true);
        }else {
            btnfengge.setSelected(false);
            drawtype = null;
            startshouhui = false;
            startfengge = false;

        }

    }

    public void btnhebing(View view) throws TableException
    {   long[] selectid = featureLayer.getSelectionIDs();
        if(startxuanze && selectid != null){
            int i;
            Geometry[] geometries;
            long[] hebingids = featureLayer.getSelectionIDs();
            geometries = new Geometry[hebingids.length];
            for(i=0;i<hebingids.length;i++){
                geometries[i] = GeometryEngine.simplify(featureLayer.getFeature(hebingids[i]).
                        getGeometry(), mapView.getSpatialReference());
            }
            Geometry geometryhebing = GeometryEngine.union(geometries, mapView.getSpatialReference());
            geodatabaseFeatureTable.updateFeature(hebingids[0], geometryhebing);

            if(hebingids.length > 1)
                for(i = 1;i<hebingids.length;i++){
                    geodatabaseFeatureTable.deleteFeature(hebingids[i]);
                }
            featureLayer.clearSelection();
            startxuanze = false;
            btnxuanze.setSelected(false);
        }

    }

    public void btnxiubian(View view){
        long[] selectid = featureLayer.getSelectionIDs();
        if(startxuanze && selectid != null && selectid.length == 1){
            drawtype = Geometry.Type.POLYLINE;
            startshouhui = true;
            startxiubian = true;
            btnxiubian.setSelected(true);
        }

    }

    public void btngudao(View view) throws TableException
    {
        if(startxuanze && featureLayer.getSelectionIDs() != null
                && featureLayer.getSelectionIDs().length == 2){
            long[] selectid = featureLayer.getSelectionIDs();
            Geometry geometryF = GeometryEngine.simplify(featureLayer.getFeature(selectid[0])
                    .getGeometry(), mapView.getSpatialReference());
            Geometry geometryS = GeometryEngine.simplify(featureLayer.getFeature(selectid[1])
                    .getGeometry(), mapView.getSpatialReference());
            boolean contains = GeometryEngine.contains(geometryF, geometryS, mapView.getSpatialReference());
            if(contains && GeometryEngine.simplify(geometryF, mapView.getSpatialReference()).calculateArea2D() >
                    GeometryEngine.simplify(geometryS, mapView.getSpatialReference()).calculateArea2D()){
                Geometry geometrydifference =  GeometryEngine.difference(geometryF, geometryS, mapView.getSpatialReference());
                geodatabaseFeatureTable.updateFeature(selectid[0],geometrydifference);
            }else {
                Geometry geometrydifference =  GeometryEngine.difference(geometryS, geometryF, mapView.getSpatialReference());
                geodatabaseFeatureTable.updateFeature(selectid[1],geometrydifference);
            }
            featureLayer.clearSelection();
            startxuanze = false;
            btnxuanze.setSelected(false);
        }

    }

    public void btntijiao(View view) throws TableException
    {
        if(startxuanze && startfengge && graphicsLayeredit.getGraphic(uid) != null){
            Polygon gonFengge;
            Polyline lineFengge;
            long[] selectid = featureLayer.getSelectionIDs();
            gonFengge = (Polygon) featureLayer.getFeature(selectid[0]).getGeometry();
            lineFengge = (Polyline) graphicsLayeredit.getGraphic(uid).getGeometry();
            boolean crosses = CutPolygonL.Crosses(gonFengge, lineFengge);
            if (crosses)
            {
                ArrayList<Polygon> _result = CutPolygonL.Cut(gonFengge, lineFengge);
                graphicsLayeredit.removeAll();
                graphicsLayerPoint.removeAll();
                List<FeatureTemplate> featureTemplateList = geodatabaseFeatureTable.getFeatureTemplates();
                FeatureTemplate featureTemplate = featureTemplateList.get(0);
                GeodatabaseFeature geodatabaseFeaturef = geodatabaseFeatureTable.createFeatureWithTemplate(featureTemplate, _result.get(0));
                GeodatabaseFeature geodatabaseFeatures = geodatabaseFeatureTable.createFeatureWithTemplate(featureTemplate, _result.get(1));
                long fenggefidf = geodatabaseFeatureTable.addFeature(geodatabaseFeaturef);
                long fenggefids = geodatabaseFeatureTable.addFeature(geodatabaseFeatures);
                Toast.makeText(getApplicationContext(),
                        "FID=" + fenggefidf + "+" + fenggefids, Toast.LENGTH_SHORT).show();
                geodatabaseFeatureTable.deleteFeature(selectid[0]);
                startfengge = false;
                drawtype = null;
                startshouhui = false;
                btnfengge.setSelected(false);
                multiPathLine = null;
                startPoint = null;
                currentPoint = null;
            }else {
                graphicsLayeredit.removeAll();
                graphicsLayerPoint.removeAll();
                multiPathLine = null;
                startPoint = null;
                currentPoint = null;
                btnfengge.setSelected(false);
            }
        }else {
            btnfengge.setSelected(false);
        }

        if(startxuanze && startxiubian){
            startxiubian = false;
            btnxiubian.setSelected(false);

        }
        lgongju.setVisibility(View.INVISIBLE);
        featureLayer.clearSelection();
        startshouhui = false;
        drawtype = null;
        btnxuanze.setSelected(false);
        startxuanze = false;
    }

    @Override
    protected void onResume () {
        /**
         *  获取方向传感器
         *  通过SensorManager对象获取相应的Sensor类型的对象
         */
        Sensor sensor = orientationmanager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        //应用在前台时候注册监听器
        orientationmanager.registerListener(lorientation, sensor,
                SensorManager.SENSOR_DELAY_GAME);
        super.onResume();
    }

    @Override
    protected void onPause () {
        //应用不在前台时候销毁掉监听器
        orientationmanager.unregisterListener(lorientation);
        super.onPause();
    }

    private final class SensorListener implements SensorEventListener
    {

        private float predegree = 0;

        @Override
        public void onSensorChanged(SensorEvent event)
        {
            /**
             *  values[0]: x-axis 方向加速度
             　　 values[1]: y-axis 方向加速度
             　　 values[2]: z-axis 方向加速度
             */
            float degree = event.values[0];// 存放了方向值
            /**动画效果*/
            RotateAnimation animation = new RotateAnimation(predegree, degree,
                    Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            animation.setDuration(200);
            imageView.startAnimation(animation);
            predegree = -degree;

            /**
             float x=event.values[SensorManager.DATA_X];
             float y=event.values[SensorManager.DATA_Y];
             float z=event.values[SensorManager.DATA_Z];
             Log.i("XYZ", "x="+(int)x+",y="+(int)y+",z="+(int)z);
             */
        }

        //精度
        @Override
        public void onAccuracyChanged(Sensor sensor, int accuracy)
        {

        }

    }

    public void shuxingView(){
        // 装载app\src\main\res\layout\login.xml界面布局文件
        View loginForm = getLayoutInflater()
                .inflate(R.layout.shuxing, null);
        Map<String, Object> mapxiang = new HashMap<String, Object>();
        mapxiang.put("002", "伊西哈拉镇");
        mapxiang.put("003", "阿格乡");
        mapxiang.put("004", "牙哈镇");
        mapxiang.put("005", "三道桥乡");
        mapxiang.put("006", "二八台农场");
        mapxiang.put("007", "乌恰镇");
        mapxiang.put("008", "乌尊镇");
        mapxiang.put("009", "比西巴格乡");
        mapxiang.put("010", "阿拉哈格镇");
        mapxiang.put("011", "齐满镇");
        mapxiang.put("012", "墩阔坦镇");
        mapxiang.put("014", "胡杨林管理站");
        mapxiang.put("015", "阿克斯塘乡");
        mapxiang.put("016", "劳改农场");
        mapxiang.put("017", "哈尼哈塔木乡");
        mapxiang.put("020", "库车县林管站");
        mapxiang.put("050", "库车天山国有林管理局");

        final Map<String, Object> mapxiangfan = new HashMap<String, Object>();
        mapxiangfan.put("伊西哈拉镇","002" );
        mapxiangfan.put("阿格乡", "003");
        mapxiangfan.put( "牙哈镇","004");
        mapxiangfan.put("三道桥乡","005" );
        mapxiangfan.put("二八台农场", "006");
        mapxiangfan.put("乌恰镇","007" );
        mapxiangfan.put("乌尊镇","008");
        mapxiangfan.put("比西巴格乡","009");
        mapxiangfan.put("阿拉哈格镇","010" );
        mapxiangfan.put("齐满镇","011" );
        mapxiangfan.put("墩阔坦镇","012" );
        mapxiangfan.put("胡杨林管理站","014");
        mapxiangfan.put("阿克斯塘乡","015" );
        mapxiangfan.put("劳改农场","016" );
        mapxiangfan.put("哈尼哈塔木乡", "017");
        mapxiangfan.put("库车县林管站","020" );
        mapxiangfan.put( "库车天山国有林管理局","050");


        Map<String, Object> mapdimao = new HashMap<String, Object>();
        mapdimao.put("1", "极高");
        mapdimao.put("2", "高山");
        mapdimao.put("3", "中山");
        mapdimao.put("4", "低山");
        mapdimao.put("5", "丘陵");
        mapdimao.put("6", "平原");


        Map<String, Object> mappoxiang= new HashMap<String, Object>();
        mappoxiang.put("1", "北");
        mappoxiang.put("2", "东北");
        mappoxiang.put("3", "东");
        mappoxiang.put("4", "东南");
        mappoxiang.put("5", "南");
        mappoxiang.put("6", "西南");
        mappoxiang.put("7", "西");
        mappoxiang.put("8", "西北");
        mappoxiang.put("9", "无坡");

        Map<String, Object> mappowei= new HashMap<String, Object>();
        mappowei.put("1", "脊");
        mappowei.put("2", "上");
        mappowei.put("3", "中");
        mappowei.put("4", "下");
        mappowei.put("5", "谷");
        mappowei.put("6", "平地");

        Map<String, Object> mappodu= new HashMap<String, Object>();
        mappodu.put("1", "平");
        mappodu.put("2", "缓");
        mappodu.put("3", "斜");
        mappodu.put("4", "陡");
        mappodu.put("5", "急");
        mappodu.put("6", "险");

        Map<String, Object> mapqiyuan = new HashMap<String, Object>();
        mapqiyuan.put("11", "纯天然");
        mapqiyuan.put("12","人工促进");
        mapqiyuan.put("13", "天然萌生");
        mapqiyuan.put("21", "植苗");
        mapqiyuan.put("22", "直播");
        mapqiyuan.put("23", "飞播");
        mapqiyuan.put("24", "人工萌生");

        Map<String, Object> maplinzhong = new HashMap<String, Object>();
        maplinzhong.put("113", "防风固沙林");
        maplinzhong.put("114", "农田牧场防护林");
        maplinzhong.put("115", "护岸林");
        maplinzhong.put("116", "护路林");
        maplinzhong.put("124", "环境保护林");
        maplinzhong.put("251", "果树林");

        Map<String, Object> mapyoushi = new HashMap<String, Object>();
        mapyoushi.put("934000", "怪柳");
        mapyoushi.put("859000", "其他");
        mapyoushi.put("749000", "其他经济树种");
        mapyoushi.put("734000", "石榴");
        mapyoushi.put("710000", "核桃");
        mapyoushi.put("707000", "枣");
        mapyoushi.put("706000", "杏");
        mapyoushi.put("704000", "桃");
        mapyoushi.put("703000", "梨");
        mapyoushi.put("702000", "苹果");
        mapyoushi.put("594100", "法国梧桐");
        mapyoushi.put("593100", "胡杨");
        mapyoushi.put("535000", "柳树");
        mapyoushi.put("530000", "杨树");
        mapyoushi.put("496100", "沙枣");
        mapyoushi.put("496000", "桑树");
        mapyoushi.put("495000", "刺槐");
        mapyoushi.put("460000", "榆树");

        Map<String, Object> mapdilei = new HashMap<String, Object>();
        mapdilei.put("0111", "乔木林");
        mapdilei.put("0120", "疏林地");
        mapdilei.put("0131", "国家特别规定灌木林地");
        mapdilei.put("0141", "未成林造林地");
        mapdilei.put("0150", "苗圃地");
        mapdilei.put("0161", "采伐迹地");
        mapdilei.put("0163", "其他无立木林地");
        mapdilei.put("0171", "宜林荒山荒地");
        mapdilei.put("0172", "宜林沙漠地");
        mapdilei.put("0210", "耕地");
        mapdilei.put("0240", "未利用地");
        mapdilei.put("0250", "建设用地");

        final long[] selectid = featureLayer.getSelectionIDs();
//        final Feature selectfeature = featureLayer.getFeature(selectid[0]);

        String dimao = String.valueOf(mapdimao.get(featureLayer.getFeature(selectid[0]).getAttributeValue("DI_MAO")));
        String poxiang = String.valueOf(mappoxiang.get(featureLayer.getFeature(selectid[0]).getAttributeValue("PO_XIANG")));
        String powei = String.valueOf(mappowei.get(featureLayer.getFeature(selectid[0]).getAttributeValue("PO_WEI")));
        String podu= String.valueOf(mappodu.get(String.valueOf(featureLayer.getFeature(selectid[0]).getAttributeValue("PO_DU"))));
        final String xiang = String.valueOf(mapxiang.get(String.valueOf(featureLayer.getFeature(selectid[0]).getAttributeValue("XIANG"))));
//地貌
        sdimaoming = (Spinner) loginForm.findViewById(R.id.sdimaoming);
        String[] stringdimao = {"极高","高山", "中山", "低山", "丘陵", "平原"};
        // 创建ArrayAdapter对象
        ArrayAdapter<String> adapterdimaoming = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, stringdimao);
        // 为Spinner设置Adapter
        sdimaoming.setAdapter(adapterdimaoming);
        int dmid = adapterdimaoming.getPosition(dimao);
        sdimaoming.setSelection(dmid, true);
//坡位
        spoweiming = (Spinner) loginForm.findViewById(R.id.spoweiming);
        final String[] stringpowei = {"脊","上", "中", "下", "谷", "平地"};
        // 创建ArrayAdapter对象
        ArrayAdapter<String> adapterpowei = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, stringpowei);
        // 为Spinner设置Adapter
        spoweiming.setAdapter(adapterpowei);
        int poweiid = adapterpowei.getPosition(powei);
        spoweiming.setSelection(poweiid, true);
//坡向
        spoxiangming = (Spinner) loginForm.findViewById(R.id.spoxiangming);
        final String[] stringpoxiang = {"北","东北", "东", "东南","南" , "西南", "西", "西北", "无坡"};
        // 创建ArrayAdapter对象
        ArrayAdapter<String> adapterpoxiang = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, stringpoxiang);
        // 为Spinner设置Adapter
        spoxiangming.setAdapter(adapterpoxiang);
        int poxiangid = adapterpoxiang.getPosition(poxiang);
        spoxiangming.setSelection(poxiangid, true);
//坡度
        spoduming = (Spinner) loginForm.findViewById(R.id.spoduming);
        final String[] spodu = {"平","缓", "斜", "陡", "急", "险"};
        // 创建ArrayAdapter对象
        ArrayAdapter<String> adapterpodu = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, spodu);
        // 为Spinner设置Adapter
        spoduming.setAdapter(adapterpodu);
        int poduid = adapterpodu.getPosition(podu);
        spoduming.setSelection(poduid, true);
//乡
        sxiangming = (Spinner) loginForm.findViewById(R.id.sxiangming);
        final String[] stringxiang = {"伊西哈拉镇","阿格乡", "牙哈镇", "三道桥乡", "二八台农场", "乌恰镇"
        ,"乌尊镇","比西巴格乡", "阿拉哈格镇", "齐满镇", "墩阔坦镇", "胡杨林管理站"
                ,"阿克斯塘乡", "劳改农场", "哈尼哈塔木乡", "库车县林管站", "库车天山国有林管理局"};
        // 创建ArrayAdapter对象
        final ArrayAdapter<String> adapterxiang = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_checked, stringxiang);
        // 为Spinner设置Adapter
        sxiangming.setAdapter(adapterxiang);
        int xiangid = adapterxiang.getPosition(xiang);
        sxiangming.setSelection(xiangid, true);



        new AlertDialog.Builder(this)
                // 设置对话框的图标
                //                .setIcon(R.drawable.camera_32)
                // 设置对话框的标题
                .setTitle("属性编辑")
                        // 设置对话框显示的View对象
                .setView(loginForm)
                        // 为对话框设置一个“确定”按钮
                .setPositiveButton("确定", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        long dimaoItemId = sdimaoming.getSelectedItemId() + 1;
                        long poxiangItemId = spoxiangming.getSelectedItemId() + 1;
                        long poweiItemId = spoweiming.getSelectedItemId() + 1;
                        long poduItemId = spoduming.getSelectedItemId() + 1;
//                        乡
                        long xiangItemId = sxiangming.getSelectedItemId();
                        String itemxiang = adapterxiang.getItem((int)xiangItemId);
                        Object objectxiang = mapxiangfan.get(itemxiang);

                        Map<String, Object> attributes =  new HashMap<String, Object>();
                        attributes.put("DI_MAO",String.valueOf(dimaoItemId));
                        attributes.put("PO_WEI",String.valueOf(poweiItemId));
                        attributes.put("PO_XIANG",String.valueOf(poxiangItemId));
                        attributes.put("PO_DU",String.valueOf(poduItemId));
                        attributes.put("XIANG", String.valueOf(objectxiang));

                        try
                        {
                            geodatabaseFeatureTable.updateFeature(selectid[0],attributes);
                        } catch (TableException e)
                        {
                            e.printStackTrace();
                        }
                        featureLayer.clearSelection();
                        btnxuanze.setSelected(false);
                        startxuanze = false;
                        lgongju.setVisibility(View.INVISIBLE);
                    }
                })
                        // 为对话框设置一个“取消”按钮
                .setNegativeButton("取消", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which)
                    {
                        featureLayer.clearSelection();
                        btnxuanze.setSelected(false);
                        startxuanze = false;
                        lgongju.setVisibility(View.INVISIBLE);
                    }
                })
                .create()
                .show();
    }

    public void btnceliangmian(View view) {
        if(!startceliangxian){
            if (!startceliangmian){
                startceliangmian = true;
                btnceliangmian.setSelected(true);
            }else if (!startceliangdandian && !startcelianglianxu){
                startceliangmian = false;
                btnceliangmian.setSelected(false);
            }
        }
    }

    public void btnceliangxian(View view) {
        if(!startceliangmian){
            if (!startceliangxian){
                startceliangxian = true;
                btnceliangxian.setSelected(true);
            }else if (!startceliangdandian && !startcelianglianxu){
                startceliangxian = false;
                btnceliangxian.setSelected(false);
            }
        }
    }

    public void btnceliangdandian(View view) {
        if(startceliangmian){
            if (!startcelianglianxu ){
                if(!startceliangdandian){
                    startceliangdandian = true;
                    btnceliangdandian.setSelected(true);
                    celiangtype = Geometry.Type.POLYGON;
                }else if ( celiangstartPoint == null){
                    startceliangdandian = false;
                    btnceliangdandian.setSelected(false);
                    celiangtype = null;
                }
            }
        }else if (startceliangxian)
        {
            if (!startcelianglianxu)
            {
                if (!startceliangdandian)
                {
                    startceliangdandian = true;
                    btnceliangdandian.setSelected(true);
                    celiangtype = Geometry.Type.POLYLINE;
                } else if (celiangstartPoint == null)
                {
                    startceliangdandian = false;
                    btnceliangdandian.setSelected(false);
                    celiangtype = null;
                }
            }
        }

    }

    public void btncelianglianxu(View view) {
        if (startceliangmian){
            if(!startceliangdandian ){
                if (!startcelianglianxu){
                    startcelianglianxu = true;
                    startshouhui = true;
                    btnceliangliangxu.setSelected(true);
                    drawtype = Geometry.Type.POLYGON;
                }else if(celiangstartPoint == null){
                    startcelianglianxu = false;
                    startshouhui = false;
                    btnceliangliangxu.setSelected(false);
                    drawtype = null;
                }
            }
        }else if (startceliangxian){
            if (!startceliangdandian){
                if(!startcelianglianxu ){
                    startcelianglianxu = true;
                    startshouhui = true;
                    btnceliangliangxu.setSelected(true);
                    drawtype = Geometry.Type.POLYLINE;
                }else if (celiangstartPoint == null){
                    startcelianglianxu = false;
                    startshouhui = true;
                    btnceliangliangxu.setSelected(false);
                    drawtype = null;
                }
            }
        }
    }

    public void btnceliangqingchu(View view) {
        graphicsLayerPoint.removeAll();
        graphicsLayeredit.removeAll();
        celiangmultiPath = null;
        celiangstartPoint = null;
        multiPath = null;
        startPoint = null;
        currentPoint = null;
        tvceliang.setText("");


    }

    public void btnceliangguanbi(View view) {

        graphicsLayeredit.removeAll();
        graphicsLayerPoint.removeAll();
        startceliang = false;
        startceliangxian = false;
        startceliangdandian = false;
        startceliangmian = false;
        startcelianglianxu = false;
        startshouhui = false;

        lceliang.setVisibility(View.INVISIBLE);
        btnceliang.setSelected(false);
        btnceliangdandian.setSelected(false);
        btnceliangliangxu.setSelected(false);
        btnceliangxian.setSelected(false);
        btnceliangmian.setSelected(false);

        celiangstartPoint = null;
        celiangmultiPath = null;
        celiangtype = null;
        celianguid = 0;

        multiPath = null;
        startPoint = null;
        currentPoint = null;
        drawtype = null;
        lceliangshuzhi.setVisibility(View.INVISIBLE);

    }

    public void btnceliang(View view){
        if ( !startbianji && !startxuanze){
            if (!startceliang){
                startceliang = true;
                lceliang.setVisibility(View.VISIBLE);
                btnceliang.setSelected(true);
            }else if (multiPath  == null && celiangmultiPath == null){
                graphicsLayeredit.removeAll();
                graphicsLayerPoint.removeAll();
                lceliang.setVisibility(View.INVISIBLE);
                btnceliang.setSelected(false);

                startceliang = false;
                startceliangxian = false;
                startceliangdandian = false;
                startceliangmian = false;
                startcelianglianxu = false;
                startshouhui = false;

                btnceliangdandian.setSelected(false);
                btnceliangliangxu.setSelected(false);
                btnceliangxian.setSelected(false);
                btnceliangmian.setSelected(false);

                celiangstartPoint = null;
                celiangmultiPath = null;
                celiangtype = null;
                celianguid = 0;

                multiPath = null;
                startPoint = null;
                currentPoint = null;
                drawtype = null;
                lceliangshuzhi.setVisibility(View.INVISIBLE);
            }

        }

    }

    public void btndaohang(View view){

    }

    public void btntuceng(View view){

    }

    public void btnpaizhao(View view){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }

    public void btnshezhi(View view){

    }

    public void caculatemultiPath(){
        if (multiPath != null){
            double mianji;
            double changdu;
            lceliangshuzhi.setVisibility(View.VISIBLE);
            if (multiPath.getType() == Geometry.Type.POLYGON){
                mianji = GeometryEngine.project((GeometryEngine.simplify(multiPath, mapView.getSpatialReference())),
                        mapView.getSpatialReference(), SpatialReference.create(2340 )).calculateArea2D();
                BigDecimal mianjiS = new BigDecimal(mianji);
                mianjiS = mianjiS.setScale(2, BigDecimal.ROUND_HALF_UP);
                tvceliang.setText(String.valueOf(mianjiS) + "平方米");
            }else {
                changdu = GeometryEngine.project((GeometryEngine.simplify(multiPath, mapView.getSpatialReference())),
                        mapView.getSpatialReference(), SpatialReference.create(2340 )).calculateLength2D();
                BigDecimal changduS = new BigDecimal(changdu);
                changduS = changduS.setScale(2, BigDecimal.ROUND_HALF_UP);
                tvceliang.setText(String.valueOf(changduS) + "米");
            }
        }
    }

    public void caculateceliangmultiPath(){
        if (celiangmultiPath != null){
            double mianji;
            double changdu;
            lceliangshuzhi.setVisibility(View.VISIBLE);
            if (celiangmultiPath.getType() == Geometry.Type.POLYGON){
                mianji = GeometryEngine.project((GeometryEngine.simplify(celiangmultiPath, mapView.getSpatialReference())),
                        mapView.getSpatialReference(), SpatialReference.create(2340 )).calculateArea2D();
                BigDecimal mianjiS = new BigDecimal(mianji);
                mianjiS = mianjiS.setScale(2, BigDecimal.ROUND_HALF_UP);
                tvceliang.setText(String.valueOf(mianjiS) + "平方米");
            }else {
                changdu = GeometryEngine.project((GeometryEngine.simplify(celiangmultiPath, mapView.getSpatialReference())),
                        mapView.getSpatialReference(), SpatialReference.create(2340)).calculateLength2D();
                BigDecimal changduS = new BigDecimal(changdu);
                changduS = changduS.setScale(2, BigDecimal.ROUND_HALF_UP);
                tvceliang.setText(String.valueOf(changduS) + "米");
            }
        }
    }

    public void btndingwei(View view){
        if (!startdingwei){
            btndingwei.setSelected(true);
            startdingwei = true;
            //判断GPS是否正常启动
            if (!locMag.isProviderEnabled(LocationManager.GPS_PROVIDER))
            {
                showToast("请开启GPS导航...");
                //返回开启GPS导航设置界面
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, 0);
                return;
            }

            Location location = locMag.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            markLocation(location);
            locMag.addGpsStatusListener(gpslistener);
            locMag.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        }else {
            btndingwei.setSelected(false);
            startdingwei = false;
            toggleGPS();

        }

    }

    private void toggleGPS() {
        Intent gpsIntent = new Intent();
        gpsIntent.setClassName("com.android.settings",
                "com.android.settings.widget.SettingsAppWidgetProvider");
        gpsIntent.addCategory("android.intent.category.ALTERNATIVE");
        gpsIntent.setData(Uri.parse("custom:3"));
        try {
            PendingIntent.getBroadcast(this, 0, gpsIntent, 0).send();
        }
        catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }



    public void btndandian(View view){

    }

    public void btnlianxu(View view){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            String sdStatus = Environment.getExternalStorageState();
            if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd是否可用
                Log.v("TestFile",
                        "SD card is not avaiable/writeable right now.");
                return;
            }

            Bundle bundle = data.getExtras();
            Bitmap bitmap = (Bitmap) bundle.get("data");// 获取相机返回的数据，并转换为Bitmap图片格式
            FileOutputStream b = null;
            //图片名称 时间命名
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            String latlng = "N:" + locx + "E:" +locy;
            String filename = latlng + format.format(date);
            String path = "/sdcard/林地变更/照片/";
            File file = new File(path);
            file.mkdirs();// 创建文件夹
            String fileName = path + filename +".jpg";

            try {
                b = new FileOutputStream(fileName);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    b.flush();
                    b.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bitmap);// 将图片显示在ImageView里
        }
    }



    private void markLocation(Location location)
    {
        if(location!=null){
            Log.i("TAG", "时间："+location.getTime());
            Log.i("TAG", "经度："+location.getLongitude());
            Log.i("TAG", "纬度："+location.getLatitude());
            Log.i("TAG", "海拔："+location.getAltitude());
            locx = location.getLongitude()  ;
            locy = location.getLatitude()  ;
            ShowPointOnMap(locx, locy);
            if (locx != 0 && locy != 0){
                String latitude = ConvertLatlng.convertToSexagesimal(locx);
                String longitude = ConvertLatlng.convertToSexagesimal(locy);
                tvjinweidu.setText("N:" + latitude + " " + "E:" + longitude);
            }
        }
    }

    public void ShowPointOnMap(double lon,double lat){
        //清空定位图层
        gLayerGps.removeAll();
        //接收到的GPS的信号X(lat),Y(lon)
        locx = lon;
        locy = lat;
        gpsPoint = new Point(locx, locy);
        //
        //        Point mapPoint = GeometryEngine.project(locx, locy,SpatialReference.create(2344));
        Point mapPoint = (Point) GeometryEngine.project(gpsPoint, SpatialReference.create(4326), mapView.getSpatialReference());
        //图层的创建
        Graphic graphic = new Graphic(mapPoint,new SimpleMarkerSymbol(Color.BLUE,15, SimpleMarkerSymbol.STYLE.CIRCLE));
        //        PictureMarkerSymbol pms =  new PictureMarkerSymbol(this.getResources().getDrawable(
        //                R.drawable.location32));
        //        Graphic graphic = new Graphic(mapPoint,pms);
        gLayerGps.addGraphic(graphic);

        if (gpsStartPoint == null) {
            gpsLine = new Polyline();
            startPoint = mapPoint;
            gpsLine.startPath((float) startPoint.getX(),
                    (float) startPoint.getY());
            Graphic graphicLine = new Graphic(startPoint,new SimpleLineSymbol(Color.RED,2));
            gLayerGps.addGraphic(graphicLine);
        }
        gpsLine.lineTo((float) mapPoint.getX(), (float) mapPoint.getY());
        gLayerGps.addGraphic(new Graphic(gpsLine,new SimpleLineSymbol(Color.BLACK,2)));
    }


    private void showToast(String msg)
    {
        if(toast == null)
        {
            toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        }
        else
        {
            toast.setText(msg);
            toast.setDuration(Toast.LENGTH_SHORT);
        }
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.show();
    }


}
