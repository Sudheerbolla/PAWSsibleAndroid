package com.pawssibleandroid.fragments

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.pawssibleandroid.BaseApplication
import com.pawssibleandroid.R
import com.pawssibleandroid.activities.LandingActivity
import com.pawssibleandroid.activities.MainActivity
import com.pawssibleandroid.databinding.FragmentProfileBinding


class ProfileFragment : Fragment(), View.OnClickListener {
    private var fragmentProfileBinding: FragmentProfileBinding? = null
    private var mainActivity: MainActivity? = null
    override fun onResume() {
        super.onResume()
        if (mainActivity != null) {
            mainActivity?.showTopBar("My Profile", false)
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
        fragmentProfileBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        initComponents()
        return fragmentProfileBinding?.getRoot()
    }

    private fun initComponents() {
        fragmentProfileBinding!!.btnLogout.setOnClickListener(this)
        fragmentProfileBinding!!.txtBookingHistory.setOnClickListener(this)
        fragmentProfileBinding!!.txtPersonalDetails.setOnClickListener(this)
//        fragmentProfileBinding!!.txtPersonalQuestionnaire.setOnClickListener(this)
        fragmentProfileBinding!!.txtRequests.setOnClickListener(this)
        fragmentProfileBinding!!.txtTrackCustomer.setOnClickListener(this)
        if (BaseApplication.isCustomer) {
            fragmentProfileBinding!!.txtTrackCustomer.setVisibility(View.GONE)
//            fragmentProfileBinding!!.txtPersonalQuestionnaire.setVisibility(View.GONE)
            fragmentProfileBinding!!.txtRequests.setVisibility(View.GONE)
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnLogout -> showLogoutDialog()
            R.id.txtTrackCustomer -> mainActivity?.replaceFragment(
                TrackLocationFragment.newInstance(),
                true
            )
            R.id.txtBookingHistory -> mainActivity?.replaceFragment(
                BookingsListFragment.newInstance(),
                true
            )
            R.id.txtPersonalDetails -> mainActivity?.replaceFragment(
                PersonalDetailsFragment.newInstance(),
                true
            )
//            R.id.txtPersonalQuestionnaire -> mainActivity.replaceFragment(
//                PersonalQuestionnaireFragment.newInstance(),
//                true
//            )
            R.id.txtRequests -> mainActivity?.replaceFragment(
                BookingsListFragment.newInstance("Requests"),
                true
            )
            else -> {
            }
        }
    }

    private fun showLogoutDialog() {
        val builder1 = AlertDialog.Builder(mainActivity!!)
        builder1.setTitle("Logout")
        builder1.setMessage("Are you sure you want to logout?")
        builder1.setCancelable(true)
        builder1.setPositiveButton("Logout") { dialog: DialogInterface, id: Int ->
            dialog.cancel()
            BaseApplication.pawssibleStorage?.clear()
            startActivity(Intent(mainActivity, LandingActivity::class.java))
            mainActivity?.finishAffinity()
        }
        builder1.setNeutralButton(
            "Cancel"
        ) { dialog: DialogInterface, id: Int -> dialog.cancel() }
        val alert11 = builder1.create()
        alert11.show()
    }

    companion object {
        fun newInstance(): ProfileFragment {
            return ProfileFragment()
        }
    }
}