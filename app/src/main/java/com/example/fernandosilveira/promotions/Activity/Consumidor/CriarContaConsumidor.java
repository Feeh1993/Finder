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
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.fernandosilveira.promotions.Activity.Anunciante.CriarContaAnunciante;
import com.example.fernandosilveira.promotions.Activity.Geral.Loading;
import com.example.fernandosilveira.promotions.Activity.Geral.TermosUso;
import com.example.fernandosilveira.promotions.Activity.Validacao.TelefoneValidar;
import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.example.fernandosilveira.promotions.Model.Consumidor;
import com.example.fernandosilveira.promotions.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.wang.avi.AVLoadingIndicatorView;

import java.lang.ref.WeakReference;

public class CriarContaConsumidor extends Activity
{
    private EditText edtNomeCriar, edtTelefoneCriar, edtEmailCriar, edtSenhaCriar;
    private Button criarnovaconta;
    private Consumidor consumidor;
   // private ProgressBar progressBarCC;
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

       // progressBarCC = (ProgressBar) findViewById(R.id.progressBarCC);
        edtNomeCriar = (EditText) findViewById(R.id.edtNomeCC);
        edtSenhaCriar = (EditText) findViewById(R.id.edtSenhaCC);
        edtEmailCriar = (EditText) findViewById(R.id.edtEmailCC);
        edtTelefoneCriar = (EditText) findViewById(R.id.edtTelefoneCC);
        criarnovaconta = (Button) findViewById(R.id.btnCriarContaConsumidor);
        TermosUso = (TextView) findViewById(R.id.txtTermosdeUso);

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
                String email = edtEmailCriar.getText().toString();
                final String senha = edtSenhaCriar.getText().toString();
                String telefone = edtTelefoneCriar.getText().toString();
                if (TextUtils.isEmpty(email))
                {
                    Toast.makeText(getApplicationContext(), "Digite seu Email!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(telefone))
                {
                    Toast.makeText(getApplicationContext(), "Digite seu Telefone!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(senha))
                {
                    Toast.makeText(getApplicationContext(), "Digite sua senha!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else if (TextUtils.isEmpty(nome))
                {
                    Toast.makeText(getApplicationContext(), "Digite seu Nome!", Toast.LENGTH_SHORT).show();
                    return;
                }
                else
                {
                 //   progressBarCC.setVisibility(View.VISIBLE);
                    startAnim();
                    consumidor = new Consumidor();
                    consumidor.setNome(edtNomeCriar.getText().toString());
                    consumidor.setEmail(edtEmailCriar.getText().toString());
                    consumidor.setSenha(edtSenhaCriar.getText().toString());
                    consumidor.setTelefone(edtTelefoneCriar.getText().toString());
                    cadastrarUsuario();
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
    } @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
private void ShowEnterAnimation() {
    Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
    getWindow().setSharedElementEnterTransition(transition);

    transition.addListener(new Transition.TransitionListener() {
        @Override
        public void onTransitionStart(Transition transition) {
            cvAdd.setVisibility(View.GONE);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onTransitionEnd(Transition transition) {
            transition.removeListener(this);
            animateRevealShow();
        }

        @Override
        public void onTransitionCancel(Transition transition) {

        }

        @Override
        public void onTransitionPause(Transition transition) {

        }

        @Override
        public void onTransitionResume(Transition transition) {

        }


    });
}
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
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


}
