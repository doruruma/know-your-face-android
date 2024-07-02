package id.andra.knowmyface.view.main

import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.tasks.Task
import id.andra.knowmyface.databinding.ActivityMainBinding
import id.andra.knowmyface.helper.PermissionHelper

class MainActivity : AppCompatActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: ActivityMainBinding
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
        if (PermissionHelper.requestLocationPermissionCallback(
                requestCode = requestCode,
                grantResults = grantResults
            )
        ) {
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
        // handle request camera result
        if (!PermissionHelper.requestCameraPermissionCallback(
                requestCode = requestCode,
                grantResults = grantResults
            )
        )
            Toast.makeText(
                this,
                "Aplikasi memerlukan akses kamera!",
                Toast.LENGTH_LONG
            ).show()
    }

    private fun getLastLocation() {
        try {
            val locationResult: Task<Location> = fusedLocationClient.lastLocation
            locationResult.addOnCompleteListener(this) { task ->
                if (task.isSuccessful && task.result != null) {
                    val location = task.result
                    val latitude = location.latitude
                    val longitude = location.longitude
                    viewModel.setLatitude(latitude.toString())
                    viewModel.setLongitude(longitude.toString())
                    viewModel.setIsRedirectToRecordPresence(true)
                    Toast.makeText(
                        this,
                        "Latitude: $latitude, Longitude: $longitude",
                        Toast.LENGTH_LONG
                    ).show()
                } else {
                    Toast.makeText(
                        this,
                        "Gagal mendapatkan lokasi, Pastikan GPS anda aktif!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun observeState() {
        viewModel.isRedirectToRecordPresence.observe(this) { value ->
            if (value) {
                // TODO: open record presence activity
            }
        }
    }

    private fun setEventListeners() {
        binding.btnRecordPresence.setOnClickListener {
            if (!PermissionHelper.isLocationPermissionGranted(this)) {
                viewModel.setIsWaitForLocationPerm(true)
                PermissionHelper.requestLocationPermission(this)
                return@setOnClickListener
            }
            if (!PermissionHelper.isCameraPermissionGranted(this)) {
                PermissionHelper.requestCameraPermission(this)
                return@setOnClickListener
            }
            getLastLocation()
        }
    }

}