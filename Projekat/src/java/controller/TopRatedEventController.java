/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import entity.Event;
import entity.Hours;
import entity.Link;
import entity.Picture;
import entity.Rating;
import entity.Reservation;
import entity.Video;
import hibernate.HibernateUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;


/**
 *
 * @author popina
 */
@ViewScoped
@ManagedBean
public class TopRatedEventController implements Serializable {
    public static final String TOP_RATED_EVENT = "topRatedEvent";
    
    private List<Event> listEvent;

    public List<Event> getListEvent() {
        return listEvent;
    }

    public void setListEvent(List<Event> listEvent) {
        this.listEvent = listEvent;
    }
    
    
    
    public static String beginPageStatic()
    {
        return TOP_RATED_EVENT + HomePageController.REDIRECT_EXT;
    }
    
    public String beginPage()
    {
        return beginPageStatic();
    }
    
    public static class WrapperEvent 
    {
        Event event;
        double rating;

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        public double getRating() {
            return rating;
        }

        public void setRating(double rating) {
            this.rating = rating;
        }

        public WrapperEvent(Event event, double rating) {
            this.event = event;
            this.rating = rating;
        }

        public WrapperEvent() {
        }
        
        
        
    }
    
    
    @PostConstruct
    void init()
    {
        LinkedList<WrapperEvent> listWrapper = null;
          Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM Event WHERE 1=1");

            listEvent = query.list();
            listWrapper = new LinkedList<>();

            for (Event it : listEvent)
            {
                double avg = calcAvg(it, session);
                WrapperEvent wEvent = new WrapperEvent(it, avg);
                listWrapper.add(wEvent);
            }
            listWrapper.sort((t, t1) ->{
                WrapperEvent first = (WrapperEvent)t;
                WrapperEvent second = (WrapperEvent)t1;
            
                if (second.rating - first.rating < 0)
                {
                    return -1;
                }
                else if (second.rating - first.rating > 0)
                {
                    return 1;
                }
                return 0;
            });
            listEvent = new LinkedList<>();
            for (WrapperEvent wEvent : listWrapper)
            {
                listEvent.add(wEvent.event);
            }
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
    public double calcAvg(Event event, Session session) throws Exception
    {
        List<Rating> listRating = null;
        
        double avgRating = 0;
        
        Query query = session.createQuery("FROM Rating r WHERE r.reservation.event.idevent=:EVENT_ID");
        query.setParameter("EVENT_ID", event.getIdevent());
        listRating = query.list();

        for (Rating it : listRating)
        {
            avgRating += it.getRating();
        }
        
        if (!listRating.isEmpty())
        {
            avgRating /=listRating.size();
        }
        else 
        {
            avgRating = 0;
        }
        
        
        return avgRating;
    }
}
