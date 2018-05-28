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
import com.example.fernandosilveira.promotions.Validacao.ValidarData;
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
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Historico extends Fragment
{
    private DatabaseReference mDatabase = ConfiguracaoFirebase.getFirebase();
    private ArrayList<Produto> listhistorico = new ArrayList<>();
    private ArrayList<String> key = new ArrayList<>();
    private TextView txterro;
    private Button btnerro;
    private ProgressBar prgerro;

    private RecyclerView recyclerView;

    public Historico()
    {

    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_historico, container, false);

        txterro =(TextView)view.findViewById(R.id.txtajudaHist);
        btnerro =(Button) view.findViewById(R.id.btnCarregarHist);
        prgerro =(ProgressBar) view.findViewById(R.id.prgCarregarHist);
        listhistorico = new ArrayList<>();

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

       /*
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(),Anuncio_Notificacao.class);
                Bundle dados = new Bundle();
                dados.putString("nome",listhistorico.get(position).nome);
                dados.putString("cat",listhistorico.get(position).subcategoria);
                dados.putString("key",key.get(position));
                dados.putString("activity","historico");
                intent.putExtras(dados);
                startActivity(intent);
            }
        });

        */
        btnerro.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                key.clear();
                listhistorico.clear();
                exibirMsgLoad("NAO");
                recperarids(ConfiguracaoFirebase.getUID());
            }
        });
        if (listhistorico.size() == 0)
        {
                txterro.setText("Ops!Parece que voce não recebeu nenhum anuncio..."+"\n Selecione Categorias na aba Categorias e ative seu GPS!");
                txterro.setVisibility(View.VISIBLE);
        }
        return view;
    }
    @Override
    public void onStart()
    {
        super.onStart();
            listhistorico.clear();
            key.clear();
            exibirMsgLoad("NAO");
            recperarids(ConfiguracaoFirebase.getUID());

    }
    public void atualizar(final String id,final String caminho,final String meuuid)
    {
        listhistorico.clear();
            final String[] separarids = id.split(":");
            mDatabase.child("produtos").child(separarids[2]).addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        Produto produto = ds.child(separarids[1]).getValue(Produto.class);
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
                                mDatabase.child("produtos").child(separarids[2]).child(produto.meuUid).child(produto.nome).child("diasRestantes").setValue(diasrest);
                                Log.d("HISTORICO","diasrest ="+diasrest);
                                if (diasrest == "0")
                                {
                                    Log.d("HISTORICO","dias rest = vence hoje");
                                    mDatabase.child("produtos").child(separarids[2]).child(produto.meuUid).child(produto.nome).child("diasRestantes").setValue("vence hoje");
                                }
                                if (dataFIN.before(dataATU))
                                {
                                    Log.d("HISTORICO","VENCEU");
                                    mDatabase.child("produtos").child(separarids[2]).child(produto.meuUid).child(produto.nome).child("diasRestantes").setValue("vencido");
                                }
                                listhistorico.add(produto);
                            } catch (ParseException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                    if (listhistorico.size() != 0)
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
                                mDatabase.child("usuarios").child(meuuid).child("historico").child(key.get(i)).setValue(null);
                            }
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    exibirMsgLoad("CONEXAO");
                }
            });
    }
    private void recperarids(final String meuuid)
    {
        mDatabase.child("usuarios").child(meuuid).child("historico").addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getValue() != null)
                {
                    for (DataSnapshot ds : dataSnapshot.getChildren())
                    {
                        String chave = ds.getValue().toString();
                        atualizar(chave, ds.getKey(), meuuid);
                        key.add(ds.getKey());
                        Log.d("KEY_HIST", String.valueOf(key));
                        Log.i("IDS_HIST", ds.getValue().toString());
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
                txterro.setText("Sem conexão com a internet!");
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
            txterro.setText("Ops!Seu historico está vazio!Adicione categorias para receber promoções");
        }
    }
    @Override
    public void onResume()
    {
        super.onResume();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        ProdutoAdapter rvAdapter = new ProdutoAdapter(getContext(), listhistorico, new CustomItemClickListener()
        {
            @Override
            public void onItemClick(View v, int position)
            {
                Intent intent = new Intent(getContext(),Anuncio_Notificacao.class);
                Bundle dados = new Bundle();
                dados.putString("nome",listhistorico.get(position).nome);
                dados.putString("cat",listhistorico.get(position).subcategoria);
                dados.putString("key",key.get(position));
                dados.putString("activity","historico");
                intent.putExtras(dados);
                startActivity(intent);
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

                    ref.child("usuarios").child(ConfiguracaoFirebase.getUID()).child("historico").child(key.get(swipedPosition)).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getContext(),"Anuncio removido com sucesso do seu historico",Toast.LENGTH_LONG).show();
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
                    Log.d("GETSWIPE", String.valueOf(position));
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