package com.modul.marketplace.holder.orderonline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.modul.marketplace.model.orderonline.DmStatusOrder;
import com.modul.marketplace.util.DateTimeUtil;
import com.modul.marketplace.R;
import com.modul.marketplace.app.Constants;
import com.modul.marketplace.holder.AbsRecyleHolder;
import com.modul.marketplace.model.orderonline.DmDeliveryInfo;
import com.modul.marketplace.model.orderonline.DmOrderOnline;
import com.modul.marketplace.util.FormatNumberUtil;

public class HistoryOrderOnlineRecycleHolder extends AbsRecyleHolder {

    private TextView mTitle;
    private TextView mTime;
    private TextView mStatus;
    private TextView mPrice;
    private DmOrderOnline dmOrderOnline;
    private TextView mAdress;
    private HistoryOrderOnlineRecycleHolder.OnItemClickRycle mOnItemClickRycle;


    public HistoryOrderOnlineRecycleHolder(Context mContext, View view, HistoryOrderOnlineRecycleHolder.OnItemClickRycle mOnItemClickRycle) {
        super(mContext, view);
        this.mOnItemClickRycle = mOnItemClickRycle;
        findViewd(getConvertView());

    }

    public static HistoryOrderOnlineRecycleHolder newInstance(Context mContext, LayoutInflater inflater, HistoryOrderOnlineRecycleHolder.OnItemClickRycle mOnItemClickRycle) {
        View convertView = inflater.inflate(getItemLayout(), null);
        HistoryOrderOnlineRecycleHolder mMenuFoodItemRecycleHolder = new HistoryOrderOnlineRecycleHolder(mContext, convertView, mOnItemClickRycle);
        return mMenuFoodItemRecycleHolder;
    }

    private static int getItemLayout() {
        return R.layout.adapter_history_order_online;
    }

    private void findViewd(View convertView) {
        mTitle = convertView.findViewById(R.id.title);
        mTime = convertView.findViewById(R.id.time);
        mStatus = convertView.findViewById(R.id.status);
        mPrice = convertView.findViewById(R.id.price);
        mAdress = convertView.findViewById(R.id.adress);
        getConvertView().setOnClickListener(v -> mOnItemClickRycle.onItemChoice(dmOrderOnline));

    }

    @Override
    public void setElement(Object obj) {

        setData((DmOrderOnline) obj);

    }

    private void setData(DmOrderOnline item) {
        this.dmOrderOnline = item;
        mTitle.setText("#" + dmOrderOnline.getOrderCode());
        DmDeliveryInfo dmDeliveryInfo = dmOrderOnline.getDmDeliveryInfo();
        if (dmDeliveryInfo != null) {
            String nguoinhan = mContext.getString(R.string.nguoi_nhan) + ": " + item.getContactName() + " - " + item.getContactPhone() + "\n" +
                    mContext.getString(R.string.dia_chi_nhan_hang) + ": " + dmDeliveryInfo.getAddress();
            mAdress.setText(nguoinhan);
        }
        mPrice.setText(FormatNumberUtil.formatCurrency(dmOrderOnline.getAmount()));
        mStatus.setText(dmOrderOnline.getStatusName(mContext));

        if (DmStatusOrder.TYPE_WAITCONFIRM.equals(dmOrderOnline.getStatus())) {
            mStatus.setBackground(mContext.getDrawable(R.drawable.view_deposit_pending));
            mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.orangeF9));
        } else if (DmStatusOrder.TYPE_CONFIRMED.equals(dmOrderOnline.getStatus())) {
            mStatus.setBackground(mContext.getDrawable(R.drawable.view_deposit_pending));
            mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.orangeF9));
        } else if (DmStatusOrder.TYPE_PAYING.equals(dmOrderOnline.getStatus())) {
            mStatus.setBackground(mContext.getDrawable(R.drawable.view_deposit_pending));
            mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.orangeF9));
        } else if (DmStatusOrder.TYPE_PENDING.equals(dmOrderOnline.getStatus())) {
            mStatus.setBackground(mContext.getDrawable(R.drawable.view_deposit_pending));
            mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.orangeF9));
        } else if (DmStatusOrder.TYPE_CANCELED.equals(dmOrderOnline.getStatus())) {
            mStatus.setBackground(mContext.getDrawable(R.drawable.view_deposit_rejected));
            mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.redE7));
        }else{
            mStatus.setBackground(mContext.getDrawable(R.drawable.view_caring));
            mStatus.setTextColor(ContextCompat.getColor(mContext, R.color.green2F));
        }

        mTime.setText(DateTimeUtil.convertTimeStampToDate(
                DateTimeUtil.convertStringToTimeStamp(
                        item.getCreatedTime(),
                        Constants.Date.Format.YYYY_MM_DD_HH_MM_SS_2
                ),
                Constants.Date.Format.HH_MM_DD_MM_YYYY
        ));
    }

    public interface OnItemClickRycle {

        void onItemChoice(DmOrderOnline dmOrderOnline);

    }


}

