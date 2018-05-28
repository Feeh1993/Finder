package com.example.fernandosilveira.promotions.Activity.Anunciante;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.fernandosilveira.promotions.Validacao.ValidarData;
import com.example.fernandosilveira.promotions.Adapter.Anunciante.TabAdapterAnunc;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Model.Produto;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/* classe criada para edição do anuncio ...
   vinculada a lista de anuncios do Anunciante_Mod ...Quando clicado em um item da lista é possivel altera-la através desta classe
*/
public class AnuncioEditar extends AppCompatActivity
{
    private String[] validade = new String[] { "A duração anterior sera somada ao atual!","1 dia", "2 dias", " 5 dias","uma semana", "10 dias ", "15 dias",
            "20 dias", "25 dias", "1 mes" };
    private EditText edtDescrprod;
    private Spinner spinnerduracao;
    private Button btnSalvarProd;
    private TextView categoriaAE,subcategoriaAE,txtpreco,txtNomeprod;
    private ImageButton btnimg;
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private AlertDialog alerta;
    private String codimg = "";
    private String subcategoria = "";
    private String nomeProd = "";
    private String categoria = "";
    private String descricao= "";
    private String preco= "";
    private String caminhoimg  = "";
    private String tipo = "";
    private String empresa = "";
    private int dias = 0;
    private int diasspinner= 0;
    private ProgressBar prgb;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anuncio_editar);
        btnimg = (ImageButton) findViewById(R.id.btnImgProdAE);
        categoriaAE = (TextView) findViewById(R.id.categoriaAE);
        subcategoriaAE = (TextView) findViewById(R.id.subcategoria);
        btnSalvarProd = (Button) findViewById(R.id.btnSalvarProdAE);
        edtDescrprod = (EditText) findViewById(R.id.edtDescricaoProdAE);
        txtpreco = (TextView) findViewById(R.id.txtPrecoProdAE);
        txtNomeprod = (TextView) findViewById(R.id.txtNomeProdAE);
        spinnerduracao = (Spinner) findViewById(R.id.spinnerDuracaoAE);
        prgb =(ProgressBar) findViewById(R.id.prgAE);
        ArrayAdapter adaptadorvalidade = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, validade);
        adaptadorvalidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerduracao.setAdapter(adaptadorvalidade);
        //recebendo dados do fragmento meus anuncios
        Intent intent = getIntent();
        final Bundle dados = intent.getExtras();
        if (dados != null)
        {
            nomeProd = dados.getString("nome");
            categoria = dados.getString("cat");
            descricao = dados.getString("descricao");
            subcategoria = dados.getString("sub");
            preco  = dados.getString("preco");
            codimg = dados.getString("img");
            if (dados.getString("dias").contains("vencido"))
            {
                dias = 0;
            }
            else dias = Integer.parseInt(dados.getString("dias"));
            caminhoimg  = dados.getString("caminho");
            tipo = dados.getString("tipo");
            empresa = dados.getString("empres");
        }
        txtNomeprod.setText(nomeProd);
        txtpreco.setText(preco);
        edtDescrprod.setText(descricao);
        categoriaAE.setText(categoria);
        subcategoriaAE.setText(subcategoria);

        //download imagem com GLIDE
        Glide.with(getApplicationContext()).load(codimg).into(btnimg);

        txtNomeprod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),tratarCliques(0),Toast.LENGTH_SHORT).show();
            }
        });
        txtpreco.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(),tratarCliques(1),Toast.LENGTH_SHORT).show();
        }
        });
        categoriaAE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),tratarCliques(3),Toast.LENGTH_SHORT).show();
            }
        });
        subcategoriaAE.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),tratarCliques(4),Toast.LENGTH_SHORT).show();
            }
        });
        btnimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),tratarCliques(5),Toast.LENGTH_SHORT).show();
            }
        });
        spinnerduracao.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(getApplicationContext(),"Você precisa selecionar a data de validade!",Toast.LENGTH_LONG).show();
            }
            public void onItemSelected(AdapterView parent, View v, int posicao, long id)
            {
                diasspinner = verificarDias(posicao);
                Log.e("LOG","posicao "+posicao);
            }
        });
        btnSalvarProd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    ValidarData validarData = new ValidarData();
                    String atual = validarData.dataAtual();
                    Log.e("LOG","atual "+atual);
                    String cal = calData(atual,diasspinner);
                    Log.e("LOG","cal "+cal);
                    String rest = validarData.diasRestantes(atual,cal);
                    Log.e("LOG","rest "+rest);
                    salvaralteracoes(atual,cal,rest,ConfiguracaoFirebase.getUID());
                } catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
        });
    }
