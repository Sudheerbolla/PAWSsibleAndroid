package com.pawssibleandroid.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.pawssibleandroid.BaseApplication
import com.pawssibleandroid.R
import com.pawssibleandroid.databinding.ItemBookingsBinding
import com.pawssibleandroid.interfaces.IClickListener
import com.pawssibleandroid.models.BookingModel
import java.util.*

/**
 * Booking list adapter
 */
class BookingsListAdapter(
    var isRequests: Boolean,
    private val bookingModelArrayList: ArrayList<BookingModel>,
    private val iClickListener: IClickListener?
) : RecyclerView.Adapter<BookingsListAdapter.UsersViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): UsersViewHolder {
        return UsersViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(
                    parent.context
                ), R.layout.item_bookings, parent, false
            )
        )
    }

    override fun onBindViewHolder(
        fileFoldersViewHolder: UsersViewHolder,
        position: Int
    ) {
        val (_, dog, customer, owner, hours, totalAmount, _, status) = bookingModelArrayList[position]
//          status - R - requested, X - cancelled, C - Completed, P - Accepted
        fileFoldersViewHolder.itemFolderBinding.linButtons.setVisibility(View.GONE)
        fileFoldersViewHolder.itemFolderBinding.btnComplete.setVisibility(View.GONE)
        fileFoldersViewHolder.itemFolderBinding.btnConfirm.setVisibility(View.GONE)
        fileFoldersViewHolder.itemFolderBinding.btnReject.setVisibility(View.GONE)
        if (isRequests || !BaseApplication.isCustomer) {
//          only owner can see this screen
            when (status) {
                "P", "p" -> {
                    fileFoldersViewHolder.itemFolderBinding.linButtons.setVisibility(View.VISIBLE)
                    fileFoldersViewHolder.itemFolderBinding.btnReject.setVisibility(View.VISIBLE)
                    fileFoldersViewHolder.itemFolderBinding.btnComplete.setVisibility(View.VISIBLE)
                    fileFoldersViewHolder.itemFolderBinding.txtStatus.setText("Accepted/In Progress")
                }
                "R", "r" -> {
                    fileFoldersViewHolder.itemFolderBinding.linButtons.setVisibility(View.VISIBLE)
                    fileFoldersViewHolder.itemFolderBinding.btnReject.setVisibility(View.VISIBLE)
                    fileFoldersViewHolder.itemFolderBinding.btnConfirm.setVisibility(View.VISIBLE)
                    fileFoldersViewHolder.itemFolderBinding.txtStatus.setText("Waiting For Approval")
                }
                "X", "x" -> {
                    fileFoldersViewHolder.itemFolderBinding.txtStatus.setText("Cancelled/Rejected")
                }
                "C", "c" -> {
                    fileFoldersViewHolder.itemFolderBinding.txtStatus.setText("Completed")
                }
                else -> fileFoldersViewHolder.itemFolderBinding.txtStatus.setText(status)
            }
        } else {
            when (status) {
                "P", "p" -> {
                    fileFoldersViewHolder.itemFolderBinding.btnComplete.setVisibility(View.VISIBLE)
                    fileFoldersViewHolder.itemFolderBinding.txtStatus.setText("Accepted/In Progress")
                }
                "R", "r" -> {
                    fileFoldersViewHolder.itemFolderBinding.linButtons.setVisibility(View.VISIBLE)
                    fileFoldersViewHolder.itemFolderBinding.btnReject.setVisibility(View.VISIBLE)
                    fileFoldersViewHolder.itemFolderBinding.txtStatus.setText("Waiting For Approval")
                }
                "X", "x" -> {
                    fileFoldersViewHolder.itemFolderBinding.txtStatus.setText("Cancelled/Rejected")
                }
                "C", "c" -> {
                    fileFoldersViewHolder.itemFolderBinding.txtStatus.setText("Completed")
                }
                else -> fileFoldersViewHolder.itemFolderBinding.txtStatus.setText(status)
            }
        }
        fileFoldersViewHolder.itemFolderBinding.txtCustomerName.setText("Customer Details: $customer")
        fileFoldersViewHolder.itemFolderBinding.txtDogBreed.setText("Dog Details: $dog")
        fileFoldersViewHolder.itemFolderBinding.txtOwnerName.setText("Owner Details: $owner")
        fileFoldersViewHolder.itemFolderBinding.txtHours.setText("Total Booking Hours: $hours")
        fileFoldersViewHolder.itemFolderBinding.txtTotalPrice.setText("Total Price: $totalAmount CAD")
        fileFoldersViewHolder.itemFolderBinding.btnConfirm.setOnClickListener { v ->
            iClickListener?.onClick(
                v, position
            )
        }
        fileFoldersViewHolder.itemFolderBinding.getRoot().setOnClickListener { v ->
            iClickListener?.onClick(
                v, position
            )
        }
        fileFoldersViewHolder.itemFolderBinding.getRoot().setOnLongClickListener { v ->
            iClickListener?.onLongClick(v, position)
            false
        }
    }

    override fun getItemCount(): Int {
        return bookingModelArrayList.size
    }

    class UsersViewHolder(itemFileFolderBinding: ItemBookingsBinding) :
        RecyclerView.ViewHolder(itemFileFolderBinding.getRoot()) {
        var itemFolderBinding: ItemBookingsBinding

        init {
            itemFolderBinding = itemFileFolderBinding
        }
    }
}
