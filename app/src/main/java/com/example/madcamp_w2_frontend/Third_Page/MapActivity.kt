package com.example.madcamp_w2_frontend

import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.maps.*
import noman.googleplaces.PlacesListener


class MapActivity: AppCompatActivity(){//, OnMapReadyCallback, PlacesListener, ActivityCompat.OnRequestPermissionsResultCallback {
    /*
    //private lateinit var fusedLocationClient: FusedLocationProviderClient
    var previous_marker: ArrayList<Marker>? = null
    var nearRestaurant:JSONObject? = null
    var nearCafe:JSONObject? = null
    private var mMap: GoogleMap? = null
    private var currentMarker: Marker? = null
    var radius: Int? = null
    var cnt:Int = 0


    var locationCallback : LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            val locationList:List<Location> = locationResult.locations
            if (locationList.size > 0) {
                location = locationList[locationList.size - 1]
                //location = locationList.get(0);
                currentPosition = LatLng(location!!.getLatitude(), location!!.getLongitude())
                val markerTitle: String = getCurrentAddress(currentPosition!!)
                val markerSnippet = "위도:" + location!!.getLatitude()
                    .toString() + " 경도:" + location!!.getLongitude().toString()
                Log.d(TAG, "onLocationResult : $markerSnippet")
                setCurrentLocation(location!!, markerTitle, markerSnippet)
                mCurrentLocatiion = location
            }
        }
    }


    private val TAG = "googlemap_example"
    private val GPS_ENABLE_REQUEST_CODE = 2001
    private val UPDATE_INTERVAL_MS = 1000
    private val FASTEST_UPDATE_INTERVAL_MS = 500

    private val PERMISSIONS_REQUEST_CODE = 100
    var needRequest = false

    var REQUIRED_PERMISSIONS = arrayOf<String>(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    var mCurrentLocatiion: Location? = null
    var currentPosition: LatLng? = null

    private var mFusedLocationClient: FusedLocationProviderClient? = null
    private var locationRequest: LocationRequest? = null
    private var location: Location? = null

    private var mLayout: View? = null


    lateinit var MyLocation: LatLng
    @SuppressLint("MissingPermission")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getWindow().setFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)


        //previous_marker = List<Marker>()
        setContentView(R.layout.googlemaplayer)
        mLayout = layout_main

        //locationRequest = LocationRequest()
        //    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        //   .setInterval(UPDATE_INTERVAL_MS.toLong())

        val builder = LocationSettingsRequest.Builder()

        builder.addLocationRequest(LocationRequest()
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            .setInterval(UPDATE_INTERVAL_MS.toLong())
            .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS.toLong()))

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)

        val mapFragment: SupportMapFragment? = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(this)
        Log.d("Maps", "getMapAsync")
        var intent = getIntent()
        if(intent.hasExtra("radius")){
            radius = intent.getIntExtra("radius", 500)
            Log.d("radius",radius.toString())
        }

        /* Place API */
        //var button = findViewById(R.id.fd_restaurant);
        fd_restaurant?.setOnClickListener {
            showPlaceInformation(MyLocation);

            val restaurantMarker = ArrayList<RestaurantMarker>()

//            val assetManager = resources.assets
//            val inputStream = assetManager.open("Restaurant.json")
//            val jsonString = inputStream.bufferedReader().use{it.readText()}
//            val jObject = JSONObject(jsonString)
//            val jArray = jObject.getJSONArray("Restaurant")

            mFusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
                if(location != null){
                    val MyLocationMarker = MarkerOptions()
                    MyLocation = LatLng(location.latitude, location.longitude)
                    Log.d("MyLocation", MyLocation.toString())
                    MyLocationMarker.position(MyLocation)
                    MyLocationMarker.title("내 위치") // marker name
                    //MyLocationMarker.snippet("되면 좋겠다") // marker specification DO
                    mMap?.addMarker(MyLocationMarker)
                    mMap?.moveCamera(CameraUpdateFactory.newLatLng(MyLocation))
                }
            }

