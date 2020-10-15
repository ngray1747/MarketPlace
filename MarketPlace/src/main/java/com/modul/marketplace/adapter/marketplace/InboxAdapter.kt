package com.modul.marketplace.adapter.marketplace

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.modul.marketplace.extension.initIconNoDefault
import com.modul.marketplace.R
import com.modul.marketplace.adapter.BaseRecycleAdapter
import com.modul.marketplace.app.Constants
import com.modul.marketplace.extension.StringExt
import com.modul.marketplace.extension.ctx
import com.modul.marketplace.extension.initAvatarCompany
import com.modul.marketplace.model.marketplace.InboxModel
import com.modul.marketplace.util.DateTimeUtil.Companion.convertTimeStampToDate
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.holder_inbox.*

class InboxAdapter(
        private val context: Context,
        private val data: List<InboxModel>,
        val itemClickListener: (InboxModel) -> Unit,
) :
        RecyclerView.Adapter<InboxAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.ctx).inflate(R.layout.holder_inbox, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return data.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (itemCount > 0) {
            val item = data[position]
            holder.bind(item)
        }
    }

    inner class ViewHolder(itemView: View) : BaseRecycleAdapter(context, itemView), LayoutContainer {
        override val containerView: View?
            get() = itemView

        fun bind(model: InboxModel) {
            with(model) {
                mTitle.text = StringExt.isTextEmpty(notification?.notify_title)
                mContent.text = StringExt.isTextEmpty(notification?.notify_messages)
                created_at?.let{
                    mTime.text = StringExt.isTextEmpty(convertTimeStampToDate(it, Constants.Date.Format.HH_MM_DD_MM_YYYY))
                }
                read_at?.let{
                    mView.setBackgroundColor(mContext.resources.getColor(R.color.grayF5))
                    initIconNoDefault(context,mTick,R.drawable.icon_circle_gray)
                }?:run{
                    mView.setBackgroundColor(mContext.resources.getColor(R.color.white))
                    initIconNoDefault(context,mTick,R.drawable.icon_circle_blue)
                }
                initAvatarCompany(context,mImage,notification?.notify_image_path.toString(),mContext.getDrawable(R.drawable.icon_default))

                convertView.setOnClickListener {
                    itemClickListener(this)
                }
            }
        }
    }
}