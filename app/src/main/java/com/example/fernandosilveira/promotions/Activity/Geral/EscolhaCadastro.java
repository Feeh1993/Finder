package com.example.fernandosilveira.promotions.Activity.Geral;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.fernandosilveira.promotions.Activity.Anunciante.CriarContaAnunciante;
import com.example.fernandosilveira.promotions.Activity.Consumidor.CriarContaConsumidor;
import com.example.fernandosilveira.promotions.R;

public class EscolhaCadastro extends Activity
{
    public Button btnEmpresa,btnUsuario,btnJasouCadastrado;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_escolha_cadastro);
        btnEmpresa = (Button) findViewById(R.id.btnSouEmpresa);
        btnUsuario = (Button) findViewById(R.id.btnSouCliente);
        btnJasouCadastrado = (Button) findViewById(R.id.btnjasoucadastro);
        btnUsuario.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), CriarContaConsumidor.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);
            }
        });
        btnEmpresa.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(getApplicationContext(), CriarContaAnunciante.class);
                startActivity(intent);
                overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);
            }
        });
        btnJasouCadastrado.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(getApplicationContext(), Login.class);
                startActivity(i);
                overridePendingTransition(R.anim.anim_fadein, R.anim.anim_fadeout);
            }
        });
    }
}