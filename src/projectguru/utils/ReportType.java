/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.utils;

/**
 *
 * @author medlan
 */
public enum ReportType {
    STANJE_PROJEKTA_REPORT,
    FINANSIJSKI_PREGLED_PRIHODA_REPORT,
    FINANSIJSKI_PREGLED_RASHODA_REPORT,
    PREGLED_AKTIVNOSTI_REPORT,
    NONE;
    private static final String t1 = "Стање пројекта";
    private static final String t2 = "Финансијски преглед расхода";
    private static final String t3 = "Финансијски преглед прихода";
    private static final String t4 = "Преглед активности";
    
    private static final String q1 = "";
    private static final String q2 = "select * from rashodi";
    private static final String q3 = "select * from prihodi";
    private static final String q4 = "select * from aktivnosti";
    
    public static ReportType getType(String str){
        if(null != str)
            switch (str) {
            case t1:
                return STANJE_PROJEKTA_REPORT;
            case t2:
                return FINANSIJSKI_PREGLED_RASHODA_REPORT;
            case t3:
                return FINANSIJSKI_PREGLED_PRIHODA_REPORT;
            case t4:
                return PREGLED_AKTIVNOSTI_REPORT;
        }
        return NONE;
    }
    
    public String getText(){
        switch(this){
            case STANJE_PROJEKTA_REPORT:
                return t1;
            case FINANSIJSKI_PREGLED_RASHODA_REPORT:
                return t2;
            case FINANSIJSKI_PREGLED_PRIHODA_REPORT:
                return t3;
            case PREGLED_AKTIVNOSTI_REPORT:
                return t4;
        }
        return null;
    }
    
    public String getQuerry(){
        switch(this){
            case STANJE_PROJEKTA_REPORT:
                return q1;
            case FINANSIJSKI_PREGLED_RASHODA_REPORT:
                return q2;
            case FINANSIJSKI_PREGLED_PRIHODA_REPORT:
                return q3;
            case PREGLED_AKTIVNOSTI_REPORT:
                return q4;
        }
        return null;
    }
}
