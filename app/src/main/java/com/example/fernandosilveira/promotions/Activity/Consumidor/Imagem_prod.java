package com.example.fernandosilveira.promotions.Activity.Consumidor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.fernandosilveira.promotions.R;

public class Imagem_prod extends AppCompatActivity
{
    private String caminho = "";
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_imagem_prod);
        imageView = (ImageView) findViewById(R.id.imagem_prod_);
        Intent intent = getIntent();
        final Bundle dados = intent.getExtras();
        if (dados != null)
        {
            caminho = dados.getString("codimg");
        }
        Glide.with(getApplicationContext()).load(caminho).into(imageView);
    }
}
