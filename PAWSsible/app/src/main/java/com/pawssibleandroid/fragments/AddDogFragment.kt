package com.pawssibleandroid.fragments

import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.gson.JsonElement
import com.pawssibleandroid.BaseApplication
import com.pawssibleandroid.R
import com.pawssibleandroid.activities.MainActivity
import com.pawssibleandroid.databinding.FragmentAddDogBinding
import com.pawssibleandroid.interfaces.IParseListener
import com.pawssibleandroid.models.DogModel
import com.pawssibleandroid.utils.PawssibleStorage
import com.pawssibleandroid.utils.StaticUtils
import com.pawssibleandroid.wsutils.WSCallBacksListener
import com.pawssibleandroid.wsutils.WSUtils
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import retrofit2.Call

private const val ARG_PARAM1 = "param1"

/**
 * Add/Edit/Delete dog features are in this screen
 */
class AddDogFragment : Fragment(), View.OnClickListener, IParseListener<JsonElement?> {

    private var dogModel: DogModel? = null
    private var fragmentAddDogBinding: FragmentAddDogBinding? = null
    private var mainActivity: MainActivity? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            dogModel = it.getParcelable(ARG_PARAM1)
        }
    }

    override fun onResume() {
        super.onResume()
        if (mainActivity != null) {
            mainActivity?.showTopBar("Add/Edit Dog", true)
            mainActivity?.hideBottomBar()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentAddDogBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_add_dog, container, false)
        initComponents()
        return fragmentAddDogBinding?.getRoot()
    }

    private fun initComponents() {
        setData()
        fragmentAddDogBinding!!.btnAdd.setOnClickListener(this)
        fragmentAddDogBinding!!.btnDelete.setOnClickListener(this)
    }

    companion object {
        @JvmStatic
        fun newInstance(dogModel: DogModel?) =
            AddDogFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_PARAM1, dogModel)
                }
            }
    }

    /**
     * Set data
     */
    private fun setData() {
        if (dogModel != null) {
            fragmentAddDogBinding!!.edtName.setText(dogModel!!.breedName)
            fragmentAddDogBinding!!.edtLikes.setText(dogModel!!.likes)
            fragmentAddDogBinding!!.edtDisLikes.setText(dogModel!!.disLikes)
            fragmentAddDogBinding!!.edtAllergies.setText(dogModel!!.allergies)
            fragmentAddDogBinding!!.edtDescription.setText(dogModel!!.description)
            fragmentAddDogBinding!!.edtAge.setText("${dogModel!!.ageInMonths}")
            fragmentAddDogBinding!!.edtRate.setText("${dogModel!!.hourlyPrice}")
            fragmentAddDogBinding!!.edtImageLink.setText(dogModel!!.photo)
            try {
                if (!TextUtils.isEmpty(dogModel!!.photo)) {
                    Glide.with(mainActivity!!).load(dogModel!!.photo)
                        .into(fragmentAddDogBinding!!.imgDog)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            fragmentAddDogBinding!!.btnAdd.setText(R.string.submit_changes)
            fragmentAddDogBinding!!.btnDelete.setText(if (dogModel!!.active) R.string.delete_dog else R.string.activate_dog)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnAdd -> addDog()
            R.id.btnDelete -> deleteDog()
            else -> {
            }
        }
    }

    /**
     * check and add or edit dog
     */
    private fun addDog() {
        assignDog()
        if (fragmentAddDogBinding!!.btnAdd.text.toString().trim()
                .equals(getString(R.string.add_dog), true)
        ) {
            requestForAddDog()
        } else {
            requestForEditDog()
        }
    }

    /**
     * method assigning data for api parsing
     */
    private fun assignDog() {
        if (dogModel == null) {
            dogModel = DogModel()
        }
        dogModel!!.breedName = fragmentAddDogBinding!!.edtName.text.toString().trim()
        dogModel!!.hourlyPrice = fragmentAddDogBinding!!.edtRate.text.toString().trim().toDouble()
        dogModel!!.description = fragmentAddDogBinding!!.edtDescription.text.toString().trim()
        dogModel!!.ageInMonths = fragmentAddDogBinding!!.edtAge.text.toString().trim().toInt()
        dogModel!!.allergies = fragmentAddDogBinding!!.edtAllergies.text.toString().trim()
        dogModel!!.disLikes = fragmentAddDogBinding!!.edtDisLikes.text.toString().trim()
        dogModel!!.likes = fragmentAddDogBinding!!.edtLikes.text.toString().trim()
        dogModel!!.ownerId =
            BaseApplication.pawssibleStorage?.getValue(PawssibleStorage.SP_USER_ID, "")!!
        dogModel!!.photo = if (fragmentAddDogBinding!!.edtImageLink.text.toString().trim()
                .isEmpty()
        ) "https://images.dog.ceo/breeds/akita/Akita_Dog.jpg" else fragmentAddDogBinding!!.edtImageLink.text.toString()
            .trim()
    }

    /**
     * API for delete dog
     */
    private fun deleteDog() {
        assignDog()
        dogModel?.active = dogModel?.active?.not()!!
        val call: Call<JsonElement> =
            BaseApplication.instance?.wsClientListener!!.updateDog(getRequestBodyObject())
        WSCallBacksListener().requestForJsonObject(
            mainActivity,
            WSUtils.REQ_FOR_EDIT_DOG,
            call,
            this
        )
    }

    /**
     * API for add dog
     */
    private fun requestForAddDog() {
        val call: Call<JsonElement> =
            BaseApplication.instance?.wsClientListener!!.addDog(getRequestBodyObject())
        WSCallBacksListener().requestForJsonObject(
            mainActivity,
            WSUtils.REQ_FOR_ADD_DOG,
            call,
            this
        )
    }

    /**
     * API for update dog
     */
    private fun requestForEditDog() {
        val call: Call<JsonElement> =
            BaseApplication.instance?.wsClientListener!!.updateDog(getRequestBodyObject())
        WSCallBacksListener().requestForJsonObject(
            mainActivity,
            WSUtils.REQ_FOR_EDIT_DOG,
            call,
            this
        )
    }


    /**
     * making api request data formatted
     */
    private fun getRequestBodyObject(): RequestBody {
        val jsonObjectReq = JSONObject()
        try {
            if (!TextUtils.isEmpty(dogModel?.dogId)) {
                jsonObjectReq.put("dogId", dogModel?.dogId)
            }
            jsonObjectReq.put("breedname", dogModel!!.breedName)
            jsonObjectReq.put("description", dogModel!!.description)
            jsonObjectReq.put("allergies", dogModel!!.allergies)
            jsonObjectReq.put("active", dogModel!!.active)
            jsonObjectReq.put("likes", dogModel!!.likes)
            jsonObjectReq.put("disLikes", dogModel!!.disLikes)
            jsonObjectReq.put("ageInMonths", dogModel!!.ageInMonths)
            jsonObjectReq.put("hourlyPrice", dogModel!!.hourlyPrice)
            jsonObjectReq.put("photo", dogModel!!.photo)
            jsonObjectReq.put("ownerId", dogModel!!.ownerId)
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return StaticUtils.getRequestBodyJson(jsonObjectReq)
    }

    /**
     * Success response parsing
     */
    override fun onSuccess(code: Int, response: JsonElement?) {
        when (code) {
            WSUtils.REQ_FOR_ADD_DOG -> {
                StaticUtils.showToast(mainActivity, "Added Successfully")
                mainActivity!!.onBackPressed()
            }
            WSUtils.REQ_FOR_EDIT_DOG -> {
                StaticUtils.showToast(mainActivity, "Updated Successfully")
                mainActivity!!.onBackPressed()
            }
            else -> {
            }
        }
    }

    /**
     * Error response parsing
     */
    override fun onError(code: Int, error: String?) {
        StaticUtils.showToast(mainActivity, error)
    }

    /**
     * No network
     */
    override fun onNoNetwork(code: Int) {
        StaticUtils.showToast(mainActivity, "Please check your internet connection")
    }

}