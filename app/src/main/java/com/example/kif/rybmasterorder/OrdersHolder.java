package com.example.kif.rybmasterorder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Created by Kif on 26.12.2016.
 */

public class OrdersHolder extends RecyclerView.ViewHolder {

    TextView rootorderId;
    TextView rootName;
    TextView rootmobile;
    TextView rootPrice;
    CheckBox rootCheckBoxDone;
    TextView rootMasterName;
    TextView rootDetail;
    TextView rootDate;

    public OrdersHolder(View itemView) {
        super(itemView);

        //mView=itemView;

        rootorderId = (TextView) itemView.findViewById(R.id.textViewOrderId);
        rootName = (TextView) itemView.findViewById(R.id.textViewName);
        rootmobile = (TextView) itemView.findViewById(R.id.textViewMobile);
        rootPrice = (TextView) itemView.findViewById(R.id.textViewPrice);
        rootCheckBoxDone = (CheckBox) itemView.findViewById(R.id.checkBoxDone);
        rootMasterName=(TextView) itemView.findViewById(R.id.textViewMasterName);
        rootDetail=(TextView) itemView.findViewById(R.id.textViewDetail);
        rootDate=(TextView) itemView.findViewById(R.id.textViewDate);
    }
}
