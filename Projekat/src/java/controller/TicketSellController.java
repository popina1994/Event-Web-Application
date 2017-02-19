/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Reservation;
import hibernate.HibernateUtil;
import java.io.Serializable;
import java.util.List;
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
public class TicketSellController implements Serializable{
    public static String TICKET_SELL = "ticketSell";
    private List<Reservation> listReservation;

    public List<Reservation> getListReservation() {
        return listReservation;
    }

    public void setListReservation(List<Reservation> listReservation) {
        this.listReservation = listReservation;
    }
    
    
    
    public static String beginPageStatic()
    {
        return TICKET_SELL + HomePageController.REDIRECT_EXT;
    }
    
    public String beginPage()
    {
        return beginPageStatic();
    }
    
    @PostConstruct
    public void init()
    {
         Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
        tr = session.beginTransaction();
        Query query = session.createQuery("FROM Reservation WHERE "
                + "status=:statusParam");
        query.setParameter("statusParam", Reservation.RESERVED);
        listReservation = query.list();
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
    
    public void sell(Reservation r)
    {
          Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            r.setStatus(Reservation.BOUGHT);
            session.update(r);
            tr.commit();
        }catch (Exception e) {
            if (tr != null) {
                tr.rollback();
            }
        }
        finally {
            session.close();
        }
        init();
        
    }
}
