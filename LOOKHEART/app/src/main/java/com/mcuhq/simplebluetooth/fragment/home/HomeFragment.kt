package com.mcuhq.simplebluetooth.fragment.home

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothGattService
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattDescriptor
import android.bluetooth.BluetoothProfile
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.content.BroadcastReceiver
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.location.Address
import android.location.Geocoder
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.library.KTLibrary.controller.PeakController
import com.library.KTLibrary.viewmodel.SharedViewModel
import com.mcuhq.simplebluetooth.R
import com.mcuhq.simplebluetooth.gps.GpsTracker
import com.mcuhq.simplebluetooth.noti.NotificationManager
import com.mcuhq.simplebluetooth.permission.PermissionManager
import com.mcuhq.simplebluetooth.server.RetrofitServerManager
import com.mcuhq.simplebluetooth.server.UserProfile
import java.io.BufferedReader
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.IOException
import java.sql.Time
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Arrays
import java.util.Date
import java.util.Locale
import java.util.Objects
import java.util.TimeZone
import java.util.UUID

class HomeFragment : Fragment(), PermissionManager.PermissionCallback {
    private var thread: Thread? = null
    private var tickLast: Long = 0

    //endregion
    /*localDataSave Flag variables*/ //region
    private var localDataLoop: Boolean? = null

    //endregion
    /*RetrofitServerManager variables*/ //region
    var retrofitServerManager: RetrofitServerManager? = null

    //endregion
    /*SharedPreferences variables*/ //region
    private var userDetailsSharedPref: SharedPreferences? = null
    private var userDetailsEditor: SharedPreferences.Editor? = null

    //endregion
    /*guardian variables*/ //region
    private var firstGuardian = ""
    private var secondGuardian = ""
    private var firstCheck = false
    private var secondCheck = false
    private var guardianCheck = false
    private var guardianAlertDialog: AlertDialog? = null

    //endregion
    /*FragmentActivity activity*/ //region
    @get:JvmName("Activity")
    var activity: FragmentActivity? = null

    //endregion
    /*GpsTracker variables*/ //region
    private var gpsTracker: GpsTracker? = null

    //endregion
    /*viewModel variables*/ //region
    private var viewModel: SharedViewModel? = null

    //endregion
    /*profileData variables*/ //region
    private var userName: String? = null
    private var email: String? = null
    private var iAge = 0
    private var iGender = 0
    private var dWeight = 0.0
    private var disheight = 0
    private var eCalBPM = 0
    private var sleep = 0
    private var wakeup = 0

    //endregion
    /*notification variables*/ //region
    private var notificationManager: NotificationManager? = null

    //endregion
    /*사용자 설정 FLAG*/ //region
    private var nonContactCheck = true
    private var myoCheck = true
    private var setHeartAttackNotiFlag = false
    private var setArrNotiFlag = false
    private var setMyoNotiFlag = false
    private var setNonContactNotiFlag = false
    private var setTachycardiaNotiFlag = false
    private var setBradycardiaNotiFlag = false
    private var setAtrialFibrillationNotiFlag = false
    private var HeartAttackCheck = false

    //endregion
    /*onBackPressed variables*/ //region
    private val onBackPressedDialog: AlertDialog? = null

    //endregion
    /*ForegroundService variables*/ //region
    private var serviceIntent: Intent? = null

    //endregion
    /*Permissions variables*/ //region
    private var permissionManager: PermissionManager? = null

    //endregion
    /*tenSecondData variables*/ //region
    // Home 왼쪽 상단에 표시되는 데이터의 변수
    // BPM
    private var tenSecondAvgBPM = 0
    private var tenSecondAvgBpmCnt = 0
    private var tenSecondBpmSum = 0
    private var tenMinuteAvgBPM = 0
    private var tenMinuteAvgBpmCnt = 0
    private var tenMinuteBpmSum = 0
    private var diffAvgBPM = 0
    private var diffAvgFlag: Boolean = false

    // Min BPM
    private var minBPM = 0
    private var diffMinBPM = 0
    private var diffMinFlag: Boolean = false

    // Max BPM
    private var maxBPM = 0
    private var diffMaxBPM = 0
    private var diffMaxFlag: Boolean = false

    // HRV
    private var tenSecondHRVSum = 0
    private var tenSecondAvgHRV = 0

    // TEMP
    private var tenSecondTempSum = 0.0
    private var tenSecondAvgTemp = 0.0

    // 10초 동안 쌓이는 데이터
    private var tenSecondStep = 0
    private var tenSecondArrCnt = 0
    private var tenSecondCal = 0.0
    private var tenSecondECal = 0.0
    private var tenSecondDistance = 0.0
    private var tenSecondDistanceKM = 0.0
    private var tenSecondDistanceM = 0.0

    //endregion
    /*HourlyData variables*/ //region
    // 매시간 저장되는 변수
    private var hourlyAllstep = 0
    private var hourlyDistance = 0.0
    private var hourlyTotalCal = 0.0
    private var hourlyExeCal = 0.0
    private var hourlyArrCnt = 0

    // 10초 마다 서버로 보내는 시간대 별 데이터 (sendCalcDataAsCsv)
    private var sendHourlyStep: String? = null
    private var sendHourlyDistance: String? = null
    private var sendHourlyTCal: String? = null
    private var sendHourlyECal: String? = null
    private var sendHourlyArrCnt: String? = null

    //endregion
    /*dailyData variables*/ //region
    private var allstep = 0
    private var distance = 0.0
    private var wdistance = 0.0
    private var arrCnt = 0
    private var dCal = 0.0
    private var dExeCal = 0.0

    // doLoop()
    @Volatile
    private var isRun = false
    private var dCalMinU = 0.0
    private var iCalTimer = 0
    private var iCalTimeCount = 0
    private var pBPM = 0.0
    private var cBPM = 0.0
    private var realBPM = 0.0
    private var dpscnt = 0
    private var dps = 0f
    private var realDistanceKM = 0.0
    private var realDistanceM = 0.0
    private var avgsize = 0.0
    private var nowstep = 0
    private var bodyStatus = "" // 휴식, 활동, 수면

    //endregion
    /*ecgData variables*/ //region
    // 14개 씩 들어오는 ecg 데이터를 10개 씩 모아서 서버로 보내기 위한 변수 ( 140개 )
    private val ecgPacket = DoubleArray(14)
    private val peackPacket = DoubleArray(14)
    private var ecgPacketList = StringBuilder()
    private var ecgPacketCnt = 0
    private var ecgCnt = 0

    //endregion
    /*doBufProcess() variables*/ //region
    // BLE Data 저장
    private val bluetoothData = DoubleArray(_MAX_CH)
    private val lVName = arrayOfNulls<String>(_MAX_CH) // 확인 후 삭제 필요
    private var intHRV = 0
    private var intBPM = 0
    private var doubleBAT = 0.0
    private var doubleTEMP = 0.0
    private var stringHRV: String? = null
    private var stringBPM: String? = null
    private var stringArr: String? = null
    private var stringArrCnt: String? = null
    private var intRealBPM = 0
    private var stringRealBPM: String? = null
    private var stringRealHRV: String? = null

    // BLE ECG DATA VAR
    private val dRecv = DoubleArray(500)
    private val dRecvLast = DoubleArray(500)
    private var iRecvCnt = 0
    private var iRecvLastCnt = 0

    //endregion
    /*Arr variables*/ //region
    private var arr = 0
    private val retryCounts: MutableMap<String, Int> = HashMap()
    private var arrStatus = ""
    private var currentArrCnt = 0
    private var previousArrCnt = 0

    //endregion
    /*DateAndTime variables*/ //region
    private var currentYear: String? = null
    private var currentMonth: String? = null
    private var currentDay: String? = null
    private var currentHour: String? = null
    private var currentDate: String? = null
    private var currentTime: String? = null
    private var currentMinute: String? = null
    private var currentSecond: String? = null
    private var pre_time_Hour = ""
    private var pre_date_pause = ""
    private var mDate: Date? = null
    private var mTime: Time? = null

    // utc and countryCode
    private var currentCountry: String? = null
    private var utcOffset: String? = null
    private var utcOffsetAndCountry: String? = null

    //endregion
    /*BLE variables*/ //region
    private val stateFilter: IntentFilter = IntentFilter() // BLE STATE FILTER
    private var mBluetoothGattService: BluetoothGattService? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var btManager: BluetoothManager? = null
    private var btAdapter: BluetoothAdapter? = null
    private var btScanner: BluetoothLeScanner? = null
    private var deviceMacAddress: String? = null
    private val devicesDiscovered: ArrayList<BluetoothDevice> = ArrayList()
    private var lTx: MutableList<String> = ArrayList()
    private val isBTType = 1 // 0=BTClassic 1=BLE
    private val bleHandler: Handler? = null
    private val handler2 = Handler()
    private val mConnectedThread: ConnectedThread? =
        null // bluetooth background worker thread to send and receive data
    private var BTConnected = false
    private var firstBleConnect = false
    private var mConnectionState = 0
    private var useBleFlag = false // 장치 사용 플래그

    //endregion
    /*layout variables*/ //region
    private var view: View? = null
    private var chart: LineChart? = null

    // 왼쪽 상단 view
    private var bpm_value: TextView? = null
    private var bpm_maxValue: TextView? = null
    private var bpm_diffMaxValue: TextView? = null
    private var bpm_avgValue: TextView? = null
    private var bpm_diffAvgValue: TextView? = null
    private var bpm_minValue: TextView? = null
    private var bpm_diffMinValue: TextView? = null
    private var hrv_value: TextView? = null
    private var arr_value: TextView? = null
    private var eCal_value: TextView? = null
    private var step_value: TextView? = null
    private var temp_value: TextView? = null
    private var distance_value: TextView? = null

    // body state var
    private var restText: TextView? = null
    private var sleepText: TextView? = null
    private var exerciseText: TextView? = null
    private var restImg: ImageView? = null
    private var sleepImg: ImageView? = null
    private var exerciseImg: ImageView? = null
    private var restBackground: LinearLayout? = null
    private var sleepBackground: LinearLayout? = null
    private var exerciseBackground: LinearLayout? = null

    // TEST
    private var testButton: FrameLayout? = null
    private var ecgCntTest = 0
    private var ecgCheckTest = false

