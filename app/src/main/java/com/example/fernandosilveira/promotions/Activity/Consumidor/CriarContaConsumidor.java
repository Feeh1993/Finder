package com.example.fernandosilveira.promotions.Activity.Consumidor;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Activity.Geral.Loading;
import com.example.fernandosilveira.promotions.Activity.Geral.TermosUso;
import com.example.fernandosilveira.promotions.Model.Anunciante_Mod;
import com.example.fernandosilveira.promotions.Validacao.TelefoneValidar;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Model.Consumidor;
import com.example.fernandosilveira.promotions.R;
import com.example.fernandosilveira.promotions.Validacao.ValidarCampos;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.wang.avi.AVLoadingIndicatorView;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class CriarContaConsumidor extends Activity
{
    private EditText edtNomeCriar, edtTelefoneCriar, edtEmailCriar, edtSenhaCriar;
    private TextView semcadastro;
    private Button criarnovaconta;
    private Consumidor consumidor;
    private FirebaseAuth autenticacao;
    private TextView TermosUso;
    private FloatingActionButton fab;
    private CardView cvAdd;
    private AVLoadingIndicatorView progressBarCC;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_criar_conta_consumidor);

        edtNomeCriar = (EditText) findViewById(R.id.edtNomeCC);
        edtSenhaCriar = (EditText) findViewById(R.id.edtSenhaCC);
        edtEmailCriar = (EditText) findViewById(R.id.edtEmailCC);
        edtTelefoneCriar = (EditText) findViewById(R.id.edtTelefoneCC);
        criarnovaconta = (Button) findViewById(R.id.btnCriarContaConsumidor);
        TermosUso = (TextView) findViewById(R.id.txtTermosdeUso);
        semcadastro = (TextView) findViewById(R.id.txtSemCadastro_CC);

        TelefoneValidar validarTelefone = new TelefoneValidar(new WeakReference<EditText>(edtTelefoneCriar));
        edtTelefoneCriar.addTextChangedListener(validarTelefone);

        cvAdd =(CardView) findViewById(R.id.cv_add);
        fab = (FloatingActionButton) findViewById(R.id.fabCC);
        progressBarCC = (AVLoadingIndicatorView) findViewById(R.id.avi);

        fab.setOnClickListener(new View.OnClickListener()
        {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v)
            {
                animateRevealClose();
            }
        });

        TermosUso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CriarContaConsumidor.this, TermosUso.class);
                startActivity( intent );
            }
        });
        criarnovaconta.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final String nome = edtNomeCriar.getText().toString();
                final String email = edtEmailCriar.getText().toString();
                final String senha = edtSenhaCriar.getText().toString();
                final String telefone = edtTelefoneCriar.getText().toString();
                startAnim();
                //valida os campos e cria um array list com os erros para eventuais tratamentos
                ArrayList resultado = validacao(telefone,email,senha);
                for (int i = 0; i < resultado.size(); i++)
                {

                    if (resultado.get(i).equals("Valida Login"))
                    {
                        if (TextUtils.isEmpty(nome))
                        {
                            stopAnim();
                            edtNomeCriar.setError("Digite seu Nome!");
                        }
                        else
                        {
                            consumidor = new Consumidor();
                            consumidor.setNome(edtNomeCriar.getText().toString());
                            consumidor.setEmail(edtEmailCriar.getText().toString());
                            consumidor.setSenha(edtSenhaCriar.getText().toString());
                            consumidor.setTelefone(edtTelefoneCriar.getText().toString());
                            cadastrarUsuario();
                        }
                    }
                    else if (resultado.get(i).equals("Email Erro"))
                    {
                        stopAnim();
                        semcadastro.setText("O email precisa estar no formato user@user.com");
                    }
                    else if (resultado.get(i).equals("Senha Erro"))
                    {
                        stopAnim();
                        semcadastro.setText("Sua senha deve: \n Ter entre 8 e 40 caracteres\n" +
                                "Contenha pelo menos um dígito e um caracter especial de [@ # $%! . ].\n" +
                                "Contenha pelo menos um caractere minúsculo e um caractere maiúsculo.\n");
                    }
                    else if (resultado.get(i).equals("Telefone Erro"))
                    {
                        stopAnim();
                        semcadastro.setText("\n Seu Telefone esta em formato incorreto!");
                    }
                }
                if (TextUtils.isEmpty(email) && TextUtils.isEmpty(telefone) && TextUtils.isEmpty(senha))
                {
                    semcadastro.setText("Preencha todos os campos ante de clicar em Criar Conta!");
                }else if (TextUtils.isEmpty(email))
                {
                    edtEmailCriar.setError("Digite seu email!");
                }
                else if (TextUtils.isEmpty(telefone))
                {
                   edtTelefoneCriar.setError("Digite seu telefone!");
                }
                else if (TextUtils.isEmpty(senha))
                {
                    edtSenhaCriar.setError("Digite sua Senha!");
                }
            }
        });
    }
    private void cadastrarUsuario()
    {
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();
        autenticacao.createUserWithEmailAndPassword(
                consumidor.getEmail(),
                consumidor.getSenha()
        ).addOnCompleteListener(CriarContaConsumidor.this, new OnCompleteListener<AuthResult>()
        {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    consumidor.setIduser(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    consumidor.salvar();
                //    progressBarCC.setVisibility(View.INVISIBLE);
                    stopAnim();
                    Toast.makeText(CriarContaConsumidor.this, "Sucesso ao cadastrar usuário", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(CriarContaConsumidor.this, Loading.class);
                    startActivity( intent );
                    finish();

                } else
                    {
                        String erro = "";
                        try
                        {
                            throw task.getException();
                        } catch (FirebaseAuthWeakPasswordException e)
                        {
                            erro = "Escolha uma senha que contenha, letras e números.";
                        } catch (FirebaseAuthInvalidCredentialsException e)
                        {
                            erro = "Email indicado não é válido.";
                        } catch (FirebaseAuthUserCollisionException e)
                        {
                            erro = "Já existe uma conta com esse e-mail.";
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }
                        Toast.makeText(CriarContaConsumidor.this, "Erro ao cadastrar usuário: " + erro, Toast.LENGTH_LONG).show();
                      //  progressBarCC.setVisibility(View.INVISIBLE);
                        stopAnim();
                    }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                CriarContaConsumidor.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }
    void startAnim()
    {
        progressBarCC.setVisibility(View.VISIBLE);
        progressBarCC.show();
        // or avi.smoothToShow();
    }

    void stopAnim()
    {
        progressBarCC.setVisibility(View.INVISIBLE);
        progressBarCC.hide();
        // or avi.smoothToHide();
    }
    public ArrayList validacao(String telefone,  String email , String senha)
    {
        ArrayList resultado = new ArrayList();
        ValidarCampos validarCampos = new ValidarCampos();
        String resemail = validarCampos.validarEmail(email);
        String ressenha = validarCampos.validarSenha(senha);
        String restelefone = validarCampos.validarTelefone(telefone);
        if (resemail == "OK" && ressenha == "OK" && restelefone == "OK")
        {
            resultado.add("Valida Login");
        }
        else if (resemail == "ERRO")
        {
            resultado.add("Email Erro");
        } else resultado.add("Email OK");

        if (ressenha == "ERRO")
        {
            resultado.add("Senha Erro");
        }else resultado.add("Senha OK");

        if (restelefone == "ERRO")
        {
            resultado.add("Telefone Erro");
        }else resultado.add("Telefone OK");
        return resultado;

    }



}
