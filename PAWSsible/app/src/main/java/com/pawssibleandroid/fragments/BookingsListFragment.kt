package com.pawssibleandroid.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.GsonBuilder
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import com.pawssibleandroid.BaseApplication
import com.pawssibleandroid.R
import com.pawssibleandroid.activities.MainActivity
import com.pawssibleandroid.adapter.BookingsListAdapter
import com.pawssibleandroid.databinding.FragmentBookingsListBinding
import com.pawssibleandroid.interfaces.IClickListener
import com.pawssibleandroid.interfaces.IParseListener
import com.pawssibleandroid.models.BookingModel
import com.pawssibleandroid.utils.DividerItemDecoration
import com.pawssibleandroid.utils.PawssibleStorage
import com.pawssibleandroid.utils.StaticUtils
import com.pawssibleandroid.wsutils.WSCallBacksListener
import com.pawssibleandroid.wsutils.WSUtils
import retrofit2.Call
import java.util.*

class BookingsListFragment : Fragment(), IClickListener, IParseListener<JsonElement?> {
    private var selectedView: View? = null
    private var selectedPos: Int? = -1
    private var bookingModelArrayList: ArrayList<BookingModel>? = null
    private var fragmentBookingBinding: FragmentBookingsListBinding? = null
    private var isRequests = false
    private var mainActivity: MainActivity? = null
    override fun onResume() {
        super.onResume()
        if (mainActivity != null) {
            mainActivity?.showTopBar(if (isRequests) "My Requests" else "My Bookings", true)
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
            isRequests = requireArguments().containsKey("requests")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        fragmentBookingBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_bookings_list, container, false)
        initComponents()
        return fragmentBookingBinding?.getRoot()
    }

    private fun initComponents() {
        StaticUtils.hideSoftKeyboard(mainActivity!!)
        bookingModelArrayList = ArrayList<BookingModel>()
        requestForDogsList()
        setAdapter()
    }

    private fun requestForDogsList() {
        val call: Call<JsonElement>
        call = if (BaseApplication.isCustomer) {
            BaseApplication.instance?.wsClientListener!!.getCustomerBookings(
                BaseApplication.pawssibleStorage?.getValue(
                    PawssibleStorage.SP_USER_ID,
                    ""
                )
            )
        } else {
            if (isRequests) BaseApplication.instance?.wsClientListener!!.getOwnerBookingRequests(
                BaseApplication.pawssibleStorage?.getValue(
                    PawssibleStorage.SP_USER_ID,
                    ""
                )
            ) else BaseApplication.instance?.wsClientListener!!.getOwnerBookings(
                BaseApplication.pawssibleStorage?.getValue(
                    PawssibleStorage.SP_USER_ID,
                    ""
                )
            )
        }
        WSCallBacksListener().requestForJsonObject(
            mainActivity,
            WSUtils.REQ_FOR_GET_BOOKINGS,
            call,
            this
        )
    }

    var bookingsListAdapter: BookingsListAdapter? = null

    private fun setAdapter() {
        fragmentBookingBinding?.recyclerView?.setLayoutManager(LinearLayoutManager(mainActivity))
        bookingsListAdapter = BookingsListAdapter(isRequests, bookingModelArrayList!!, this)
        fragmentBookingBinding?.recyclerView?.setAdapter(bookingsListAdapter)
        fragmentBookingBinding?.recyclerView?.addItemDecoration(
            DividerItemDecoration(
                mainActivity!!,
                DividerItemDecoration.VERTICAL_LIST
            )
        )
    }

    override fun onClick(view: View?, position: Int) {
        selectedPos = position
        selectedView = view
        when (view?.id) {
            R.id.btnConfirm -> {
                requestForConfirmBooking(bookingModelArrayList!![position].bookingId)
            }
            R.id.btnComplete -> {
                requestForCompleteBooking(bookingModelArrayList!![position].bookingId)
            }
            R.id.btnReject -> {
                requestForRejectBooking(bookingModelArrayList!![position].bookingId)
            }
        }
    }

    private fun requestForConfirmBooking(bookingId: String) {
        val call: Call<JsonElement>
        call =
            BaseApplication.instance?.wsClientListener!!.acceptBookingRequest(bookingId)

        WSCallBacksListener().requestForJsonObject(
            mainActivity,
            WSUtils.REQ_FOR_UPDATE_BOOKING_REQUESTS,
            call,
            this
        )
    }

    private fun requestForCompleteBooking(bookingId: String) {
        val call: Call<JsonElement>
        call =
            BaseApplication.instance?.wsClientListener!!.completeBooking(bookingId)

        WSCallBacksListener().requestForJsonObject(
            mainActivity,
            WSUtils.REQ_FOR_UPDATE_BOOKING_REQUESTS,
            call,
            this
        )
    }

    private fun requestForRejectBooking(bookingId: String) {
        val call: Call<JsonElement>
        call =
            BaseApplication.instance?.wsClientListener!!.rejectBookingRequest(bookingId)

        WSCallBacksListener().requestForJsonObject(
            mainActivity,
            WSUtils.REQ_FOR_UPDATE_BOOKING_REQUESTS,
            call,
            this
        )
    }

    override fun onLongClick(view: View?, position: Int) {}

    override fun onSuccess(code: Int, response: JsonElement?) {
        when (code) {
            WSUtils.REQ_FOR_GET_BOOKINGS -> parseGetDogsListResponse(response as JsonObject)
            WSUtils.REQ_FOR_UPDATE_BOOKING_REQUESTS -> parseBookingUpdateResponse(response as JsonObject)
            else -> {
            }
        }
    }

    private fun parseBookingUpdateResponse(jsonObject: JsonObject) {
        try {
            if (jsonObject.get("success").asBoolean) {
                if (selectedView != null) {
                    when (selectedView!!.id) {
                        R.id.btnConfirm -> {
                            if (selectedPos != -1) {
                                bookingModelArrayList?.get(selectedPos!!)?.status = "P"
                                bookingsListAdapter?.notifyDataSetChanged()
                            } else requestForDogsList()
                        }
                        R.id.btnComplete -> {
                            if (selectedPos != -1) {
                                bookingModelArrayList!![selectedPos!!].status = "C"
                                bookingsListAdapter?.notifyDataSetChanged()
                            } else requestForDogsList()
                        }
                        R.id.btnReject -> {
                            if (selectedPos != -1) {
                                bookingModelArrayList!![selectedPos!!].status = "X"
                                bookingsListAdapter?.notifyDataSetChanged()
                            } else requestForDogsList()
                        }
                    }
                } else requestForDogsList()
            } else StaticUtils.showToast(mainActivity!!, "Something went wrong")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun parseGetDogsListResponse(response: JsonObject) {
        try {
            val listType = object : TypeToken<ArrayList<BookingModel?>?>() {}.type
            bookingModelArrayList = GsonBuilder().create()
                .fromJson<ArrayList<BookingModel>>(response.get("bookings").asJsonArray, listType)
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
        fun newInstance(): BookingsListFragment {
            return BookingsListFragment()
        }

        fun newInstance(requests: String?): Fragment {
            val fragment = BookingsListFragment()
            val bundle = Bundle()
            bundle.putString("requests", requests)
            fragment.arguments = bundle
            return fragment
        }
    }
}