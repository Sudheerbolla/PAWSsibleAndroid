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
import com.pawssibleandroid.BaseApplication
import com.pawssibleandroid.R
import com.pawssibleandroid.activities.MainActivity
import com.pawssibleandroid.databinding.FragmentDogDetailsBinding
import com.pawssibleandroid.models.DogModel


class DogDetailsFragment : Fragment(), View.OnClickListener {
    private var dogModel: DogModel? = null
    private var fragmentDogDetailsBinding: FragmentDogDetailsBinding? = null
    private var mainActivity: MainActivity? = null

    override fun onResume() {
        super.onResume()
        if (mainActivity != null) {
            mainActivity?.showTopBar("Dog Details", true)
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
        fragmentDogDetailsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_dog_details, container, false)
        initComponents()
        return fragmentDogDetailsBinding?.getRoot()
    }

    private fun initComponents() {
        setData()
        fragmentDogDetailsBinding?.btnBook?.setOnClickListener(this)
    }

    private fun setData() {
        if (dogModel != null) {
            fragmentDogDetailsBinding?.txtName?.setText("Dog Breed: " + dogModel?.breedName)
            fragmentDogDetailsBinding?.txtLikes?.setText("Likes: " + dogModel?.likes)
            fragmentDogDetailsBinding?.txtDisLikes?.setText("DisLikes: " + dogModel?.disLikes)
            fragmentDogDetailsBinding?.txtAllergies?.setText("Allergies: " + dogModel?.allergies)
            fragmentDogDetailsBinding?.txtDescription?.setText("Description: " + dogModel?.description)
            fragmentDogDetailsBinding?.txtAge?.setText("Age in Months: " + dogModel?.ageInMonths.toString() + "")
            fragmentDogDetailsBinding?.txtHourlyPrice?.setText("Hourly Price: " + dogModel?.hourlyPrice.toString() + " CAD")
            fragmentDogDetailsBinding?.switchActive?.isChecked = dogModel?.active!!
            if (dogModel?.active!!) {
                fragmentDogDetailsBinding?.switchActive?.setText("Active")
            } else {
                fragmentDogDetailsBinding?.switchActive?.setText("InActive")
            }
            if (BaseApplication.isCustomer) {
                fragmentDogDetailsBinding?.switchActive?.isEnabled = false
                fragmentDogDetailsBinding?.switchActive?.isClickable = false

            }
            fragmentDogDetailsBinding?.txtImageLink?.setText("Image Link: " + dogModel?.photo.toString())
            try {
                if (!TextUtils.isEmpty(dogModel?.photo)) {
                    Glide.with(mainActivity!!).load(dogModel?.photo)
                        .into(fragmentDogDetailsBinding?.imgDog!!)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btnBook -> mainActivity?.replaceFragment(
                BookingFragment.newInstance(dogModel), true
            )
            else -> {
            }
        }
    }

    companion object {
        private const val ARG_PARAM1 = "param1"
        fun newInstance(dogModel: DogModel?): DogDetailsFragment {
            val fragment = DogDetailsFragment()
            val args = Bundle()
            args.putParcelable(ARG_PARAM1, dogModel)
            fragment.arguments = args
            return fragment
        }
    }
}