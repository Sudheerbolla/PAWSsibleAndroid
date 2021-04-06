package com.pawssibleandroid.activities

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.pawssibleandroid.BaseApplication
import com.pawssibleandroid.R
import com.pawssibleandroid.databinding.ActivityMainBinding
import com.pawssibleandroid.fragments.DogsListFragment
import com.pawssibleandroid.fragments.ProfileFragment
import com.pawssibleandroid.utils.StaticUtils

/**
 * This is the main or home screen after login/launch, where user has all options and flow.
 */
class MainActivity : AppCompatActivity(), View.OnClickListener {
    var activityMainBinding: ActivityMainBinding? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initComponents()
    }

    fun showTopBar(heading: String?, showBack: Boolean) {
        activityMainBinding!!.linTopBar.setVisibility(View.VISIBLE)
        if (TextUtils.isEmpty(heading)) activityMainBinding!!.txtHeading.setText(R.string.app_name) else activityMainBinding!!.txtHeading.setText(
            heading
        )
        activityMainBinding!!.imgBack.setVisibility(if (showBack) View.VISIBLE else View.INVISIBLE)
    }

    fun hideTopBar() {
        activityMainBinding!!.linTopBar.setVisibility(View.GONE)
    }

    fun hideBottomBar() {
        activityMainBinding!!.relBottomBar.setVisibility(View.GONE)
    }

    fun showBottomBar() {
        activityMainBinding!!.relBottomBar.setVisibility(View.VISIBLE)
    }

    /**
     * Initializes all components and set listener events
     */
    fun initComponents() {
        if (BaseApplication.isCustomer)
//            we will ask location permission from user if he is customer
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                val permissions = arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                ActivityCompat.requestPermissions(this, permissions, 111)
            }
        activityMainBinding!!.imgBack.setOnClickListener(this)
        activityMainBinding!!.imgHome.setOnClickListener(this)
        activityMainBinding!!.imgProfile.setOnClickListener(this)
        activityMainBinding!!.imgHome.callOnClick()
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.imgBack -> onBackPressed()
            R.id.imgHome -> {
                selectBottomBarTab("Home")
                replaceFragment(DogsListFragment.newInstance(), false)
            }
            R.id.imgProfile -> {
                selectBottomBarTab("Profile")
                replaceFragment(ProfileFragment.newInstance(), true)
            }
            else -> {
            }
        }
    }

    private fun selectBottomBarTab(tab: String) {
        if (tab.equals("Home", ignoreCase = true)) {
            activityMainBinding!!.imgHome.setSelected(true)
            activityMainBinding!!.imgProfile.setSelected(false)
        } else {
            activityMainBinding!!.imgHome.setSelected(false)
            activityMainBinding!!.imgProfile.setSelected(true)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == 111 && grantResults.size > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
        }
    }

    /**
     * Helper methods to manage fragment stack and navigations
     */
    fun replaceFragment(fragment: Fragment, needToAddToBackStack: Boolean) {
        StaticUtils.hideSoftKeyboard(this)
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (needToAddToBackStack) fragmentTransaction.replace(R.id.mainFrame, fragment, tag)
            .addToBackStack(tag).commitAllowingStateLoss() else fragmentTransaction.replace(
            R.id.mainFrame, fragment, tag
        ).commitAllowingStateLoss()
    }

    fun replaceFragment(fragment: Fragment) {
        StaticUtils.hideSoftKeyboard(this)
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrame, fragment, tag).addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    fun clearAndReplaceFragment(fragment: Fragment) {
        clearBackStack()
        StaticUtils.hideSoftKeyboard(this)
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.mainFrame, fragment, tag).addToBackStack(tag)
            .commitAllowingStateLoss()
    }

    fun replaceFragment(fragment: Fragment, needToAddToBackStack: Boolean, containerId: Int) {
        StaticUtils.hideSoftKeyboard(this)
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (needToAddToBackStack) fragmentTransaction.replace(containerId, fragment, tag)
            .addToBackStack(tag).commitAllowingStateLoss() else fragmentTransaction.replace(
            containerId,
            fragment,
            tag
        ).commitAllowingStateLoss()
    }

    fun clearBackStack() {
        val fragment = supportFragmentManager
        fragment.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun clearBackStackCompletely() {
        val fragment = supportFragmentManager
        fragment.popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    fun popBackStack() {
        StaticUtils.hideSoftKeyboard(this)
        val fragment = supportFragmentManager
        fragment.popBackStackImmediate()
    }

    fun replaceFragmentWithoutAnimation(fragment: Fragment, containerId: Int, needToAdd: Boolean) {
        StaticUtils.hideSoftKeyboard(this)
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        //        setCustomAnimation(fragmentTransaction, false);
        if (needToAdd) fragmentTransaction.replace(containerId, fragment, tag).addToBackStack(tag)
            .commitAllowingStateLoss() else fragmentTransaction.replace(containerId, fragment, tag)
            .commitAllowingStateLoss()
    }

    fun addFragment(fragment: Fragment, needToAddToBackStack: Boolean, containerId: Int) {
        StaticUtils.hideSoftKeyboard(this)
        val tag = fragment.javaClass.simpleName
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        if (needToAddToBackStack) fragmentTransaction.add(containerId, fragment, tag)
            .addToBackStack(tag).commit() else fragmentTransaction.add(containerId, fragment, tag)
            .commit()
    }

}