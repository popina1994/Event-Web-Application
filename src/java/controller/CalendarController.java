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
import org.primefaces.event.SelectEvent;
import util.UtilDate;

/**
 *
 * @author popina
 */
@ViewScoped
@ManagedBean
public class CalendarController implements Serializable {
    private List<Hours> listHours = new LinkedList<>();

    public List<Hours> getListHours() {
        return listHours;
    }

    public void setListHours(List<Hours> listHours) {
        this.listHours = listHours;
    }
    
    @PostConstruct
    public void init() {
        List<Event> listEvent = null;
         Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM Event e WHERE 1=1");
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
        
        Date curDateTime = new Date();
        curDateTime.setHours(0);
        curDateTime.setMinutes(0);
        curDateTime.setSeconds(0);
        List<Hours> tmpList= new LinkedList<>();
        for (Event it : listEvent)
        {
            for (Hours itHours : it.getHourses())
            {
                Date date = itHours.getStartTime();
                 if ( UtilDate.equalDate(itHours.getStartTime(), curDateTime))
                    {
                        tmpList.add(itHours);
                    }
            }
        }
        listHours = tmpList;
    }
    
     public void dateChange(SelectEvent event)
     {
         Date dateD = (Date)event.getObject();
          List<Event> listEvent = null;
         Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM Event e WHERE 1=1");
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
        
        Date curDateTime = dateD;
        curDateTime.setHours(0);
        curDateTime.setMinutes(0);
        curDateTime.setSeconds(0);
        List<Hours> tmpList= new LinkedList<>();
        for (Event it : listEvent)
        {
            for (Hours itHours : it.getHourses())
            {
                if (UtilDate.equalDate(itHours.getStartTime(), curDateTime))
                {
                        tmpList.add(itHours);
                    }
            }
        }
        listHours = tmpList;
     }
    
}