            val jArray = nearRestaurant!!.getJSONArray("results")
            for (i in 0 until jArray.length()){
                val obj = jArray.getJSONObject(i)
                val name = obj.getString("name")
//                val menu = obj.getString("menu")
                val menu = obj.getString("vicinity")
                val geo = obj.getJSONObject("geometry")
                Log.d("geo", geo.toString())
                val coordinate = geo.getJSONObject("location")
                Log.d("geo_location", coordinate.toString())
                val latitude = coordinate.getDouble("lat")
                val longitude = coordinate.getDouble("lng")
                val distance = FloatArray(1)
                Location.distanceBetween(MyLocation.latitude, MyLocation.longitude, latitude, longitude, distance)
//                val latitude = obj.getDouble("latitude")
//                val longitude = obj.getDouble("longitude")
                restaurantMarker.add(RestaurantMarker(name, menu, latitude, longitude, distance[0]))
            }
            val bitmapdraw: BitmapDrawable = resources.getDrawable(R.drawable.green_marker) as BitmapDrawable
            val b: Bitmap = bitmapdraw.bitmap
            val smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false)
            cnt = 0
            for (item:RestaurantMarker in restaurantMarker){
                val restaurantmarker = MarkerOptions()
                restaurantmarker.position(LatLng(item.latitude, item.longitude))
                restaurantmarker.title(item.name)
                restaurantmarker.snippet("${item.distance}m, ${item.menu}")
                //restaurantmarker.icon(url: "")
                mMap?.addMarker(restaurantmarker)

                restaurantmarker.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                cnt++
            }
            Toast.makeText(this, "${cnt}개의 음식집을 탐색하였습니다.", Toast.LENGTH_SHORT).show()
        }


        /*cafe load */
        fd_cafe.setOnClickListener{
            showPlaceInformation(MyLocation);

            val cafeMarker = ArrayList<RestaurantMarker>()


            mFusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
                if(location != null){
                    val MyLocationMarker = MarkerOptions()
                    MyLocation = LatLng(location.latitude, location.longitude)
                    Log.d("MyLocation", MyLocation.toString())
                    MyLocationMarker.position(MyLocation)
                    MyLocationMarker.title("내 위치") // marker name
                    mMap?.addMarker(MyLocationMarker)
                    mMap?.moveCamera(CameraUpdateFactory.newLatLng(MyLocation))
                }
            }


            val jArray = nearCafe!!.getJSONArray("results")
            for (i in 0 until jArray.length()){
                val obj = jArray.getJSONObject(i)
                val name = obj.getString("name")
//                val menu = obj.getString("menu")
                val menu = obj.getString("vicinity")
                val geo = obj.getJSONObject("geometry")
                Log.d("geo", geo.toString())
                val coordinate = geo.getJSONObject("location")
                Log.d("geo_location", coordinate.toString())
                val latitude = coordinate.getDouble("lat")
                val longitude = coordinate.getDouble("lng")
                val distance = FloatArray(1)
                Location.distanceBetween(MyLocation.latitude, MyLocation.longitude, latitude, longitude, distance)
//                val latitude = obj.getDouble("latitude")
//                val longitude = obj.getDouble("longitude")
                cafeMarker.add(RestaurantMarker(name, menu, latitude, longitude, distance[0]))
            }

            val bitmapdraw: BitmapDrawable = resources.getDrawable(R.drawable.green_marker) as BitmapDrawable
            val b: Bitmap = bitmapdraw.bitmap
            val smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false)
            cnt = 0
            for (item:RestaurantMarker in cafeMarker){
                val cafemarker = MarkerOptions()
                cafemarker.position(LatLng(item.latitude, item.longitude))
                cafemarker.title(item.name)
                cafemarker.snippet("${item.distance}m, ${item.menu}")
                mMap?.addMarker(cafemarker)
                cnt++
            }
            Toast.makeText(this, "${cnt}개의 카페를 탐색하였습니다.", Toast.LENGTH_SHORT).show()
        }

    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        mMap = googleMap
        Log.d("Maps", "onMapReady")

        val SEOUL: LatLng = LatLng(37.56, 126.97)
        val markerOptions = MarkerOptions()
        markerOptions.position(SEOUL)
        markerOptions.title("서울")
        markerOptions.snippet("한국의 수도")
        val bitmapdraw: BitmapDrawable = resources.getDrawable(R.drawable.green_marker) as BitmapDrawable
        val b: Bitmap = bitmapdraw.bitmap
        val smallMarker = Bitmap.createScaledBitmap(b, 100, 100, false)
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker))

        mMap!!.addMarker(markerOptions)

        mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(SEOUL, 10F))

        initLocation()

        var hasFineLocationPermission: Int =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        var hasCoarseLocationPermission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED && hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            startLocationUpdates();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    REQUIRED_PERMISSIONS[0]
                )
            ) {
                Snackbar.make(
                    layout_main, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("확인", View.OnClickListener {
                    ActivityCompat.requestPermissions(
                        this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE
                    );
                }).show()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    REQUIRED_PERMISSIONS,
                    PERMISSIONS_REQUEST_CODE
                );
            }
        }

        mMap!!.getUiSettings().setMyLocationButtonEnabled(true)
        mMap!!.animateCamera(CameraUpdateFactory.zoomTo(15F))
        mMap!!.setOnMapClickListener {
            Log.d("onMapClick : ", "onMapClick")
        }




    }



    private fun startLocationUpdates() {
        if (!checkLocationServicesStatus()) {
            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        }else{
            val hasFineLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED   ) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;

            }

            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap!!.setMyLocationEnabled(true);
        }
    }

    override fun onStart(){
        super.onStart()
        Log.d(TAG, "onStart")
        if (checkPermission()) {
            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient!!.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap!=null)
                mMap!!.setMyLocationEnabled(true);

        }
    }

    override fun onStop() {
        super.onStop()
        if(mFusedLocationClient != null){
            Log.d(TAG, "onStop : call stopLocationUpdates")
            mFusedLocationClient!!.removeLocationUpdates(locationCallback)
        }
    }

    fun getCurrentAddress(latlng: LatLng): String {
        val geocoder = Geocoder(this, Locale.getDefault())
        var addresses: List<Address?>
        try { addresses = geocoder.getFromLocation(latlng.latitude, latlng.longitude, 1);
        } catch (ioException: IOException){
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show()
            return "지오코더 서비스 사용불가"
        } catch (illegalArgumentException: IllegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표"
        }
        if (addresses == null || addresses.size == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견"
        } else{
            val address = addresses[0]!!
            return address.getAddressLine(0).toString()
        }

    }

    fun checkLocationServicesStatus(): Boolean {
        val locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var lmManager = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        return lmManager
    }

    fun setCurrentLocation(location: Location, markerTitle: String, markerSnippet:String) {
        if (currentMarker != null) currentMarker!!.remove();
        val currentLatLng = LatLng(location.latitude, location.longitude)
        val markerOptions = MarkerOptions()
        markerOptions.position(currentLatLng)
        markerOptions.title(markerTitle)
        markerOptions.snippet(markerSnippet)
        markerOptions.draggable(true)
        currentMarker = mMap!!.addMarker(markerOptions)

        val cameraUpdate: CameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng)
        mMap!!.moveCamera(cameraUpdate)
    }

    fun setDefaultLocation(){
        val DEFAULT_LOCATION = LatLng(37.56, 126.97)
        val markerTitle = "위치정보 가져올 수 없음"
        val markerSnippet = "위치 퍼미션과 GPS 활성 요부 확인하세요"
        if (currentMarker != null) currentMarker!!.remove()
        val markerOptions = MarkerOptions()
        markerOptions.position(DEFAULT_LOCATION)
        markerOptions.title(markerTitle)
        markerOptions.snippet(markerSnippet)
        markerOptions.draggable(true)
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
        currentMarker = mMap!!.addMarker(markerOptions)

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15F)
        mMap!!.moveCamera(cameraUpdate)

    }

    private fun checkPermission(): Boolean {
        val hasFineLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
        val hasCoarseLocationPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        return if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
            hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED
        ) {
            true
        } else false
    }


    fun onRequestPermissionsResult(//override and String[], Int[]
        permsRequestCode:Int, @NonNull permissions:Array<String>, @NonNull grandResults:Array<Int> ) {
        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.size == REQUIRED_PERMISSIONS.size) {//grandResults.length
            var check_result = true
            for (result in grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false
                    break
                }
            }
            if (check_result) {

                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[0]
                    )
                    || ActivityCompat.shouldShowRequestPermissionRationale(
                        this,
                        REQUIRED_PERMISSIONS[1]
                    )
                ) {
                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(
                        layout_main, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("확인", View.OnClickListener() {
                        //override void onClick(view:View) {
                        finish()
                        //}
                    }).show()
                } else {
                    Snackbar.make(
                        layout_main, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                        Snackbar.LENGTH_INDEFINITE
                    ).setAction("확인", View.OnClickListener() {
                        finish()
                    }).show()
                }
            }
        }
    }

    private fun showDialogForLocationServiceSetting(){
        val builder: AlertDialog.Builder = AlertDialog.Builder(this@MapActivity)
        builder.setTitle("위치 서비스 비활성화")
        builder.setMessage(
            """
                앱을 사용하기 위해서는 위치 서비스가 필요합니다.
                위치 설정을 수정하실래요?
                """.trimIndent()
        )
        builder.setCancelable(true);
        builder.setPositiveButton("설정", DialogInterface.OnClickListener() {dialogInterface, i ->
            //fun onClick(dialog: DialogInterface, id: Int) {
            val callGPSSettingIntent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE)
            //}
        })

        builder.setNegativeButton("취소", DialogInterface.OnClickListener() {dialogInterface, i ->
            //fun onClick(dialog: DialogInterface, id: Int) {
            dialogInterface.cancel()
            //}
        })
        builder.create().show()
    }

    override fun onActivityResult(requestCode:Int, resultCode:Int, data:Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GPS_ENABLE_REQUEST_CODE) {
            //사용자가 GPS 활성 시켰는지 검사
            if (checkLocationServicesStatus()) {
                if (checkLocationServicesStatus()) {

                    Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                    needRequest = true;
                    return;
                }
            }

        }
    }


    @SuppressLint("MissingPermission")
    fun initLocation(){

        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                applicationContext,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        mFusedLocationClient!!.lastLocation.addOnSuccessListener { location ->
            if(location != null){
                val MyLocationMarker = MarkerOptions()
                MyLocation = LatLng(location.latitude, location.longitude)
                Log.d("latlong", location.latitude.toString() + " " + location.longitude.toString())
                Log.d("MyLocation", MyLocation.toString())
                MyLocationMarker.position(MyLocation)
                MyLocationMarker.title("현 위치") // marker name
                //MyLocationMarker.snippet("되면 좋겠다") // marker specification
                mMap?.addMarker(MyLocationMarker)
                mMap?.moveCamera(CameraUpdateFactory.newLatLngZoom(MyLocation, 14F))
            }
        }
    }

    override fun onPlacesFailure(e: PlacesException?) {
        TODO("Not yet implemented")
    }
    fun showPlaceInformation(location:LatLng){
        mMap?.clear()
        if (previous_marker != null)
            previous_marker!!.clear() //지역정보 마커 클리어
        var key = "AIzaSyDzepISLlztmiLEUZOPEaD8qb5AJFZFLXc"

        var restaurantURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=${key}&location=${location.latitude},${location.longitude}&radius=${radius}&type=restaurant"
        var cafeURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?key=${key}&location=${location.latitude},${location.longitude}&radius=${radius}&type=cafe"

        Log.d("res", restaurantURL.toString())
        Log.d("TAG", restaurantURL)

        class UrlParsing_restaurant (var baseUrl: String, var context: Context): Runnable {
                        @Synchronized
                        override fun run() {
                            try{
                                var doc = Jsoup.connect(baseUrl).ignoreContentType(true).get()
                                var elements = doc.select("body")
                                //Log.d("JSON", doc.toString())
                                var result = elements.toString()
                    result = result.replace("<body>","")
                    result = result.replace("</body>","")
                    Log.d("JSON", result)
                    nearRestaurant = JSONObject(result)
                    Log.d("JSON", nearRestaurant.toString())
                }catch (e: Exception){
                    Log.d("TTT",e.toString())
                }
            }

        }

        class UrlParsing_cafe (var baseUrl: String, var context: Context): Runnable {
            @Synchronized
            override fun run() {
                try{
                    var doc = Jsoup.connect(baseUrl).ignoreContentType(true).get()
                    var elements = doc.select("body")
                    //Log.d("JSON", doc.toString())
                    var result = elements.toString()
                    result = result.replace("<body>","")
                    result = result.replace("</body>","")
                    Log.d("JSON", result)
                    nearCafe = JSONObject(result)
                    Log.d("JSON", nearCafe.toString())
                }catch (e: Exception){
                    Log.d("TTT",e.toString())
                }
            }

        }

        var t = Thread(UrlParsing_restaurant(restaurantURL, applicationContext))
        t.start()
        t.join()

        var cafe = Thread(UrlParsing_cafe(cafeURL, applicationContext))
        cafe.start()
        cafe.join()



    }

    override fun onPlacesSuccess(places: List<Place>?) {
        runOnUiThread(Runnable() {
            fun run() {
                for (place in places!!) {
                    Log.d("place", place.name)
                    var latLng = LatLng(place.getLatitude(), place.getLongitude())
                    var markerSnippet: String = getCurrentAddress(latLng)
                    val markerOptions = MarkerOptions()
                    markerOptions.position(latLng) // <- 초기화 작업 필요
                    markerOptions.title(place.getName())
                    markerOptions.snippet(markerSnippet)
                    var item:Marker = mMap!!.addMarker(markerOptions)
                    previous_marker?.add(item)

                }

                //중복 마커 제거
                val hashSet = HashSet<Marker>()
                //HashSet<Marker> hashSet = new HashSet<Marker>();
                previous_marker?.let {
                    hashSet.addAll(it)
                }
                //hashSet.addAll(previous_marker);
                previous_marker?.clear();
                previous_marker?.addAll(hashSet);

            }

        })

    }

    override fun onPlacesFinished() {
    }

    override fun onPlacesStart() {
    }
     */
}
