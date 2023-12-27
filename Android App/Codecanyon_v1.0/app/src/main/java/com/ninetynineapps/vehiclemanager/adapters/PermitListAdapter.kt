package com.ninetynineapps.vehiclemanager.adapters

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.ninetynineapps.vehiclemanager.R
import com.ninetynineapps.vehiclemanager.common.CommonUtilities
import com.ninetynineapps.vehiclemanager.interfaces.AdapterItemCallback
import com.ninetynineapps.vehiclemanager.pojo.PermitClass

class PermitListAdapter(private val context: Context,private val permitClassArrayList: ArrayList<PermitClass>,
                        private val adapterItemCallback: AdapterItemCallback
) : androidx.recyclerview.widget.RecyclerView.Adapter<PermitListAdapter.AdapterViewHolder>() {

    inner class AdapterViewHolder(view: View) : androidx.recyclerview.widget.RecyclerView.ViewHolder(view), View.OnClickListener {

        val llMain = view.findViewById(R.id.llMain) as LinearLayout
        val tvIssueMonth = view.findViewById(R.id.tvIssueMonth) as AppCompatTextView
        val tvIssueDate = view.findViewById(R.id.tvIssueDate) as AppCompatTextView
        val tvIssueYear = view.findViewById(R.id.tvIssueYear) as AppCompatTextView
        val tvExpiryMonth = view.findViewById(R.id.tvExpiryMonth) as AppCompatTextView
        val tvExpiryDate = view.findViewById(R.id.tvExpiryDate) as AppCompatTextView
        val tvExpiryYear = view.findViewById(R.id.tvExpiryYear) as AppCompatTextView
        val tvPermitNo = view.findViewById(R.id.tvPermitNo) as AppCompatTextView

        init {
            llMain.setOnClickListener(this)
        }

        override fun onClick(p0: View?) {
            val id = p0!!.id
            if (id == R.id.llMain) {
                adapterItemCallback.onItemTypeClickCallback(p0.tag as Int)
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): AdapterViewHolder {
        val view = LayoutInflater.from(p0.context).inflate(R.layout.cell_permit_list,p0,false)
        return AdapterViewHolder(view)
    }

    override fun onBindViewHolder(p0: AdapterViewHolder, p1: Int) {
        val aClass = permitClassArrayList[p1]
        val dateIssue = CommonUtilities.getDateWithMonthName(aClass.permitIssueDate).split("-")

        try {
            p0.tvIssueMonth.text = dateIssue[0]
            p0.tvIssueDate.text = dateIssue[1]
            p0.tvIssueYear.text = dateIssue[2]
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            p0.tvPermitNo.text = aClass.permitNo
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val dateExpiry = CommonUtilities.getDateWithMonthName(aClass.permitExpiryDate).split("-")

        try {
            p0.tvExpiryMonth.text = dateExpiry[0]
            p0.tvExpiryDate.text = dateExpiry[1]
            p0.tvExpiryYear.text = dateExpiry[2]
        } catch (e: Exception) {
            e.printStackTrace()
        }
        p0.llMain.tag = p1
    }

    override fun getItemCount(): Int {
        return permitClassArrayList.size
    }
}