/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util;

import java.util.Date;

/**
 *
 * @author popina
 */
public class UtilDate {
    public static boolean equalDate(Date date1, Date date2)
    {
        if ( (date1.getYear() == date2.getYear()) && 
                (date1.getDate() == date2.getDate())
                && (date1.getDay() == date2.getDay()))
                {
                    return true;
                }
                return false;
    }
    
    public static Date plusDate(Date date)
    {
        Date d = new Date();
        d.setDate(date.getDay());
        d.setYear(date.getYear());
        d.setMonth(date.getMonth());
        return d;
    }
    
}
