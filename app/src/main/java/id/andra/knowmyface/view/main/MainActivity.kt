package id.andra.knowmyface.view.main

import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import id.andra.knowmyface.databinding.ActivityMainBinding
import id.andra.knowmyface.helper.Constant
import id.andra.knowmyface.helper.PermissionHelper
import id.andra.knowmyface.helper.SharedPreferenceHelper
import id.andra.knowmyface.view.component.LoadingDialog
import id.andra.knowmyface.view.component.alertDialog.MyAlertDialog
import id.andra.knowmyface.view.login.LoginActivity
import id.andra.knowmyface.view.presenceHistory.PresenceHistoryActivity
import id.andra.knowmyface.view.recordPresence.RecordPresenceActivity

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding
    private var loadingDialog: LoadingDialog? = null
    private var alertDialog: MyAlertDialog? = null
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        binding.executePendingBindings()
        setContentView(binding.root)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        // request location permission
        if (!PermissionHelper.isLocationPermissionGranted(this))
            PermissionHelper.requestLocationPermission(this)
        // request camera permissions
        if (!PermissionHelper.isCameraPermissionGranted(this))
            PermissionHelper.requestCameraPermission(this)
        val user = SharedPreferenceHelper.getUser(this)
        viewModel.setUserName(user?.name.orEmpty())
        observeState()
        setEventListeners()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // handle request location result
        if (requestCode == Constant.LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (viewModel.isWaitForLocationPerm.value == true) {
                    getLastLocation()
                    viewModel.setIsWaitForLocationPerm(false)
                }
            } else
                Toast.makeText(
                    this,
                    "Aplikasi memerlukan akses lokasi!",
                    Toast.LENGTH_LONG
                ).show()
        }
        // handle request camera result
        if (requestCode == Constant.CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] != PackageManager.PERMISSION_GRANTED)
                Toast.makeText(
                    this,
                    "Aplikasi memerlukan akses kamera!",
                    Toast.LENGTH_LONG
                ).show()
        }
    }

    private fun toggleLoadingDialog(isShow: Boolean) {
        if (isShow) {
            loadingDialog = LoadingDialog()
            loadingDialog?.show(supportFragmentManager, "LOADING_DIALOG")
            return
        }
        loadingDialog?.dismiss()
    }

    private fun toggleErrorDialog(isShow: Boolean) {
        if (isShow) {
            alertDialog = MyAlertDialog.getInstance(
                title = "Kesalahan",
                message = viewModel.checkStatusError.value.orEmpty(),
                onSubmit = {
                    viewModel.toggleShowErrorDialog(false)
                }
            )
            alertDialog?.show(supportFragmentManager, "ALERT_DIALOG")
            return
        }
        alertDialog?.dismiss()
    }

    private fun getLastLocation() {
        try {
            viewModel.setIsLoading(true)
            val locationResult: Task<Location> = fusedLocationClient.lastLocation
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    val latitude = location.latitude
                    val longitude = location.longitude
                    viewModel.setLatitude(latitude.toString())
                    viewModel.setLongitude(longitude.toString())
                    viewModel.checkPresenceStatus()
                } else {
                    viewModel.setIsLoading(false)
                    Toast.makeText(
                        this,
                        "Gagal mendapatkan lokasi, Pastikan GPS anda aktif!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
            viewModel.setIsLoading(false)
        }
    }

    private fun observeState() {
        viewModel.isRedirectToRecordPresence.observe(this) { value ->
            if (value) {
                viewModel.setIsRedirectToRecordPresence(false)
                val checkStatusResponse = viewModel.checkStatusResponse.value?.message
                if (checkStatusResponse == "${Constant.PRESENCE_CLOCKOUT_STATE}") {
                    Toast.makeText(this, "Anda sudah absen hari ini", Toast.LENGTH_LONG).show()
                    return@observe
                }
                val intent = Intent(this, RecordPresenceActivity::class.java)
                intent.putExtra("longitude", viewModel.longitude.value.orEmpty())
                intent.putExtra("latitude", viewModel.latitude.value.orEmpty())
                intent.putExtra("presenceState", checkStatusResponse)
                startActivity(intent)
            }
        }
        viewModel.isLoading.observe(this) { value ->
            toggleLoadingDialog(value)
        }
        viewModel.showErrorDialog.observe(this) { value ->
            toggleErrorDialog(value)
        }
    }

    private fun setEventListeners() {
        binding.btnRecordPresence.setOnClickListener {
            if (!PermissionHelper.isCameraPermissionGranted(this)) {
                PermissionHelper.requestCameraPermission(this)
                return@setOnClickListener
            }
            if (!PermissionHelper.isLocationPermissionGranted(this)) {
                viewModel.setIsWaitForLocationPerm(true)
                PermissionHelper.requestLocationPermission(this)
                return@setOnClickListener
            }
            getLastLocation()
        }
        binding.btnLogout.setOnClickListener {
            val builder = AlertDialog.Builder(this).apply {
                setTitle(title)
                setMessage("Anda yakin ingin keluar?")
                setPositiveButton("Ya") { dialog, _ ->
                    SharedPreferenceHelper.clear(this@MainActivity)
                    dialog.dismiss()
                    finish()
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                }
                setNegativeButton("Batal") { dialog, _ ->
                    dialog.dismiss()
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
        binding.btnProfile.setOnClickListener {

        }
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, PresenceHistoryActivity::class.java))
        }
    }

}