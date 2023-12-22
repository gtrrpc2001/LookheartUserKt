package com.mcuhq.simplebluetooth.permission

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.mcuhq.simplebluetooth.R

class PermissionManager {
    private var fragment: Fragment? = null
    private var context: Context? = null
    private var activity: Activity? = null
    private var permissions: MutableList<String>? = null
    private lateinit var PERMISSIONS: Array<String>
    private var multiplePermissionLauncher: ActivityResultLauncher<Array<String>>? = null
    private var permissionRequestCount = 0
    private var permissionCallback: PermissionCallback? = null // 콜백 인스턴스 변수
    var flag = false
        private set

    interface PermissionCallback {
        fun onPermissionGranted()
        fun onPermissionDenied(grantedPermissions: List<String>?, deniedPermissions: List<String>?)
        fun setGuardianFlag()
    }

    fun setPermissionCallback(permissionCallback: PermissionCallback?) {
        this.permissionCallback = permissionCallback
    }

    // fragment
    constructor(
        myFragment: Fragment?,
        myActivity: Activity?,
        permissionCallback: PermissionCallback?,
        myContext: Context?,
        email: String?
    ) {
        activity = myActivity
        fragment = myFragment
        context = myContext
        permissions = ArrayList()
        permissionRequestCount =
            SharedPrefManager.getInstance(context!!, email!!)!!.permissionCheck()
        setPermissionCallback(permissionCallback)
        initializePermissions()
        initializePermissionLauncher()
    }

    // activity
    constructor(activity: Activity?) {
        this.activity = activity
        requestPostNotificationsPermission()
    }

