package com.example.fernandosilveira.promotions.Validacao;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidarCampos
{
    private static String CNPJ_PATTERN = "([0-9]{2}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[\\/]?[0-9]{4}[-]?[0-9]{2})|([0-9]{3}[\\.]?[0-9]{3}[\\.]?[0-9]{3}[-]?[0-9]{2})";
    private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
    private static String SENHA_PATTERN =  "((?=.*[a-z])(?=.*\\d)(?=.*[A-Z])(?=.*[@#$%!]).{8,40})";
    private static  final String TELEFONE_PATTERN =  "^\\([1-9]{2}\\) (?:[2-8]|9[1-9])[0-9]{3}\\-[0-9]{4}$";

    private static final Pattern pattern_email = Pattern.compile(EMAIL_PATTERN, Pattern.CASE_INSENSITIVE);
    private static final Pattern pattern_telefone = Pattern.compile(TELEFONE_PATTERN, Pattern.CASE_INSENSITIVE);
    private static final Pattern pattern_senha = Pattern.compile(SENHA_PATTERN);
    private static final Pattern pattern_cnpj = Pattern.compile(CNPJ_PATTERN);;


    public static String validarEmail(String email)
    {
        Matcher matcher = pattern_email.matcher(email);
        Boolean res = matcher.matches();
        String resultado = "";
        if (res == true)
        {
            resultado = "OK";
        }
        else resultado = "ERRO";

        return resultado;
    }

    public static String validarTelefone(String telefone)
    {
        Matcher matcher = pattern_telefone.matcher(telefone);
        Boolean res = matcher.matches();
        String resultado = "";
        if (res == true)
        {
            resultado = "OK";
        }
        else resultado = "ERRO";

        return resultado;
    }
    public static String validarCNPJ(String cnpj)
    {
        Matcher matcher = pattern_cnpj.matcher(cnpj);
        Boolean res = matcher.matches();
        String resultado = "";
        if (res == true)
        {
            resultado = "OK";
        }
        else resultado = "ERRO";

        return resultado;
    }
    public static  String  validarSenha(String senha)
    {
        Matcher matcher = pattern_senha.matcher(senha);
        Boolean res = matcher.matches();
        String resultado = "";
        if (res == true)
        {
            resultado = "OK";
        }
        else resultado = "ERRO";

        return resultado;
    }

}
