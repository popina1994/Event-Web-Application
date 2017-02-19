/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Event;
import entity.Hours;
import entity.Message;
import hibernate.HibernateUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author popina
 */
@ViewScoped
@ManagedBean
public class StartPageController implements Serializable {
    List<Event> listEvent = new LinkedList<>();

     
    public static final String START_PAGE = "start";
    public static final int FEST_NUM_LIMIT = 5;
    
    
    public static String beginPageStatic() 
    {
        return START_PAGE + HomePageController.REDIRECT_EXT;
    }
    
    public String beginPage() 
    {
        return beginPageStatic();
    }
    
    public List<Event> getListEvent() {
        return listEvent;
    }

    public void setListEvent(List<Event> listEvent) {
        this.listEvent = listEvent;
    }

    
    @PostConstruct
    public void init() {
         Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
        tr = session.beginTransaction();
        Query query = session.createQuery("FROM Event E ORDER BY E.beginDateTime");
        listEvent = query.list();
        tr.commit();
        }catch (Exception e) {
            if (tr != null) {
                tr.rollback();
            }
        }
        finally {
            session.close();
        }
        
        List<Event> listTmp = new LinkedList<>();
        Date curDate = new Date();
        for (Event it : listEvent) 
        {
            if ( (it.getBeginDateTime().before(curDate) && it.getEndDateTime().after(curDate))
                || (it.getBeginDateTime().after(curDate)) )
            {
                listTmp.add(it);
            }
        }
        listEvent = listTmp;
        listEvent.sort((t, t1) -> {
            Event firstEvent = (Event)t;
            Event secondEvent = (Event)t1;
            
            if (firstEvent.getBeginDateTime().before(secondEvent.getBeginDateTime()))
            {
                return -1;
            }
            else if (firstEvent.getBeginDateTime().after(secondEvent.getBeginDateTime()))
            {
                return 1;
            }
            
            return 0;
        });
        
        
        
    }
    
    public boolean filterByPerformer(Object value, Object filter, Locale locale)
    {
        Event curEvent = (Event)value;
        String filterStr = (String)filter;
        for (Hours it : curEvent.getHourses())
        {
            if (it.getEvent().getIdevent().equals(curEvent.getIdevent())
                    && it.getPerformer().toLowerCase().
                            contains(filterStr.toLowerCase()))
            {
                return true;
            }
        }
        
        return false;
    }
    
}
