package com.example.weatherapp.fragments.home

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.weatherapp.data.WeatherData
import com.example.weatherapp.databinding.FragmentHomeBinding
import android.Manifest
import android.location.Geocoder
import androidx.appcompat.app.AlertDialog
import com.example.weatherapp.storage.SharedPreferencesManager
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class HomeFragment : Fragment(){

    private var _binding : FragmentHomeBinding? = null
    private val  binding get() = requireNotNull(_binding)

    private  val homeViewModule : HomeViewModule by viewModel()

    private  val  fusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(requireContext())
    }
    private val  geocoder by lazy { Geocoder(requireContext()) }

    private val weatherDataAdapter = WeatherDataAdapter(
        onlocationClicked = {
            showLocationOptions()
        }
    )

    private val  locatonPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ){ isGranted ->

        if(isGranted){
            getCurrentLocation()
        }else{
            Toast.makeText(requireContext() ,"Permission denied" , Toast.LENGTH_SHORT).show()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setWeatherDataAdapter()
        setWeatherData(currentLocation = sharedPreferencesManager.getCurrentLocation())
        setObservers()
        proceedWithCurrentLocation()
    }

    private fun setWeatherDataAdapter(){
        binding.weatherDataRecyclerView.adapter = weatherDataAdapter
    }

    private val sharedPreferencesManager : SharedPreferencesManager by inject()

    private  fun setObservers(){
        with(homeViewModule){
            currentLocation.observe(viewLifecycleOwner){
                val currentLocationDataState = it ?: return@observe
                if(currentLocationDataState.isLoading){
                    showLoading()
                }
                currentLocationDataState.currentLocation?.let { currentLocation ->
                    hideLoading()
                    sharedPreferencesManager.saveCurrentLocation(currentLocation)
                    setWeatherData(currentLocation)
                }
                currentLocationDataState.error?.let { error ->
                    hideLoading()
                    Toast.makeText(requireContext(),error, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setWeatherData(currentLocation: WeatherData.CurrentLocation? = null){
        weatherDataAdapter.setData(data = listOf(currentLocation ?: WeatherData.CurrentLocation()))
    }



    private fun getCurrentLocation(){
        homeViewModule.getCurrentLocation(fusedLocationProviderClient , geocoder)
    }

    private fun isLocationPermissionGranted() : Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext() ,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

    private  fun  requestLocationPermission(){
        locatonPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    private  fun proceedWithCurrentLocation(){
        if(isLocationPermissionGranted()){
            getCurrentLocation()
        }else{
            requestLocationPermission()
        }
    }

    private  fun showLocationOptions(){
        val options = arrayOf("Currrent Location" , "Search Manullay")
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Choose location Method")
            setItems(options){ _, which ->
                when (which){
                    0 -> proceedWithCurrentLocation()
                }
            }
            show()
        }
    }

    private  fun showLoading(){
        with(binding){
            weatherDataRecyclerView.visibility = View.GONE
            swipeRefreshLayout.isRefreshing = true
        }
    }

    private fun hideLoading(){
        with(binding){
            weatherDataRecyclerView.visibility = View.VISIBLE
            swipeRefreshLayout.isRefreshing = false
        }
    }
}