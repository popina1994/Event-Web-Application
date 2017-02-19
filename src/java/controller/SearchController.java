/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Event;
import entity.Hours;
import hibernate.HibernateUtil;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.SessionScoped;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author popina
 */
@ManagedBean
@RequestScoped
public class SearchController implements Serializable{
    private List<Event> listEvent = new LinkedList<>();
    
    public static final String SEARCH_PAGE = "search";
    
    public static String beginPageStatic() 
    {
        return SEARCH_PAGE + HomePageController.REDIRECT_EXT;
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
        Query query = session.createQuery("FROM Event WHERE 1=1");
        
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
