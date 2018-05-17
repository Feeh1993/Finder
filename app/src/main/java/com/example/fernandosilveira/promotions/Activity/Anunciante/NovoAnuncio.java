package com.example.fernandosilveira.promotions.Activity.Anunciante;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Activity.Validacao.ValidarPreco;
import com.example.fernandosilveira.promotions.Adapter.Anunciante.TabAdapterAnunc;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Helper.Base64Custom;
import com.example.fernandosilveira.promotions.Model.CategoriaProd;
import com.example.fernandosilveira.promotions.Model.LocalProd;
import com.example.fernandosilveira.promotions.Model.Produto;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.vansuita.pickimage.bean.PickResult;
import com.vansuita.pickimage.bundle.PickSetup;
import com.vansuita.pickimage.dialog.PickImageDialog;
import com.vansuita.pickimage.listeners.IPickResult;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class NovoAnuncio extends AppCompatActivity implements IPickResult
{
    private ImageButton btnimageprod;
    private EditText edtpreco,edtNomeprod,edtDescrprod;
    private Spinner spinner;
    private Button btnSalvarProd;
    private Double lat;
    private Double Lng;
    private String textoCat = "";
    private String textovalidade  = "";
    private String textoSubCat= "";
    private String textotpprec = "";
    private int dias = 0;
    private Uri imagemProd;
    private String[] validade = new String[] { "Selecione a Validade","1 dia", "2 dias", " 5 dias","uma semana", "10 dias ", "15 dias","20 dias", "25 dias", "1 mes" };
    public static final String[] tipopreco = new String[] {"Escolha o tipo","KG","UN"};
    private CategoriaProd categoriaProd;
    private DatabaseReference ref = ConfiguracaoFirebase.getFirebase();
    private String empres;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_anuncio);
        btnimageprod =(ImageButton)findViewById(R.id.btnImgProd);
        btnSalvarProd=(Button) findViewById(R.id.btnSalvarProd);
        edtDescrprod = (EditText) findViewById(R.id.edtDescricaoProd);
        edtNomeprod = (EditText) findViewById(R.id.edtNomeProd);
        edtpreco = (EditText) findViewById(R.id.edtPrecoProd);
        edtpreco.addTextChangedListener(new ValidarPreco(edtpreco));
        final PickSetup setup = new PickSetup()
                .setTitle("Selecione ou tire uma Foto")
                .setCancelText("Cancelar")
                .setCameraButtonText("Camera")
                .setGalleryButtonText("Galeria");
        final Spinner spinnervalidade = (Spinner) findViewById(R.id.spinnerDuracao);
        final Spinner spinnertipo = (Spinner) findViewById(R.id.spinnerTipo);
        ArrayAdapter adaptadorvalidade = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, validade);
        adaptadorvalidade.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnervalidade.setAdapter(adaptadorvalidade);
        ArrayAdapter adaptadortipo = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, tipopreco);
        adaptadortipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnertipo.setAdapter(adaptadortipo);
        spinnervalidade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(getApplicationContext(),"Você precisa selecionar a data de validade!",Toast.LENGTH_LONG).show();
            }
            public void onItemSelected(AdapterView parent, View v, int posicao, long id)
            {
                textovalidade = parent.getItemAtPosition(posicao).toString();
                dias = verificarDias(posicao);
                Log.e("LOG","posicao "+posicao);
            }
        });
        spinnertipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(getApplicationContext(),"Você precisa selecionar o tipo!",Toast.LENGTH_LONG).show();
            }
            public void onItemSelected(AdapterView parent, View v, int posicao, long id)
            {
                textotpprec = parent.getItemAtPosition(posicao).toString();
            }
        });
        final Spinner spinnercat = (Spinner) findViewById(R.id.spinnercat);
        ArrayAdapter adaptadorcat = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, categoriaProd.categoria);
        adaptadorcat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnercat.setAdapter(adaptadorcat);
        spinnercat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                textoCat = parent.getItemAtPosition(position).toString();
                if (textoCat == "Escolha a categoria")
                {
                    spinner =AdicionarSpinner(categoriaProd.nenhumacat);
                }
                else if (textoCat =="arte,entretenimento e lazer")
                {
                    spinner =AdicionarSpinner(categoriaProd.subcatArteEntreLaz);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            textoSubCat = parent.getItemAtPosition(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {

                        }
                    });
                }else if(textoCat =="casa,eletrodomésticos e ferramentas")
                {
                    spinner = AdicionarSpinner(categoriaProd.subcatCasEletrFerr);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            textoSubCat = parent.getItemAtPosition(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                }else if(textoCat =="livros,filmes e músicas")
                {
                    spinner = AdicionarSpinner(categoriaProd.subcatLivrFilmMus);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                            textoSubCat = parent.getItemAtPosition(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                }else if(textoCat == "saúde e beleza")
                {
                    spinner = AdicionarSpinner(categoriaProd.subcatSaudBelez);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                            textoSubCat = parent.getItemAtPosition(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                }else if(textoCat == "automobilismo,indústria e varejo")
                {
                    spinner = AdicionarSpinner(categoriaProd.subcatAutIndVarej);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                            textoSubCat = parent.getItemAtPosition(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                }else if(textoCat == "tecnologia e comunicação")
                {
                    spinner = AdicionarSpinner(categoriaProd.subcatTecCom);
                    spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
                    {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
                        {
                            textoSubCat = parent.getItemAtPosition(position).toString();
                        }
                        @Override
                        public void onNothingSelected(AdapterView<?> parent)
                        {
                        }
                    });
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent)
            {
                Toast.makeText(getApplicationContext(),"Você precisa selecionar o ramo da sua empresa!",Toast.LENGTH_LONG).show();
            }
        });
        btnimageprod.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                PickImageDialog.build(setup).show(getSupportFragmentManager());

            }
        });
        btnSalvarProd.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                    if (imagemProd != null)
                    {
                        if (!TextUtils.isEmpty(edtDescrprod.getText().toString()) && !TextUtils.isEmpty(edtNomeprod.getText().toString())
                            && !TextUtils.isEmpty(edtpreco.getText().toString()))
                        {
                            Base64Custom base64Custom = new Base64Custom();
                            final String codImag = base64Custom.codificarBase64(imagemProd.getLastPathSegment());
                            StorageReference refprod = ConfiguracaoFirebase.getStorageRef().child(ConfiguracaoFirebase.getUID()).child(codImag);
                            btnSalvarProd.setClickable(false);
                            btnSalvarProd.setText("Salvando Produto ...");
                            refprod.putFile(imagemProd).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                            {
                                try
                                {
                                    Uri downloadUri = taskSnapshot.getMetadata().getDownloadUrl();
                                    Log.e("LOG","Validade "+textovalidade);
                                    String atual = dataAtual();
                                    Log.e("LOG","atual "+atual);
                                    String cal = calData(atual,dias);
                                    Log.e("LOG","cal "+cal);
                                    String rest = diasRestantes(atual,cal);
                                    Log.e("LOG","rest "+rest);
                                    gravarProduto(lat,Lng,empres,edtNomeprod.getText().toString(),edtDescrprod.getText().toString(),edtpreco.getText().toString(),
                                        textoCat,downloadUri.toString(),textoSubCat,ConfiguracaoFirebase.getUID(),textotpprec,atual,cal,rest,codImag);
                                    Toast.makeText(getApplicationContext(), "Imagem Salva com Sucesso!", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(NovoAnuncio.this, NovoAnuncio.class);
                                    startActivity(intent);
                                    finish();
                                } catch (ParseException e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener()
                            {
                                @Override
                                public void onFailure(@NonNull Exception e)
                                {
                                    Toast.makeText(getApplicationContext(),"Erro no upload da Imagem",Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else Toast.makeText(getApplicationContext(),"Todos os campos precisam estar preenchidos!",Toast.LENGTH_SHORT).show();
                    }
                    else Toast.makeText(getApplicationContext(),"Voce precisa selecionar uma imagem para continuar!",Toast.LENGTH_SHORT).show();
            }

        });

    }
    private Spinner AdicionarSpinner(String[] strings)
    {
        final Spinner spinnersubcat = (Spinner) findViewById(R.id.spinnersubcat);
        ArrayAdapter adaptadorcat = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item,strings );
        adaptadorcat.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnersubcat.setAdapter(adaptadorcat);
        spinnersubcat.setVisibility(View.VISIBLE);
        return spinnersubcat;
    }
    public void onStart()
    {
        super.onStart();
            ref.child("usuarios").child(ConfiguracaoFirebase.getUID()).addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    empres = dataSnapshot.child("nome").getValue().toString();
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
            ref.child("localizacao").child(ConfiguracaoFirebase.getUID()).addListenerForSingleValueEvent(new ValueEventListener()
            {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot)
                {
                    lat = Double.parseDouble(dataSnapshot.child("latitude").getValue().toString());
                    Lng = Double.parseDouble(dataSnapshot.child("longitude").getValue().toString());
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            });
    }

    private void gravarProduto(Double lat , Double lng ,String empres,String nome, String descricao, String preco,
                              String categoria,String codimag, String subcat,String uid,String tipo,String dataIni,String dataFinal,String diasrestantes,String caminhoimg)
    {
        //String key = ref.child("produto").child(subcat).child(uid).child(nome).getKey();
        String push = ref.child("locais").push().getKey();
        Produto produto = new Produto(descricao,nome,preco,categoria,subcat,codimag,tipo,dataIni,dataFinal,diasrestantes,empres,uid,caminhoimg);
        LocalProd locprod = new LocalProd(lat,lng,nome,preco,diasrestantes,categoria,uid,empres,tipo);
        Map<String, Object> posLoc = locprod.toMap();
        Map<String, Object> postprod= produto.toMap();
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/produtos/" + subcat+"/"+uid+"/"+nome, postprod);
        childUpdates.put("/locais/" + subcat+"/"+push, posLoc);
        ref.updateChildren(childUpdates);
        ref.child("minhascategorias").child(uid).child(textoSubCat).setValue("true");
    }
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
    public String calData(String dt,int dias) throws ParseException
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy");
        c.setTime(sdf1.parse(dt));
        c.add(Calendar.DATE, dias);
        String futuro = sdf1.format(c.getTime());
        return futuro;
    }
    public String dataAtual()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        String hoje = sdf2.format(c.getTime());
        return hoje;
    }
    public String diasRestantes(String dataIni,String dataFin) throws ParseException
    {
        SimpleDateFormat datas = new SimpleDateFormat("dd/MM/yyyy");
        Date dateI;
        Date dateF;
        dateI = datas.parse(dataIni);
        dateF = datas.parse(dataFin);
        long diferenca = Math.abs(dateI.getTime() - dateF.getTime());
        long diferencaDatas = diferenca / (24 * 60 * 60 * 1000);
        String diferencadias = Long.toString(diferencaDatas);
        return diferencadias;
    }
    @Override
    public void onBackPressed()
    {
        startActivity(new Intent(this, TabAdapterAnunc.class));
        finish();
    }
    @Override
    public void onPickResult(PickResult pickResult)
    {
        if (pickResult.getError() == null)
        {
            btnimageprod.setImageBitmap(pickResult.getBitmap());
            imagemProd = pickResult.getUri();
        } else
            {
                Toast.makeText(this, pickResult.getError().getMessage(), Toast.LENGTH_LONG).show();
            }
    }
}