    //endregion
    var peackCtrl: PeakController = PeakController()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_home, container, false)

        // init And setup
        initializeMemberVariables()
        setupSharedPreferences()
        initializeVariables()
        setupBluetooth()
        initializeUIComponents()
        initializeServicesAndUtilities()
        startContinuousTimeUpdates() // current date And time update, localData Save func
        loadUserProfile() // Server Task : getProfile
        chartInit()
        timeZone()
        notiCheck()
        permissionCheck()
        startForegroundService()
        setupBackPressHandler()
        setViewModel()
        setupTestButton() // TEST
        return view
    }

    private fun setupTestButton() {
        testButton?.setOnClickListener(View.OnClickListener {
            if (ecgCntTest == 3) {
                ecgChange(ecgCheckTest)
                ecgCntTest = 0
            }
            ecgCntTest++
            ecgCheckTest = !ecgCheckTest
        })
    }

    private fun initializeMemberVariables() {
        activity = getActivity()
    }

    private fun setupSharedPreferences() {
        val emailSharedPreferences: SharedPreferences =
            activityOrThrow?.getSharedPreferences("User", Context.MODE_PRIVATE)!!
        email = emailSharedPreferences.getString("email", "null")
        userDetailsSharedPref = activityOrThrow?.getSharedPreferences(email, Context.MODE_PRIVATE)
        userDetailsEditor = userDetailsSharedPref?.edit()
        guardianCheck = userDetailsSharedPref?.getBoolean("guardian", false)!!
    }

    private fun setupBluetooth() {
        useBleFlag = false

        // BLE 상태 요소
        stateFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED)
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED) // 연결 확인
        stateFilter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED) // 연결 끊김 확인
        stateFilter.addAction(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED)
        stateFilter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED)
        stateFilter.addAction(BluetoothDevice.ACTION_FOUND) // 기기 검색됨
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED) // 기기 검색 시작
        stateFilter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED) // 기기 검색 종료
        stateFilter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST)

        // BLE 상태 변화 탐지
        activityOrThrow?.registerReceiver(mBluetoothStateReceiver, stateFilter)

        // BLE Service 제어, 관리
        btManager = activityOrThrow?.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        btAdapter = btManager?.getAdapter()
    }

    private fun initializeUIComponents() {
        bpm_value = view?.findViewById<TextView>(R.id.bpm_Value)
        bpm_maxValue = view?.findViewById<TextView>(R.id.bpm_maxValue)
        bpm_diffMaxValue = view?.findViewById<TextView>(R.id.diffMaxBPM)
        bpm_avgValue = view?.findViewById<TextView>(R.id.bpm_avgValue)
        bpm_diffAvgValue = view?.findViewById<TextView>(R.id.diffAvg)
        bpm_minValue = view?.findViewById<TextView>(R.id.bpm_minValue)
        bpm_diffMinValue = view?.findViewById<TextView>(R.id.diffMinBPM)
        hrv_value = view?.findViewById<TextView>(R.id.HRV_Value)
        arr_value = view?.findViewById<TextView>(R.id.arr_value)
        eCal_value = view?.findViewById<TextView>(R.id.eCal_Value)
        step_value = view?.findViewById<TextView>(R.id.step_Value)
        temp_value = view?.findViewById<TextView>(R.id.temp_Value)
        distance_value = view?.findViewById<TextView>(R.id.distance_Value)
        exerciseImg = view?.findViewById<ImageView>(R.id.exerciseImg)
        exerciseText = view?.findViewById<TextView>(R.id.exerciseText)
        exerciseBackground = view?.findViewById<LinearLayout>(R.id.exercise)
        restImg = view?.findViewById<ImageView>(R.id.restImg)
        restText = view?.findViewById<TextView>(R.id.restText)
        restBackground = view?.findViewById<LinearLayout>(R.id.rest)
        sleepImg = view?.findViewById<ImageView>(R.id.sleepImg)
        sleepText = view?.findViewById<TextView>(R.id.sleepText)
        sleepBackground = view?.findViewById<LinearLayout>(R.id.sleep)
        testButton = view?.findViewById<FrameLayout>(R.id.testButton)
    }

    private fun initializeServicesAndUtilities() {
        serviceIntent = Intent(safeGetActivity(), ForegroundService::class.java)
        //        ecgPacketList = new StringBuilder();
        retrofitServerManager = RetrofitServerManager.getInstance()
        gpsTracker = GpsTracker(safeGetActivity()!!)
    }

    fun setViewModel() {
        viewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // userData
        viewModel?.getGender()?.observe(viewLifecycleOwner) { genderValue ->
            iGender = if (genderValue.equals("남자")) 1 else 2
        }
        viewModel?.getAge()?.observe(viewLifecycleOwner) { string -> iAge = string.toInt() }
        viewModel?.getHeight()?.observe(viewLifecycleOwner) { string -> disheight = string.toInt() }
        viewModel?.getWeight()?.observe(viewLifecycleOwner) { string -> dWeight = string.toInt().toDouble() }
        viewModel?.getSleep()?.observe(viewLifecycleOwner) { string -> sleep = string.toInt() }
        viewModel?.getWakeup()?.observe(viewLifecycleOwner) { string -> wakeup = string.toInt() }
        viewModel?.getBpm()?.observe(viewLifecycleOwner) { string -> eCalBPM = string.toInt() }

        // notification
        viewModel?.getEmergency()
            ?.observe(viewLifecycleOwner) { check -> setHeartAttackNotiFlag = check }
        viewModel?.getArr()?.observe(viewLifecycleOwner) { check -> setArrNotiFlag = check }
        viewModel?.getMyo()?.observe(viewLifecycleOwner) { check -> setMyoNotiFlag = check }
        viewModel?.getNonContact()
            ?.observe(viewLifecycleOwner) { check -> setNonContactNotiFlag = check }
        viewModel?.fastArr
            ?.observe(viewLifecycleOwner) { check -> setTachycardiaNotiFlag = check }
        viewModel?.getSlowarr()
            ?.observe(viewLifecycleOwner) { check -> setBradycardiaNotiFlag = check }
        viewModel?.getIrregular()
            ?.observe(viewLifecycleOwner) { check -> setAtrialFibrillationNotiFlag = check }
    }

    fun setupBackPressHandler() {
        val callback: OnBackPressedCallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                val activity: Activity? = getActivity() // null 가능성이 있는 참조
                if (activity != null) { // 액티비티가 여전히 유효한지 확인
                    AlertDialog.Builder(activity)
                        .setTitle(resources.getString(R.string.noti))
                        .setMessage(resources.getString(R.string.exit))
                        .setNegativeButton(resources.getString(R.string.rejectLogout), null)
                        .setPositiveButton(
                            resources.getString(R.string.exit2),
                            object : DialogInterface.OnClickListener {
                                override fun onClick(arg0: DialogInterface, arg1: Int) {
                                    activity.finish() // 액티비티 종료
                                }
                            })
                        .create()
                        .show()
                }
            }
        }
        if (safeGetActivity() != null) {
            activityOrThrow?.onBackPressedDispatcher?.addCallback(viewLifecycleOwner, callback)
        }
    }

    fun startForegroundService() {
        activityOrThrow?.startService(serviceIntent)
    }

    fun stopService() {
        activityOrThrow?.stopService(serviceIntent)
    }

    private fun localDataSave() {
        // 1초 마다 내부에 저장

        // 현재 시간
        val now = System.currentTimeMillis()
        val current_Date = Date(now)
        val date = SimpleDateFormat("yyyy-MM-dd")
        val stringCurrentDate = date.format(current_Date)
        pre_date_pause = userDetailsSharedPref?.getString("preDate", "2023-01-01")!!
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val checkCurrentDate = LocalDate.parse(stringCurrentDate, formatter)
        val checkPreDate = LocalDate.parse(pre_date_pause, formatter)

        // 날짜 비교
        if (checkCurrentDate.isEqual(checkPreDate)) {
            // 날짜가 같음 (기존 데이터 유지)
            userDetailsEditor?.putString("distance", distance.toString())
            userDetailsEditor?.putString("cal", dCal.toString())
            userDetailsEditor?.putString("execal", dExeCal.toString())
            userDetailsEditor?.putString("allstep", allstep.toString())
            userDetailsEditor?.putString("arr", arrCnt.toString())
            userDetailsEditor?.putString("currentDate", currentDate)
            userDetailsEditor?.putString("hour", currentHour)

            // 시간대 별 저장 데이터(1시간)
            if (currentMinute == "00" && currentSecond == "00") {
                // 초기화
                userDetailsEditor?.putInt("hourlyArrCnt", arrCnt)
                userDetailsEditor?.putInt("hourlyAllStep", allstep)
                userDetailsEditor?.putFloat("hourlyDistance", (distance / 100).toFloat())
                userDetailsEditor?.putFloat("hourlyTotalCal", dCal.toFloat())
                userDetailsEditor?.putFloat("hourlyExeCal", dExeCal.toFloat())
                userDetailsEditor?.putString("preHour", currentHour)
                userDetailsEditor?.apply()
                hourlyArrCnt = userDetailsSharedPref?.getInt("hourlyArrCnt", 0)!!
                hourlyAllstep = userDetailsSharedPref?.getInt("hourlyAllStep", 0)!!
                hourlyDistance = userDetailsSharedPref?.getFloat("hourlyDistance", 0f)?.toDouble()!!
                hourlyTotalCal = userDetailsSharedPref?.getFloat("hourlyTotalCal", 0f)?.toDouble()!!
                hourlyExeCal = userDetailsSharedPref?.getFloat("hourlyExeCal", 0f)?.toDouble()!!
            }
        } else {
            // 날짜가 다른 경우(초기화)
            userDetailsEditor?.putString("distance", "0")
            userDetailsEditor?.putString("cal", "0")
            userDetailsEditor?.putString("execal", "0")
            userDetailsEditor?.putString("allstep", "0")
            userDetailsEditor?.putString("arr", "0")

            // 시간대 별 저장 데이터(1시간)
            userDetailsEditor?.putInt("hourlyArrCnt", 0)
            userDetailsEditor?.putInt("hourlyAllStep", 0)
            userDetailsEditor?.putFloat("hourlyDistance", 0f)
            userDetailsEditor?.putFloat("hourlyTotalCal", 0f)
            userDetailsEditor?.putFloat("hourlyExeCal", 0f)

            // date And time
            userDetailsEditor?.putString("preDate", currentDate)
            userDetailsEditor?.putString("preHour", currentHour)
            userDetailsEditor?.putString("currentDate", currentDate)
            userDetailsEditor?.apply()
            hourlyArrCnt = userDetailsSharedPref?.getInt("hourlyArrCnt", 0)!!
            hourlyAllstep = userDetailsSharedPref?.getInt("hourlyAllStep", 0)!!
            hourlyDistance = userDetailsSharedPref?.getFloat("hourlyDistance", 0f)?.toDouble()!!
            hourlyTotalCal = userDetailsSharedPref?.getFloat("hourlyTotalCal", 0f)?.toDouble()!!
            hourlyExeCal = userDetailsSharedPref?.getFloat("hourlyExeCal", 0f)?.toDouble()!!
            distance = 0.0
            dCal = 0.0
            dExeCal = 0.0
            allstep = 0
            arrCnt = 0
        }
        userDetailsEditor?.apply()
    }

    // BPM 데이터 저장
    fun saveBpmDataAsCsv() {
        if (tenSecondAvgBPM == 0) {
            tenSecondAvgBPM = 70
        }
        try {
            val directory =
                getFileDirectory("LOOKHEART/$email/$currentYear/$currentMonth/$currentDay")
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // 소수점 제거
            val decimalFormat = DecimalFormat("#.##")
            tenSecondAvgTemp = decimalFormat.format(tenSecondAvgTemp).toDouble()
            val file = File(directory, "BpmData.csv")

            // 시간, bpm, temp, hrv
            val fos = FileOutputStream(file, true) // 'true' to append
            val csvData =
                "$currentTime,$utcOffsetAndCountry,$tenSecondAvgBPM,$tenSecondAvgTemp,$tenSecondAvgHRV\n"
            fos.write(csvData.toByteArray())
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // 시간 별 데이터 저장( 걸음수, 걸음거리, 활동 칼로리, 전체 칼로리, 부정맥 횟수)
    fun saveCalAndDistanceAsCsv() {
        try {

            // 현재 hour 값(정수)
            val intCurrentHour = currentHour!!.toInt()
            // 이전 hour 값(정수)
            val intPreHour: Int

            // 경로
            val directory =
                getFileDirectory("LOOKHEART/$email/$currentYear/$currentMonth/$currentDay")

            // 디렉토리가 없는 경우 생성
            if (!directory.exists()) {
                directory.mkdirs()
            }

            // 파일 경로와 이름
            val file = File(directory, "CalAndDistanceData.csv")

            // array : 시간, 걸음, 거리, 총칼로리, 활동칼로리, 비정상 맥박 횟수
            if (file.exists()) {
                // 파일이 있는 경우

                // 기존 데이터를 저장하는 변수와 배열
                var line = ""
                val lines: MutableList<String> = ArrayList()
                // 파일 읽기
                val reader = BufferedReader(FileReader(file))

                // 기존 데이터 저장
                while (reader.readLine().also { line = it } != null) {
                    lines.add(line)
                }
                reader.close()
                val lastLine = lines[lines.size - 1] // 마지막 행
                val values: Array<String?> =
                    lastLine.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray() // 행 구분

                // 정수로 변환
                intPreHour = values[0]!!.toInt()
                if (intPreHour == intCurrentHour) {
                    // 같은 시간인 경우 ( 덮어쓰기 )
                    // 파일 쓰기
                    val fos = FileOutputStream(file, false)
                    val intDailyTotalCal = (dCal - Math.round(hourlyTotalCal * 10) / 10.0).toInt()
                    val intDailyExeCal = (dExeCal - Math.round(hourlyExeCal * 10) / 10.0).toInt()
                    //                    int intDailyExeCal = (int) dCal;
                    val intdailyDistanceM = Math.round(distance / 100 - hourlyDistance).toInt()
                    values[0] = intCurrentHour.toString() // 시간
                    values[1] = utcOffsetAndCountry // utc Offset / 국가 코드
                    values[2] = (allstep - hourlyAllstep).toString() // 걸음
                    values[3] = intdailyDistanceM.toString() // 거리
                    values[4] = intDailyTotalCal.toString() // 총 칼로리
                    values[5] = intDailyExeCal.toString() // 활동 칼로리
                    values[6] = (arrCnt - hourlyArrCnt).toString() // 비정상맥박 횟수
                    sendHourlyStep = values[2]
                    sendHourlyDistance = values[3]
                    sendHourlyTCal = values[4]
                    sendHourlyECal = values[5]
                    sendHourlyArrCnt = values[6]
                    currentArrCnt = sendHourlyArrCnt!!.toInt()
                    if (shouldNotify(previousArrCnt, currentArrCnt, 10) ||
                        shouldNotify(previousArrCnt, currentArrCnt, 20) ||
                        shouldNotify(previousArrCnt, currentArrCnt, 30) ||
                        shouldNotify(previousArrCnt, currentArrCnt, 50)
                    ) {
                        runOnActivityUiThread { hourlyArrEvent(currentArrCnt) }
                    }
                    previousArrCnt = currentArrCnt // 현재 값을 이전 값으로 업데이트
                    lines[lines.size - 1] = java.lang.String.join(",", *values) // 저장

                    // 파일에 다시 쓰기
                    for (writeLine in lines) {
                        fos.write(
                            """$writeLine
""".toByteArray()
                        )
                    }
                    fos.close()
                } else {
                    // 파일 쓰기 ( 줄바꿈 )
                    val fos = FileOutputStream(file, true)
                    val preHour = values[0]!!.toInt() + 1

                    // 비어있는 시간(행) 채우기
                    // 이전 시간 값(+1)부터 채우기
                    var i = preHour
                    while (intCurrentHour >= i) {
                        val csvData = "$i,$utcOffsetAndCountry,0,0,0,0,0\n"
                        fos.write(csvData.toByteArray())
                        i++
                    }
                    fos.close()
                }
            } else {
                // 파일이 없는 경우 ( 이전 시간 데이터 값 채우기 )
                val fos = FileOutputStream(file, false)
                var i = 0
                while (intCurrentHour >= i) {
                    val csvData = "$i,$utcOffsetAndCountry,0,0,0,0,0\n"
                    fos.write(csvData.toByteArray())
                    i++
                }
                fos.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun saveArrEcgDataAsCsv() {
        val filename = ""
        try {
            val directory =
                getFileDirectory("LOOKHEART/$email/$currentYear/$currentMonth/$currentDay/arrEcgData")
            if (!directory.exists()) {
                directory.mkdirs()
            }
            val file = File(directory, "arrEcgData_$arrCnt.csv")

            // 앞 뒤 "[" "]" 제거
            var strArrEcgData = Arrays.toString(dRecvLast)
            strArrEcgData = strArrEcgData.replace("[", "").replace("]", "")

            // arr Fragment update
            viewModel?.addArrList(currentTime!!)

//            Log.d("strEcgData", strArrEcgData);
            val fos = FileOutputStream(file, true) // 'true' to append
            val csvData =
                "$currentTime,$utcOffsetAndCountry,$bodyStatus,$arrStatus,$strArrEcgData\n"
            fos.write(csvData.toByteArray())
            fos.close()

            // serverTask
            sendArrData(currentDate, currentTime, arrCnt.toString(), csvData, arrStatus)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    fun arrAction() {
        Thread { saveArrEcgDataAsCsv() }.start()
    }

    //    public void ecgAction(int bpm, StringBuilder ecgList) {
    //        sendEcgData(bpm, ecgList);
    //        ecgPacketList.clear();
    //        ecgPacketCnt = 0;
    //    }
    fun ecgAction(bpm: Int, ecgList: StringBuilder?) {
        sendEcgData(bpm, ecgList)
        ecgPacketList = StringBuilder()
        leng = 0
        ecgPacketCnt = 0
    }

    fun initializeVariables() {

        // init
        lVName[0] = "ECG"
        lVName[1] = "BPM" // lV[1];
        lVName[2] = "TEMP"
        lVName[3] = "STEP"
        lVName[4] = "HRV"
        lVName[5] = "Arr"
        stringRealBPM = "70"
        stringRealHRV = "0"
        intRealBPM = 70
        minBPM = 70
        maxBPM = 70

        // csv 파일 저장 bpm
        tenSecondAvgBPM = 70
        tenSecondAvgBpmCnt = 0
        tenSecondBpmSum = 0

        // csv 파일 저장 HRV
        tenSecondAvgHRV = 0
        tenSecondHRVSum = 0

        // csv 파일 저장 temp
        tenSecondAvgTemp = 0.0
        tenSecondTempSum = 0.0

        // ui 표현 bpm
        tenMinuteAvgBPM = 70
        tenMinuteAvgBpmCnt = 0
        tenMinuteBpmSum = 0
        intHRV = 0
        Arrays.fill(bluetoothData, 0.0)

        // 현재 시간
        val now = System.currentTimeMillis()
        val current_Date = Date(now)
        val date = SimpleDateFormat("yyyy-MM-dd")
        val hour = SimpleDateFormat("HH")
        val stringCurrentDate = date.format(current_Date)
        val stringCurrentHour = hour.format(current_Date)

        // 이전 시간
        pre_date_pause = userDetailsSharedPref?.getString("preDate", "2023-01-01")!!
        pre_time_Hour = userDetailsSharedPref?.getString("preHour", "0")!!
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        val currentDate = LocalDate.parse(stringCurrentDate, formatter)
        val preDate = LocalDate.parse(pre_date_pause, formatter)
        Log.i("currentDate", "currentDate : $currentDate")
        Log.i("preDate", "preDate : $preDate")

        // 날짜 비교
        if (currentDate.isEqual(preDate)) {
            Log.i("DateCompare", "-------------- same --------------")
            Log.i("DateCompare", "CurrentDate : $currentDate PreDate : $preDate")

            // 날짜가 같은 경우(기존 데이터 유지)
            val sdistance: String = userDetailsSharedPref?.getString("distance", "0")!!
            val sdCal: String = userDetailsSharedPref?.getString("cal", "0")!!
            val sdExeCal: String = userDetailsSharedPref?.getString("execal", "0")!!
            val sallstep: String = userDetailsSharedPref?.getString("allstep", "0")!!
            val sarrCnt: String = userDetailsSharedPref?.getString("arr", "0")!!
            distance = sdistance.toDouble()
            dCal = sdCal.toDouble()
            dExeCal = sdExeCal.toDouble()
            allstep = sallstep.toInt()
            arrCnt = sarrCnt.toInt()
            hourlyArrCnt = userDetailsSharedPref?.getInt("hourlyArrCnt", 0)!!
            hourlyAllstep = userDetailsSharedPref?.getInt("hourlyAllStep", 0)!!
            hourlyDistance = userDetailsSharedPref?.getFloat("hourlyDistance", 0f)?.toDouble()!!
            hourlyTotalCal = userDetailsSharedPref?.getFloat("hourlyTotalCal", 0f)?.toDouble()!!
            hourlyExeCal = userDetailsSharedPref?.getFloat("hourlyExeCal", 0f)?.toDouble()!!
            if (stringCurrentHour != pre_time_Hour) {
                Log.i("DateCompare", "-------------- different --------------")
                Log.i("HourCompare", "CurrentHour : $stringCurrentHour PreHour : $pre_time_Hour")

                // hour 변경 시 기존 데이터 저장 및 초기화
                userDetailsEditor?.putInt("hourlyArrCnt", arrCnt)
                userDetailsEditor?.putInt("hourlyAllStep", allstep)
                userDetailsEditor?.putFloat("hourlyDistance", (distance / 100).toFloat())
                userDetailsEditor?.putFloat("hourlyTotalCal", dCal.toFloat())
                userDetailsEditor?.putFloat("hourlyExeCal", dExeCal.toFloat())
                userDetailsEditor?.commit()
                hourlyArrCnt = userDetailsSharedPref?.getInt("hourlyArrCnt", 0)!!
                hourlyAllstep = userDetailsSharedPref?.getInt("hourlyAllStep", 0)!!
                hourlyDistance = userDetailsSharedPref?.getFloat("hourlyDistance", 0f)!!.toDouble()
                hourlyTotalCal = userDetailsSharedPref?.getFloat("hourlyTotalCal", 0f)!!.toDouble()
                hourlyExeCal = userDetailsSharedPref?.getFloat("hourlyExeCal", 0f)!!.toDouble()
            }
        } else {
            Log.i("DateCompare", "different -> CurrentDate : $currentDate PreDate : $preDate")

            // 날짜가 다른 경우(초기화)
            userDetailsEditor?.putString("distance", "0")
            userDetailsEditor?.putString("cal", "0")
            userDetailsEditor?.putString("execal", "0")
            userDetailsEditor?.putString("allstep", "0")
            userDetailsEditor?.putString("arr", "0")
            userDetailsEditor?.putInt("hourlyArrCnt", 0)
            userDetailsEditor?.putInt("hourlyAllStep", 0)
            userDetailsEditor?.putFloat("hourlyDistance", 0f)
            userDetailsEditor?.putFloat("hourlyTotalCal", 0f)
            userDetailsEditor?.putFloat("hourlyExeCal", 0f)
            userDetailsEditor?.apply()

            // daily Data
            distance = 0.0
            dCal = 0.0
            dExeCal = 0.0
            allstep = 0
            arrCnt = 0

            // hourly Data
            hourlyArrCnt = 0
            hourlyAllstep = 0
            hourlyDistance = 0.0
            hourlyTotalCal = 0.0
            hourlyExeCal = 0.0
        }
        Log.i("LocalData", "-------------- LocalData --------------")
        Log.i("LocalData", "allstep : $allstep")
        Log.i("LocalData", "distance : $distance")
        Log.i("LocalData", "dCal : $dCal")
        Log.i("LocalData", "dExeCal : $dExeCal")
        Log.i("LocalData", "arrCnt : $arrCnt")
        Log.i("HourlyData", "-------------- HourlyData --------------")
        Log.i("HourlyData", "hourlyAllstep : $hourlyAllstep")
        Log.i("HourlyData", "hourlyDistance : $hourlyDistance")
        Log.i("HourlyData", "hourlyTotalCal : $hourlyTotalCal")
        Log.i("HourlyData", "hourlyExeCal : $hourlyExeCal")
        Log.i("HourlyData", "hourlyArrCnt : $hourlyArrCnt")

        // 시간 저장
        userDetailsEditor?.putString("preDate", stringCurrentDate)
        userDetailsEditor?.putString("preHour", stringCurrentHour)
        userDetailsEditor?.apply()
    }

    // 데이터 처리
    var leng = 0
    fun doBufProcess(buf: ByteArray) {
        try {
            if (buf.size >= 19) {
                if (buf[0].toInt() == 1) {

                    // battery = buf[1]
                    bluetoothData[0] = (buf[1].toInt() and 0xFF).toDouble()
                    doubleBAT = bluetoothData[0]

                    // bpm = buf[2]
                    bluetoothData[1] = (buf[2].toInt() and 0xFF).toDouble()

                    // temp = buf[3]
                    bluetoothData[2] = (buf[3].toInt() and 0xFF).toDouble()
                    bluetoothData[2] = (bluetoothData[2] + 186) / 10.0
                    bluetoothData[2] = bluetoothData[2] + 0.5


                    // ecg data = buf[6] ~ buf[19]
                    for (i in 6..19) {
                        var ecgP = buf[i].toInt() and 0xFF
                        ecgP = ecgP * 4

                        // graph And Arr
//                        dRecv[iRecvCnt] = ecgP;
                        dRecv[iRecvCnt] = peackCtrl.getPeackData(ecgP)
                        iRecvCnt++
                        dRecvLast[iRecvLastCnt] = ecgP.toDouble()
                        iRecvLastCnt++
                        val xmax = 500
                        if (iRecvLastCnt == xmax) {
                            for (j in 1 until xmax) {
                                dRecvLast[j - 1] = dRecvLast[j]
                            }
                            iRecvLastCnt = xmax - 1
                        }

                        // ecg
                        val exmax = 14
                        if (ecgCnt == exmax) {
                            for (j in 1 until exmax) {
                                ecgPacket[j - 1] = ecgPacket[j]
                            }
                            ecgCnt = exmax - 1
                        }
                        ecgPacket[ecgCnt] = ecgP.toDouble()
                        ecgCnt++
                    } // for
                    //                    for(int i = leng; i < ecgPacket.length; i++){
//                        ecgPacketList[i] = (int)ecgPacket[i];
//                    }
//                    leng += 13;
                    ecgPacketList.append(Arrays.toString(ecgPacket))
                    ecgPacketCnt++

                    // step = buf[4]
                    var step = buf[4].toInt() and 0xFF
                    if (step >= 14) step -= 14
                    if (step >= 10) {
                        var notificationString: String? = null
                        var arrTypeString: String? = null
                        if (step / 10 == EVENT_ARR) {
                            arr = EVENT_ARR
                            arrCnt++
                            tenSecondArrCnt++
                            arrStatus = ARR
                            arrAction()
                            if (setArrNotiFlag) arrTypeString = ARR
                        }
                        if (step / 10 == EVENT_HEARTATTACK) {
                            if (setHeartAttackNotiFlag && notificationManager!!.notiPermissionCheck) runOnActivityUiThread { heartAttackEvent() }
                        }
                        if (step / 10 == EVENT_NON_CONTACT) {
                            if (nonContactCheck && setNonContactNotiFlag) {
                                notificationString = NON_CONTACT
                                nonContactCheck = false // 10초
                            }
                        }
                        if (step / 10 == EVENT_MYO) {
                            if (myoCheck && setMyoNotiFlag) {
                                notificationString = MYO
                                myoCheck = false // 10초
                            }
                        }
                        if (step / 10 == EVENT_FAST_ARR) {
                            arr = EVENT_ARR
                            arrCnt++
                            tenSecondArrCnt++
                            arrStatus = STATUS_TACHYCARDIA
                            arrAction()
                            if (setTachycardiaNotiFlag) arrTypeString = TACHYCARDIA
                        }
                        if (step / 10 == EVENT_SLOW_ARR) {
                            if (bluetoothData[1] > 3) {
                                arr = EVENT_ARR
                                arrCnt++
                                tenSecondArrCnt++
                                arrStatus = STATUS_BRADYCARDIA
                                arrAction()
                                if (setBradycardiaNotiFlag) arrTypeString = BRADYCARDIA
                            }
                        }
                        if (step / 10 == EVENT_IRREGULAR_ARR) {
                            arr = EVENT_ARR
                            arrCnt++
                            tenSecondArrCnt++
                            arrStatus = STATUS_IRREGULAR
                            arrAction()
                            if (setAtrialFibrillationNotiFlag) arrTypeString = ATRIAL_FIBRILLATION
                        }
                        if (notificationManager!!.notiPermissionCheck && notificationString != null) {
                            notificationManager!!.sendNotification(
                                notificationString,
                                NOTIFICATION_ID,
                                currentTime!!
                            )
                        } else if (notificationManager!!.notiPermissionCheck && arrTypeString != null) {
                            when (arrCnt) {
                                50 -> notificationManager!!.sendArrNotification(
                                    ARR_50,
                                    ARR_NOTIFICATION_ID,
                                    currentTime!!
                                )

                                100 -> notificationManager!!.sendArrNotification(
                                    ARR_100,
                                    ARR_NOTIFICATION_ID,
                                    currentTime!!
                                )

                                200 -> notificationManager!!.sendArrNotification(
                                    ARR_200,
                                    ARR_NOTIFICATION_ID,
                                    currentTime!!
                                )

                                300 -> notificationManager!!.sendArrNotification(
                                    ARR_300,
                                    ARR_NOTIFICATION_ID,
                                    currentTime!!
                                )

                                else -> notificationManager!!.sendArrNotification(
                                    arrTypeString,
                                    ARR_NOTIFICATION_ID,
                                    currentTime!!
                                )
                            }
                        } // notifications
                    } else {
                        arr = 0
                    }
                    step = step % 10

                    // step = buf[4]
                    bluetoothData[3] += step.toDouble()
                    nowstep += step
                    allstep += step
                    tenSecondStep += step
                    bluetoothData[4] = (buf[5].toInt() and 0xFF).toDouble()
                    bluetoothData[4] = bluetoothData[4] * 10.0
                }
                dpscnt += 1
                if (intBPM > 3) {
                    pBPM = cBPM
                    cBPM = intBPM.toDouble()
                    if (pBPM != cBPM) {
                        realBPM = cBPM
                    }
                }
                doubleBAT = bluetoothData[0]
                intBPM = bluetoothData[1].toInt()
                doubleTEMP = bluetoothData[2]
                intHRV = bluetoothData[4].toInt()
                stringHRV = Integer.toString(intHRV)
                stringBPM = Integer.toString(intBPM)
                stringArr = arr.toString()
                stringArrCnt = arrCnt.toString()
                if (intBPM > 3) {
                    stringRealHRV = stringHRV
                    stringRealBPM = stringBPM
                    intRealBPM = intBPM

                    // avgBPM
                    tenSecondBpmSum += intRealBPM
                    tenMinuteBpmSum += intRealBPM
                    tenSecondAvgBpmCnt++
                    tenMinuteAvgBpmCnt++
                    tenSecondAvgBPM = tenSecondBpmSum / tenSecondAvgBpmCnt // csv 파일 저장 변수
                    tenMinuteAvgBPM = tenMinuteBpmSum / tenMinuteAvgBpmCnt // home 화면에 보여주는 변수

                    // avgHRV
                    tenSecondHRVSum += intHRV
                    tenSecondAvgHRV = tenSecondHRVSum / tenSecondAvgBpmCnt

                    // avgTemp
                    tenSecondTempSum += doubleTEMP
                    tenSecondAvgTemp = tenSecondTempSum / tenSecondAvgBpmCnt

                    // maxBPM
                    if (intRealBPM > maxBPM) {
                        maxBPM = intRealBPM
                    }
                    // minBPM
                    if (intRealBPM < minBPM) {
                        minBPM = intRealBPM
                    }

                    // 평균 BPM 차이
                    if (intRealBPM - tenMinuteAvgBPM > 0) {
                        diffAvgFlag = true
                        diffAvgBPM = intRealBPM - tenMinuteAvgBPM
                    } else {
                        diffAvgFlag = false
                        diffAvgBPM = intRealBPM - tenMinuteAvgBPM
                    }
                    // 최대 BPM 차이
                    if (intRealBPM - maxBPM > 0) {
                        diffMaxFlag = true
                        diffMaxBPM = intRealBPM - maxBPM
                    } else {
                        diffMaxFlag = false
                        diffMaxBPM = intRealBPM - maxBPM
                    }
                    // 최소 BPM 차이
                    if (intRealBPM - minBPM > 0) {
                        diffMinFlag = true
                        diffMinBPM = intRealBPM - minBPM
                    } else {
                        diffMinFlag = false
                        diffMinBPM = intRealBPM - minBPM
                    }
                } // if

                // sendECG
                if (ecgPacketCnt == 10) {
                    ecgAction(intRealBPM, ecgPacketList)
                }
                handler2.post { addEntry() }
            } // if
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun addEntry() {
        try {
            val data: LineData = chart?.getData()!!
            for (k in 0..0) {
                val ds: LineDataSet = data.getDataSetByIndex(k) as LineDataSet
                for (j in 0 until iRecvCnt - 1) {
                    val ff = dRecv[j].toFloat()
                    data.addEntry(Entry(ds.getEntryCount().toFloat(), ff), k)
                    while (ds.getEntryCount() > GRAPH_MAX) {
                        ds.removeEntry(0)
                        for (i in 0 until ds.getEntryCount()) {
                            val e: Entry = ds.getEntryForIndex(i) ?: continue
                            e.x = e.x - 1
                        }
                    }
                }
                iRecvCnt = 0
            }
            chart?.invalidate()
        } catch (ignored: Exception) {
        }
    }

    fun doLoop() {
        Log.v("doLoop", "doLoop() Start")
        if (thread == null || !thread!!.isAlive) {
            thread = Thread(object : Runnable {
                var lTick = System.currentTimeMillis()
                var lTick500ms = System.currentTimeMillis()
                var lTick1s = System.currentTimeMillis()
                var lTick10s = System.currentTimeMillis()
                var lTick10m = System.currentTimeMillis()
                @SuppressLint("MissingPermission")
                override fun run() {
                    isRun = true
                    while (isRun) {
                        val lNow = System.currentTimeMillis()
                        if (lNow - lTick >= 1000) {
                            lTick = lNow
                            dps = dpscnt.toFloat()
                            dpscnt = 0
                            if (realBPM > 50) {
                                iCalTimer++
                            } else {
                                iCalTimer = 0
                            }
                            if (iCalTimer >= 2) {
                                iCalTimeCount++
                                dCalMinU = iCalTimeCount / 60.0
                                iCalTimeCount = 0
                                if (iGender == 1) { // male
                                    dCal =
                                        dCal + (iAge * 0.2017 + dWeight * 0.1988 + realBPM * 0.6309 - 55.0969) * dCalMinU / 4.184
                                    tenSecondCal =
                                        tenSecondCal + (iAge * 0.2017 + dWeight * 0.1988 + realBPM * 0.6309 - 55.0969) * dCalMinU / 4.184
                                    if (realBPM > eCalBPM) {
                                        dExeCal =
                                            dExeCal + (iAge * 0.2017 + dWeight * 0.1988 + realBPM * 0.6309 - 55.0969) * dCalMinU / 4.184
                                        tenSecondECal =
                                            tenSecondECal + (iAge * 0.2017 + dWeight * 0.1988 + realBPM * 0.6309 - 55.0969) * dCalMinU / 4.184
                                    }
                                } else { // female
                                    dCal =
                                        dCal + (iAge * 0.074 + dWeight * 0.1263 + realBPM * 0.4472 - 20.4022) * dCalMinU / 4.184
                                    tenSecondCal =
                                        tenSecondCal + (iAge * 0.2017 + dWeight * 0.1988 + realBPM * 0.6309 - 55.0969) * dCalMinU / 4.184
                                    if (realBPM > eCalBPM) {
                                        dExeCal =
                                            dExeCal + (iAge * 0.074 + dWeight * 0.1263 + realBPM * 0.4472 - 20.4022) * dCalMinU / 4.184
                                        tenSecondECal =
                                            tenSecondECal + (iAge * 0.2017 + dWeight * 0.1988 + realBPM * 0.6309 - 55.0969) * dCalMinU / 4.184
                                    }
                                }
                                dCal = Math.round(dCal * 10) / 10.0
                                dExeCal = Math.round(dExeCal * 10) / 10.0
                                tenSecondCal = Math.round(tenSecondCal * 10) / 10.0
                                tenSecondECal = Math.round(tenSecondECal * 10) / 10.0
                            }

                            // calc cal
                            val dh = disheight.toDouble()
                            avgsize = (dh * 0.37 + (dh - 100)) / 2.0 //  dh 사용자의 키
                            if (avgsize < 0) {
                                avgsize = 10.0
                            }
                            if (realBPM < eCalBPM) {
                                distance =
                                    distance + avgsize * nowstep // nowstep는 걸음수  계산 값의 단위는 cm
                                tenSecondDistance = tenSecondDistance + avgsize * nowstep
                            } else if (realBPM >= eCalBPM && realBPM < eCalBPM + 20) {
                                distance = distance + (avgsize + 1) * nowstep
                                tenSecondDistance = tenSecondDistance + (avgsize + 1) * nowstep
                            } else if (realBPM >= eCalBPM + 20 && realBPM < eCalBPM + 40) {
                                distance = distance + (avgsize + 2) * nowstep
                                tenSecondDistance = tenSecondDistance + (avgsize + 2) * nowstep
                            } else if (realBPM >= eCalBPM + 40 && realBPM < 250) {
                                distance = distance + (avgsize + 3) * nowstep
                                tenSecondDistance = tenSecondDistance + (avgsize + 3) * nowstep
                            }

                            //운동중
                            if (realBPM < eCalBPM) //BPMchk는 맥박
                            {
                                wdistance =
                                    wdistance + avgsize * nowstep // nowstep는 걸음수  계산 값의 단위는 cm
                            } else if (realBPM >= eCalBPM && realBPM < eCalBPM + 15) {
                                wdistance = wdistance + (avgsize + 1) * nowstep
                            } else if (realBPM >= eCalBPM + 15 && realBPM < eCalBPM + 30) {
                                wdistance = wdistance + (avgsize + 3) * nowstep
                            } else if (realBPM >= eCalBPM + 30 && realBPM < 250) {
                                wdistance = wdistance + (avgsize + 4) * nowstep
                            }
                            distance = Math.round(distance * 1000) / 1000.0
                            wdistance = Math.round(wdistance * 1000) / 1000.0
                            tenSecondDistance = Math.round(tenSecondDistance * 1000) / 1000.0
                            tenSecondDistanceKM = tenSecondDistance / 100 / 1000
                            tenSecondDistanceM = tenSecondDistanceKM * 1000
                            realDistanceKM = distance / 100 / 1000
                            realDistanceM = realDistanceKM * 1000
                            nowstep = 0
                        }

                        // 그래프
//                        if (iRecvCnt > 100 || tickTime() > 200) {
//                            handler2.post(() -> addEntry());
//                        }
                        if (lNow - lTick500ms >= 500) { // 0.5
                            handler2.post { uiTick() }
                            lTick500ms = lNow
                            tickReset()
                        }
                        if (lNow - lTick1s >= 1000) { // 1초
                            lTick1s = lNow
                            if (safeGetActivity() != null) runOnActivityUiThread { statusCheck() }
                        }
                        var lTxNow: MutableList<String>? = null
                        synchronized(lTx) {
                            if (lTx.size > 0) {
                                lTxNow = lTx
                                lTx = ArrayList()
                            }
                        }
                        if (lTxNow != null) {
                            if (lTxNow!!.size > 0) {
                                while (lTxNow!!.size > 0) {
                                    val sTx = lTxNow!![0]
                                    if (BTConnected) {
                                        if (isBTType == 0) {
                                            mConnectedThread?.write(sTx)
                                        } else if (isBTType == 1) {
                                            var status = true
                                            try {
                                                //check mBluetoothGatt is available
                                                if (bluetoothGatt == null) {
                                                    Log.v("BLE", "lost connection")
                                                    status = false
                                                }
                                                val Service: BluetoothGattService =
                                                    bluetoothGatt?.getService(
                                                        UUID_SERVICE
                                                    )!!
                                                if (Service == null) {
                                                    Log.v("BLE", "service not found!")
                                                    status = false
                                                }
                                                val charac: BluetoothGattCharacteristic = Service
                                                    .getCharacteristic(UUID_TX)
                                                if (charac == null) {
                                                    Log.v("BLE", "char not found!")
                                                    status = false
                                                }
                                                if (status) {
                                                    charac.setValue(sTx) //, 20, 0);
                                                    status =
                                                        bluetoothGatt?.writeCharacteristic(charac)!!
                                                    if (status) {
//                                                    safeGetActivity()().runOnUiThread(() -> toast("work"));
                                                    }
                                                }
                                            } catch (ignored: Exception) {
                                            }
                                        }
                                    }
                                    lTxNow?.removeAt(0)
                                }
                            }
                        }

                        // 10초
                        if (lNow - lTick10s >= 10000) {
                            lTick10s = lNow // 타이머 리셋
                            nonContactCheck = true // 10초에 한 번만 울리게끔
                            myoCheck = true
                            tenSecondAction()
                            if (safeGetActivity() != null) {
                                runOnActivityUiThread { batReceived(doubleBAT.toInt()) }
                            }
                        }

                        // 10분
                        if (lNow - lTick10m >= 600000) {
                            lTick10m = lNow // 타이머 리셋
                            tenMinuteAction()
                        }
                        try {
                            Thread.sleep(1)
                        } catch (ignored: Exception) {
                        }
                    }
                }
            })
            thread!!.start()
        }
    }

    fun sendCalcDataAsCsv(
        hour: String?,
        step: String?,
        distance: String?,
        cal: String?,
        eCal: String?,
        arrcnt: String?
    ) {
        retrofitServerManager?.sendHourlyData(
            email!!,
            currentYear!!,
            currentMonth!!,
            currentDay!!,
            hour!!,
            utcOffsetAndCountry!!,
            step!!,
            distance!!,
            cal!!,
            eCal!!,
            arrcnt!!,
            object : RetrofitServerManager.ServerTaskCallback {
                override fun onSuccess(result: String?) {
                    Log.i("sendCalcDataAsCsv", result!!)
                }

                override fun onFailure(e: Exception?) {
                    Log.e("sendCalcDataAsCsv", "send Err")
                    e!!.printStackTrace()
                }
            })
    }

    fun sendArrData(
        currentDate: String?,
        currentTime: String?,
        sendArrCnt: String,
        sendArrData: String,
        arrStatus: String
    ) {
        val arrTime = "$currentDate $currentTime"
        retrofitServerManager?.sendArrData(
            email!!,
            utcOffsetAndCountry!!,
            arrTime,
            sendArrData,
            arrStatus,
            object : RetrofitServerManager.ServerTaskCallback {
                override fun onSuccess(result: String?) {
                    Log.i("sendArrData", result!!)
                    retryCounts.remove(sendArrCnt)
                }

                override fun onFailure(e: Exception?) {
                    Log.e("sendArrData", "send Err")
                    e!!.printStackTrace()
                    if (retryCounts.containsKey(sendArrCnt)) {
                        val currentRetryCount = retryCounts[sendArrCnt]!! // 재전송 횟수 체크
                        if (currentRetryCount < MAX_RETRY_COUNT) {
                            retryCounts[sendArrCnt] = currentRetryCount + 1
                            retransmitAfterDelay(
                                currentDate,
                                currentTime,
                                sendArrCnt,
                                sendArrData,
                                arrStatus
                            )
                        } else {
                            Log.e("sendArrData", "ARR DATA Server Task Fail (6 times)")
                            retryCounts.remove(sendArrCnt)
                        }
                    } else {
                        retryCounts[sendArrCnt] = 1
                        retransmitAfterDelay(
                            currentDate,
                            currentTime,
                            sendArrCnt,
                            sendArrData,
                            arrStatus
                        )
                    }
                }
            })
    }

    // ARR DATA Server 재전송 함수
    private fun retransmitAfterDelay(
        currentDate: String?,
        currentTime: String?,
        sendArrCnt: String,
        sendArrData: String,
        arrStatus: String
    ) {
        val handler: Handler = Handler(Looper.getMainLooper()) // 메인 스레드의 Looper를 사용
        handler.postDelayed({
            sendArrData(
                currentDate,
                currentTime,
                sendArrCnt,
                sendArrData,
                arrStatus
            )
        }, 60000) // 1분 후 재전송
    }

    fun sendEcgData(bpm: Int, ecgPacketList: StringBuilder?) {
        val email: String = userDetailsSharedPref?.getString("email", "NULL")!!
        val writeTime = currentUtcTime()
        retrofitServerManager?.sendEcgData(
            email,
            writeTime,
            utcOffsetAndCountry!!,
            bpm,
            ecgPacketList!!,
            object : RetrofitServerManager.ServerTaskCallback {
                override fun onSuccess(result: String?) {
                    Log.i("sendECGData", result!!)
                }

                override fun onFailure(e: Exception?) {
                    Log.e("sendECGData", " send Err")
                    e!!.printStackTrace()
                }
            })
    }

    //    public void sendEcgData(int bpm, StringBuilder ecgPacketList) {
    //
    //        String email = userDetailsSharedPref.getString("email", "NULL");
    //        String writeTime = currentUtcTime();
    //
    //        retrofitServerManager.sendEcgData(email, writeTime, utcOffsetAndCountry, bpm, ecgPacketList, new RetrofitServerManager.ServerTaskCallback() {
    //
    //            @Override
    //            public void onSuccess(String result) {
    //                Log.i("sendECGData", result);
    //            }
    //
    //            @Override
    //            public void onFailure(Exception e) {
    //                Log.e("sendECGData", " send Err");
    //                e.printStackTrace();
    //            }
    //        });
    //    }
    private fun tenSecondAction() {
        timeZone()

        // csv 파일 저장
        saveBpmDataAsCsv()
        saveCalAndDistanceAsCsv()
        tenSecondServerTask(
            tenSecondAvgBPM,
            tenSecondAvgTemp,
            tenSecondAvgHRV,
            tenSecondStep,
            tenSecondDistanceKM,
            tenSecondCal,
            tenSecondECal,
            tenSecondArrCnt
        )
        sendCalcDataAsCsv(
            currentHour,
            sendHourlyStep,
            sendHourlyDistance,
            sendHourlyTCal,
            sendHourlyECal,
            sendHourlyArrCnt
        )

        // 변수 초기화
        tenSecondBpmSum = 0
        tenSecondAvgBPM = 0
        tenSecondAvgBpmCnt = 0
        tenSecondAvgHRV = 0
        tenSecondHRVSum = 0
        tenSecondAvgTemp = 0.0
        tenSecondTempSum = 0.0
        tenSecondArrCnt = 0
        tenSecondCal = 0.0
        tenSecondECal = 0.0
        tenSecondDistance = 0.0
        tenSecondDistanceKM = 0.0
        tenSecondDistanceM = 0.0
        tenSecondStep = 0
    }

    private fun tenMinuteAction() {
        // 변수 초기화
        tenMinuteBpmSum = 0
        tenMinuteAvgBPM = 0
        tenMinuteAvgBpmCnt = 0
    }

    private fun tenSecondServerTask(
        bpm: Int,
        temp: Double,
        hrv: Int,
        step: Int,
        distance: Double,
        tCal: Double,
        eCal: Double,
        arrCnt: Int
    ) {

        // bpm, temp, hrv, step, distance, tCal, eCal, arrCnt
        val email: String = userDetailsSharedPref?.getString("email", "NULL")!!
        val writeTime = currentUtcTime()
        retrofitServerManager?.sendTenSecondData(
            email,
            utcOffsetAndCountry!!,
            writeTime,
            bpm,
            temp,
            hrv,
            step,
            distance,
            tCal,
            eCal,
            arrCnt,
            object : RetrofitServerManager.ServerTaskCallback {
                override fun onSuccess(result: String?) {
                    Log.i("tenSecondServerTask", result!!)
                }

                override fun onFailure(e: Exception?) {
                    Log.e("tenSecondServerTask", "send Err")
                    e!!.printStackTrace()
                }
            })
    }

    fun startContinuousTimeUpdates() {
        localDataLoop = true
        Thread {
            while (localDataLoop!!) {
                currentTimeCheck()
                localDataSave()
                try {
                    Thread.sleep(1000) // 1초 대기
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    fun currentTimeCheck() {

        // 시간 갱신 메서드
        val mNow = System.currentTimeMillis()
        mDate = Date(mNow)
        mTime = Time(mNow)
        val df = SimpleDateFormat("yyyy-MM-dd")
        val stf = SimpleDateFormat("HH:mm:ss")
        val th = SimpleDateFormat("HH")
        val minutes = SimpleDateFormat("mm")
        val second = SimpleDateFormat("ss")
        val year = SimpleDateFormat("yyyy")
        val month = SimpleDateFormat("MM")
        val day = SimpleDateFormat("dd")
        currentYear = year.format(mDate)
        currentMonth = month.format(mDate)
        currentDay = day.format(mDate)
        currentDate = df.format(mDate)
        currentTime = stf.format(mTime)
        currentHour = th.format(mTime)
        currentMinute = minutes.format(mTime)
        currentSecond = second.format(mTime)
    }

    fun tickReset() {
        tickLast = SystemClock.uptimeMillis()
    }

    fun tickTime(): Long {
        return SystemClock.uptimeMillis() - tickLast
    }

    fun loadUserProfile() {
        val getEmail: String = userDetailsSharedPref?.getString("email", "NULL")!!
        Log.e("getEmail", getEmail)
        try {
            retrofitServerManager?.getProfile(getEmail, object : RetrofitServerManager.UserDataCallback {
                override fun userData(user: UserProfile?) {

                    // 로컬 저장
                    userDetailsEditor?.putString("gender", user?.gender)
                    userDetailsEditor?.putString("birthday", user?.birthday)
                    userDetailsEditor?.putString("age", user?.age)
                    userDetailsEditor?.putString("name", user?.name)
                    userDetailsEditor?.putString("number", user?.phone)
                    userDetailsEditor?.putString("weight", user?.weight)
                    userDetailsEditor?.putString("height", user?.height)
                    userDetailsEditor?.putString("sleep1", user?.sleepStart)
                    userDetailsEditor?.putString("sleep2", user?.sleepEnd)
                    userDetailsEditor?.putString("current_date", user?.joinDate)
                    userDetailsEditor?.putString("o_cal", user?.getDailyCalorie())
                    userDetailsEditor?.putString("o_ecal", user?.getDailyActivityCalorie())
                    userDetailsEditor?.putString("o_step", user?.dailyStep)
                    userDetailsEditor?.putString("o_distance", user?.dailyDistance)
                    userDetailsEditor?.commit()
                    iGender = if (user?.gender == "남자") 1 else 2
                    iAge = user?.age?.toInt()!!
                    userName = user?.name
                    dWeight = user.weight?.toDouble()!!
                    disheight = user.height?.toInt()!!
                    sleep = user.sleepStart?.toInt()!!
                    wakeup = user.sleepEnd?.toInt()!!
                    eCalBPM = user.activityBPM?.toInt()!!
                    Log.i("loadUserProfile", "----------------- UserProfile -----------------")
                    Log.i("UserProfile", "iGender : $iGender")
                    Log.i("UserProfile", "iAge : $iAge")
                    Log.i("UserProfile", "userName : $userName")
                    Log.i("UserProfile", "disheight : $disheight")
                    Log.i("UserProfile", "dWeight : $dWeight")
                    Log.i("UserProfile", "sleep : $sleep")
                    Log.i("UserProfile", "wakeup : $wakeup")
                    Log.i("UserProfile", "JoinDate : " + user.joinDate)
                    Log.i("UserProfile", "Tcal : " + user.getDailyCalorie())
                    Log.i("UserProfile", "Ecal : " + user.getDailyActivityCalorie())
                    Log.i("UserProfile", "Step : " + user.dailyStep)
                    Log.i("UserProfile", "Distance : " + user.dailyDistance)
                }

                override fun onFailure(e: Exception?) {
                    toast(resources.getString(R.string.serverErr))
                    e!!.printStackTrace()

                    // 기본(내부) 정보 사용
                    val bGender: String = userDetailsSharedPref?.getString("gender", "남자")!!
                    iGender = if (bGender == "남자") 1 else 2
                    eCalBPM = userDetailsSharedPref?.getString("o_bpm", "90")!!.toInt()
                    iAge = userDetailsSharedPref?.getString("age", "40")!!.toInt()
                    userName = userDetailsSharedPref?.getString("name", "관리자")
                    dWeight = userDetailsSharedPref?.getString("weight", "60")!!.toDouble()
                    disheight = userDetailsSharedPref?.getString("height", "170")!!.toInt()
                    sleep = userDetailsSharedPref?.getString("sleep1", "23")!!.toInt()
                    wakeup = userDetailsSharedPref?.getString("sleep2", "7")!!.toInt()
                    Log.e(
                        "loadUserProfile",
                        "----------------- UserProfile onFailure-----------------"
                    )
                    Log.i("UserProfile", "bGender : " + bGender + "iGender : " + iGender)
                    Log.i("UserProfile", "iAge : $iAge")
                    Log.i("UserProfile", "userName : $userName")
                    Log.i("UserProfile", "disheight : $disheight")
                    Log.i("UserProfile", "dWeight : $dWeight")
                    Log.i("UserProfile", "sleep : $sleep")
                    Log.i("UserProfile", "wakeup : $wakeup")
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("uiProfileLoad", "Error loading profile", e)
        }
    }

    fun chartInit() {
        chart = view?.findViewById<View>(R.id.myChart) as LineChart
        chart?.setDrawGridBackground(true)
        chart?.setBackgroundColor(Color.WHITE)
        chart?.setGridBackgroundColor(Color.WHITE)
        chart!!.getDescription().setEnabled(false)
        chart?.setTouchEnabled(false)
        chart?.setDragEnabled(false)
        chart?.setScaleEnabled(false)
        chart?.setAutoScaleMinMaxEnabled(true)
        chart?.setPinchZoom(false)
        chart!!.getXAxis().setDrawGridLines(true)
        chart!!.getXAxis().setDrawAxisLine(false)
        chart!!.getXAxis().setEnabled(false) //x축 표시에 대한 함수
        chart!!.getXAxis().setDrawGridLines(false)
        val l: Legend = chart?.getLegend()!!
        l.setEnabled(true)
        l.setFormSize(10f) // set the size of the legend forms/shapes
        l.setTextSize(12f)
        l.setTextColor(Color.DKGRAY)
        val leftAxis: YAxis = chart?.getAxisLeft()!!
        leftAxis.setEnabled(true)
        leftAxis.setTextColor(resources.getColor(R.color.colorDKGRAY))
        leftAxis.setDrawGridLines(true)
        leftAxis.setGridColor(resources.getColor(R.color.colorGainsbro))
        val rightAxis: YAxis = chart?.getAxisRight()!!
        leftAxis.setAxisMaximum(1024f)
        leftAxis.setAxisMinimum(0f)
        rightAxis.setEnabled(false)
        var data: LineData? = chart?.getData()
        if (data == null) {
            data = LineData()
            chart?.setData(data)
        }
        for (k in 0..0) {
            var ds: LineDataSet? = data.getDataSetByIndex(k) as LineDataSet?
            if (ds == null) {
                ds = createSet(lVName[k])
                data.addDataSet(ds)
                Log.v("ds", data.toString())
                if (k == 0) ds.setColor(Color.BLUE) else if (k == 1) ds.setColor(Color.RED)
            }
        }
        chart?.invalidate()
    }

    private fun createSet(title: String?): LineDataSet {
        val set = LineDataSet(null, title)
        Log.v("set", set.toString())
        set.setLineWidth(1f)
        set.setDrawValues(false)
        set.setValueTextColor(resources.getColor(R.color.white))
        set.setColor(resources.getColor(R.color.white))
        set.setMode(LineDataSet.Mode.LINEAR)
        set.setDrawCircles(false)
        set.setHighLightColor(Color.rgb(190, 190, 190))
        return set
    }

    @SuppressLint("DefaultLocale", "SetTextI18n")
    fun uiTick() {
        try {
            val data: LineData = chart?.getData()!!
            data.notifyDataChanged()
            if (diffAvgFlag != null) {
                // avg bpm 차이
                if (diffAvgFlag) {
                    bpm_diffAvgValue?.setText("+$diffAvgBPM")
                } else {
                    bpm_diffAvgValue?.setText(diffAvgBPM.toString())
                }
                // max bpm 차이
                if (diffMaxFlag!!) {
                    bpm_diffMaxValue?.setText("+$diffMaxBPM")
                } else {
                    bpm_diffMaxValue?.setText(diffMaxBPM.toString())
                }
                // avg bpm 차이
                if (diffMinFlag!!) {
                    bpm_diffMinValue?.setText("+$diffMinBPM")
                } else {
                    bpm_diffMinValue?.setText(diffMinBPM.toString())
                }
            }
            bpm_value?.setText(stringRealBPM)
            bpm_avgValue?.setText(Integer.toString(tenMinuteAvgBPM))
            bpm_maxValue?.setText(Integer.toString(maxBPM))
            bpm_minValue?.setText(Integer.toString(minBPM))
            hrv_value?.setText(stringRealHRV)
            arr_value?.setText(Integer.toString(arrCnt))
            eCal_value?.setText(
                dExeCal.toInt().toString() + " " + resources.getString(R.string.eCalValue2)
            )
            step_value?.setText(allstep.toString() + " " + resources.getString(R.string.stepValue2))
            distance_value?.setText(
                String.format(
                    "%.3f",
                    realDistanceKM
                ) + " " + resources.getString(R.string.distanceValue2)
            )
            temp_value?.setText(
                String.format(
                    "%.1f",
                    bluetoothData[2]
                ) + " " + resources.getString(R.string.temperatureValue2)
            )
        } catch (E: Exception) {
            Log.d("ERR", "" + E.message)
        }
    }

    // -------------------------------- BLE -------------------------------- //
    fun BleConnectCheck() {
        if (btAdapter == null) {
            // 장치가 블루투스를 지원하지 않는 경우.
//            FragmentActivity activity = this;
            toast(resources.getString(R.string.notSupportBle))
        } else {
            btScanner = btAdapter?.getBluetoothLeScanner()

            // 장치가 블루투스를 지원하는 경우
            if (btAdapter?.isEnabled()!!) {
                if (activityOrThrow?.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    activityOrThrow?.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                ) {
                    startScanning()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun startScanning() {
        println("start Scanning")
        devicesDiscovered.clear()
        stopScanning()

        // 블루투스 연결 활성화 시 연결 해제
        try {
            if (bluetoothGatt != null) {
                println("bluetoothGatt.disconnect")
                bluetoothGatt?.disconnect() // gatt 클라이언트에서 gatt 서버 연결 해제
                bluetoothGatt?.close() // 클라이언트, 리소스 해제
            }
        } catch (ignored: Exception) {
        }

        // 비동기 블루투스 스캔 시작
        AsyncTask.execute(Runnable { btScanner?.startScan(leScanCallback) })

        // 10초 후 스캔 종료
        bleHandler?.postDelayed({ stopScanning() }, SCAN_PERIOD)
    }

    @SuppressLint("MissingPermission")
    fun stopScanning() {
        if (btScanner != null) AsyncTask.execute(Runnable { btScanner?.stopScan(leScanCallback) })
    }

    // 블루투스 스캔
    // BLE 디바이스 스캔의 결과를 처리하는 콜백 메소드를 포함하는 추상 클래스
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        // onScanResult 메소드는 Bluetooth 스캐너가 새로운 BLE 디바이스를 발견할 때마다 호출
        @SuppressLint("MissingPermission")
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            devicesDiscovered.add(result.device) // 발견 디바이스 추가

            // 발견 디바이스의 이름을 가지고 비교
            @SuppressLint("MissingPermission") val name = result.device.name
            if (name != null) {
                if (name == "ECG") {
                    // 연결 시도
                    if (safeGetActivity() != null && !firstBleConnect) {
                        bluetoothGatt = result.device.connectGatt(
                            activityOrThrow?.getApplicationContext(),
                            true,
                            btleGattCallback
                        )
                        val device: BluetoothDevice = bluetoothGatt?.getDevice()!!
                        deviceMacAddress = device.getAddress()
                        firstBleConnect = true
                        Log.v("macAddress", deviceMacAddress!!)

                        // 연결 우선순위 설정
                        if (bluetoothGatt != null) {
                            bluetoothGatt?.requestConnectionPriority(BluetoothGatt.CONNECTION_PRIORITY_HIGH)
                        }
                        stopScanning() // 발견 시 스캔 종료
                    }
                }
            }
        }
    }

    // 블루투스 이벤트 콜백 제공 인스턴스
    private val btleGattCallback: BluetoothGattCallback = object : BluetoothGattCallback() {
        // GATT characteristic 변경 시 호출되는 메서드
        override fun onCharacteristicChanged(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic
        ) {
            try {
                if (BTConnected == false) BTConnected = true

                // 변경된 값을 버퍼에 저장
                val buf: ByteArray = characteristic.getValue()
                doBufProcess(buf)
            } catch (E: Exception) {
                Log.d("BLE", "onCharacteristicChanged: " + E.message)
            }
        }

        // GATT 연결 상태가 변경될 때 호출
        @SuppressLint("MissingPermission")
        override fun onConnectionStateChange(gatt: BluetoothGatt, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.v("BLE_STATE", "STATE_CONNECTED")
                mConnectionState = STATE_CONNECTED
                bluetoothGatt?.discoverServices()
                stopScanning()
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.v("BLE_STATE", "STATE_DISCONNECTED")
                mConnectionState = STATE_DISCONNECTED
                try {
                    synchronized(lTx) { lTx.clear() }
                    //                    bleHandler.postDelayed(() -> startScanning(), 1000);
                } catch (e: Exception) {
                    Log.e("onConnectionStateChange", "synchronized Err")
                    e.printStackTrace()
                }
            }
        }

        // GATT 서비스가 발견될 때 호출
        @SuppressLint("MissingPermission")
        override fun onServicesDiscovered(gatt: BluetoothGatt, status: Int) {
            if (status == BluetoothGatt.GATT_SUCCESS) {
                try {
                    mBluetoothGattService = gatt.getService(UUID_SERVICE)
                    val receiveCharacteristic: BluetoothGattCharacteristic =
                        mBluetoothGattService?.getCharacteristic(
                            UUID_RECIEVE
                        )!!
                    if (receiveCharacteristic != null) {
                        BTConnected = true
                        val receiveConfigDescriptor: BluetoothGattDescriptor =
                            receiveCharacteristic.getDescriptor(
                                UUID_CONFIG
                            )
                        if (receiveConfigDescriptor != null) {
                            gatt.setCharacteristicNotification(receiveCharacteristic, true)
                            receiveConfigDescriptor.setValue(
                                BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE
                            )
                            gatt.writeDescriptor(receiveConfigDescriptor)
                        } else {
                            Log.e("BLE", "RFduino receive config descriptor not found!")
                        }
                        //                        mHandler.postDelayed(() -> sendPOTConst(), 1000);//11111_POT 시간 조절
                    } else {
                        Log.e("BLE", "RFduino receive characteristic not found!")
                    }
                } catch (ignored: Exception) {
                }
            } else {
                Log.w("BLE", "onServicesDiscovered received: $status")
            }
        }

        // GATT characteristic을 읽을 때 호출
        // Result of a characteristic read operation
        override fun onCharacteristicRead(
            gatt: BluetoothGatt,
            characteristic: BluetoothGattCharacteristic,
            status: Int
        ) {
            Log.v("status", status.toString())
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic)
            }
        }
    }

    private fun broadcastUpdate(
        action: String,
        characteristic: BluetoothGattCharacteristic
    ) {
        println(characteristic.getUuid())
    }

    // 블루투스 상태 확인
    var mBluetoothStateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action: String = intent.getAction()!! //입력된 action
            val device: BluetoothDevice =
                intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)!!
            var macAddress: String? = null
            if (device != null) macAddress = device.getAddress()
            when (action) {
                BluetoothAdapter.ACTION_STATE_CHANGED -> {
                    val state: Int =
                        intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)
                    when (state) {
                        BluetoothAdapter.STATE_OFF -> {
                            Log.v("BLE_STATE", "STATE_OFF")
                            if (deviceMacAddress == macAddress) {
                                activityOrThrow?.unregisterReceiver(this)
                                useBleFlag = false
                                firstBleConnect = false
                                stopThread()
                            }
                        }

                        BluetoothAdapter.STATE_TURNING_OFF -> Log.v(
                            "BLE_STATE",
                            "STATE_TURNING_OFF"
                        )

                        BluetoothAdapter.STATE_ON -> {
                            Log.v("BLE_STATE", "STATE_ON")
                            if (deviceMacAddress == macAddress) {
                                activityOrThrow?.registerReceiver(this, stateFilter)
                                if (activityOrThrow?.checkSelfPermission(Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                                    activityOrThrow?.checkSelfPermission(Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    BleConnectCheck()
                                    useBleFlag = true
                                }
                            }
                        }

                        BluetoothAdapter.STATE_TURNING_ON -> Log.v("BLE_STATE", "STATE_TURNING_ON")
                    }
                }

                BluetoothDevice.ACTION_ACL_CONNECTED -> {
                    Log.v("ACTION_ACL_CONNECTED", "ACTION_ACL_CONNECTED")
                    if (deviceMacAddress == macAddress) {
                        useBleFlag = true
                        initializeVariables()
                        if (thread == null) doLoop() else restartThread()
                    }
                }

                BluetoothDevice.ACTION_BOND_STATE_CHANGED -> Log.v(
                    "ACTION_BOND_STATE_CHANGED",
                    "ACTION_BOND_STATE_CHANGED"
                )

                BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                    Log.v("ACTION_ACL_DISCONNECTED", "ACTION_ACL_DISCONNECTED")
                    if (deviceMacAddress == macAddress) {
                        useBleFlag = false
                        stopThread()
                    }
                }

                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> Log.v(
                    "ACTION_DISCOVERY_STARTED",
                    "ACTION_DISCOVERY_STARTED"
                )

                BluetoothDevice.ACTION_FOUND -> Log.v("ACTION_FOUND", "ACTION_FOUND")
            }
        }
    }

    fun getFileDirectory(name: String): File {
        return File(activityOrThrow?.getFilesDir(), name)
    }

    private fun hourlyArrEvent(arrCnt: Int) {
        var title: String? = null
        var message: String? = null
        var hourlyArrFlag = 0
        val thresholds = intArrayOf(10, 20, 30, 50)
        for (i in thresholds.indices) {
            hourlyArrFlag =
                if (arrCnt >= thresholds[i]) i + 1 else break // 현재 임계값 보다 arrCnt 값이 작으면 반복문 탈출
        }
        when (hourlyArrFlag) {
            1 -> {
                title = resources.getString(R.string.notiHourlyArr10)
                message = resources.getString(R.string.notiHourlyArr10Text)
            }

            2 -> {
                title = resources.getString(R.string.notiHourlyArr20)
                message = resources.getString(R.string.notiHourlyArr20Text)
            }

            3 -> {
                title = resources.getString(R.string.notiHourlyArr30)
                message = resources.getString(R.string.notiHourlyArr30Text)
            }

            4 -> {
                title = resources.getString(R.string.notiHourlyArr50)
                message = resources.getString(R.string.notiHourlyArr50Text)
            }
        }

        // 알림 대화상자 표시
        val builder = AlertDialog.Builder(activityOrThrow, R.style.AlertDialogTheme)
        val v: View = LayoutInflater.from(activityOrThrow).inflate(
            R.layout.heartattack_dialog,
            view?.findViewById<View>(R.id.layoutDialog) as LinearLayout
        )
        builder.setView(v)
        (v.findViewById<View>(R.id.heartattack_title) as TextView).setText(title)
        (v.findViewById<View>(R.id.textMessage) as TextView).setText(message)
        (v.findViewById<View>(R.id.btnOk) as TextView).setText(resources.getString(R.string.ok))
        val alertDialog = builder.create()
        v.findViewById<View>(R.id.btnOk).setOnClickListener { alertDialog.dismiss() }

        // 다이얼로그 형태 지우기
        if (alertDialog.window != null) {
            alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        alertDialog.show()
    }

    private fun shouldNotify(previous: Int, current: Int, threshold: Int): Boolean {
        return previous < threshold && current >= threshold
    }

    private fun heartAttackEvent() {
        if (!HeartAttackCheck) {
            HeartAttackCheck = true // 중첩 알림이 안뜨게 하는 flag

            // 시스템 사운드 재생
            val mediaPlayer: MediaPlayer =
                MediaPlayer.create(activityOrThrow, R.raw.heartattacksound)
            mediaPlayer.setLooping(true) // 반복
            mediaPlayer.start()

            // 알림 대화상자 표시
            val builder = AlertDialog.Builder(activityOrThrow, R.style.AlertDialogTheme)
            val v: View = LayoutInflater.from(activityOrThrow).inflate(
                R.layout.heartattack_dialog,
                view?.findViewById<View>(R.id.layoutDialog) as LinearLayout
            )
            builder.setView(v)
            val title: TextView = v.findViewById<TextView>(R.id.heartattack_title)
            val message: TextView = v.findViewById<TextView>(R.id.textMessage)
            val btnOk = v.findViewById<Button>(R.id.btnOk)
            title.setText(resources.getString(R.string.emergency))
            message.setText(resources.getString(R.string.emergencyTxt))
            btnOk.text = resources.getString(R.string.ok) + "(10)"
            val alertDialog = builder.create()
            val countDownTimer: CountDownTimer = object : CountDownTimer(11000, 1000) {
                override fun onTick(millisUntilFinished: Long) {
                    btnOk.text =
                        resources.getString(R.string.ok) + "(" + millisUntilFinished / 1000 + ")"
                }

                override fun onFinish() {
                    btnOk.text = resources.getString(R.string.sendEmergencyAlert)
                    val writeTime = currentUtcTime()
                    val address: String = address
                    retrofitServerManager?.sendEmergencyData(
                        email!!,
                        utcOffsetAndCountry!!,
                        writeTime,
                        address,
                        object : RetrofitServerManager.ServerTaskCallback {
                            override fun onSuccess(result: String?) {
                                Log.i("sendEmergencyData", result!!)
                                if (result.lowercase(Locale.getDefault()).trim { it <= ' ' }
                                        .contains("true")) toast(
                                    resources.getString(R.string.sendEmergencyAlert)
                                ) else toast(
                                    resources.getString(R.string.noGuardianSet)
                                )
                            }

                            override fun onFailure(e: Exception?) {
                                Log.e("sendEmergencyData", "send err")
                                e!!.printStackTrace()
                                toast(resources.getString(R.string.guardianSeverErr))
                            }
                        })
                }
            }.start()
            v.findViewById<View>(R.id.btnOk).setOnClickListener {
                countDownTimer.cancel()
                mediaPlayer.release()
                HeartAttackCheck = false
                sendTx("c\n")
                alertDialog.dismiss()
            }
            if (alertDialog.window != null) { // 다이얼로그 형태 지우기
                alertDialog.window!!.setBackgroundDrawable(ColorDrawable(0))
            }
            alertDialog.setCancelable(false)
            alertDialog.setCanceledOnTouchOutside(false) // 다른 곳이 터치되어도 사라지지 않음
            alertDialog.show()
        }
    }

    fun guardianEvent() {
        val phoneNumberPattern = "^[0-9]{9,11}$"
        val builder = AlertDialog.Builder(activityOrThrow, R.style.AlertDialogTheme)
        val v: View = LayoutInflater.from(activityOrThrow).inflate(
            R.layout.guardian_dialog,
            view?.findViewById<View>(R.id.layoutDialog) as LinearLayout
        )
        builder.setView(v)
        val firstGuardianET: EditText = v.findViewById<EditText>(R.id.guardian_First_EditText)
        val secondGuardianET: EditText = v.findViewById<EditText>(R.id.guardian_Second_EditText)
        (v.findViewById<View>(R.id.guardian_title) as TextView).setText(resources.getString(R.string.setupGuardian))
        (v.findViewById<View>(R.id.guardian_Button) as TextView).setText(resources.getString(R.string.ok))
        val guardianTextWatcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                if (firstGuardianET.hasFocus()) {
                    firstGuardian = editable.toString()
                    firstCheck =
                        firstGuardian.trim { it <= ' ' }.matches(phoneNumberPattern.toRegex())
                } else {
                    secondGuardian = editable.toString()
                    secondCheck =
                        secondGuardian.trim { it <= ' ' }.matches(phoneNumberPattern.toRegex())
                }
            }
        }
        firstGuardianET.addTextChangedListener(guardianTextWatcher)
        secondGuardianET.addTextChangedListener(guardianTextWatcher)
        guardianAlertDialog = builder.create()

        // button event
        v.findViewById<View>(R.id.guardian_Button).setOnClickListener {
            val isFirstValid = firstGuardian.isEmpty() || firstCheck
            val isSecondValid = secondGuardian.isEmpty() || secondCheck
            if (isFirstValid && isSecondValid) {
                val email: String = userDetailsSharedPref?.getString("email", "NULL")!!
                val writeTime = currentUtcTime()
                val phoneArrList = ArrayList<String>()
                if (!firstGuardian.isEmpty()) phoneArrList.add(firstGuardian)
                if (!secondGuardian.isEmpty()) phoneArrList.add(secondGuardian)
                saveGuardianToServer(email, writeTime, phoneArrList)
                userDetailsEditor?.putString("s_guard_num1", firstGuardian)
                userDetailsEditor?.putString("s_guard_num2", secondGuardian)
                userDetailsEditor?.putBoolean("guardian", true)
                userDetailsEditor?.apply()
            } else {
                toast(resources.getString(R.string.setupGuardianTxt))
            }
        }

        // 다이얼로그 형태 지우기
        if (guardianAlertDialog?.getWindow() != null) {
            guardianAlertDialog?.getWindow()!!.setBackgroundDrawable(ColorDrawable(0))
        }
        guardianAlertDialog?.setCancelable(false)
        guardianAlertDialog?.setCanceledOnTouchOutside(false) // 다른 곳이 터치되어도 사라지지 않음
        guardianAlertDialog?.show()
    }

    private fun saveGuardianToServer(
        email: String,
        writeTime: String,
        guardian: ArrayList<String>
    ) {
        retrofitServerManager?.setGuardian(
            email,
            utcOffsetAndCountry!!,
            writeTime,
            guardian,
            object : RetrofitServerManager.ServerTaskCallback {
                override fun onSuccess(result: String?) {
                    Log.i("saveGuardianToServer", result!!)
                    if (result.lowercase(Locale.getDefault()).contains("true")) {
                        toast(resources.getString(R.string.setGuardianComp))
                        guardianAlertDialog!!.dismiss()
                    } else {
                        toast(resources.getString(R.string.setGuardianFail))
                        guardianAlertDialog!!.dismiss()
                    }
                }

                override fun onFailure(e: Exception?) {
                    toast(resources.getString(R.string.setGuardianFail))
                    guardianAlertDialog!!.dismiss()
                    Log.e("saveGuardianToServer", "send Err")
                    e!!.printStackTrace()
                }
            })
    }

    fun batReceived(bat: Int) {
        val activity: Activity? = safeGetActivity()
        if (activity != null) {
            val progressBar: ProgressBar = activity.findViewById(R.id.batProgress)
            progressBar.setProgress(bat)
            val batValue: TextView = activity.findViewById(R.id.myBatValue)
            batValue.setText("" + bat)
        }
    }

    // send BLE
    fun ecgChange(check: Boolean) {
        if (check) {
            // peak
            sendTx("e\n")
        } else {
            // ecg
            sendTx("p\n")
        }
    }

    fun sendTx(s: String) {
        try {
            synchronized(lTx) { lTx.add(s) }
        } catch (ignored: Exception) {
        }
    }

    fun notiCheck() {
        try {
            setHeartAttackNotiFlag = userDetailsSharedPref?.getBoolean("s_emergency", true)!!
            setArrNotiFlag = userDetailsSharedPref?.getBoolean("s_arr", true)!!
            setMyoNotiFlag = userDetailsSharedPref?.getBoolean("s_muscle", false)!!
            setNonContactNotiFlag = userDetailsSharedPref?.getBoolean("s_drop", true)!!
            setTachycardiaNotiFlag = userDetailsSharedPref?.getBoolean("s_fastarr", false)!!
            setBradycardiaNotiFlag = userDetailsSharedPref?.getBoolean("s_slowarr", false)!!
            setAtrialFibrillationNotiFlag = userDetailsSharedPref?.getBoolean("s_irregular", false)!!
            userDetailsEditor?.putBoolean("profile2check", false)
            userDetailsEditor?.apply()
        } catch (ignored: Exception) {
        }
    }

    fun statusCheck() {
        val intCurrentHour = currentHour!!.toInt()
        if (eCalBPM <= intRealBPM || tenSecondStep > 6) {
            // 활동중
            bodyStatus = "E"
            exerciseBackground?.setBackground(
                ContextCompat.getDrawable(
                    activityOrThrow!!,
                    R.drawable.rest_round_press
                )
            )
            exerciseText?.setTextColor(Color.WHITE)
            exerciseImg!!.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
            restBackground?.setBackgroundColor(Color.TRANSPARENT)
            restText?.setTextColor(ContextCompat.getColor(activityOrThrow!!, R.color.lightGray))
            restImg?.setColorFilter(ContextCompat.getColor(activityOrThrow!!, R.color.lightGray))
            sleepBackground?.setBackgroundColor(Color.TRANSPARENT)
            sleepText?.setTextColor(ContextCompat.getColor(activityOrThrow!!, R.color.lightGray))
            sleepImg?.setColorFilter(ContextCompat.getColor(activityOrThrow!!, R.color.lightGray))
        } else if ((sleep < intCurrentHour || wakeup > intCurrentHour) && tenSecondStep < 6) {
            // 수면중
            bodyStatus = "S"
            exerciseBackground?.setBackgroundColor(Color.TRANSPARENT)
            exerciseText?.setTextColor(ContextCompat.getColor(activityOrThrow!!, R.color.lightGray))
            exerciseImg?.setColorFilter(ContextCompat.getColor(activityOrThrow!!, R.color.lightGray))
            restBackground?.setBackgroundColor(Color.TRANSPARENT)
            restText?.setTextColor(ContextCompat.getColor(activityOrThrow!!, R.color.lightGray))
            restImg?.setColorFilter(ContextCompat.getColor(activityOrThrow!!, R.color.lightGray))
            sleepBackground?.setBackground(
                ContextCompat.getDrawable(
                    activityOrThrow!!,
                    R.drawable.rest_round_press
                )
            )
            sleepText?.setTextColor(Color.WHITE)
            sleepImg!!.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
        } else {
            // 휴식중
            bodyStatus = "R"
            exerciseBackground?.setBackgroundColor(Color.TRANSPARENT)
            exerciseText?.setTextColor(ContextCompat.getColor(activityOrThrow!!, R.color.lightGray))
            exerciseImg?.setColorFilter(ContextCompat.getColor(activityOrThrow!!, R.color.lightGray))
            restBackground?.setBackground(
                ContextCompat.getDrawable(
                    activityOrThrow!!,
                    R.drawable.rest_round_press
                )
            )
            restText?.setTextColor(Color.WHITE)
            restImg!!.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY)
            sleepBackground?.setBackgroundColor(Color.TRANSPARENT)
            sleepText?.setTextColor(ContextCompat.getColor(activityOrThrow!!, R.color.lightGray))
            sleepImg?.setColorFilter(ContextCompat.getColor(activityOrThrow!!, R.color.lightGray))
        }
    }

    fun stopThread() {
        if (thread != null) thread!!.interrupt()
        isRun = false
    }

    fun restartThread() {
        Log.v("doLoop", "restartThread")
        stopThread()
        isRun = true
        doLoop()
    }

    fun timeZone() {
        // 국가 코드
        val current: Locale = activityOrThrow!!.getResources().getConfiguration().getLocales().get(0)
        currentCountry = current.country

        // 현재 시스템의 기본 타임 존
        val currentTimeZone = TimeZone.getDefault()

        // 타임 존의 아이디
        val timeZoneId = currentTimeZone.id
        val zoneId = ZoneId.of(timeZoneId)
        val offset = LocalDateTime.now().atZone(zoneId).offset
        val utcTime = offset.toString()
        val firstChar = utcTime[0].toString()
        utcOffset = if (firstChar == "+" || firstChar == "-") {
            utcTime
        } else {
            "+$utcTime"
        }
        utcOffsetAndCountry = "$utcOffset/$currentCountry"
    }

    fun currentUtcTime(): String {
        val now = ZonedDateTime.now(ZoneOffset.UTC)
        val currentTimeZone = TimeZone.getDefault()
        val timeZoneId = currentTimeZone.id
        val currentTimezone = now.withZoneSameInstant(ZoneId.of(timeZoneId))
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        return formatter.format(currentTimezone)
    }

    private fun safeGetActivity(): FragmentActivity? {
        if (activity == null) {
            Log.e("HomeFragment", "Fragment is not attached to an activity.")
            return null
        }
        return activity
    }

    private val activityOrThrow: FragmentActivity?
        private get() = Objects.requireNonNull<FragmentActivity?>(safeGetActivity())

    fun runOnActivityUiThread(action: Runnable?) {
        if (activity != null) {
            activityOrThrow?.runOnUiThread(action)
        } else {
            Log.e("runOnActivityUiThread", "Activity is null. Cannot run on UI thread.")
        }
    }

    fun toast(string: String?) {
        runOnActivityUiThread {
            Toast.makeText(safeGetActivity(), string, Toast.LENGTH_SHORT).show()
        }
    }

    private val address: String
        private get() {
            val latitude: Double = gpsTracker!!.getLatitude() // 위도
            val longitude: Double = gpsTracker!!.getLongitude() // 경도
            return getCurrentAddress(latitude, longitude)
        }

    fun getCurrentAddress(latitude: Double, longitude: Double): String {
        val geocoder = Geocoder(safeGetActivity()!!, Locale.getDefault())
        val addresses: List<Address>
        addresses = try {
            geocoder.getFromLocation(
                latitude,
                longitude,
                7
            )!!
        } catch (ioException: IOException) {
            //네트워크 문제
            toast("geocoder Service unavailable")
            return "geocoder Service unavailable"
        } catch (illegalArgumentException: IllegalArgumentException) {
            toast("Invalid GPS coordinates")
            return "Invalid GPS coordinates"
        }
        if (addresses == null || addresses.size == 0) {
            toast("Address not found")
            return "Address not found"
        }
        val address = addresses[0]
        return """
               ${address.getAddressLine(0)}
               
               """.trimIndent()
    }

    /*PERMISSION*/ //region
    private fun permissionCheck() { // 권한 확인
        permissionManager = PermissionManager.getInstance(this, getActivity(), this, context, email)
        if (permissionManager!!.checkAndRequestPermissions(email)) // permission all granted
            onAllPermissionsGranted() else permissionDeniedCheck()
    }

    // 모든 권한 승인 callback
    override fun onPermissionGranted() {
        onAllPermissionsGranted()
    }

    // 하나 이상의 권한 거부 callback
    override fun onPermissionDenied(
        grantedPermissions: List<String>?,
        deniedPermissions: List<String>?
    ) {
        permissionDeniedCheck()
    }

    override fun setGuardianFlag() {
        setGuardian()
    }

    private fun onAllPermissionsGranted() {
        notificationManager = NotificationManager.getInstance(safeGetActivity()!!)
        BleConnectCheck()
    }

    private fun permissionDeniedCheck() {
        if (hasBlePermissions(context)) { // BLE
            BleConnectCheck()
            permissionManager!!.showPermissionDialog()
        }
        if (Build.VERSION.SDK_INT >= ANDROID_O_MR1) { // NOTIFICATION
            if (ContextCompat.checkSelfPermission(
                    Objects.requireNonNull(
                        requireContext()
                    ), Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) notificationManager = NotificationManager.getInstance(safeGetActivity()!!)
        } else {
            notificationManager = NotificationManager.getInstance(safeGetActivity()!!)
        }
    }

    private fun hasBlePermissions(context: Context?): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.BLUETOOTH_SCAN
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun setGuardian() {
        if (!guardianCheck) // first time check
            guardianEvent() // set guardian
    }

    //endregion
    /*LIFECYCLE*/ //region
    @SuppressLint("MissingPermission")
    override fun onDestroy() {
        super.onDestroy()
        Log.d("Fragment", "onDestroy called")
        localDataLoop = false
        PermissionManager.reset()
        activityOrThrow?.unregisterReceiver(mBluetoothStateReceiver)
        stopScanning()
        stopThread()
        if (serviceIntent != null) stopService()

        // 블루투스 연결 활성화 시 연결 해제
        try {
            if (bluetoothGatt != null) {
                Log.v("BLE_STATE", "DISCONNECT")
                sendCalcDataAsCsv(
                    currentHour,
                    sendHourlyStep,
                    sendHourlyDistance,
                    sendHourlyTCal,
                    sendHourlyECal,
                    sendHourlyArrCnt
                )
                bluetoothGatt?.disconnect() // gatt 클라이언트에서 gatt 서버 연결 해제
                bluetoothGatt?.close() // 클라이언트, 리소스 해제
            }
        } catch (e: Exception) {
            Log.e("BLE BLE_STATE", "DISCONNECT FAIL")
            e.printStackTrace()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
        if (permissionManager?.flag!!) { // ACCESS_BACKGROUND_LOCATION_REQUEST_CODE 설정 후 다시 돌아올 때 발생
            setGuardian()
            permissionManager?.setFlag()
        }
    }

    override fun onPause() {
        super.onPause()
        if (onBackPressedDialog != null && onBackPressedDialog.isShowing) {
            onBackPressedDialog.dismiss()
        }
    } //endregion

    companion object {
        /*final*/ //region
        const val ACTION_DATA_AVAILABLE = "com.example.bluetooth.le.ACTION_DATA_AVAILABLE"
        val UUID_SERVICE = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
        val UUID_RECIEVE = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E")
        val UUID_TX = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
        val UUID_CONFIG = UUID.fromString("00002902-0000-1000-8000-00805f9b34fb")
        private const val ANDROID_O_MR1 = 33
        private const val SCAN_PERIOD: Long = 10000
        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTED = 2
        private const val MAX_RETRY_COUNT = 6
        private const val _MAX_CH = 6
        private const val GRAPH_MAX = 500
        private const val NOTIFICATION_ID = 1610
        private const val ARR_NOTIFICATION_ID = 1611

        //endregion
        /*ARR*/ //region
        private const val ARR = "arr"
        private const val BRADYCARDIA = "bradycardia"
        private const val TACHYCARDIA = "tachycardia"
        private const val ATRIAL_FIBRILLATION = "atrialFibrillation"
        private const val MYO = "myo"
        private const val NON_CONTACT = "nonContact"
        private const val ARR_50 = "50"
        private const val ARR_100 = "100"
        private const val ARR_200 = "200"
        private const val ARR_300 = "300"
        private const val STATUS_TACHYCARDIA = "fast"
        private const val STATUS_BRADYCARDIA = "slow"
        private const val STATUS_IRREGULAR = "irregular"
        private const val EVENT_ARR = 1
        private const val EVENT_HEARTATTACK = 2
        private const val EVENT_NON_CONTACT = 3
        private const val EVENT_MYO = 4
        private const val EVENT_FAST_ARR = 5
        private const val EVENT_SLOW_ARR = 6
        private const val EVENT_IRREGULAR_ARR = 7
    }
}