// salvar alterações mapeando os produtos
    private void salvaralteracoes(String dataIni, String dataFinal, String diasrestantes, final String meuUid)
    {
        prgb.setVisibility(View.VISIBLE);
        btnSalvarProd.setText("Alterando anuncio...");
        Produto produto = new Produto(edtDescrprod.getText().toString(),nomeProd,preco,categoria,subcategoria,codimg,tipo,dataIni,dataFinal,diasrestantes,empresa,meuUid,caminhoimg);
        Map<String, Object> postprod= produto.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/produtos/" + subcategoria+"/"+meuUid+"/"+nomeProd, postprod);
        ref.updateChildren(childUpdates).addOnCompleteListener(new OnCompleteListener<Void>()
        {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ref.child("minhascategorias").child(meuUid).child(subcategoria).setValue("true");
                prgb.setVisibility(View.INVISIBLE);
                btnSalvarProd.setText("Anuncio alterado com sucesso!");
                Toast.makeText(AnuncioEditar.this,"Anuncio alterado com sucesso!",Toast.LENGTH_LONG).show();
                startActivity(new Intent(AnuncioEditar.this, TabAdapterAnunc.class));
                finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_anuncio, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.menuremover_anunc:
                caixadialogo();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    //exibe um alert dialog para exclusão de anuncio
    public void caixadialogo()
    {
        final android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(AnuncioEditar.this);
        builder.setTitle("Excluir Anuncio");
        builder.setMessage("Deseja excluir este anuncio da sua lista?");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener()
        {
            public void onClick(DialogInterface arg0, int arg1)
            {

                prgb.setVisibility(View.VISIBLE);
                btnSalvarProd.setText("Excluindo anuncio...");
                StorageReference desertRef = ConfiguracaoFirebase.getStorageRef().child(ConfiguracaoFirebase.getUID()).child(caminhoimg);
                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid)
                    {
                        ref.child("produtos").child(subcategoria).child(ConfiguracaoFirebase.getUID()).child(nomeProd).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(AnuncioEditar.this,"Anuncio removido com sucesso",Toast.LENGTH_LONG).show();
                                prgb.setVisibility(View.INVISIBLE);
                                btnSalvarProd.setText("Excluido com sucesso!");
                                ref.child("minhascategorias").child(ConfiguracaoFirebase.getUID()).child(subcategoria).removeValue();
                                ref.child("classificacao").child(categoria).child(ConfiguracaoFirebase.getUID()).child(nomeProd).removeValue();
                                startActivity(new Intent(AnuncioEditar.this, TabAdapterAnunc.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(AnuncioEditar.this,"Erro na conexão!",Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener()
                {
                    @Override
                    public void onFailure(@NonNull Exception exception)
                    {
                            Toast.makeText(AnuncioEditar.this,"Imagem já foi excluida!",Toast.LENGTH_LONG).show();
                            prgb.setVisibility(View.INVISIBLE);
                            btnSalvarProd.setText("Salvar Alterações!");
                    }
                });

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
    // tratamento de cliques nos campos que não podem ser editados
    public String tratarCliques(int tipo)
    {
        String resp = "";
        switch (tipo)
        {
            case 0:
                resp = "Voce não pode editar o nome do produto!"+"\n"+"Caso algo esteja incorreto apague este anuncio e crie novamente!";
                break;
            case 1:
                resp = "Voce não pode editar o preço do produto!"+"\n"+"Caso algo esteja incorreto apague este anuncio e crie novamente!";
                break;
            case 2:
                resp = "Voce não pode editar o tipo do produto!"+"\n"+"Caso algo esteja incorreto apague este anuncio e crie novamente!";
                break;
            case 3:
                resp = "Voce não pode editar o categoria do produto!"+"\n"+"Caso algo esteja incorreto apague este anuncio e crie novamente!";
                break;
            case 4:
                resp = "Voce não pode editar o subcategoria do produto!"+"\n"+"Caso algo esteja incorreto apague este anuncio e crie novamente!";
                break;
            case 5:
                resp = "Voce não pode alterar a imagem!"+"\n"+"Caso algo esteja incorreto apague este anuncio e crie novamente!";
                break;
        }
        return resp;
    }
    //verifica dias para contabilizar vencimento do anuncio
    public int verificarDias(int spinner)
    {
        int dias = 0;
        switch (spinner)
        {
            case 0:
                dias = 0;
                break;
            case 1:
                dias = 1;
                break;
            case 2:
                dias = 2;
                break;
            case 3:
                dias = 5;
                break;
            case 4:
                dias = 7;
                break;
            case 5:
                dias = 10;
                break;
            case 6:
                dias = 15;
                break;
            case 7:
                dias = 20;
                break;
            case 8:
                dias = 25;
                break;
            case 9:
                dias = 30;
                break;
        }
        return dias;
    }
    // calcular data final
    public String calData(String dt,int diassom) throws ParseException
    {
        int diasomado = dias+diassom;
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        c.setTime(sdf1.parse(dt));
        c.add(Calendar.DATE, diasomado);
        String futuro = sdf1.format(c.getTime());
        return futuro;
    }
}
