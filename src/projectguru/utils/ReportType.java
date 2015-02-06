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
    REPORT1,
    REPORT2,
    REPORT3,
    NONE;
    private static final String t1 = "Извјештај 1";
    private static final String t2 = "Извјештај 2";
    private static final String t3 = "Извјештај 3";
    
    public static ReportType getType(String str){
        if(null != str)
            switch (str) {
            case t1:
                return REPORT1;
            case t2:
                return REPORT2;
            case t3:
                return REPORT3;
        }
        return NONE;
    }
    
    public String getText(){
        switch(this){
            case REPORT1:
                return t1;
            case REPORT2:
                return t2;
            case REPORT3:
                return t3;
        }
        return null;
    }
}
