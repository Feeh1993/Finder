package com.example.fernandosilveira.promotions.Adapter.Consumidor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Activity.Geral.Login;
import com.example.fernandosilveira.promotions.Entity.TabEntity;
import com.example.fernandosilveira.promotions.Entity.ViewFindUtils;
import com.example.fernandosilveira.promotions.Fragment.Consumidor.Categoria;
import com.example.fernandosilveira.promotions.Fragment.Consumidor.Configuracoes;
import com.example.fernandosilveira.promotions.Fragment.Consumidor.Favoritos;
import com.example.fernandosilveira.promotions.Fragment.Consumidor.Historico;
import com.example.fernandosilveira.promotions.R;
import com.example.fernandosilveira.promotions.Service.Geofence.ServicoLocalConsumidor;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class TabAdapterConsumidor extends AppCompatActivity
{
    private int MULTIPLA_PERMISSÃO_PEDIDO_CODIGO = 1;
    private String[] mTitles = {"Categoria", "Historico","Favoritos", "Configurações"};
    private int[] mIconUnselectIds =
            {
                    R.drawable.categoria_seleciona, R.drawable.historic_seleciona,
                    R.drawable.favorito_unselec, R.drawable.config_seleciona
            };
    private int[] mIconSelectIds =
            {
                    R.drawable.ic_content_paste, R.drawable.ic_history,
                    R.drawable.ic_favorite_border, R.drawable.config_unselec
            };
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private View mDecorView;
    private ViewPager mViewPager;
    private CommonTabLayout mTabLayout_2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_consumidor);
        iniciarGeofenceService();
        for (int i = 0; i < mTitles.length; i++)
        {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        mDecorView = getWindow().getDecorView();
        mViewPager = ViewFindUtils.find(mDecorView, R.id.vp_2);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        /** com ViewPager */
        mTabLayout_2 = ViewFindUtils.find(mDecorView, R.id.tl_2);
        tl_2();
        mTabLayout_2.setMsgMargin(0, -5, 5);
        mTabLayout_2.setMsgMargin(1, -5, 5);
        mTabLayout_2.setMsgMargin(3, 0, 5);
    }
    private void tl_2()
    {
        mTabLayout_2.setTabData(mTabEntities);
        mTabLayout_2.setOnTabSelectListener(new OnTabSelectListener()
        {
            @Override
            public void onTabSelect(int position) {
                mViewPager.setCurrentItem(position);
            }
            @Override
            public void onTabReselect(int position)
            {
            }
        });
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
            }
            @Override
            public void onPageSelected(int position) {
                mTabLayout_2.setCurrentTab(position);
            }
            @Override
            public void onPageScrollStateChanged(int state)
            {
            }
        });
        mViewPager.setCurrentItem(2);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_usuario, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menulogout:
                DeslogarUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private class MyPagerAdapter extends FragmentPagerAdapter
    {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public int getCount() {
            return mTitles.length;
        }
        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }
        @Override
        public Fragment getItem(int position)
        {
            Fragment fragment = null;
            switch (position)
            {
                case 0:
                    fragment = new Categoria();
                    break;
                case 1:
                    fragment = new Historico();
                    break;
                case 2:
                    fragment = new Favoritos();
                    break;
                case 3:
                    fragment = new Configuracoes();
                    break;
            }
            return fragment;
        }
    }
    private void iniciarGeofenceService()
    {
        Intent intent = new Intent(this, ServicoLocalConsumidor.class);
        Log.i("Sistema", "Início do serviço do cliente de localização");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            boolean hasPermissionAccesssFineLocation = ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            boolean hasPermissionAccesssCoarseLocation = ContextCompat.checkSelfPermission(getApplicationContext(),
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            if (!hasPermissionAccesssFineLocation || !hasPermissionAccesssCoarseLocation)
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, MULTIPLA_PERMISSÃO_PEDIDO_CODIGO);
            else
                startService(intent);
        }
        else
            {
                startService(intent);
            }
    }
    public void DeslogarUser()
    {
        //busca a instancia do usuario e desloga o usuario
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(this, Login.class));
        stopService(new Intent(getApplicationContext(), ServicoLocalConsumidor.class));
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);
        Toast.makeText(this, "Consumidor Desconectado!", Toast.LENGTH_LONG).show();
        finish();
    }
}