    private fun initializePermissions() {
        // 필요 권한 추가
        permissions!!.add(Manifest.permission.ACCESS_FINE_LOCATION)
        permissions!!.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissions!!.add(Manifest.permission.BLUETOOTH_SCAN)
            permissions!!.add(Manifest.permission.BLUETOOTH_CONNECT)
        }
        if (Build.VERSION.SDK_INT >= 33) {
            permissions!!.add(Manifest.permission.POST_NOTIFICATIONS)
        }
        PERMISSIONS = permissions!!.toTypedArray()
    }

    private fun initializePermissionLauncher() {

        // 사용자 권한 응답에 대한 처리
        multiplePermissionLauncher =
            fragment!!.registerForActivityResult<Array<String>, Map<String, Boolean>>(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { isGranted: Map<String, Boolean> ->
                val grantedPermissions: MutableList<String> = ArrayList()
                val deniedPermissions: MutableList<String> = ArrayList()
                if (isGranted.containsValue(false)) {
                    // 하나 이상의 권한 거부
                    Toast.makeText(
                        fragment!!.context,
                        context!!.resources.getString(R.string.permissionToast),
                        Toast.LENGTH_SHORT
                    ).show()
                    for ((key, value) in isGranted) {
                        if (!value) { // 거부된 권한
                            deniedPermissions.add(key)
                            Log.d("PERMISSIONS", "Permission denied: $key")
                        } else { // 승인된 권한
                            grantedPermissions.add(key)
                            Log.d("PERMISSIONS", "Permission granted: $key")
                        }
                    }
                } else {
                    // 모든 권한이 승인
                    Log.d("PERMISSIONS", "Permission All granted")
                    for ((key, value) in isGranted) {
                        if (value) {
                            Log.d("PERMISSIONS", "Permission granted: $key")
                        }
                    }
                    showPermissionDialog()
                }
                if (!deniedPermissions.isEmpty()) { // Callback
                    if (permissionCallback != null) { // 하나 이상의 권한 거부
                        permissionCallback!!.onPermissionDenied(
                            grantedPermissions,
                            deniedPermissions
                        )
                    }
                } else {
                    if (permissionCallback != null) { // 모든 권한 승인
                        permissionCallback!!.onPermissionGranted()
                    }
                }
            }
    }

    fun checkAndRequestPermissions(email: String?): Boolean {
        return if (!hasPermissions(PERMISSIONS)) {
            // 일부 권한 또는 모든 권한 거부
            if (permissionRequestCount < PERMISSION_MAX) {
                multiplePermissionLauncher!!.launch(PERMISSIONS)
                permissionRequestCount++
                SharedPrefManager.getInstance(context!!, email!!)!!
                    .setPermissionCnt(permissionRequestCount)
                false
            } else {
                showSettingsAlert()
                false
            }
        } else {
            // 모든 권한 승인
            Log.d("RequestPermissions", "true")
            true
        }
    }

    private fun hasPermissions(permissions: Array<String>): Boolean {
        return if (fragment!!.isAdded && fragment!!.context != null) { // 프래그먼트 상태 확인
            for (permission in permissions) {
                if (ContextCompat.checkSelfPermission(
                        fragment!!.requireContext(),
                        permission
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    Log.d("Permission denied", permission)
                    return false
                } else {
                    Log.d("permission granted", permission)
                }
            }
            true
        } else {
            Log.e("PermissionManager", "Fragment not added or context is null.")
            false // 프래그먼트가 비활성 상태이거나 Context가 null인 경우
        }
    }

    private fun showSettingsAlert() {
        val alertDialog = AlertDialog.Builder(
            fragment!!.context
        )
        alertDialog.setTitle(context!!.resources.getString(R.string.requiredPermission))
        alertDialog.setMessage(context!!.resources.getString(R.string.requiredPermissionList))
        alertDialog.setPositiveButton(context!!.resources.getString(R.string.goToSettings)) { dialog, which ->
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts(
                "package", fragment!!.activity!!
                    .packageName, null
            )
            intent.data = uri
            fragment!!.startActivity(intent)
        }
        alertDialog.setNegativeButton(context!!.resources.getString(R.string.rejectLogout)) { dialog, which -> dialog.cancel() }
        alertDialog.show()
    }

    fun requestPostNotificationsPermission() {
        // Notification 권한 확인
        if (ContextCompat.checkSelfPermission(activity!!, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            // 권한 요청
            ActivityCompat.requestPermissions(
                activity!!, arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                NOTIFICATIONS_REQUEST_CODE
            )
        }
    }

    // Android API 30 and above requires setting the backgroundPermission directly
    private fun requestBackgroundPermission() {
        ActivityCompat.requestPermissions(
            activity!!, arrayOf(Manifest.permission.ACCESS_BACKGROUND_LOCATION),
            ACCESS_BACKGROUND_LOCATION_REQUEST_CODE
        )
    }

    // Background permission request dialog
    fun showPermissionDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(context!!.resources.getString(R.string.locationPermission))
        builder.setMessage(context!!.resources.getString(R.string.locationPermissionMessage))
        val listener = DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int ->
            flag = true
            if (which == DialogInterface.BUTTON_POSITIVE) {
                requestBackgroundPermission()
            }
        }
        val guardianFlag =
            DialogInterface.OnClickListener { dialog: DialogInterface?, which: Int -> permissionCallback!!.setGuardianFlag() }
        builder.setPositiveButton(context!!.resources.getString(R.string.ok), listener)
        builder.setNegativeButton(
            context!!.resources.getString(R.string.rejectLogout),
            guardianFlag
        )
        builder.show()
    }

    fun setFlag() {
        flag = false
    }

    companion object {
        private const val PERMISSION_MAX = 2
        private const val NOTIFICATIONS_REQUEST_CODE = 3000
        private const val ACCESS_BACKGROUND_LOCATION_REQUEST_CODE = 3001
        private var instance: PermissionManager? = null
        @JvmStatic
        fun getInstance(
            fragment: Fragment?,
            myActivity: Activity?,
            permissionCallback: PermissionCallback?,
            context: Context?,
            email: String?
        ): PermissionManager? {
            if (instance == null) {
                instance =
                    PermissionManager(fragment, myActivity, permissionCallback, context, email)
            }
            return instance
        }

        @JvmStatic
        fun reset() {
            instance = null // 싱글턴 인스턴스 해제
        }
    }
}