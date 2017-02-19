/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Event;
import entity.Hours;
import entity.Link;
import entity.Message;
import entity.Picture;
import entity.Reservation;
import entity.User;
import entity.Video;
import hibernate.HibernateUtil;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author popina
 */
@SessionScoped
@ManagedBean
public class EditFestivalController implements Serializable{
    public static final String EDIT_FESTIVAL = "editFestival";
    private Event event;
    private Picture picture = new Picture();
    private Video video = new Video();
    
    public Picture getPicture() {
        return picture;
    }

    public void setPicture(Picture picture) {
        this.picture = picture;
    }

    public Video getVideo() {
        return video;
    }

    public void setVideo(Video video) {
        this.video = video;
    }
    

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }
    
    private Link link = new Link();

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
    
    public void addLink() {
        System.out.println("Dodalo je link");
        event.getLinks().add(link);
        link = new Link();
    }
    
    public void addHours() {
        event.getHourses().add(hours);
        hours = new Hours();
    }
    
    
    private Hours hours = new Hours();

    public Hours getHours() {
        return hours;
    }

    public void setHours(Hours hours) {
        this.hours = hours;
    }
    
    public void changePerformer(ValueChangeEvent eventChange) {
        List<Reservation> listReservation = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;

        try {
            tr = session.beginTransaction();
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
                    Message message = new Message(it, Message.CHANGED_PERFORMER, false);
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
    
    public void changeHour(ValueChangeEvent eventChange) {
         List<Reservation> listReservation = null;
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;

        try {
            tr = session.beginTransaction();
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
                    Message message = new Message(it, Message.CHANGHED_HOURS, false);
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
                video.setApproved(true);
                video.setName(fileName);
                video.setPath("/Projekat" + resourcePathDB);
                System.out.println("PUtanja za videO" + video.getPath());
                event.getVideos().add(video);
                video = new Video();
            }
            else if (saveToDatabase){
                picture.setApproved(true);
                picture.setName(fileName);
                picture.setPath(resourcePathDB);
                event.getPictures().add(picture);
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
    
    
    
    
    
    
    public static String beginPageStatic()
    {
        return EDIT_FESTIVAL + HomePageController.REDIRECT_EXT;
    }
    
    public String beginPage()
    {
        return beginPageStatic();
    }
    
    private int activeIndex = 0;
    
    public String editFestival(Event event)
    {
       this.event = event;
       return beginPage();
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }
    
    
    
    public String activateStep(int idx)
    {
        String address = beginPage();
        setActiveIndex(idx);
        return address;
    }
    
    
     public String addNewFestival() {
        String address = beginPage();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        
        try {
            transaction = session.beginTransaction();
            
            session.update(event);
            for (Link it : event.getLinks())
            {
                it.setEvent(event);
                session.saveOrUpdate(it);
            }

            for (Hours it : event.getHourses())
            {
                it.setEvent(event);
                session.saveOrUpdate(it);
            }

            event.getVideos().forEach((it)->{
                it.setEvent(event);
                session.saveOrUpdate(it);
            });

            event.getPictures().forEach((it) -> {
                it.setEvent(event);
                session.saveOrUpdate(it);
            });
            setActiveIndex(0);
            transaction.commit();
            address = "successfulEdit" + HomePageController.REDIRECT_EXT;
        }
        catch (Exception e) {
            System.err.println(e.toString());
            if (transaction != null)
            {
                transaction.rollback();
            }
        }
        finally {
            session.close();
        }
        return address;
    }
    
    
}
