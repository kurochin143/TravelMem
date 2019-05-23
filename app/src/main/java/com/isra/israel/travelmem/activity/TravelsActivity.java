package com.isra.israel.travelmem.activity;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.libraries.places.api.Places;
import com.google.maps.android.SphericalUtil;
import com.isra.israel.travelmem.R;
import com.isra.israel.travelmem.adapter.TravelsAdapter;
import com.isra.israel.travelmem.dao.FirebaseSessionSPDAO;
import com.isra.israel.travelmem.dao.SettingsSPDAO;
import com.isra.israel.travelmem.dao.TravelMemLocalCacheDAO;
import com.isra.israel.travelmem.fragment.SettingsFragment;
import com.isra.israel.travelmem.fragment.TravelFragment;
import com.isra.israel.travelmem.model.Travel;
import com.isra.israel.travelmem.view_model.TravelsViewModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class TravelsActivity extends AppCompatActivity {

    private String uid;
    private String token;
    private TravelsAdapter travelsAdapter;
    private TravelsViewModel travelsViewModel;
    private int openedTravelPosition;
    private Timer locationNotificationTimer = new Timer();
    private FusedLocationProviderClient fusedLocationProviderClient;
    public static final String NOTIFICATION_CHANNEL_ID = "id:1";
    public static final String NOTIFICATION_NAME = "name:1";
    private int notificationId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_travels);

        Toolbar toolbar = findViewById(R.id.a_travels_toolbar);
        setSupportActionBar(toolbar);

        Places.initialize(getApplicationContext(), getString(R.string.google_maps_api_key));

        final View disabledScreenView = findViewById(R.id.a_travels_v_disabled_screen);
        final SwipeRefreshLayout travelsSRL = findViewById(R.id.a_travels_srl_travels);
        travelsSRL.setRefreshing(true);

        // setup recycler view
        RecyclerView recyclerView = findViewById(R.id.a_travels_r_travels);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        travelsAdapter = new TravelsAdapter();
        recyclerView.setAdapter(travelsAdapter);

        travelsViewModel = ViewModelProviders.of(this).get(TravelsViewModel.class);
        uid = FirebaseSessionSPDAO.getUid(this);
        token = FirebaseSessionSPDAO.getIdToken(this);

        // travels observer
        travelsViewModel.getTravelsLiveData(this, uid, token).observe(this, new Observer<ArrayList<Travel>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Travel> travels) {
                if (travels == null) {
                    return;
                }

                travelsAdapter.setTravels(travels);

                travelsSRL.setRefreshing(false);
                disabledScreenView.setVisibility(View.GONE);
            }
        });

        travelsSRL.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                travelsViewModel.reloadData(uid, token);
                disabledScreenView.setVisibility(View.VISIBLE);
            }
        });

        // adapter
        travelsAdapter.setOnTravelClickListener(new TravelsAdapter.OnTravelClickListener() {
            @Override
            public void onTravelClick(Travel travel, int position) {
                openedTravelPosition = position;

                // open travel fragment for viewing/editing
                TravelFragment travelFragment = TravelFragment.newInstance(travel, false);

                // on edit
                travelFragment.setOnTravelEditListener(new TravelFragment.OnTravelEditListener() {
                    @Override
                    public void onTravelEdit(Travel travel) {
                        travel.setLastUpdatedTime(System.currentTimeMillis());
                        travelsViewModel.updateTravel(uid, token, travel);
                        travelsAdapter.setTravelAt(travel, openedTravelPosition);
                    }
                });

                // on delete
                travelFragment.setOnTravelDeleteListener(new TravelFragment.OnTravelDeleteListener() {
                    @Override
                    public void onTravelDelete(String id) {
                        travelsViewModel.removeTravel(uid, token, id);
                        travelsAdapter.removeTravel(id);
                    }
                });

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.a_travels_fl_root, travelFragment)
                        .addToBackStack(null)
                        .commit();

            }
        });

        // add travel button
        findViewById(R.id.a_travels_fab_add_travel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // open travel fragment for creation
                TravelFragment travelFragment = TravelFragment.newInstance(new Travel(), true);

                // on create
                travelFragment.setOnTravelCreateListener(new TravelFragment.OnTravelCreateListener() {
                    @Override
                    public void onTravelCreate(final Travel travel) {
                        travel.setCreationTime(System.currentTimeMillis());

                        travelsViewModel.addTravel(uid, token, travel);
                        travelsAdapter.addTravel(travel);
                    }
                });

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.a_travels_fl_root, travelFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        // TODO notification
        locationNotificationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boolean isNotificationEnabled = SettingsSPDAO.isNotificationEnabled(TravelsActivity.this);
                        if (!isNotificationEnabled) {
                            return;
                        }

                        if (ContextCompat.checkSelfPermission(TravelsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                            // use user location as origin
                            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    LatLng userLatLng = new LatLng(location.getLatitude(), location.getLongitude());
                                    ArrayList<Travel> travels = TravelMemLocalCacheDAO.getTravels(TravelsActivity.this, uid);
                                    for (Travel travel : travels) {
                                        if (travel.shouldNotify() == 1 && travel.getDestination() != null) {
                                            LatLng destinationLatLng = travel.getDestination().getLatLng();

                                            double distanceMeters = SphericalUtil.computeDistanceBetween(userLatLng, destinationLatLng);
                                            if (distanceMeters <= 100.0) {

                                                // set notify to false
                                                travel.setShouldNotify(0);

                                                travelsViewModel.updateTravel(uid, token, travel);
                                                travelsAdapter.updateTravel(travel);

                                                notifyDestinationReached();
                                            }
                                        }
                                    }
                                }
                            });
                        } else {
                            ActivityCompat.requestPermissions(TravelsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
                        }
                    }
                });
            }
        }, 0, 1000);

    }

    private void notifyDestinationReached() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // channel
            NotificationManager notificationManager = (NotificationManager) getSystemService(TravelsActivity.NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, NOTIFICATION_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("desc: This is an example notification");
            notificationManager.createNotificationChannel(channel);

            // builder
            NotificationCompat.Builder builder = new NotificationCompat.Builder(TravelsActivity.this, NOTIFICATION_CHANNEL_ID)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setContentTitle("Destination reached")
                    .setColor(Color.argb(255, 100, 100, 255))
                    .setSmallIcon(R.drawable.ic_flag_32dp)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);

            notificationManager.notify(notificationId++, builder.build());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m_app_bar_settings: {
                // open settings
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.a_travels_fl_root, new SettingsFragment())
                        .addToBackStack(null)
                        .commit();
            } break;
        }

        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        locationNotificationTimer.cancel();
    }
}
