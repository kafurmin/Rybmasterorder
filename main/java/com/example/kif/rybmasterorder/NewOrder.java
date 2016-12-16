package com.example.kif.rybmasterorder;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static com.example.kif.rybmasterorder.MainActivity.ORDERS;

/**
 * Created by Kif on 13.12.2016.
 */

public class NewOrder extends AppCompatActivity{

    private Button save;
    private EditText orderid,mobile;
    private CheckBox done;
    private DatabaseReference mDatabaseReference;


    @Override
    public void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_order_layout);
        orderid = (EditText)findViewById(R.id.editTextNewOrderId);
        mobile = (EditText)findViewById(R.id.editTextNewMobile);
        done = (CheckBox)findViewById(R.id.checkBoxNewDone);
        save = (Button)findViewById(R.id.buttonSaveNewOrder);
        mDatabaseReference= FirebaseDatabase.getInstance().getReference();
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Orders order = new Orders(done.isChecked(),
                        Integer.parseInt(mobile.getText().toString()),
                        Integer.parseInt(orderid.getText().toString())
                        );
                mDatabaseReference.child(ORDERS).push().setValue(order);
                finish();
            }
        });


    }
}
