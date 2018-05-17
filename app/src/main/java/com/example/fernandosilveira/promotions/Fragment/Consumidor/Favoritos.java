package com.example.fernandosilveira.promotions.Fragment.Consumidor;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Activity.Consumidor.Anuncio_Notificacao;
import com.example.fernandosilveira.promotions.Activity.Validacao.ValidarData;
import com.example.fernandosilveira.promotions.Adapter.Consumidor.ProdutoAdapter;
import com.example.fernandosilveira.promotions.Config.ConexaoTeste;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Helper.SwipeUtil.SwipeUtil;
import com.example.fernandosilveira.promotions.Interface.CustomItemClickListener;
import com.example.fernandosilveira.promotions.Model.Produto;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Favoritos extends Fragment
{
    private DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
    private ArrayList<Produto> listfavorito = new ArrayList<>();
    private ArrayList<String> key = new ArrayList<>();
    private TextView txterro;
    private Button btnerro;
    private ProgressBar prgerro;

    private RecyclerView recyclerView;

    public Favoritos()
    {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_favoritos, container, false);
        listfavorito = new ArrayList<>();
        txterro =(TextView)view.findViewById(R.id.txtajudaFav);
        btnerro =(Button) view.findViewById(R.id.btnCarregarFav);
        prgerro =(ProgressBar) view.findViewById(R.id.prgCarregarFav);
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);


        btnerro.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                key.clear();
                listfavorito.clear();
                exibirMsgLoad("NAO");
                recperarids(ConfiguracaoFirebase.getUID());
            }
        });
        if (listfavorito.size() == 0)
        {
            txterro.setText("Ops!Parece que voce não possui produtos nos seus favoritos ");
            txterro.setVisibility(View.VISIBLE);
        }
        return view;
    }
    @Override
    public void onStart()
    {
        super.onStart();
            key.clear();
            listfavorito.clear();
            exibirMsgLoad("NAO");
            recperarids(ConfiguracaoFirebase.getUID());

    }
    public void atualizar(String id, final String caminho)
    {
        listfavorito.clear();
        int n = 0;
        final String[] separarids = id.split(":");
        mDatabase.child("produtos").child(separarids[2]).child(separarids[1]).child(separarids[0]).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                        Produto produto = dataSnapshot.getValue(Produto.class);
                        Log.i("FAV", String.valueOf(produto));
                        if (produto != null)
                        {
                            try
                            {
                                ValidarData validarData = new ValidarData();
                                String dataatual = validarData.dataAtual();
                                SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
                                Date dataFIN = formato.parse(produto.dataFinal);
                                Date dataATU = formato.parse(dataatual);
                                String diasrest = validarData.diasRestantes(dataatual,produto.dataFinal);
                                mDatabase.child("produtos").child(separarids[2]).child(separarids[1]).child(produto.nome).child("diasRestantes").setValue(diasrest);
                                Log.d("Favoritos","diasrest ="+diasrest);
                                if (diasrest == "0")
                                {
                                    Log.d("Favoritos","dias rest = vence hoje");
                                    mDatabase.child("produtos").child(separarids[2]).child(separarids[1]).child(produto.nome).child("diasRestantes").setValue("vence hoje");
                                }
                                if (dataFIN.before(dataATU))
                                {
                                    Log.d("Favoritos","VENCEU");
                                    mDatabase.child("produtos").child(separarids[2]).child(separarids[1]).child(produto.nome).child("diasRestantes").setValue("vencido");
                                }
                                listfavorito.add(produto);
                            } catch (ParseException e)
                            {
                                e.printStackTrace();
                            }
                        }
                        else
                        {
                            mDatabase.child("usuarios").child(ConfiguracaoFirebase.getUID()).child("favoritos").child(caminho).setValue(null);
                        }
                        if (listfavorito.size() != 0)
                        {
                           exibirMsgLoad("SIM");
                        }
                        else
                        {
                            exibirMsgLoad("VAZIA");
                            if (key.size() != 0)
                            {
                                for (int i = 0; i < key.size(); i++)
                                {
                                    mDatabase.child("usuarios").child(ConfiguracaoFirebase.getUID()).child("favoritos").child(key.get(i)).setValue(null);
                                }
                            }
                        }
//                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
               exibirMsgLoad("CONEXAO");
            }
        });
    }
    private void recperarids(String meuUid)
    {
        key.clear();
        mDatabase.child("usuarios").child(meuUid).child("favoritos").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getValue() != null)
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        key.add(ds.getKey());
                        atualizar(ds.getValue().toString(),ds.getKey());
                        Log.d("KEY", String.valueOf(key));
                        Log.i("IDS",ds.getValue().toString());
                    }
                }
                else
                {
                    exibirMsgLoad("VAZIA");
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                exibirMsgLoad("ERRO");
            }
        });
    }
    public void exibirMsgLoad(String res)
    {
        ConexaoTeste conexaoTeste = new ConexaoTeste();
        if (res == "SIM")
        {
            txterro.setVisibility(View.GONE);
            btnerro.setVisibility(View.GONE);
            prgerro.setActivated(false);
            prgerro.setVisibility(View.GONE);
        }
        else if(res == "NAO")
        {
            String teste = conexaoTeste.checkNetworkType(getContext());
            if (teste == "OK")
            {
                prgerro.setVisibility(View.VISIBLE);
                txterro.setText("Carregando Promoções");
                txterro.setVisibility(View.VISIBLE);
            }
            else
            {
                txterro.setVisibility(View.VISIBLE);
                txterro.setText("Sem conexão co a internet!");
                btnerro.setVisibility(View.VISIBLE);
            }

        }
        else if (res == "CONEXAO")
        {
            txterro.setVisibility(View.VISIBLE);
            btnerro.setVisibility(View.VISIBLE);
        }
        else if(res == "ERRO")
        {
            txterro.setVisibility(View.VISIBLE);
            txterro.setText("Ops,parece que algo de errado aconteceu... Carregar Novamente?");
            btnerro.setVisibility(View.VISIBLE);
        }
        else if (res == "VAZIA")
        {
            prgerro.setVisibility(View.GONE);
            txterro.setVisibility(View.VISIBLE);
            txterro.setText("Voce não possui produtos adicionados nos favoritos! ");
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        ProdutoAdapter rvAdapter = new ProdutoAdapter(getContext(), listfavorito, new CustomItemClickListener()
        {
            @Override
            public void onItemClick(View v, int position)
            {
                Intent intent = new Intent(getContext(),Anuncio_Notificacao.class);
                Bundle dados = new Bundle();
                dados.putString("nome",listfavorito.get(position).nome);
                dados.putString("cat",listfavorito.get(position).subcategoria);
                dados.putString("key",key.get(position));
                dados.putString("activity","favoritos");
                intent.putExtras(dados);
                startActivity(intent);
                key.clear();
                listfavorito.clear();
                //adapter.notifyDataSetChanged();
            }
        });
        recyclerView.setAdapter(rvAdapter);

        setSwipeForRecyclerView();
    }


    private void setSwipeForRecyclerView()
    {
        SwipeUtil swipeHelper = new SwipeUtil(0, ItemTouchHelper.LEFT, getActivity())
        {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction)
            {
                int swipedPosition = viewHolder.getAdapterPosition();
                ProdutoAdapter adapter = (ProdutoAdapter) recyclerView.getAdapter();
                adapter.pendingRemoval(swipedPosition);
                Log.d("ONSWIPPED", String.valueOf(swipedPosition));
                DatabaseReference ref = ConfiguracaoFirebase.getFirebase();

                ref.child("usuarios").child(ConfiguracaoFirebase.getUID()).child("favoritos").child(key.get(swipedPosition)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getContext(),"Anuncio removido com sucesso da sua lista",Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(getContext(),"Erro na conexão!", Toast.LENGTH_LONG).show();
                    }
                });
            }

            @Override
            public int getSwipeDirs(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
            {
                int position = viewHolder.getAdapterPosition();
                ProdutoAdapter adapter = (ProdutoAdapter) recyclerView.getAdapter();
                if (adapter.isPendingRemoval(position))
                {
                    return 0;
                }
                return super.getSwipeDirs(recyclerView, viewHolder);
            }
        };

        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(swipeHelper);
        mItemTouchHelper.attachToRecyclerView(recyclerView);

        swipeHelper.setLeftSwipeLable("Desfazer");
        swipeHelper.setLeftcolorCode(ContextCompat.getColor(getActivity(), R.color.swipebg));
    }

}
