package com.example.fernandosilveira.promotions.Activity.Validacao;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Fernando Silveira on 08/11/2017.
 */

public class ValidarData
{
    public static String dataAtual()
    {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");
        String hoje = sdf2.format(c.getTime());
        return hoje;
    }
    public static String diasRestantes(String dataIni,String dataFin) throws ParseException
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
}
