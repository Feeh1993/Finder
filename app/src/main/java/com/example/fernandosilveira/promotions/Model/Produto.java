package com.example.fernandosilveira.promotions.Model;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Fernando Silveira on 30/06/2017.
 */

public class Produto
{
    public String descricao ;
    public String nome;
    public String preco;
    public String categoria;
    public String subcategoria;
    public String tipo;
    public String codimg;
    public String caminhoimg;
    public String dataFinal;
    public String dataInicial;
    public String diasRestantes;
    public String empresa;
    public String meuUid;
    public float classificacao = 0;
    public float qtduserclass = 0;

    public Map<String, Boolean> produt = new HashMap<>();
    public Produto(String descricao, String nome, String preco,
                   String categoria, String subcategoria, String codimg,
                   String tipo,String dataInicial,String dataFinal,String diasRestantes,String empres,String meuUid,String caminhoimg)
    {
        this.descricao = descricao;
        this.nome = nome;
        this.preco = preco;
        this.categoria = categoria;
        this.subcategoria = subcategoria;
        this.codimg = codimg;
        this.caminhoimg = caminhoimg;
        this.dataInicial = dataInicial;
        this.dataFinal = dataFinal;
        this.diasRestantes = diasRestantes;
        this.tipo = tipo;
        this.empresa = empres;
        this.meuUid = meuUid;
    }
    public Produto(){}
    @Exclude
    public Map<String, Object> toMap()
    {
        HashMap<String, Object> result = new HashMap<>();
        result.put("nome", nome);
        result.put("descricao", descricao);
        result.put("preco", preco);
        result.put("categoria", categoria);
        result.put("subcategoria", subcategoria);
        result.put("codimg", codimg);
        result.put("dataInicial", dataInicial);
        result.put("dataFinal", dataFinal);
        result.put("diasRestantes", diasRestantes);
        result.put("tipo",tipo);
        result.put("empresa",empresa);
        result.put("meuUid",meuUid);
        result.put("caminhoimg",caminhoimg);
        result.put("classificacao",classificacao);
        result.put("qtduserclass",qtduserclass);
        return result;
    }
}