/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Comment;
import entity.Event;
import entity.Hours;
import entity.Message;
import entity.Picture;
import entity.Rating;
import entity.Reservation;
import entity.User;
import entity.Video;
import hibernate.HibernateUtil;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;
import util.UtilDate;

/**
 *
 * @author popina
 */
@SessionScoped
@ManagedBean
public class FestivalPageController implements Serializable{
    Event event;
    private String errorMessage;
    private int ticketNum = 1;
    private User user;
    private Comment comment = new Comment();
    private Rating rating = new Rating();
    private Video video = new Video();
    private Picture picture = new Picture();
    private List<Video> listVideos = new LinkedList<>();
    private List<Picture> listPictures = new LinkedList<>();

    
    
    public List<Video> getListVideos() {
        return listVideos;
    }

    public void setListVideos(List<Video> listVideos) {
        this.listVideos = listVideos;
    }

    public List<Picture> getListPictures() {
        return listPictures;
    }

    public void setListPictures(List<Picture> listPictures) {
        this.listPictures = listPictures;
    }
    

    
    
    public boolean isFinished()
    {
        return event.getEndDateTime().before(new Date());
    }
    
    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public Rating getRating() {
        return rating;
    }

    public void setRating(Rating rating) {
        this.rating = rating;
    }
    
    

    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getTicketNum() {
        return ticketNum;
    }

    public void setTicketNum(int ticketNum) {
        this.ticketNum = ticketNum;
    }
    
    
    

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
    
    
    
    
    public static final String FESTIVAL_PAGE = "festival";
    public static String beginPageStatic() 
    {
        return FESTIVAL_PAGE + HomePageController.REDIRECT_EXT;
    }
    public String beginPage()
    {
        return beginPageStatic();
    }
    
