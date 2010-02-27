package com.mysema.rdfbean.xsd;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mysema.rdfbean.query.QueryFunctions;
import com.mysema.rdfbean.xsd.DateConverter;

/**
 * DateTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DateConverterTest {
    
    private Locale defaultLocale;
    
    @Before
    public void setUp(){
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.ENGLISH);
    }
    
    @After
    public void tearDown(){
        Locale.setDefault(defaultLocale);
    }
    
    @Test
    public void test(){
        Date date = new Date();
        DateConverter converter = new DateConverter();
        String str = converter.toString(date);
        
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        assertEquals(
            String.valueOf(cal.get(Calendar.WEEK_OF_YEAR)), 
            QueryFunctions.week(str));
    }

}