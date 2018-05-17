package com.example.fernandosilveira.promotions.Activity.Consumidor;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fernandosilveira.promotions.Adapter.Consumidor.TabAdapterConsumidor;
import com.example.fernandosilveira.promotions.Config.ConexaoTeste;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Model.Produto;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sackcentury.shinebuttonlib.ShineButton;

import me.zhanghai.android.materialratingbar.MaterialRatingBar;

public class Anuncio_Notificacao extends AppCompatActivity
{
    private AlertDialog alerta;
    private ImageView imgprod;
    private TextView txtempres,txtdescr,txtvalidade,txtpreco,txtproduto,txtajuda;
    private Button btnrecarregaranunc;
    private ProgressBar prcarregandoanunc;
    private ShineButton btnfavoritos;
    private MaterialRatingBar rtbclassificar;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference ref = database.getReference();
    private Produto produto;
    private float classif = 0;
    private float somaclas = 0;
    private float numclass = 0;
    private float classificacao = 0;
    private float qtduserclass = 0;
    private String empres,cat,prod;
    private String caminho;
    private String key;
    private String activity;
    private String uidempres= "";
    private String classificado = "";
    private TextView txtclass;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncio__notificacao);
        imgprod = (ImageView) findViewById(R.id.imgProd_Anuncio);
        txtempres =(TextView)findViewById(R.id.txtEmpresa_Anuncio);
        txtproduto =(TextView)findViewById(R.id.txtProduto_Anuncio);
        txtdescr =(TextView)findViewById(R.id.txtDescricao_Anuncio);
        btnrecarregaranunc =(Button) findViewById(R.id.btnCarregarAnunc_not);
        prcarregandoanunc =(ProgressBar) findViewById(R.id.prgCarregarAnunc_notf);
        txtajuda =(TextView)findViewById(R.id.txtajudaAnunc_not);
        txtvalidade=(TextView)findViewById(R.id.txtValidade_Anuncio);
        txtpreco =(TextView)findViewById(R.id.txtPreco_Anuncio);
        txtclass =(TextView)findViewById(R.id.txtclassificarprod);
        rtbclassificar = (MaterialRatingBar) findViewById(R.id.rtbAnuncio);
        btnfavoritos = (ShineButton) findViewById(R.id.shbtnfavorito);

        Intent intent = getIntent();
        final Bundle dados = intent.getExtras();
        if (dados != null)
        {
           empres = dados.getString("empres");
           cat = dados.getString("cat");
           prod = dados.getString("nome");
           key = dados.getString("key");
           activity= dados.getString("activity");
        }
        else
        {
            exibirMsgLoad("ERRO","");
        }
        btnrecarregaranunc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
                {
                    exibirMsgLoad("NAO",activity);
            }
        });
        rtbclassificar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                classificacao = rating;
                Log.i("OI", String.valueOf(classificacao));
            }
        });
        txtempres.setText(prod);
        ref.child("produtos").child(cat).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                for (DataSnapshot ds : dataSnapshot.getChildren())
                {
                    if (ds.child(prod).getValue() != null)
                    {
                        produto =ds.child(prod).getValue(Produto.class);

                        recfav(produto.nome +":"+produto.meuUid+":"+produto.subcategoria);
                        txtempres.setText(produto.empresa);
                        txtproduto.setText(prod);
                        txtdescr.setText(produto.descricao);
                        txtpreco.setText("Apenas R$"+produto.preco+ " a(o) " + produto.tipo);
                        uidempres = produto.meuUid;
                        // recuperar dados de classificaçao do usuario
                        recuperarClass(ConfiguracaoFirebase.getUID());
                        // recuperar classificação do produto
                        recuperarClassProd();
                        txtvalidade.setText("Oferta valida por "+produto.diasRestantes +" dias"+" ou enquanto durarem os estoques !");
                        caminho = produto.codimg;
                        Glide.with(getApplicationContext()).load(produto.codimg).into(imgprod);
                        exibirMsgLoad("SIM",activity);
                        if (produto.diasRestantes.contains("vencido"))
                        {
                            anunciovencido(activity,ConfiguracaoFirebase.getUID());
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
                exibirMsgLoad("ERRO","");
            }
        });
        btnfavoritos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String favorito = produto.nome +":"+produto.meuUid+":"+produto.subcategoria;
                ref.child("usuarios").child(ConfiguracaoFirebase.getUID()).child("favoritos").push().setValue(favorito).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        Toast.makeText(getApplicationContext(),"Adicionado aos Favoritos",Toast.LENGTH_SHORT).show();
                        btnfavoritos.setEnabled(false);
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"Não foi possivel adicionar aos favoritos...Verifique sua conexão e tente novamente",Toast.LENGTH_SHORT).show();
                        btnfavoritos.setBtnFillColor(Color.DKGRAY);
                    }
                });
            }
        });
        imgprod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(getApplicationContext(), Imagem_prod.class);
                Bundle bundle = new Bundle();
                bundle.putString("codimg",caminho);
                intent1.putExtras(bundle);
                startActivity(intent1);
            }
        });
    }
    @Override
    protected void onStart()
    {
        super.onStart();
        Log.d("Anuncio_not","onStart");
            exibirMsgLoad("NAO","");
    }
    @Override
    public void onBackPressed()
    {
        salvarClass(classificado);
        prcarregandoanunc.setVisibility(View.GONE);
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        Log.d("Anuncio_not","Inflando Menu");
        Log.d("Anuncio_not",activity);
        if (!activity.contains("Servico"))
        {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.menu_anuncio, menu);
        }
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuremover_anunc:
                caixadialogo();
                Log.d("Anuncio_not","Caminho == "+caminho);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void caixadialogo()
    {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Anuncio_Notificacao.this);
        builder.setTitle("Excluir Anuncio");
        builder.setMessage("Deseja excluir este anuncio da sua lista?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                if (activity.contains("favoritos"))
                {
                    ref.child("usuarios").child(ConfiguracaoFirebase.getUID()).child("favoritos").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(Anuncio_Notificacao.this,"Anuncio removido com sucesso da sua lista",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Anuncio_Notificacao.this, TabAdapterConsumidor.class));
                    }
                    }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Anuncio_Notificacao.this,"Erro na conexão!",Toast.LENGTH_LONG).show();
                    }
                    });
                }else
                {
                    ref.child("usuarios").child(ConfiguracaoFirebase.getUID()).child("historico").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(Anuncio_Notificacao.this,"Anuncio removido com sucesso do seu historico",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(Anuncio_Notificacao.this, TabAdapterConsumidor.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Anuncio_Notificacao.this,"Erro na conexão!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });
        builder.setNegativeButton("Não", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        alerta = builder.create();
        alerta.show();
    }
    public void recfav(final String fav)
    {
            ref.child("usuarios").child(ConfiguracaoFirebase.getUID()).child("favoritos").addValueEventListener(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    for (DataSnapshot dados: dataSnapshot.getChildren())
                    {
                        if (dados.getValue().toString().equals(fav))
                        {
                            btnfavoritos.setVisibility(View.INVISIBLE);
                        }
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                    throw databaseError.toException();
                }
            });
    }
    public void salvarClass(String tipo)
    {
        Log.d("salvarClass", "Total classificaçãoAntes:"+classif);
        Float soma = (somaclas - numclass)+ classificacao;
        classif = soma/qtduserclass;
        Log.d("salvarClass antes if", "Total classificaçãoDepois:"+classif+ "\n TIPO"+ tipo+"\nSoma das classes:"+soma +"\n Numero de classificações " +qtduserclass+ "\n classific atual"+classificacao);
        if (tipo.equals("Sim"))
        {
            Log.d("salvarClass","tipo.equals(\"Sim\")");
            ref.child("classificacaouser").child(ConfiguracaoFirebase.getUID()).child(cat).child(uidempres).child(prod).child("numclass").setValue(classificacao);
            ref.child("classificacao").child(cat).child(uidempres).child(prod).child("somaclass").setValue(soma);
            ref.child("classificacao").child(cat).child(uidempres).child(prod).child("classificacao").setValue(classif);
            ref.child("produtos").child(cat).child(uidempres).child(prod).child("classificacao").setValue(classif);
        }
        else
        {
            if (somaclas!=0 && qtduserclass!=0)
            {
                qtduserclass = qtduserclass + 1;
                classif = soma/qtduserclass;
                Log.d("salvarClass dentro if","if (somaclas!=0 && qtduserclass!=0) "+"\n qtduser"+qtduserclass);
                ref.child("classificacao").child(cat).child(uidempres).child(prod).child("somaclass").setValue(soma);
                ref.child("classificacao").child(cat).child(uidempres).child(prod).child("classificacao").setValue(classif);
                ref.child("classificacao").child(cat).child(uidempres).child(prod).child("qtduserclass").setValue(qtduserclass);
                ref.child("produtos").child(cat).child(uidempres).child(prod).child("classificacao").setValue(classif);
                ref.child("produtos").child(cat).child(uidempres).child(prod).child("qtduserclass").setValue(qtduserclass);
                ref.child("classificacaouser").child(ConfiguracaoFirebase.getUID()).child(cat).child(uidempres).child(prod).child("classificado").setValue("Sim");
                ref.child("classificacaouser").child(ConfiguracaoFirebase.getUID()).child(cat).child(uidempres).child(prod).child("numclass").setValue(classificacao);
            }
            else
            {
                qtduserclass = qtduserclass + 1;
                classif = soma/qtduserclass;
                Log.d("salvarClass","else");
                ref.child("classificacao").child(cat).child(uidempres).child(prod).child("classificacao").setValue(classificacao);
                ref.child("classificacao").child(cat).child(uidempres).child(prod).child("somaclass").setValue(classificacao);
                ref.child("classificacao").child(cat).child(uidempres).child(prod).child("qtduserclass").setValue(qtduserclass);
                ref.child("produtos").child(cat).child(uidempres).child(prod).child("classificacao").setValue(classificacao);
                ref.child("produtos").child(cat).child(uidempres).child(prod).child("qtduserclass").setValue(qtduserclass);
                ref.child("classificacaouser").child(ConfiguracaoFirebase.getUID()).child(cat).child(uidempres).child(prod).child("classificado").setValue("Sim");
                ref.child("classificacaouser").child(ConfiguracaoFirebase.getUID()).child(cat).child(uidempres).child(prod).child("numclass").setValue(classificacao);
            }
        }
    }
    public void recuperarClassProd()
    {
        ref.child("classificacao").child(cat).child(uidempres).child(prod).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getValue() != null)
                {
                    classif = Float.parseFloat(dataSnapshot.child("classificacao").getValue().toString());
                    qtduserclass = Float.parseFloat(dataSnapshot.child("qtduserclass").getValue().toString());
                    somaclas = Float.parseFloat(dataSnapshot.child("somaclass").getValue().toString());    Log.d("recuperarClassProd","classif"+classif+"\n numuser"+qtduserclass+"\nsomaclass"+somaclas);
                }
            }
                @Override
                public void onCancelled(DatabaseError databaseError)
                {
                }
        });
    }
    public void recuperarClass(String meuUid)
    {
        Log.d("recuperarClass",meuUid+cat+uidempres+prod);
        ref.child("classificacaouser").child(meuUid).child(cat).child(uidempres).child(prod).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.getValue() != null)
                {
                    classificado = dataSnapshot.child("classificado").getValue().toString();
                    numclass = Float.parseFloat(dataSnapshot.child("numclass").getValue().toString());
                    rtbclassificar.setMax(5);
                    rtbclassificar.invalidate();
                    rtbclassificar.setRating(numclass);
                    txtclass.setText("Voce já classificou este produto");
                    Log.d("recuperarClass","num"+numclass+"\n classificado"+classificado);
                }
                else txtclass.setText("Classifique este produto");
            }
            @Override
            public void onCancelled(DatabaseError databaseError)
            {
            }
        });
    }
    public void exibirMsgLoad(String res,String fav)
    {
        ConexaoTeste conexaoTeste = new ConexaoTeste();
        if (res == "SIM")
        {
            if (fav != "favoritos")
            {
                btnfavoritos.setVisibility(View.VISIBLE);
            }
            txtajuda.setVisibility(View.GONE);
            btnrecarregaranunc.setVisibility(View.GONE);
            prcarregandoanunc.setActivated(false);
            prcarregandoanunc.setVisibility(View.GONE);
            txtclass.setVisibility(View.VISIBLE);
            txtempres.setVisibility(View.VISIBLE);
            txtvalidade.setVisibility(View.VISIBLE);
            txtdescr.setVisibility(View.VISIBLE);
            txtproduto.setVisibility(View.VISIBLE);
            txtpreco.setVisibility(View.VISIBLE);
            imgprod.setVisibility(View.VISIBLE);

            rtbclassificar.setVisibility(View.VISIBLE);
        }
        else if(res == "NAO")
        {
            String teste =conexaoTeste.checkNetworkType(this);
            if (teste == "OK")
            {
                txtclass.setVisibility(View.INVISIBLE);
                txtempres.setVisibility(View.INVISIBLE);
                txtvalidade.setVisibility(View.INVISIBLE);
                txtdescr.setVisibility(View.INVISIBLE);
                txtproduto.setVisibility(View.INVISIBLE);
                txtpreco.setVisibility(View.INVISIBLE);
                btnfavoritos.setVisibility(View.INVISIBLE);
                imgprod.setVisibility(View.INVISIBLE);
                rtbclassificar.setVisibility(View.INVISIBLE);

                prcarregandoanunc.setVisibility(View.VISIBLE);
                txtajuda.setText("Carregando Promoção");
                txtajuda.setVisibility(View.VISIBLE);
            }
            else
            {
                txtclass.setVisibility(View.INVISIBLE);
                txtempres.setVisibility(View.INVISIBLE);
                txtvalidade.setVisibility(View.INVISIBLE);
                txtdescr.setVisibility(View.INVISIBLE);
                txtproduto.setVisibility(View.INVISIBLE);
                txtpreco.setVisibility(View.INVISIBLE);
                btnfavoritos.setVisibility(View.INVISIBLE);
                imgprod.setVisibility(View.INVISIBLE);
                rtbclassificar.setVisibility(View.INVISIBLE);

                txtajuda.setVisibility(View.VISIBLE);
                btnrecarregaranunc.setVisibility(View.VISIBLE);
            }
        }
        else if (res == "CONEXAO")
        {
            txtclass.setVisibility(View.INVISIBLE);
            txtempres.setVisibility(View.INVISIBLE);
            txtvalidade.setVisibility(View.INVISIBLE);
            txtdescr.setVisibility(View.INVISIBLE);
            txtproduto.setVisibility(View.INVISIBLE);
            txtpreco.setVisibility(View.INVISIBLE);
            btnfavoritos.setVisibility(View.INVISIBLE);
            imgprod.setVisibility(View.INVISIBLE);
            rtbclassificar.setVisibility(View.INVISIBLE);

            txtajuda.setVisibility(View.VISIBLE);
            btnrecarregaranunc.setText("Fechar Anuncio");
            btnrecarregaranunc.setVisibility(View.VISIBLE);
        }
        else if(res == "ERRO")
        {
            txtclass.setVisibility(View.INVISIBLE);
            txtempres.setVisibility(View.INVISIBLE);
            txtvalidade.setVisibility(View.INVISIBLE);
            txtdescr.setVisibility(View.INVISIBLE);
            txtproduto.setVisibility(View.INVISIBLE);
            txtpreco.setVisibility(View.INVISIBLE);
            btnfavoritos.setVisibility(View.INVISIBLE);
            imgprod.setVisibility(View.INVISIBLE);
            rtbclassificar.setVisibility(View.INVISIBLE);

            txtajuda.setText("Ops Alguma coisa inesperada aconteceu!");
            txtajuda.setVisibility(View.VISIBLE);
            btnrecarregaranunc.setText("Fechar Anuncio");
            btnrecarregaranunc.setVisibility(View.VISIBLE);
        }
    }
    public void anunciovencido(final String tipo, final String meuuid)
    {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Anuncio Vencido!");
        builder.setMessage("Este anuncio se encontra vencido!Deseja exclui-lo?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {
                if (tipo.contains("favoritos"))
                {
                    ref.child("usuarios").child(meuuid).child("favoritos").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Anuncio removido com sucesso da sua lista",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), TabAdapterConsumidor.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Erro na conexão!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                else
                {
                    ref.child("usuarios").child(meuuid).child("historico").child(key).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getApplicationContext(),"Anuncio removido com sucesso do seu historico",Toast.LENGTH_LONG).show();
                            startActivity(new Intent(getApplicationContext(), TabAdapterConsumidor.class));
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(),"Erro na conexão!",Toast.LENGTH_LONG).show();
                        }
                    });
                }
                finish();
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {

            }
        });
        alerta = builder.create();
        alerta.show();
    }

}
