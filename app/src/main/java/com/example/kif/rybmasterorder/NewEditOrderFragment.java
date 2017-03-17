package com.example.kif.rybmasterorder;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Kif on 13.12.2016.
 */

public class NewEditOrderFragment extends DialogFragment{

    private static final String EXTRA_ORDER = "com.example.kif.rybmasterorder.extra.ORDER";
    private static final String EXTRA_ORDER_KEY = "com.example.kif.rybmasterorder.extra.ORDER_POSITION";


    private Order mOrder;

    private String mKey;


    private EditText mEditTextorderid;
    private EditText mEditTextname;
    private EditText mEditTextmobile;
    private EditText mEditTextprice;
    private EditText mEditTextMasterName;
    private EditText mEditTextDetail;
    private CheckBox mCheckBoxType;
    private Boolean mDone;
    private TextView mTextViewNewDate;

    public NewEditOrderFragment(){

    }

    public static NewEditOrderFragment newInstance(Order order, String key) {
        NewEditOrderFragment fragment = new NewEditOrderFragment();

        Bundle args = new Bundle();
        args.putString(EXTRA_ORDER_KEY, key);
        args.putParcelable(EXTRA_ORDER, order);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.new_edit_order_layout_fragment, container, false);

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);

        mOrder = getArguments().getParcelable(EXTRA_ORDER);
        mKey = getArguments().getString(EXTRA_ORDER_KEY);


        //TODO change date format
        final String date = java.text.DateFormat.getDateInstance().format(Calendar.getInstance().getTime());

        mEditTextname = (EditText) fragmentView.findViewById(R.id.editTextNewName);
        mEditTextorderid = (EditText)fragmentView.findViewById(R.id.editTextNewOrderId);
        mEditTextmobile = (EditText)fragmentView.findViewById(R.id.editTextNewMobile);
        mEditTextprice = (EditText)fragmentView.findViewById(R.id.editTextNewPrice);
        mEditTextMasterName = (EditText)fragmentView.findViewById(R.id.editTextMasterName);
        mEditTextDetail = (EditText)fragmentView.findViewById(R.id.editTextDetail);
        mCheckBoxType = (CheckBox) fragmentView.findViewById(R.id.checkBoxType);
        mTextViewNewDate = (TextView) fragmentView.findViewById(R.id.textviewNewDate);


        if(mKey != null){
            mEditTextname.setText(mOrder.getName());
            mEditTextorderid.setText(String.valueOf(mOrder.getOrderId()));
            mEditTextmobile.setText(mOrder.getMobile());
            mEditTextprice.setText(String.valueOf(mOrder.getPrice()));
            mEditTextMasterName.setText(mOrder.getMasterName());
            mEditTextDetail.setText(mOrder.getDetail());
            mCheckBoxType.setChecked(mOrder.getType());
            mTextViewNewDate.setText(mOrder.getDate());
            mDone = mOrder.getDone();
        } else {
            mDone = false;
            mTextViewNewDate.setText(date);
        }

        fragmentView.findViewById(R.id.buttonSaveNewOrder).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    try {
                        Order order = new Order(
                                Integer.parseInt(mEditTextorderid.getText().toString()),
                                mEditTextname.getText().toString(),
                                mEditTextmobile.getText().toString(),
                                Integer.parseInt(mEditTextprice.getText().toString()),
                                mDone,
                                mCheckBoxType.isChecked(),
                                date,
                                mEditTextMasterName.getText().toString(),
                                mEditTextDetail.getText().toString()
                        );

                        mListener.save(order,mKey);

                    }catch (NumberFormatException e) {
                        Toast.makeText(getActivity(), "№ ремонта и Цена должны быть числами", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
        return fragmentView;
    }

    private OrderListener mListener;

    public void setOrder(OrderListener listener) {
        mListener = listener;
    }

    public interface OrderListener {
        void save(Order order, String key);
        void cancel();
    }
}
