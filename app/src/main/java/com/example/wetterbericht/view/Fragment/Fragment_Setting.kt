package com.example.wetterbericht.view.Fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.wetterbericht.R
import com.example.wetterbericht.model.repo.api.mainrepo
import com.example.wetterbericht.viewmodel.api.Mainviewmodel
import com.example.wetterbericht.viewmodel.api.Vmfactory
import com.example.wetterbericht.viewmodel.room.Cuacaviewmodel
import com.example.wetterbericht.viewmodel.room.todoviewmodel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import kotlinx.android.synthetic.main.fragment_setting.view.*

/**
 * A simple [Fragment] subclass.
 * Use the [Fragment_Setting.newInstance] factory method to
 * create an instance of this fragment.
 */
class Fragment_Setting : Fragment() {
    lateinit var mviewmodel : todoviewmodel
    lateinit var mapiviewmodel : Mainviewmodel
    lateinit var mroomviewmodel : Cuacaviewmodel

    //location
    lateinit var localclient : FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback
    private var reqcode = 100


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_setting, container, false)
        mviewmodel = ViewModelProvider(this).get(todoviewmodel::class.java)

        //weather
        val repo = mainrepo()
        val vmfactory = Vmfactory(repo)
        mapiviewmodel = ViewModelProvider(this, vmfactory).get(Mainviewmodel::class.java) //set
        mroomviewmodel = ViewModelProvider(this).get(Cuacaviewmodel::class.java)


        view.btn_deletetodo.setOnClickListener {
            val alert = AlertDialog.Builder(requireContext())
            alert.setPositiveButton("yes"){_,_ ->
                mviewmodel.deleteall()
                Toast.makeText(requireContext(), "delete complete", Toast.LENGTH_SHORT).show()
            }
            alert.setNegativeButton("no"){_,_ ->}
            alert.setTitle("Are u sure want to delete all?")
            alert.setMessage("choose")
            alert.create().show()
        }


        view.btn_deletecuaca.setOnClickListener {
            val alert = AlertDialog.Builder(requireContext())
            alert.setPositiveButton("yes"){_,_ ->
                mroomviewmodel.delete()
                Toast.makeText(requireContext(), "delete complete", Toast.LENGTH_SHORT).show()
            }
            alert.setNegativeButton("no"){_,_ ->}
            alert.setTitle("Are u sure want to delete all?")
            alert.setMessage("choose")
            alert.create().show()
        }
        return view
    }

}