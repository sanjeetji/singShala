package com.sensibol.lucidmusic.singstr.gui.app.payment

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sensibol.android.base.gui.layoutInflater
import com.sensibol.lucidmusic.singstr.domain.model.SubscriptionPlan
import com.sensibol.lucidmusic.singstr.gui.convertDaysToMonths
import com.sensibol.lucidmusic.singstr.gui.convertPerMonthPrice
import com.sensibol.lucidmusic.singstr.gui.databinding.TileSubscriptionPlanBinding
import java.util.*
import javax.inject.Inject
import kotlin.properties.Delegates

class ProSubscriptionPlanAdapter @Inject constructor() : RecyclerView.Adapter<ProSubscriptionPlanAdapter.ProSubscriptionPlanVH>() {

    internal var onPlanClickListener: (SubscriptionPlan) -> Unit = { plan -> }

    internal var plans: List<SubscriptionPlan> by Delegates.observable(listOf()) { _, _, _ -> notifyDataSetChanged() }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = ProSubscriptionPlanVH(
        TileSubscriptionPlanBinding.inflate(parent.layoutInflater, parent, false)
    )

    override fun onBindViewHolder(holder: ProSubscriptionPlanVH, position: Int) = holder.bind(plans[position])

    override fun getItemCount(): Int = plans.size

    inner class ProSubscriptionPlanVH(val binding: TileSubscriptionPlanBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(subscriptionPlan: SubscriptionPlan) {

            binding.apply {
                if (adapterPosition == 1) {
                    viewBorder.visibility = View.VISIBLE
                    tvPlanType.visibility = View.VISIBLE
                } else {
                    viewBorder.visibility = View.GONE
                    tvPlanType.visibility = View.GONE
                }
                val validity = convertDaysToMonths(subscriptionPlan.unlockInfo[0].validity)
                val month = if (validity == 1) "$validity Month" else "$validity Months"
                tvDesc.text = subscriptionPlan.desc
                tvPrice.text = convertPerMonthPrice(validity, subscriptionPlan.originalPrice).toString()
                tvTotalPrice.text = "\u20B9 ${subscriptionPlan.originalPrice} + GST"
                tvValidity.text = month.toUpperCase(Locale.getDefault())
                tvValidity.setOnClickListener {
                    onPlanClickListener(subscriptionPlan)
                }
            }
        }
    }
}