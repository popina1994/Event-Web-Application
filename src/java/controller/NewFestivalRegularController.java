/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;


import entity.Event;
import entity.Hours;
import entity.Link;
import entity.Picture;
import entity.Video;
import hibernate.HibernateUtil;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author popina
 */
@ManagedBean
@SessionScoped
public class NewFestivalRegularController implements Serializable{
    private int activeIndex = 0;
    private String name;
    private Date beginDateTime;
    private Date endDateTime;
    private int cntTicketPerDay;
    private int cntTicketPerUser;

    public int getCntTicketPerDay() {
        return cntTicketPerDay;
    }

    public void setCntTicketPerDay(int cntTicketPerDay) {
        this.cntTicketPerDay = cntTicketPerDay;
    }

    public int getCntTicketPerUser() {
        return cntTicketPerUser;
    }

    public void setCntTicketPerUser(int cntTicketPerUser) {
        this.cntTicketPerUser = cntTicketPerUser;
    }
    private String place;
    private int ticketPriceInd;
    private int ticketPriceFull;
    private String info;
    private Link link = new Link();
    private Hours hours = new Hours();
    private Picture picture = new Picture();
    private Video video = new Video();
    private List<Link> listLink = new LinkedList<>();
    private List<Hours> listHours = new LinkedList<>();
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
    
    
    
    public List<Hours> getListHours() {
        return listHours;
    }

    public void setListHours(List<Hours> listHours) {
        this.listHours = listHours;
    }
    
    public Hours getHours() {
        return hours;
    }

    public void setHours(Hours hours) {
        this.hours = hours;
    }

    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
        System.out.println("Indeks je "+ activeIndex);
    }
    
    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }
    
    

    public List<Link> getListLink() {
        return listLink;
    }

    public void setListLink(List<Link> listLink) {
        this.listLink = listLink;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addLink() {
        System.out.println("Dodalo je link");
        listLink.add(link);
        link = new Link();
    }
    
    public Date getBeginDateTime() {
        return beginDateTime;
    }

    public void setBeginDateTime(Date beginDateTime) {
        this.beginDateTime = beginDateTime;
    }

    public Date getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(Date endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public int getTicketPriceInd() {
        return ticketPriceInd;
    }

    public void setTicketPriceInd(int ticketPriceInd) {
        this.ticketPriceInd = ticketPriceInd;
    }

    public int getTicketPriceFull() {
        return ticketPriceFull;
    }

    public void setTicketPriceFull(int ticketPriceFull) {
        this.ticketPriceFull = ticketPriceFull;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    
    public String addFestivalFirstPart()
    {
        String address = beginPage();
        activeIndex = 1;
        return address;
    }
    
    public String activateStep(int idx) {
        String address = beginPage();
        setActiveIndex(idx);
        System.out.println("Odrajedna promena stepa" + idx);
        return address;
        
    }
    
    public void addHours() {
        System.out.println("Dodalo je festival");
        listHours.add(hours);
        hours = new Hours();
    }
    
    public static final String DATABASE_PATH = 
            "C:/Users/popina/Documents/NetBeansProjects/Projekat/web";
    public static final String PICTURES_PATH = "/resources/pictures";
    public static final String VIDEOS_PATH = "/resources/videos";
    
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
                listVideos.add(video);
                video = new Video();
            }
            else if (saveToDatabase){
                picture.setApproved(true);
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
    
    public void handlePictureUpload(FileUploadEvent event)
    {
        decodeFileNameAndWrite(PICTURES_PATH, event);
    }
    
    public void handleVideoUpload(FileUploadEvent event)
    {
        decodeFileNameAndWrite(VIDEOS_PATH, event);
    }
    
    
    public String addNewFestival() {
        String address = beginPage();
        Event event = new Event();
        event.setBeginDateTime(beginDateTime);
        event.setEndDateTime(endDateTime);
        endDateTime.setHours(23);
        endDateTime.setMinutes(59);
        endDateTime.setSeconds(59);
        event.setTicketPerDay(cntTicketPerDay);
        event.setTicketPerUser(cntTicketPerUser);
        //event.setCntTicket(cntTicket);
        //event.setCntSold(0);
        event.setCntClick(0);
        event.setPlace(place);
        event.setTicketPriceInd(ticketPriceInd);
        event.setTicketPriceFull(ticketPriceFull);
        event.setInfo(info);
        event.setName(name);
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = null;
        
        try {
            transaction = session.beginTransaction();
            
            session.save(event);
            for (Link it : listLink)
            {
                it.setEvent(event);
                session.save(it);
            }

            for (Hours it : listHours)
            {
                it.setEvent(event);
                session.save(it);
            }

            listVideos.forEach((it)->{
                it.setEvent(event);
                session.save(it);
            });

            listPictures.forEach((it) -> {
                it.setEvent(event);
                session.save(it);
            });
            
            transaction.commit();
            address = AdminPageController.NEW_FESTIVAL_ADDED + HomePageController.REDIRECT_EXT;
            resetData();
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
    
    private void resetData() {
        activeIndex = 0;
        name = null;
        beginDateTime = null;
        endDateTime = null;
        cntTicketPerDay = 0;
        cntTicketPerUser = 0;
        place = null;
        ticketPriceInd = 0;
        ticketPriceFull = 0;
        info  = null;
        link = new Link();
        hours = new Hours();
        picture = new Picture();
        video = new Video();
        listLink = new LinkedList<>();
        listHours = new LinkedList<>();
        listVideos = new LinkedList<>();
        listPictures = new LinkedList<>();
    }
    
    public String beginPageStatic()
    {
        return AdminPageController.NEW_FESTIVAL_REGULAR  + HomePageController.REDIRECT_EXT;
    }
    
    public String  beginPage() {
        return beginPageStatic();
    }
}
