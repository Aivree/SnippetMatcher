package it.generali.test;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {


    public static void main(String[] args) throws ParseException {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");

        //Date startDate = simpleDateFormat.parse(args[0]);
        Date startDate = simpleDateFormat.parse("2013-10-08 02:33:11,041");
        Date endDate = simpleDateFormat.parse("2013-10-08 02:33:24,091");

        System.out.println(startDate.getTime());
        System.out.println(endDate.getTime());
        System.out.println((endDate.getTime() - startDate.getTime())/1000);



    }
}
