/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Reservation;
import entity.User;
import hibernate.HibernateUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author popina
 */
@SessionScoped
@ManagedBean
public class ReservationController implements Serializable{
    public static final String RESERVATION = "reservation";
    
    public static String beginPageStatic()
    {
        return RESERVATION + HomePageController.REDIRECT_EXT;
    }
    
    public String beginPage()
    {
        return beginPageStatic();
    }
    
    private List<Reservation> listReservation;

    public List<Reservation> getListReservation() {
        return listReservation;
    }

    public void setListReservation(List<Reservation> listReservation) {
        this.listReservation = listReservation;
    }
    
    public void init(User user)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM Reservation r WHERE "
                    + "r.user.iduser=:userId AND "
                    + " (r.status=:reserved OR r.status=:bought)");
            query.setParameter("userId", user.getIduser());
            query.setParameter("reserved", Reservation.RESERVED);
            query.setParameter("bought", Reservation.BOUGHT);
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
    
    public String showReservations(User u)
    {
        init(u);
        return beginPage();
    }
    
    public boolean checkIfPassed(Date d)
    {
        if (d.before(new Date()))
        {
            return  true;
        }
        return false;
    }
}
