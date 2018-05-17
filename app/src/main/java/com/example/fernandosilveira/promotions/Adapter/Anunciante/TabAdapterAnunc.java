package com.example.fernandosilveira.promotions.Adapter.Anunciante;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Activity.Anunciante.NovoAnuncio;
import com.example.fernandosilveira.promotions.Activity.Geral.Login;
import com.example.fernandosilveira.promotions.AlarmReceiver.AlarmDataAnunciante;
import com.example.fernandosilveira.promotions.Entity.TabEntity;
import com.example.fernandosilveira.promotions.Entity.ViewFindUtils;
import com.example.fernandosilveira.promotions.Fragment.Anunciante.Configuracoes;
import com.example.fernandosilveira.promotions.Fragment.Anunciante.MeusAnuncios;
import com.example.fernandosilveira.promotions.R;
import com.example.fernandosilveira.promotions.Service.ServicoDataAnunciante;
import com.flyco.tablayout.CommonTabLayout;
import com.flyco.tablayout.listener.CustomTabEntity;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

/**
 * Created by Fernando Silveira on 30/08/2017.
 */
public class TabAdapterAnunc extends AppCompatActivity
{
    private Context mContext = this;
    private String[] mTitles = {"Anuncios","Configurações"};
    private int[] mIconUnselectIds = {R.drawable.categoria_seleciona,R.drawable.config_seleciona};
    private int[] mIconSelectIds = {R.drawable.ic_content_paste, R.drawable.config_unselec};
    private ArrayList<CustomTabEntity> mTabEntities = new ArrayList<>();
    private View mDecorView;
    private ViewPager mViewPager;
    private CommonTabLayout mTabLayout_2;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_anunc);
        IniciarServico();
        for (int i = 0; i < mTitles.length; i++)
        {
            mTabEntities.add(new TabEntity(mTitles[i], mIconSelectIds[i], mIconUnselectIds[i]));
        }
        mDecorView = getWindow().getDecorView();
        mViewPager = ViewFindUtils.find(mDecorView, R.id.vp_1);
        mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
        mTabLayout_2 = ViewFindUtils.find(mDecorView, R.id.tl_1);
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
        mViewPager.setCurrentItem(1);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_toolbar_anunciante, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menunovoanuncio:
                startActivity(new Intent(this, NovoAnuncio.class));
                return true;
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
                    fragment = new MeusAnuncios();
                    break;
                case 1:
                    fragment = new Configuracoes();
                    break;
            }
            return fragment;
        }
    }
    public void DeslogarUser()
    {
        //busca a instancia do usuario e desloga o usuario
        FirebaseAuth.getInstance().signOut();
        PackageManager pm  = TabAdapterAnunc.this.getPackageManager();
        ComponentName componentName = new ComponentName(TabAdapterAnunc.this, AlarmDataAnunciante.class);
        pm.setComponentEnabledSetting(componentName,PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        startActivity(new Intent(this, Login.class));
        stopService(new Intent(getApplicationContext(), ServicoDataAnunciante.class));
        overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);
        Toast.makeText(this, "Anunciante_Mod Desconectado!", Toast.LENGTH_LONG).show();
        finish();
    }
    public void IniciarServico()
    {
        Intent intent = new Intent(this, ServicoDataAnunciante.class);
        startService(intent);
    }
}