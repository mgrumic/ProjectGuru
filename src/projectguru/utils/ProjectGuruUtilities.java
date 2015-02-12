/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.utils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 *
 * @author Marko
 */
public class ProjectGuruUtilities {

     public static String pass2sha1(String password){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(password.getBytes(), 0, password.length());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }
     
    public static boolean tryParseInt(String s) {
        boolean retVal = false;
        try {
            Integer.valueOf(s);
            retVal = true;
        } catch (NumberFormatException e) {
            System.out.println("String " + s + " nije moguce pretvoriti u int");
//            e.printStackTrace();
        }
        return retVal;
    }

    public static boolean tryParseLong(String s) {
        boolean retVal = false;
        try {
            Long.valueOf(s);
            retVal = true;
        } catch (NumberFormatException e) {
            System.out.println("String " + s + " nije moguce pretvoriti u long");
//            e.printStackTrace();
        }
        return retVal;
    }

    public static boolean tryParseDouble(String s) {
        boolean retVal = false;
        try {
            Double.valueOf(s);
            retVal = true;
        } catch (NumberFormatException e) {
            System.out.println("String " + s + " nije moguce pretvoriti u double");
//            e.printStackTrace();
        }
        return retVal;
        
    }
}
