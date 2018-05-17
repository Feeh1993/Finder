package com.example.fernandosilveira.promotions.Fragment.Consumidor;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Model.LocalProd;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.example.fernandosilveira.promotions.Maps.Local.MY_PERMISSIONS_REQUEST_LOCATION;
public class Inicial extends Fragment implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener, GoogleMap.OnMarkerClickListener
{
    MapView mMapView;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mLastLocation;
    private Marker mCurrLocationMarker;
    private GoogleMap mMap;
    private DatabaseReference mDatabase = ConfiguracaoFirebase.getFirebase();
    private String meuUid  = ConfiguracaoFirebase.getFirebaseAutenticacao().getCurrentUser().getUid();
    private ArrayList<LocalProd> listlocais = new ArrayList<>();
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        Log.d("INICIAL","onCreateView");
        View rootView = inflater.inflate(R.layout.fragment_inicial_consumidor, container, false);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            checkLocationPermission();
        }
        LocationManager mlocManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!enabled)
        {
            localdesativado();
        }
        mMapView = (MapView) rootView.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();
        try
        {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        mMapView.getMapAsync(this);
        return rootView;
    }
    @Override
    public void onStart()
    {
        super.onStart();
        Log.d("INICIAL","onStart");
    }

    @Override
    public void onResume()
    {
        Log.d("INICIAL","onResume");
        super.onResume();
        mMapView.onResume();
    }
    @Override
    public void onPause()
    {
        Log.d("INICIAL","onPause");
        super.onPause();
        mMapView.onPause();
    }
    @Override
    public void onDestroy()
    {
        Log.d("INICIAL","onDestroy");
        super.onDestroy();
        mMapView.onDestroy();
    }
    @Override
    public void onLowMemory()
    {
        super.onLowMemory();
        Log.d("INICIAL","onlowmemory");
        mMapView.onLowMemory();
    }
    public boolean checkLocationPermission()
    {
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION))
            {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
            }
            else
                {
                    ActivityCompat.requestPermissions(getActivity(),
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);
                }
                return false;
        }
        else
            {
                return true;
            }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults)
    {
        switch (requestCode)
        {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                {
                    if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    {
                        if (ContextCompat.checkSelfPermission(getContext(),
                            android.Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED)
                        {
                            if (mGoogleApiClient == null)
                            {
                                buildGoogleApiClient();
                            }
                            mMap.setMyLocationEnabled(true);
                        }
                    }
                    else
                        {
                            Toast.makeText(getContext(), "Permissão Negada", Toast.LENGTH_LONG).show();
                        }
                        return;
                }
        }
    }
    @Override
    public void onConnected(@Nullable Bundle bundle)
    {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED)
        {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
        recperarids();
    }
    @Override
    public void onConnectionSuspended(int i)
    {
    }
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult)
    {
    }
    @Override
    public void onLocationChanged(Location location)
    {
        mLastLocation = location;
        LatLng latLng = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        if (mCurrLocationMarker != null)
        {
            mCurrLocationMarker.remove();
        }
        if (mGoogleApiClient != null)
        {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15.5f));
            recperarids();
        }
    }
    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }
    @Override
    public void onMapReady(GoogleMap googleMap)
    {
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);
        uiSettings.setMapToolbarEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);
        uiSettings.setZoomControlsEnabled(false);
        mMap.setOnMarkerClickListener(this);
        Log.d("INICIAL","QTD de locais adicionados" +"\n"+listlocais.size());
        for (int i = 0; i < listlocais.size(); i++)
        {
            LatLng latLng = new LatLng(listlocais.get(i).lat,listlocais.get(i).lng);
            googleMap.addMarker(new MarkerOptions().position(latLng).title("Recebeu promoções aqui!").snippet(listlocais.get(i).empres)).setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(getContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED)
            {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
        }
        else
            {
                buildGoogleApiClient();
                mMap.setMyLocationEnabled(true);
            }
    }
    protected synchronized void buildGoogleApiClient()
    {
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }
    private void recperarids()
    {
        listlocais.clear();
        mDatabase.child("usuarios").child(meuUid).child("idmaps").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getValue() != null)
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        String ids =ds.getValue().toString();
                        final String[] recuperarStrings = ids.split(":");
                        recuperarListLocais(recuperarStrings[0],recuperarStrings[1]);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }
    private void recuperarListLocais(final String cat, final String nomeProd)
    {
        //listlocais.clear();
        mDatabase.child("locais").child(cat).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    String test = ds.child("nomeProd").getValue().toString();
                    Log.d("INICIAL","test "+ test +" E " +nomeProd);
                    if (nomeProd.contains(test))
                    {
                        LocalProd localProd = ds.getValue(LocalProd.class);
                        LatLng latLng = new LatLng(localProd.lat,localProd.lng);
                        Log.d("INICIAL","SIM QTD"+ listlocais);
                        for (int i = 0; i < listlocais.size(); i++)
                        {
                            if (!listlocais.get(i).empres.equals(localProd.empres))
                            {
                              listlocais.add(localProd);
                            }
                        }
                        if (listlocais.size() == 0)
                        {
                            listlocais.add(localProd);
                        }
                    }else Log.d("INICIAL","NAO");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }
    public void localdesativado()
    {
        Snackbar snackbar = Snackbar
                .make(getActivity().findViewById(android.R.id.content), "Voce não conseguirá visualizar as empresas se não ativar seu local!", Snackbar.LENGTH_INDEFINITE)
                .setAction("Ativar", new View.OnClickListener()
                {
                    @Override
                    public void onClick(View view)
                    {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                });
        snackbar.setActionTextColor(Color.WHITE);
        View sbView = snackbar.getView();
        TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.WHITE);
        snackbar.show();
    }
}