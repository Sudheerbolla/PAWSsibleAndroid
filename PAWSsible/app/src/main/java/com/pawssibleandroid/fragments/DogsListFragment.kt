package com.pawssibleandroid.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.pawssibleandroid.BaseApplication
import com.pawssibleandroid.R
import com.pawssibleandroid.activities.MainActivity
import com.pawssibleandroid.adapter.DogsListAdapter
import com.pawssibleandroid.databinding.FragmentDogsListBinding
import com.pawssibleandroid.interfaces.IClickListener
import com.pawssibleandroid.interfaces.IParseListener
import com.pawssibleandroid.models.DogModel
import com.pawssibleandroid.utils.DividerItemDecoration
import com.pawssibleandroid.utils.PawssibleStorage
import com.pawssibleandroid.utils.StaticUtils
import com.pawssibleandroid.wsutils.WSCallBacksListener
import com.pawssibleandroid.wsutils.WSUtils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call
import java.util.*

class DogsListFragment : Fragment(), IClickListener, View.OnClickListener,
    IParseListener<JsonElement?> {
    private var dogModelArrayList: ArrayList<DogModel>? = null
    private var fragmentDogsListBinding: FragmentDogsListBinding? = null
    var dogsListAdapter: DogsListAdapter? = null
    private var mainActivity: MainActivity? = null

    override fun onResume() {
        super.onResume()
        if (mainActivity != null) {
            if (!BaseApplication.isCustomer) {
                mainActivity?.showTopBar("My Dogs", false)
            } else {
                mainActivity?.hideTopBar()
            }
            mainActivity?.showBottomBar()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentDogsListBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_dogs_list, container, false)
        initComponents()
        return fragmentDogsListBinding?.getRoot()
    }

    private fun initComponents() {
        StaticUtils.hideSoftKeyboard(mainActivity!!)
        dogModelArrayList = ArrayList<DogModel>()
        setAdapter()
        requestForDogsList()
        fragmentDogsListBinding!!.imgSearch.requestFocus()
        fragmentDogsListBinding!!.imgAdd.setOnClickListener(this)
        if (BaseApplication.isCustomer) {
            fragmentDogsListBinding!!.relTopBar.setVisibility(View.VISIBLE)
            fragmentDogsListBinding!!.imgAdd.setVisibility(View.GONE)
        } else {
            fragmentDogsListBinding!!.relTopBar.setVisibility(View.GONE)
            fragmentDogsListBinding!!.imgAdd.setVisibility(View.VISIBLE)
        }
    }

    private fun requestForDogsList() {
        val call: Call<JsonElement>
        call = if (BaseApplication.isCustomer) {
            BaseApplication.instance?.wsClientListener!!.getAllDogsForCustomer()
        } else {
            BaseApplication.instance?.wsClientListener!!.getAllDogsForOwner(
                BaseApplication.pawssibleStorage?.getValue(
                    PawssibleStorage.SP_USER_ID,
                    ""
                )
            )
        }
        WSCallBacksListener().requestForJsonObject(
            mainActivity,
            WSUtils.REQ_FOR_GET_ALL_DOGS,
            call,
            this
        )
    }

    private fun setAdapter() {
        fragmentDogsListBinding!!.recyclerView.setLayoutManager(GridLayoutManager(mainActivity, 2))
        fragmentDogsListBinding!!.recyclerView.addItemDecoration(
            DividerItemDecoration(
                mainActivity!!,
                DividerItemDecoration.HORIZONTAL_LIST
            )
        )
        dogsListAdapter = DogsListAdapter(mainActivity!!, dogModelArrayList, this)
        fragmentDogsListBinding!!.recyclerView.setAdapter(dogsListAdapter)
    }

    override fun onClick(view: View?, position: Int) {
        if (BaseApplication.isCustomer)
            mainActivity?.replaceFragment(
                DogDetailsFragment.newInstance(
                    dogModelArrayList!![position]
                ), true
            ) else mainActivity?.replaceFragment(
            AddDogFragment.newInstance(
                dogModelArrayList!![position]
            ), true
        )
    }

    override fun onLongClick(view: View?, position: Int) {}

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imgAdd -> mainActivity?.replaceFragment(AddDogFragment.newInstance(null), true)
        }
    }

    override fun onSuccess(requestCode: Int, response: JsonElement?) {
        when (requestCode) {
            WSUtils.REQ_FOR_GET_ALL_DOGS -> parseGetDogsListResponse(response as JsonObject)
            else -> {
            }
        }
    }

    private fun parseGetDogsListResponse(response: JsonObject) {
        try {
            val listType = object : TypeToken<ArrayList<DogModel?>?>() {}.type
            dogModelArrayList =
                GsonBuilder().create().fromJson<ArrayList<DogModel>>(response.get("dogs").asJsonArray, listType)
            setAdapter()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onError(code: Int, error: String?) {
        StaticUtils.showToast(mainActivity, error)
    }

    override fun onNoNetwork(code: Int) {
        StaticUtils.showToast(mainActivity, "Please check your internet connection")
    }

    companion object {
        fun newInstance(): DogsListFragment {
            val fragment = DogsListFragment()
            val args = Bundle()
            fragment.setArguments(args)
            return fragment
        }
    }

}