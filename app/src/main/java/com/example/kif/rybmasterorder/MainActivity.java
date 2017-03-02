package com.example.kif.rybmasterorder;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    //public static final String ORDERS = "orders";// как правильно объявить?
    public static final String ORDERS = "orders";

    private RecyclerView mRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;

    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter <Order, OrdersHolder> mFirebaseAdapter;
    private Query query;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    MaterialSearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //init search
        searchView = (MaterialSearchView) findViewById(R.id.search_view);

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
            }

            @Override
            public void onSearchViewClosed() {

                if(!isNetworkConnected()){
                    Toast.makeText(MainActivity.this, "Нет интернета... доступна ограниченная работа в режиме оффлайн", Toast.LENGTH_LONG).show();
                }
                query = mFirebaseDatabaseReference.child(ORDERS);
                setUpFirebaseAdapter(query);

            }
        });

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String mQuery) {
                if(mQuery !=null && !mQuery.isEmpty() ){
                    try {
                    query = mFirebaseDatabaseReference.child(ORDERS).orderByChild("orderId").startAt(Integer.parseInt(mQuery)).endAt(Integer.parseInt(mQuery));
                    setUpFirebaseAdapter(query);
                    }catch (NumberFormatException e) {
                        Toast.makeText(MainActivity.this, mQuery+" is not a number", Toast.LENGTH_LONG).show();
                    }
                }

                else{
                    if(!isNetworkConnected()){
                        Toast.makeText(MainActivity.this, "Нет интернета... доступна ограниченная работа в режиме оффлайн", Toast.LENGTH_LONG).show();
                    }

                    query = mFirebaseDatabaseReference.child(ORDERS);
                    setUpFirebaseAdapter(query);

                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


        // Auth on navigationView
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        progressDialog = new ProgressDialog(this);

        firebaseAuth = FirebaseAuth.getInstance();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View headerview = navigationView.getHeaderView(0);
        TextView profilename = (TextView) headerview.findViewById(R.id.username);

        if(firebaseAuth.getCurrentUser()!=null) {
            profilename.setText(firebaseAuth.getCurrentUser().getEmail().toString());
        }

        Button profilesignup = (Button) headerview.findViewById(R.id.nav_header_signup);
        profilesignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(firebaseAuth.getCurrentUser()!=null) {
                    firebaseAuth.signOut();
                }
                Intent intent= new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
            }
        });

        // init recycler
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mLinearLayoutManager = new LinearLayoutManager(this);
        mLinearLayoutManager.setStackFromEnd(true);

        //Database initialization
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseDatabaseReference.keepSynced(true);

        /// create new Order
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editOrder(new Order(),null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(!isNetworkConnected()){
            Toast.makeText(this, "Нет интернета... доступна ограниченная работа в режиме оффлайн", Toast.LENGTH_LONG).show();
        }
        query = mFirebaseDatabaseReference.child(ORDERS);
        setUpFirebaseAdapter(query);
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void editOrder(Order order, String key) {
        final NewEditOrderFragment fragment = NewEditOrderFragment.newInstance(order, key);

        fragment.setOrder(new NewEditOrderFragment.OrderListener() {

            @Override
            public void save(Order order, String key) {
                if (key == null) {
                    mFirebaseDatabaseReference.child(ORDERS).push().setValue(order);
                    fragment.dismiss();
                } else{
                   mFirebaseDatabaseReference.child(ORDERS).child(key).setValue(order);
                   fragment.dismiss();
                }
            }

            @Override
            public void cancel() {
                fragment.dismiss();
            }
        });

        FragmentManager fm = getSupportFragmentManager();
        fragment.show(fm, "fragment_edit_name");
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(mFirebaseAdapter!=null){
            mFirebaseAdapter.cleanup();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(mFirebaseAdapter!=null){
            mFirebaseAdapter.cleanup();
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_allOrders) {
            if(mFirebaseAdapter!=null){
                mFirebaseAdapter.cleanup();
                mFirebaseAdapter.notifyDataSetChanged();
            }
            query = mFirebaseDatabaseReference.child(ORDERS);
            setUpFirebaseAdapter(query);


        } else if (id == R.id.nav_spinning) {
            if(mFirebaseAdapter!=null){
                mFirebaseAdapter.cleanup();
                mFirebaseAdapter.notifyDataSetChanged();
            }
            query = mFirebaseDatabaseReference.child(ORDERS).orderByChild("type").equalTo(true);
            setUpFirebaseAdapter(query);

            // Handle the camera action
        } else if (id == R.id.nav_reel) {
            if(mFirebaseAdapter!=null){
                mFirebaseAdapter.cleanup();
                mFirebaseAdapter.notifyDataSetChanged();
            }
            query = mFirebaseDatabaseReference.child(ORDERS).orderByChild("type").equalTo(false);
            setUpFirebaseAdapter(query);

        } else if (id == R.id.nav_undone) {
            if(mFirebaseAdapter!=null){
                mFirebaseAdapter.cleanup();
                mFirebaseAdapter.notifyDataSetChanged();
            }

            query = mFirebaseDatabaseReference.child(ORDERS).orderByChild("done").equalTo(false);
            setUpFirebaseAdapter(query);

        } /*else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

private void setUpFirebaseAdapter(Query query) {

    progressDialog.setMessage("Loading Please Wait...");
    progressDialog.show();


    mFirebaseAdapter = new FirebaseRecyclerAdapter<Order, OrdersHolder>(
            Order.class,
            R.layout.orderlayout,
            OrdersHolder.class,
            query) {

        @Override
        protected void populateViewHolder(final OrdersHolder viewHolder, Order model, final int position) {

            final Order mOrder= model;

            viewHolder.rootorderId.setText("№"+String.valueOf(model.getOrderId()));
            viewHolder.rootName.setText(String.valueOf(model.getName()));
            viewHolder.rootmobile.setText(String.valueOf(model.getMobile()));
            viewHolder.rootPrice.setText(String.valueOf(model.getPrice())+" грн.");
            viewHolder.rootCheckBoxDone.setChecked(model.getDone());
            viewHolder.rootMasterName.setText(model.getMasterName());
            viewHolder.rootDetail.setText(model.getDetail());
            viewHolder.rootDate.setText(model.getDate());



            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    String key = mFirebaseAdapter.getRef(position).getKey();
                    editOrder(mOrder, key);
                }
            });



            viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {

                    AlertDialog.Builder adb=new AlertDialog.Builder(MainActivity.this);
                    adb.setTitle("Удалить");
                    adb.setMessage("Вы уверенны, что хотите удалить ремонт №"+String.valueOf(mOrder.getOrderId()));
                    adb.setPositiveButton("Удалить", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                            mFirebaseAdapter.getRef(position).removeValue();
                        }
                    });
                    adb.setIcon(android.R.drawable.ic_delete);
                    adb.create().show();

                    return true;
                }
            });

            //     обработчик изменения галочки "готов"
            viewHolder.rootCheckBoxDone.setOnClickListener(new CompoundButton.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final String key = mFirebaseAdapter.getRef(position).getKey();

                    if(viewHolder.rootCheckBoxDone.isChecked()){

                        AlertDialog.Builder adb=new AlertDialog.Builder(MainActivity.this);
                        adb.setTitle("Подтвердить");
                        adb.setMessage("Вы уверенны, что ремонт №"+String.valueOf(mOrder.getOrderId())+ " готов?");
                        adb.setPositiveButton("Готов", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mFirebaseDatabaseReference.child(ORDERS).child(key).child("done").setValue(true);
                                Toast.makeText(MainActivity.this,"Ремонт готов", Toast.LENGTH_SHORT).show();
                            }
                        });
                        adb.create().show();
                    }
                    else {
                        AlertDialog.Builder adb=new AlertDialog.Builder(MainActivity.this);
                        adb.setTitle("Подтвердить");
                        adb.setMessage("Вы уверенны, что ремонт №"+String.valueOf(mOrder.getOrderId())+ " не готов?");
                        adb.setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                mFirebaseDatabaseReference.child(ORDERS).child(key).child("done").setValue(false);
                                Toast.makeText(MainActivity.this,"Ремонт еще не готов", Toast.LENGTH_SHORT).show();
                            }
                        });
                        adb.create().show();
                    }
                }
            });
        }
    };


    mLinearLayoutManager.setReverseLayout(true);
    mLinearLayoutManager.setStackFromEnd(true);

    mRecyclerView.setLayoutManager(mLinearLayoutManager);


    // init adapter
    mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver(){
        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            int orderCount = mFirebaseAdapter.getItemCount();
            int lastVisiblePosition = mLinearLayoutManager.findLastCompletelyVisibleItemPosition();

            if (lastVisiblePosition == -1 ||
                    (positionStart >= (orderCount - 1) &&
                            lastVisiblePosition == (positionStart - 1))) {
                mLinearLayoutManager.scrollToPosition(positionStart);
            }
        }
    });
    mRecyclerView.setAdapter(mFirebaseAdapter);

    query.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {

            progressDialog.dismiss();
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
    if(!isNetworkConnected()){
        progressDialog.dismiss();}
  }
}