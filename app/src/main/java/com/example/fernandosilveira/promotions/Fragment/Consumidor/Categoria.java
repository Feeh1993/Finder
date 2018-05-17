package com.example.fernandosilveira.promotions.Fragment.Consumidor;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;

import com.example.fernandosilveira.promotions.Adapter.Consumidor.CategoriaAdapter;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Interface.CustomCheckClickListener;
import com.example.fernandosilveira.promotions.Model.Categoria_Mod;
import com.example.fernandosilveira.promotions.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Categoria extends Fragment
{
    private ArrayList<String> addDestaques = new ArrayList<>();
    private ArrayList<String> destaques = new ArrayList<>();
    private ArrayList<Categoria_Mod> listcategoria = new ArrayList<>();
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private ValueEventListener valueEventListener;
    private RecyclerView recyclerView;

    public Categoria()
    {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view =inflater.inflate(R.layout.fragment_categoria, container, false);
        addCategoria();
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view_categoria);
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getContext(), 2);
        final String[] categoria = getResources().getStringArray(R.array.categoria_array);
        return view;
    }
    @Override
    public void onResume()
    {
        super.onResume();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        CategoriaAdapter rvAdapter = new CategoriaAdapter(getContext(), listcategoria, new CustomCheckClickListener()
        {
            @Override
            public void oncheckClick(CheckBox checkBox, int position, String categoria)
            {
                if (checkBox.isChecked())
                {
                    ref.child("destaques").child(ConfiguracaoFirebase.getUID()).child(checkBox.getText().toString()).setValue(true);
                } else
                {
                    ref.child("destaques").child(ConfiguracaoFirebase.getUID()).child(checkBox.getText().toString()).setValue(false);
                }
            }
        });
        recyclerView.setAdapter(rvAdapter);
    }
    @Override
    public void onStart()
    {
        super.onStart();
        Log.d("Sistema","onStart(); Categoria_Mod");
    }
    @Override
    public void onStop()
    {
        super.onStop();
        Log.d("Sistema","onStop(); Categoria_Mod");
    }
    public void addCategoria()
    {
        listcategoria.add(new Categoria_Mod("Games",R.mipmap.games_img));
        listcategoria.add(new Categoria_Mod("Brinquedos",R.mipmap.brinquedos_img));
        listcategoria.add(new Categoria_Mod("Colecionavéis",R.mipmap.colecionaveis_img));
        listcategoria.add(new Categoria_Mod("Esporte e Lazer",R.mipmap.esporte_img));
        listcategoria.add(new Categoria_Mod("Arte e Antiguidade",R.mipmap.arte_img));
        listcategoria.add(new Categoria_Mod("Instrumentos Musicais",R.mipmap.instrumentos_musicais_img));
        listcategoria.add(new Categoria_Mod("Fotografia",R.mipmap.fotografia_img));
        listcategoria.add(new Categoria_Mod("Assinaturas e Revistas",R.mipmap.revista_img));
        listcategoria.add(new Categoria_Mod("Artigos Religiosos",R.mipmap.artigo_religioso_img));
        listcategoria.add(new Categoria_Mod("Casa e Decoração",R.mipmap.casa_decoracao_img));
        listcategoria.add(new Categoria_Mod("Construção e Ferramentas",R.mipmap.construcao_img));
        listcategoria.add(new Categoria_Mod("Eletrodomésticos",R.mipmap.eletrodomestico));
        listcategoria.add(new Categoria_Mod("Pet Shop",R.mipmap.pet_shop_img));
        listcategoria.add(new Categoria_Mod("Livros",R.mipmap.livros_img));
        listcategoria.add(new Categoria_Mod("CDs,DVDs,Blu-Ray e HD-DVD",R.mipmap.cd_img));
        listcategoria.add(new Categoria_Mod("Filme Digital",R.mipmap.games_img));
        listcategoria.add(new Categoria_Mod("Música Digital",R.mipmap.musica_digital_img));
        listcategoria.add(new Categoria_Mod("Fitas K7 Gravadas , VHS e Discos de Vinil",R.mipmap.vhs_img));
        listcategoria.add(new Categoria_Mod("Moda e Acessórios",R.mipmap.moda_img));
        listcategoria.add( new Categoria_Mod("Perfumaria e Cosméticos",R.mipmap.perfumaria_img));
        listcategoria.add(new Categoria_Mod("Saúde",R.mipmap.saude_img));
        listcategoria.add(new Categoria_Mod("Jóias e Relógios",R.mipmap.joias_img));
        listcategoria.add(new Categoria_Mod("Sex Shop",R.mipmap.sex_shop_img));
        listcategoria.add(new Categoria_Mod("Alimentos e Bebidas",R.mipmap.alimento_img));
        listcategoria.add(new Categoria_Mod("Indústria,Comércio e Negócios",R.mipmap.industria_img));
        listcategoria.add(new Categoria_Mod("Automóveis e Veículos",R.mipmap.automoveis_img));
        listcategoria.add(new Categoria_Mod("Informática",R.mipmap.informatica_img));
        listcategoria.add(new Categoria_Mod("Eletrônicos",R.mipmap.eletronicos_img));
        listcategoria.add(new Categoria_Mod("Telefonia",R.mipmap.telefone_img));

    }
}