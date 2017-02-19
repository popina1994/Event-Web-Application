/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.sun.java.swing.plaf.windows.WindowsTreeUI;
import entity.Event;
import entity.Hours;
import entity.Link;
import entity.Picture;
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
@ManagedBean
@ViewScoped
public class AdminPageController implements Serializable{

    private List<Event> listViewedEvent;
    private List<Event> listSoldEvent;
    
    public static final String NEW_FESTIVAL_REGULAR = "newFestivalRegular";
    public static final String NEW_FESTIVAL_FILE = "newFestivalFile";
    public static final String NEW_FESTIVAL_ADDED = "addedFestival";
    public static final String HOME_ADMIN = "administrator";

    public List<Event> getListViewedEvent() {
        return listViewedEvent;
    }

    public void setListViewedEvent(List<Event> listViewedEvent) {
        this.listViewedEvent = listViewedEvent;
    }

    public List<Event> getListSoldEvent() {
        return listSoldEvent;
    }

    public void setListSoldEvent(List<Event> listSoldEvent) {
        this.listSoldEvent = listSoldEvent;
    }
    
    
    
    
    
    public static String beginPageStatic() 
    {
        return HOME_ADMIN + HomePageController.REDIRECT_EXT;
    }
    
    public String beginPage() 
    {
        return beginPageStatic();
    }
    
    public static class WrapperEvent implements Comparable<WrapperEvent>
    {
        Event event;
        int ticket;

        public Event getEvent() {
            return event;
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        public int getTicket() {
            return ticket;
        }

        public void setTicket(int ticket) {
            this.ticket = ticket;
        }

        public WrapperEvent(Event event, int ticket) {
            this.event = event;
            this.ticket = ticket;
        }

        @Override
        public int compareTo(WrapperEvent t) {
            return ticket - t.ticket;
        }
        
    }
    
    
    @PostConstruct
    void init()
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM Event e ORDER BY e.cntClick DESC");
            query.setMaxResults(5);
            listViewedEvent = query.list();
            
            query = session.createQuery("FROM Event e");
            
            LinkedList<WrapperEvent> listTicket = new LinkedList<>();
            
            List<Event> listEvent = query.list();
            
            for (Event it : listEvent)
            {
                listTicket.addLast(new WrapperEvent(it, 0));
                for (Reservation itRes : it.getReservations())
                {
                    if (itRes.getStatus() == Reservation.BOUGHT)
                    {
                        listTicket.getLast().setTicket(listTicket.getLast().getTicket() + 1);
                    }
                }
            }
           
        
         listTicket.sort((t, t1) -> {
            WrapperEvent first = (WrapperEvent)t;
            WrapperEvent second = (WrapperEvent)t1;
            
            return first.ticket -second.ticket;
        });
            Collections.reverse(listTicket);
         listSoldEvent = new LinkedList<>();
        for (int idx = 0; idx < ((listTicket.size() > 5) ? 5 : listTicket.size()); idx ++)
        {
            listSoldEvent.add(listTicket.get(idx).getEvent());
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
    
    
}