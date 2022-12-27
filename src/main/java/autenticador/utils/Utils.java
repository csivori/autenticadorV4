package autenticador.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Utils {
    public static String codificarPwd(String sIn){
        if (sIn == null) {return null;}

        String sOut = "";
        for (int i=0; i<sIn.length(); i++){
            sOut += (char) ((int) sIn.charAt(i) + 1);
        }
        return sOut;
    }
    public static LocalDateTime formatearFecha(String f) {
        if (f.length() == 8) {
            int anio = Integer.parseInt(f.substring(0, 4));
            if ((anio > 1900) && (anio < 2100)) {
                int mes = Integer.parseInt(f.substring(4, 6));
                if ((mes > 0) && (mes < 13)) {
                    int dia = Integer.parseInt(f.substring(6, 8));
                    if ((dia > 0) && (dia < 32)) {
                        try {
                            return LocalDateTime.of(anio, mes, dia, 0, 0);
                        } catch (Exception e) {
                            System.out.println("Error convirtiendo Fecha " + f + " con LocalDateTime.of()");
                        }
                    }
                }
            }
        }
        System.out.println("Error convirtiendo Fecha " + f);
        return null;
    }

    public static List<String> getList(String item1) {
        return getList(item1, "", "", "");
    }

    public static List<String> getList(String item1, String item2) {
        return getList(item1, item2, "", "");
    }

    public static List<String> getList(String item1, String item2, String item3, String item4) {
        List<String> l = new ArrayList<>();
        l.add(item1);
        if (!item2.equals("")) {
            l.add(item2);
            if (!item3.equals("")) {
                l.add(item3);
                if (!item4.equals("")) {
                    l.add(item4);
                }
            }
        }
        return l;
    }

    public static String int2Str(int i, int largo){
        String s = Integer.toString(i);
        while (s.length() < largo) {
            s = '0' + s;
        }
        return s;
    }

    public static String date2YYYYMMAA(LocalDate dt){
        return int2Str(dt.getYear(), 4) + int2Str(dt.getMonthValue(), 2) + int2Str(dt.getDayOfMonth(), 2) ;
    }

    public static String date2YYYYMMAA(LocalDateTime dt){
        return date2YYYYMMAA(LocalDate.of(dt.getYear(), dt.getMonthValue(), dt.getDayOfMonth()));
    }
}
