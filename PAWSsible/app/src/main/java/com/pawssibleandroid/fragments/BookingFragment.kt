package com.pawssibleandroid.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.pawssibleandroid.BaseApplication
import com.pawssibleandroid.R
import com.pawssibleandroid.activities.MainActivity
import com.pawssibleandroid.databinding.FragmentBookingBinding
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
import java.util.*

class BookingFragment : Fragment(), View.OnClickListener, IParseListener<JsonElement?>,
    DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private var dogModel: DogModel? = null
    private var fragmentBookingBinding: FragmentBookingBinding? = null
    private var hours = 0.0
    private var tax = 0.0
    private var total = 0.0
    private var subTotal = 0.0
    private var mainActivity: MainActivity? = null
    var day = 0
    var month: Int = 0
    var year: Int = 0

    override fun onResume() {
        super.onResume()
        if (mainActivity != null) {
            mainActivity?.showTopBar("Booking", true)
            mainActivity?.hideBottomBar()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = context as MainActivity
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            dogModel = requireArguments().getParcelable(ARG_PARAM1) as DogModel?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBookingBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_booking, container, false)
        initComponents()
        return fragmentBookingBinding?.getRoot()
    }

    var bookingCal: Calendar? = null
    private fun initComponents() {
        setData()
        fragmentBookingBinding?.btnBook?.setOnClickListener(this)
        fragmentBookingBinding?.txtDate?.setOnClickListener {
            bookingCal = Calendar.getInstance()
            val calendar: Calendar = Calendar.getInstance()
            day = calendar.get(Calendar.DAY_OF_MONTH)
            month = calendar.get(Calendar.MONTH)
            year = calendar.get(Calendar.YEAR)
            val datePickerDialog =
                DatePickerDialog(mainActivity!!, this, year, month, day)
            datePickerDialog.show()
        }
        fragmentBookingBinding?.edtHours?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun afterTextChanged(editable: Editable) {
                hours =
                    if (TextUtils.isEmpty(
                            editable.toString().trim { it <= ' ' })
                    ) 0.0 else editable.toString().toDouble()
                calculateAmount()
                updateAmount()
            }
        })
    }

    private fun updateAmount() {
        fragmentBookingBinding?.txtSubTotal?.setText(getString(R.string.subtotal) + subTotal + " CAD")
        fragmentBookingBinding?.txtTaxes?.setText(getString(R.string.tax_12) + tax + " CAD")
        fragmentBookingBinding?.txtTotal?.setText(getString(R.string.grand_total) + total + " CAD")
    }

    private fun setData() {
        if (dogModel != null) {
            fragmentBookingBinding?.txtName?.setText("Dog Breed: " + dogModel?.breedName)
            fragmentBookingBinding?.txtDescription?.setText("Description: " + dogModel?.description)
            fragmentBookingBinding?.txtAge?.setText("Age in Months: " + dogModel?.ageInMonths.toString() + "")
            fragmentBookingBinding?.txtHourlyPrice?.setText("Hourly Price: " + dogModel?.hourlyPrice.toString() + " CAD")
        }
        fragmentBookingBinding?.txtDate?.setText("")
    }

    private fun processPayment() {
        if (fragmentBookingBinding?.txtDate?.text.toString().trim().isEmpty()) {
            StaticUtils.showToast(mainActivity, "Please Select Booking Date")
        } else if (hours == 0.0) {
            StaticUtils.showToast(mainActivity, "Please enter minimum hours for rent")
        } else {
            requestForCreateBooking()
        }
    }

    private fun requestForCreateBooking() {
        val call: Call<JsonElement> =
            BaseApplication.instance?.wsClientListener!!.createBooking(requestBodyObject)
        WSCallBacksListener().requestForJsonObject(
            mainActivity!!,
            WSUtils.REQ_FOR_CREATE_BOOKING,
            call,
            this
        )
    }

    private val requestBodyObject: RequestBody
        get() {
            val jsonObjectReq = JSONObject()
            try {
                jsonObjectReq.put("dogId", dogModel?.dogId)
                jsonObjectReq.put("ownerId", dogModel?.ownerId)
                jsonObjectReq.put(
                    "customerId",
                    BaseApplication.pawssibleStorage?.getValue(PawssibleStorage.SP_USER_ID, "")
                )
                jsonObjectReq.put("ownerId", dogModel?.ownerId)
                jsonObjectReq.put("hours", hours)
                jsonObjectReq.put("total", total)
                jsonObjectReq.put("timestamp", bookingCal?.time)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return StaticUtils.getRequestBodyJson(jsonObjectReq)
        }

    private fun calculateAmount() {
        subTotal = hours * dogModel?.hourlyPrice!!
        tax = 12 * subTotal / 100
        total = subTotal + tax
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnBook -> processPayment()
            else -> {
            }
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"

        fun newInstance(dogModel: DogModel?): BookingFragment {
            val fragment = BookingFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, dogModel)
            fragment.arguments = args
            return fragment
        }

    }

    override fun onSuccess(code: Int, response: JsonElement?) {
        when (code) {
            WSUtils.REQ_FOR_CREATE_BOOKING -> parseGetDogsListResponse(response as JsonObject)
            else -> {
            }
        }
    }

    private fun parseGetDogsListResponse(response: JsonObject) {
        try {
            if (response.get("success").asBoolean) {
                StaticUtils.showToast(
                    mainActivity,
                    "Successfully created Booking. Please wait till Pet Owner accepts your booking."
                )
                mainActivity!!.clearBackStackCompletely()
            } else {
                StaticUtils.showToast(mainActivity, "Something went wrong")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onError(code: Int, error: String?) {
        StaticUtils.showToast(mainActivity!!, error)
    }

    override fun onNoNetwork(code: Int) {
        StaticUtils.showToast(mainActivity!!, "Please check your internet connection")
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()

        bookingCal?.set(Calendar.YEAR, year)
        bookingCal?.set(Calendar.DAY_OF_MONTH, dayOfMonth)
        bookingCal?.set(Calendar.MONTH, month)

        val timePickerDialog =
            TimePickerDialog(
                mainActivity!!,
                this,
                calendar.get(Calendar.HOUR),
                calendar.get(Calendar.MINUTE),
                is24HourFormat(mainActivity)
            )
        timePickerDialog.show()
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        bookingCal?.set(Calendar.HOUR_OF_DAY, hourOfDay)
        bookingCal?.set(Calendar.MINUTE, minute)

        fragmentBookingBinding?.txtDate?.text = "${bookingCal?.time}"
    }
}