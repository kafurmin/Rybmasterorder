package com.example.kif.rybmasterorder;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    public static class OrdersHolder extends RecyclerView.ViewHolder {
        public TextView rootorderId;
        public TextView rootmobile;
        public TextView rootdone;

        public OrdersHolder(View itemView) {
            super(itemView);
            rootorderId = (TextView) itemView.findViewById(R.id.textViewOrderId);
            rootmobile = (TextView) itemView.findViewById(R.id.textViewMobile);
            rootdone = (TextView) itemView.findViewById(R.id.textViewDone);
        }
    }


    public static final String ORDERS = "orders";// как правильно объявить?

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter <Orders, OrdersHolder> mFirebaseAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        //Database initialization
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Orders, OrdersHolder>(
                Orders.class,
                R.layout.orderlayout,
                OrdersHolder.class,
                mFirebaseDatabaseReference.child(ORDERS)) {
            @Override
            protected void populateViewHolder(OrdersHolder viewHolder, Orders model, int position) {
                viewHolder.rootorderId.setText(String.valueOf(model.getOrderId()));
                viewHolder.rootmobile.setText(String.valueOf(model.getMobile()));
                viewHolder.rootdone.setText(String.valueOf(model.getDone()));
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int orderCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                if (lastVisiblePosition==-1 || (positionStart >= (orderCount -1) && lastVisiblePosition == (positionStart -1))){
                    mRecyclerView.scrollToPosition(positionStart);
                }
            }
        });

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mFirebaseAdapter);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
              //          .setAction("Action", null).show();
            Intent intent = new Intent(MainActivity.this, NewOrder.class);
            startActivity(intent);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