    public List<Picture> getApprovedPicutres()
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        List<Picture> listRet = null;
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM Picture p WHERE p.event.idevent="
                    + ":IDEVENT AND p.approved=true");
            query.setParameter("IDEVENT", event.getIdevent());

            listRet = query.list();
            tr.commit();
        }catch (Exception e) {
            if (tr != null) {
                tr.rollback();
            }
        }
        finally {
            session.close();
        }
        return listRet;
        
    }
    
    
    public String viewFestival(Event e, User u) 
    {
        String address = beginPage();
        event = e;
        user = u;
        Clicked();
        errorMessage = "";
        return address;
    }
    
    public String AddUserExp()
    {   
        String address = beginPage();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            
              Query query = session.createQuery("FROM Reservation r WHERE r.event.idevent="
                        + ":IDEVENT AND r.user.iduser=:IDUSER AND r.status = :BOUGHT");
                
                query.setParameter("IDEVENT", event.getIdevent());
                query.setParameter("IDUSER", user.getIduser());
                query.setParameter("BOUGHT", Reservation.BOUGHT);
                List<Reservation> listReservation = query.list();
                Reservation r = listReservation.get(0);
                
                query = session.createQuery("FROM Comment c WHERE c.idreservation=:IDRES");
                query.setParameter("IDRES", r.getIdreservation());
                if (query.list().size() != 0)
                {
                    address = beginPage();
                    errorMessage = "Vec ste dodali komentar";
                }
                else 
                {
                    comment.setReservation(r);
                    session.saveOrUpdate(comment);
                    rating.setReservation(r);
                    session.saveOrUpdate(rating);

                    listVideos.forEach((it)->{
                        it.setEvent(event);
                        session.saveOrUpdate(it);
                    });

                    listPictures.forEach((it) -> {
                        it.setEvent(event);
                        session.saveOrUpdate(it);
                    });
                    address = "commentAdded" + HomePageController.REDIRECT_EXT;
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
        
        cleanUserExp();
        return address;
    }
    
    public static class Exp
    {
        Comment comment;
        Rating rating;

        public Exp() {
        }

        public Exp(Comment comment, Rating rating) {
            this.comment = comment;
            this.rating = rating;
        }
        
        
        
        public Comment getComment() {
            return comment;
        }

        public void setComment(Comment comment) {
            this.comment = comment;
        }

        public Rating getRating() {
            return rating;
        }

        public void setRating(Rating rating) {
            this.rating = rating;
        }
        
    }
    
    public Integer avgRating()
    {
        List<Rating> listRating = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        double avgRating = 0;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM Rating r WHERE r.reservation.event.name=:EVENT_NAME");
            query.setParameter("EVENT_NAME", event.getName());
            listRating = query.list();
            
            for (Rating it : listRating)
            {
                avgRating += it.getRating();
            }
            if (listRating.size() != 0)
            {
                avgRating /=listRating.size();
            }
            else 
            {
                avgRating  =0;
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
        
        return (int)avgRating;
    }
    
    public List<Exp> listUserExp()
    {
        List<Exp> listExp = new LinkedList<>();
        List<Comment> listComment = null;
        List<Rating> listRating = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM Comment c WHERE c.reservation.event.name=:EVENT_NAME");
            query.setParameter("EVENT_NAME", event.getName());
            listComment = query.list();
            
            for (Comment it : listComment)
            {
                query = session.createQuery("FROM Rating r WHERE r.reservation.event.name=:EVENT_NAME"
                        + " AND r.idreservation=:RESERVATION_ID");
                query.setParameter("EVENT_NAME", event.getName());
                query.setParameter("RESERVATION_ID", it.getIdreservation());
                listRating = query.list();
                Rating r = listRating.get(0);
                Exp exp = new Exp(it, r);
                listExp.add(exp);
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
        
        return listExp;
    }
    
    public void cleanUserExp()
    {
        listPictures = new LinkedList<>();
        listVideos = new LinkedList<>();
        comment = new Comment();
        rating = new Rating();
    }
    
    public boolean canLeave()
    {
        boolean ret = false;
        
        if (isFinished())
        {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = null;

            try {
                tr = session.beginTransaction();
                Query query = session.createQuery("FROM Reservation r WHERE r.event.idevent="
                        + ":IDEVENT AND r.user.iduser=:IDUSER AND r.status = :BOUGHT");
                
                query.setParameter("IDEVENT", event.getIdevent());
                query.setParameter("IDUSER", user.getIduser());
                query.setParameter("BOUGHT", Reservation.BOUGHT);
                List<Reservation> listReservation = query.list();
                
                if (listReservation.size() != 0)
                {
                    ret = true;
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
        return ret;
        
    }
        
    
    public void CancelEventAdmin()
    {
        List<Reservation> listReservation = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;

        try {
            tr = session.beginTransaction();
            event.setCancelled(true);
            session.update(event);
            Query query = session.createQuery("FROM Reservation r WHERE "
                    + "r.event.idevent=:paramEventId");
            query.setParameter("paramEventId", event.getIdevent());
            listReservation = query.list();
            for (Reservation it : listReservation)
            {
                if ( (it.getUser().getUserType() == User.REGISTERED_CONFIRMED_USER)
                    && ( (it.getStatus() == Reservation.BOUGHT)
                         || (it.getStatus() == Reservation.RESERVED)) )
                {
                    Message message = new Message(it, Message.CANCELED_EVENT, false);
                    session.save(message);
                }
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
    
    public void initUser()
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;

        try {
            List <User> listUser = null;
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM User WHERE 1=1");
            listUser = query.list();
            for (User u : listUser)
            {
                if (u.getUserName().equals(user.getUserName()))
                {
                    user = u;
                    break;
                }
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
    
    public String ReserveAdmin(Hours hours)
    {
        String address = "successfulReservation";
        int type = Reservation.IS_DAY;
        
        if (checkLimitation(hours, false) != LIMIT_NO_CARDS)
        {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = null;

            try {
                tr = session.beginTransaction();

                Reservation r =  new Reservation(hours.getEvent(), user, 
                        hours.getStartTime(), new Date(), Reservation.BOUGHT, type, ticketNum);
                session.save(r);

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
        else 
        {
            address = "unsuccessfulReservation";
        }
        return address;
    }
    
    public String ReserveAdminFull()
    {
        String address = "successfulReservation";
        int type = Reservation.IS_FULL;
        
        int limit = checkLimitation(null, true);
        
        if (limit != LIMIT_NO_CARDS)
        {
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = null;

            try {
                tr = session.beginTransaction();
                Reservation r =  new Reservation(event, user, 
                                event.getBeginDateTime(), new Date(), 
                                Reservation.BOUGHT, type, ticketNum);
                session.save(r);

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
        else 
        {
            address = "unsuccessfulReservation";
        }
        return address;
    }
    
    public String ReserveOrCancel(Hours hours)
    {
        int type;
        initUser();
        type = Reservation.IS_DAY;
        if (!isReserved(hours))
        {
            if (checkForbiden())
            {
                errorMessage = "Nemate vise pravo na rezervacije, jer je 48 sati proslo";
            }
            else 
            {
                int limit = checkLimitation(hours, false);
                if (limit == LIMIT_NO_CARDS)
                {
                    errorMessage = "Nema vise karata za taj dan festivala u prodaji";
                }
                else if (limit == LIMIT_NO_RESERVATION)
                {
                    errorMessage = "Nemate pravo kao korisnik na vise rezervacija za ovaj festival";
                }
                else
                {
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    Transaction tr = null;

                    try {
                        tr = session.beginTransaction();

                        Reservation r=  new Reservation(hours.getEvent(), user, 
                                hours.getStartTime(), new Date(), Reservation.RESERVED, type, ticketNum);
                        session.save(r);
                        MakeJobForReservation(r, user);

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
        }
        else 
        {
            List<Reservation> listReservation = null;
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = null;

            try {
                tr = session.beginTransaction();
                Query query = session.createQuery("FROM Reservation WHERE 1=1");
                listReservation = query.list();

                for (Reservation it : listReservation)
                {
                    if ( (Objects.equals(it.getEvent().getIdevent(), event.getIdevent()))
                            && (Objects.equals(user.getIduser(), it.getUser().getIduser()))
                            && (it.getReservationType() == type) 
                            && (it.getStatus() == Reservation.RESERVED)
                            &&( UtilDate.equalDate(it.getEventTime(), hours.getStartTime())))
                    {
                        it.setStatus(Reservation.CANCELED);
                        session.saveOrUpdate(it);
                        errorMessage = "";
                        break;
                    }
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
        return beginPage();
    }
    
    public String reserveOrCancelFull()
    {
        initUser();
        int type = Reservation.IS_FULL;
        if (!isReservedFull())
        {
            if (checkForbiden())
            {
                errorMessage = "Nemate vise pravo na rezervacije, jer je 48 sati proslo";
            }
            else 
            {
                int limit = checkLimitation(null, true);
                if (limit == LIMIT_NO_CARDS)
                {
                    errorMessage = "Nema vise karata za taj dan festivala u prodaji";
                }
                else if (limit == LIMIT_NO_RESERVATION)
                {
                    errorMessage = "Nemate pravo kao korisnik na vise rezervacija za ovaj festival";
                }
                else 
                {
                    Session session = HibernateUtil.getSessionFactory().openSession();
                    Transaction tr = null;

                    try {
                        tr = session.beginTransaction();

                        Reservation r=  new Reservation(event, user, 
                                event.getBeginDateTime(), new Date(), Reservation.RESERVED, type, ticketNum);
                        session.save(r);
                        MakeJobForReservation(r, user);
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
        }
        else 
        {
            List<Reservation> listReservation = null;
            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = null;

            try {
                tr = session.beginTransaction();
                Query query = session.createQuery("FROM Reservation WHERE 1=1");
                listReservation = query.list();

                for (Reservation it : listReservation)
                {
                    if ( (it.getEvent().getIdevent() .equals(event.getIdevent()))
                            && (user.getIduser().equals(it.getUser().getIduser()))
                            && (it.getReservationType() == type)
                            && (it.getStatus() == Reservation.RESERVED))
                    {
                        it.setStatus(Reservation.CANCELED);
                        session.update(it);
                        errorMessage = "";
                        break;
                    }
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
        return beginPage();
        
    }
    
    public boolean isReserved(Hours hours) 
    {
        Event event = hours.getEvent();
        boolean found = false;
        int type = Reservation.IS_DAY;
        List<Reservation> listReservation = null;
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM Reservation WHERE 1=1");
            listReservation = query.list();
            
            for (Reservation it : listReservation)
            {
                if (isReservedCheck(event, user, it, type, hours.getStartTime()))
                {
                    found = true;
                    break;
                }
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
        return found;
    }
    
    public boolean isReservedFull() 
    {
        boolean found = false;
        int type = Reservation.IS_FULL;
        List<Reservation> listReservation = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM Reservation WHERE 1=1");
            listReservation = query.list();
            
            for (Reservation it : listReservation)
            {
                if (isReservedCheck(event, user, it, type, null))
                {
                    found = true;
                    break;
                }
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
        return found;
    }

    private boolean checkForbiden() {
        boolean forbiden = false;
        List<Reservation> listReservation = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM Reservation WHERE 1=1");
            listReservation = query.list();
            
            for (Reservation it : listReservation)
            {
                if (isExpired(event, user, it))
                {
                    forbiden = true;
                    break;
                }
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
        return forbiden;
    }
    
    public static int LIMIT_OK = 0;
    public static int LIMIT_NO_CARDS = 1;
    public static int LIMIT_NO_RESERVATION = 2;

    private void Clicked() {
         Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = null;

            try {
                tr = session.beginTransaction();
                
                Query query = session.createQuery("UPDATE Event e SET e.cntClick = e.cntClick +1 "
                        + "where e.idevent =:setParam"
                        + " ");
                query.setParameter("setParam", event.getIdevent());
                int result = query.executeUpdate();
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
    
    private class ThreadReservation extends Thread
    {
        
        Reservation r;
        User u;
        public static final int SLEEP_TIME_S = 4147200;
        public static final int S_MS = 1000;

        public Reservation getR() {
            return r;
        }

        public void setR(Reservation r) {
            this.r = r;
        }

        public User getU() {
            return u;
        }

        public void setU(User u) {
            this.u = u;
        }
        
        

        public ThreadReservation(Reservation r, User u) {
            this.r = r;
            this.u = u;
        }
        
        public void run()
        {
            try {
                Thread.sleep(SLEEP_TIME_S * S_MS);
                FestivalPageController.this.MakeReservationExpired(r);
            } catch (InterruptedException ex) {
                Logger.getLogger(FestivalPageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private static boolean isReservedCheck(Event event, User user, Reservation r, int type, Date startTime)
    {
        return ((r.getEvent().getIdevent().equals( event.getIdevent()))
                        && (user.getIduser().equals(r.getUser().getIduser()))
                        && (r.getReservationType() == type)
                        && (r.getStatus() == Reservation.RESERVED)
                        &&( (type == Reservation.IS_DAY &&
                            (UtilDate.equalDate(r.getEventTime(), startTime)))
                            ||
                            (type ==Reservation.IS_FULL)) 
                );
    }
    
    private static boolean isExpired(Event event, User user, Reservation r)
    {
        return ((r.getEvent().getIdevent() .equals(event.getIdevent()))
                        && (user.getIduser().equals(r.getUser().getIduser()))
                        && (r.getStatus() == Reservation.EXPIRED));
    }
    
    
    private static boolean haveYouReservedAlready(Event event, User user, Reservation r)
    {
        return ( r.getEvent().getIdevent().equals(event.getIdevent())
                && (user.getIduser().equals(r.getUser().getIduser()))
                && (r.getStatus() == Reservation.RESERVED));
    }
    
    private static boolean isLimited(Event event, User user, Reservation r, Date startTime)
    {
        return
        ( (r.getEvent().getIdevent() .equals(event.getIdevent()))
            && ( (r.getStatus() == Reservation.RESERVED) || 
                (r.getStatus() == Reservation.BOUGHT) )
            && ((UtilDate.equalDate(r.getEventTime(), startTime))
                ||   (r.getReservationType() == Reservation.IS_FULL)));
    }
    
    public static final int BLOCK_NUMBER = 2;
    
    public void MakeReservationExpired(Reservation r)
    {
        
        initUser();
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            List <Reservation> listReservation = null;
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM Reservation r WHERE r.idreservation"
                    + " = :idR");
            query.setParameter("idR", r.getIdreservation());
            List<Reservation> lr = query.list();
            r  = lr.get(0);
            if (r.getStatus() == Reservation.RESERVED)
            {
                r.setStatus(Reservation.EXPIRED);
            
                // findreservation

                if  (user.getUnsoldTickets() + 1 == BLOCK_NUMBER) 
                {
                    user.setBlocked(true);
                    user.setUnsoldTickets(BLOCK_NUMBER);
                }
                else 
                {
                    user.setUnsoldTickets(user.getUnsoldTickets() + 1);
                }
                session.update(r);
                session.update(user);
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

    public void MakeJobForReservation(Reservation r, User u)
    {
        ThreadReservation t = new ThreadReservation(r, u);
        t.start();
    }

    private int checkLimitation(Hours hours, boolean isFull) {
        int retValue = LIMIT_OK;
        int cntPerDay = 0;
        int cntPerEvent = 0;
        List<Reservation> listReservation = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM Reservation WHERE 1=1");
            listReservation = query.list();
            
            if (!isFull)
            {
                for (Reservation it : listReservation)
                {
                    if (haveYouReservedAlready(event, user, it))
                    {
                        cntPerEvent += it.getTicketNum();
                    }
                    if (isLimited(event, user, it, hours.getStartTime()))
                    {
                        cntPerDay += it.getTicketNum();
                    }
                }
            }
            else 
            {
                Calendar calBeginDateTime = Calendar.getInstance();
                calBeginDateTime.setTime(event.getBeginDateTime());
                
                  do {
                    Date eventDateHours = calBeginDateTime.getTime();
                    
                    cntPerDay = 0;
                    cntPerEvent = 0;
                    
                    for (Reservation it : listReservation)
                    {
                        if ( haveYouReservedAlready(event, user, it))
                        {
                            cntPerEvent += it.getTicketNum();
                        }
                        if (isLimited(event, user, it, eventDateHours))
                        {
                            cntPerDay += it.getTicketNum();
                        }
                    }
                    if ((user.getUserType() != User.ADMINISTRATOR) && (event.getTicketPerUser()  < cntPerEvent + ticketNum))
                    {
                        retValue = LIMIT_NO_RESERVATION;
                        break;
                    }
                    else if (event.getTicketPerDay() < cntPerDay + ticketNum)
                    {
                        retValue = LIMIT_NO_CARDS;
                        break;
                    }
                calBeginDateTime.add(Calendar.DAY_OF_MONTH, 1);
                } while (calBeginDateTime.getTime().before(event.getEndDateTime()));
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
        if (retValue == LIMIT_OK)
        {
            if ( (user.getUserType() != User.ADMINISTRATOR) &&
                    (event.getTicketPerUser() < cntPerEvent + ticketNum))
            {
                retValue = LIMIT_NO_RESERVATION;
            }
            else if (event.getTicketPerDay() < cntPerDay + ticketNum)
            {
                retValue = LIMIT_NO_CARDS;
            }
        }

        return retValue;
    }
    
     public static final String DATABASE_PATH = 
            "C:/Users/popina/Documents/NetBeansProjects/Projekat/web";
    public static final String PICTURES_PATH = "/resources/pictures";
    public static final String VIDEOS_PATH = "/resources/videos";
    
    
    public void decodeFileNameAndWrite(String enviromentPath, FileUploadEvent event) {
        final String WEB_ABS_PATH = FacesContext.getCurrentInstance().getExternalContext().getRealPath(enviromentPath);
        final String DATABASE_ABS_PATH = DATABASE_PATH + enviromentPath; 
        if (enviromentPath.equals(PICTURES_PATH))
        {
            System.out.println("Uploadova-lo sliku");
        }
        else if (enviromentPath.equals(VIDEOS_PATH))
        {
            System.out.println("Uploadova-lo video");
        }
        
        UploadedFile fileUploaded = event.getFile();
       
        String fullFile = fileUploaded.getFileName();
        String[] tokens = fullFile.split("\\.(?=[^\\.]+$)");
        String fileName = tokens[0];
        String ext = tokens[1];
        System.out.println("Ekstenzija " + ext);
        
        Date date = new Date();
        byte[] data = fileUploaded.getContents();
        
        WritePicture(WEB_ABS_PATH, enviromentPath, fileName, data, date, ext, true);
        WritePicture(DATABASE_ABS_PATH, enviromentPath, fileName, data, date, ext, false);
    }
    
      private void WritePicture(String filePath, String resourcePath, String fileName, 
            byte[] data, Date date, String ext, boolean saveToDatabase) 
        {SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss") ;
        FileOutputStream fileOutputStream = null; 
        try {
            String path = filePath + "/" + 
                    fileName + dateFormat.format(date) + "." + ext;
            String resourcePathDB = resourcePath + "/" + 
                    fileName + dateFormat.format(date) + "." + ext;
            System.out.println("Path is " +path);
            fileOutputStream = new FileOutputStream(path);
            
            fileOutputStream.write(data);
            
            if (saveToDatabase && ext.equals("mp4") )
            {
                video.setApproved(false);
                video.setNeedsApproval(true);
                video.setName(fileName);
                video.setPath("/Projekat" + resourcePathDB);
                System.out.println("PUtanja za videO" + video.getPath());
                listVideos.add(video);
                video = new Video();
            }
            else if (saveToDatabase){
                picture.setApproved(false);
                picture.setNeedsApproval(true);
                picture.setName(fileName);
                picture.setPath(resourcePathDB);
                listPictures.add(picture);
                picture = new Picture();
            }
                    
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AdminPageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(AdminPageController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                fileOutputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(AdminPageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void handlePictureUpload(FileUploadEvent event)
    {
        decodeFileNameAndWrite(PICTURES_PATH, event);
    }
    
    public void handleVideoUpload(FileUploadEvent event)
    {
        decodeFileNameAndWrite(VIDEOS_PATH, event);
    }
    
}
