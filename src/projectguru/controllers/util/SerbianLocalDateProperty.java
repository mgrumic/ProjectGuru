/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package projectguru.controllers.util;

import java.time.LocalDate;
import javafx.beans.property.SimpleObjectProperty;

/**
 *
 * @author marko
 */
public class SerbianLocalDateProperty extends SimpleObjectProperty<LocalDate>{
    protected SerbianLocalDateStringConverter converter = new SerbianLocalDateStringConverter();
    
    public SerbianLocalDateProperty(LocalDate ld){
        this.setValue(ld);
    }
    
    @Override
    public String toString(){
        return converter.toString(this.getValue());
    }
}
