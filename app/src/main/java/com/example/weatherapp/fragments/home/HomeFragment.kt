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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.Manifest
import androidx.appcompat.app.AlertDialog



class HomeFragment : Fragment(){

    private var _binding : FragmentHomeBinding? = null
    private val  binding get() = requireNotNull(_binding)

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
        setWeatherData()
    }

    private fun setWeatherDataAdapter(){
        binding.weatherDataRecyclerView.adapter = weatherDataAdapter
    }

    private fun setWeatherData(){
        weatherDataAdapter.setData(data = listOf(WeatherData.CurrentLocation(data = getCurrentData())))
    }

    private  fun getCurrentData() : String{
        val currentData = Date()
        val formatter = SimpleDateFormat("d MMMM , yyyy" , Locale.getDefault())
        return "Today, $(formatter.format(currentData)"

    }

    private fun getCurrentLocation(){
        Toast.makeText(requireContext(),"getCurrentLocation()", Toast.LENGTH_SHORT).show()
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
}