package com.pawssibleandroid.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.pawssibleandroid.BaseApplication
import com.pawssibleandroid.R
import com.pawssibleandroid.activities.MainActivity
import com.pawssibleandroid.databinding.FragmentPersonalDetailsBinding
import com.pawssibleandroid.utils.PawssibleStorage

class PersonalDetailsFragment : Fragment(), View.OnClickListener {
    private var fragmentPersonalDetailsBinding: FragmentPersonalDetailsBinding? = null
    private var mainActivity: MainActivity? = null
    override fun onResume() {
        super.onResume()
        if (mainActivity != null) {
            mainActivity?.showTopBar("Personal Details", true)
            mainActivity?.hideBottomBar()
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
        fragmentPersonalDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_personal_details, container, false)
        setData()
        return fragmentPersonalDetailsBinding?.getRoot()
    }

    override fun onClick(view: View) {}

    private fun setData() {
        try {
            fragmentPersonalDetailsBinding?.txtName?.setText(
                "Name: " + BaseApplication.pawssibleStorage?.getValue(
                    PawssibleStorage.SP_USER_NAME,
                    ""
                )
            )
            fragmentPersonalDetailsBinding?.txtEmail?.setText(
                "Email Address: " + BaseApplication.pawssibleStorage?.getValue(
                    PawssibleStorage.SP_USER_EMAIL,
                    ""
                )
            )
            fragmentPersonalDetailsBinding?.txtDescription?.setText(
                "Address: \n" + BaseApplication.pawssibleStorage?.getValue(
                    PawssibleStorage.SP_USER_ADDRESS,
                    ""
                )
            )
            fragmentPersonalDetailsBinding?.txtPhoneNumber?.setText(
                "Phone Number: " + BaseApplication.pawssibleStorage?.getValue(
                    PawssibleStorage.SP_USER_PHONE,
                    ""
                )
            )
            fragmentPersonalDetailsBinding?.txtUserType?.setText(
                """
                        User Type: 
                        ${
                    if (BaseApplication.pawssibleStorage?.getValue(
                            PawssibleStorage.SP_USER_NAME,
                            ""
                        ).equals("o", true)
                    ) "Owner" else "Customer"
                }
                        """.trimIndent()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        fun newInstance(): PersonalDetailsFragment {
            return PersonalDetailsFragment()
        }
    }
}