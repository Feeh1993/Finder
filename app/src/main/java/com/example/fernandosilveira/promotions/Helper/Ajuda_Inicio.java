package com.example.fernandosilveira.promotions.Helper;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.chyrta.onboarder.OnboarderActivity;
import com.chyrta.onboarder.OnboarderPage;
import com.example.fernandosilveira.promotions.Activity.Geral.Login;
import com.example.fernandosilveira.promotions.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fernando Silveira on 28/08/2017.
 */

public class Ajuda_Inicio extends OnboarderActivity
{
    List<OnboarderPage> onboarderPages;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        onboarderPages = new ArrayList<OnboarderPage>();
        // Criar paginas
        OnboarderPage onboarderPage1 = new OnboarderPage("Seja bem vindo ao Finder", "Uma nova ferramenta de ver promoções!",R.drawable.logo1);
        OnboarderPage onboarderPage2 = new OnboarderPage("Veja aquilo que voce realmente quer ver", "Selecione as categorias que mais lhe interessam e descubra promoções proximas a voce", R.drawable.visualize_inicio);
        OnboarderPage onboarderPage3 = new OnboarderPage("Receba notificações dos produtos", "Atraves da sua localização,lojas proximas a voce enviaram anuncios onde voce estiver!",R.drawable.not_inicio);
        OnboarderPage onboarderPage4 = new OnboarderPage("Tudo na palma da sua mão", "Uma aplicação feita para sua necessidade", R.drawable.palma_inicio);
        setFinishButtonTitle("Vamos la!");
        setSkipButtonTitle("Pular");

        onboarderPage1.setDescriptionColor(R.color.tabTitle);
        onboarderPage1.setTitleColor(R.color.tabTitle);
        onboarderPage1.setBackgroundColor(R.color.colorgray);

        onboarderPage2.setBackgroundColor(R.color.colorgray);
        onboarderPage2.setDescriptionColor(R.color.tabTitle);
        onboarderPage2.setTitleColor(R.color.tabTitle);

        onboarderPage3.setBackgroundColor(R.color.colorgray);
        onboarderPage3.setDescriptionColor(R.color.tabTitle);
        onboarderPage3.setTitleColor(R.color.tabTitle);

        onboarderPage4.setBackgroundColor(R.color.colorgray);
        onboarderPage4.setDescriptionColor(R.color.tabTitle);
        onboarderPage4.setTitleColor(R.color.tabTitle);

        onboarderPages.add(onboarderPage1);
        onboarderPages.add(onboarderPage2);
        onboarderPages.add(onboarderPage3);
        onboarderPages.add(onboarderPage4);
        setOnboardPagesReady(onboarderPages);
    }
    @Override
    public void onSkipButtonPressed()
    {
        super.onSkipButtonPressed();
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
    @Override
    public void onFinishButtonPressed()
    {
        startActivity(new Intent(getApplicationContext(), Login.class));
        finish();
    }
    @Override
    public void onBackPressed()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pular ?");
        builder.setMessage("Pular");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(getApplicationContext(), Login.class));
                finish();
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}