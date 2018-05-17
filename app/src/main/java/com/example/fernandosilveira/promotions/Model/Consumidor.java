package com.example.fernandosilveira.promotions.Model;

/**
 * Created by Fernando Silveira on 08/03/2017.
 */


import com.example.fernandosilveira.promotions.Config.ConfiguracaoFirebase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;


public class Consumidor
{

    private String iduser ;
    private String nome;
    private String email;
    private String senhauser;
    private String telefone;
    private String tipo = "usuario" ;


    public Consumidor()
    {

    }

    public void salvar()
    {
        DatabaseReference referenciaFirebase = ConfiguracaoFirebase.getFirebase();
        referenciaFirebase.child("usuarios").child( getIduser() ).setValue( this );
    }



    @Exclude
    public String getIduser() {
        return iduser;
    }

    public void setIduser(String id) {
        this.iduser = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    @Exclude
    public String getSenha() {
        return senhauser;
    }

    public void setSenha(String senha) {
        this.senhauser = senha;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
