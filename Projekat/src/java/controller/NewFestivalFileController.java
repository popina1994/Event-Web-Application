/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.opencsv.CSVReader;
import static controller.NewFestivalRegularController.DATABASE_PATH;
import static controller.NewFestivalRegularController.PICTURES_PATH;
import static controller.NewFestivalRegularController.VIDEOS_PATH;
import entity.Event;
import entity.Hours;
import entity.Link;
import entity.Picture;
import entity.Video;
import hibernate.HibernateUtil;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.expression.SearchExpressionResolver;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author popina
 */
@ManagedBean
@SessionScoped
public class NewFestivalFileController implements Serializable {
    public static final int PER_DAY = 100;
    public static final int PER_USER  = 5;
    private int activeIndex = 0;
    private String errorMessage = ""; 
    private Boolean fileAdded = false;
    private UploadedFile uploadedFile = null;
    public static final String JSON_EXT = "json";
    public static final String CSV_EXT = "csv";

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<Link> getListLink() {
        return listLink;
    }

    public void setListLink(List<Link> listLink) {
        this.listLink = listLink;
    }

    public List<Hours> getListHours() {
        return listHours;
    }

    public void setListHours(List<Hours> listHours) {
        this.listHours = listHours;
    }
    
    private Event event = new Event();
    private String name;
    private Date beginDateTime;
    private Date endDateTime;
    private int cntTicketPerDay;
    private int cntTicketPerUser;
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
    

    
    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    
    public int getActiveIndex() {
        return activeIndex;
    }

    public void setActiveIndex(int activeIndex) {
        this.activeIndex = activeIndex;
    }
    
    
    public static String beginPageStatic()
    {
        return AdminPageController.NEW_FESTIVAL_FILE + HomePageController.REDIRECT_EXT;
    }
    
    public String beginPage() {
        return beginPageStatic();
    }
    
    public void handleEventData(FileUploadEvent event) {
        
        uploadedFile = event.getFile();
    }
    
    public static final String CSV_FESTIVAL = "Festival";
    public static final String CSV_PLACE = "Place";
    public static final String CSV_START_DATE = "StartDate";
    public static final String CSV_END_DATE = "EndDate";
    public static final String CSV_TICKET_TYPE = "TicketType";
    public static final String CSV_PRICE = "Price";
    public static final String CSV_PERFORMER  = "Performer";
    public static final String CSV_PERFORMER_START_DATE  = "StartDate";
    public static final String CSV_PERFORMER_END_DATE  = "EndDate";
    public static final String CSV_PERFORMER_START_TIME  = "StartTime";
    public static final String CSV_PERFORMER_END_TIME  = "EndTime";
    public static final String CSV_SOCIAL_NETWORK  = "Social Network";
    public static final String CSV_LINK = "Link";
    
    public String activateStep(int idx) {
        String address = beginPage();
        switch (idx)
        {
            case 1:
                if (null == uploadedFile)
                {
                    errorMessage = "Nije dodat fajl";
                }
                else 
                {
                     String fullFile = uploadedFile.getFileName();
                     String[] tokens = fullFile.split("\\.(?=[^\\.]+$)");
                     String ext = tokens[1];
                     if (JSON_EXT.equals(ext))
                        {
                         JSONParser parser = new JSONParser();
                         try {
                             Object obj = parser.parse(new BufferedReader(
                                     new InputStreamReader(uploadedFile.getInputstream())));
                             JSONObject jsonFest = (JSONObject)((JSONObject)obj).get("Festival");
                             name = (String)jsonFest.get("Name");
                             place = (String)jsonFest.get("Place");
                             SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                             SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
                                     "dd/MM/yyyy hh:mm:ss a");

                             beginDateTime = dateFormat.parse((String)jsonFest.get("StartDate"));
                             endDateTime = dateFormat.parse((String)jsonFest.get("EndDate"));
                             endDateTime.setHours(23);
                             endDateTime.setMinutes(59);
                             endDateTime.setSeconds(59);
                             JSONArray tickets = (JSONArray)jsonFest.get("Tickets");
                             ticketPriceInd = Integer.parseInt((String)tickets.get(0));
                             ticketPriceFull = Integer.parseInt((String)tickets.get(1));
                             
                             event = new Event();
                             event.setBeginDateTime(beginDateTime);
                             event.setEndDateTime(endDateTime);
                             event.setCntClick(0);
                             event.setTicketPerDay(PER_DAY);
                             event.setTicketPerUser(PER_USER);
                             event.setPlace(place);
                             event.setTicketPriceFull(ticketPriceFull);
                             event.setTicketPriceInd(ticketPriceInd);
                             event.setInfo("");
                             event.setName(name);
                             
                             JSONArray performers = (JSONArray)jsonFest.get("PerformersList");
                             for (Object objIt : performers) 
                             {
                                 String startDateString, endDateString;
                                 String startTimeString, endTimeString;
                                 JSONObject curPerformer = (JSONObject)objIt;
                                 hours.setPerformer((String)curPerformer.get("Performer"));
                                 startDateString = (String)curPerformer.get("StartDate");
                                 endDateString = (String)curPerformer.get("EndDate");
                                 startTimeString = (String)curPerformer.get("StartTime");
                                 endTimeString = (String)curPerformer.get("EndTime");
                                 hours.setStartTime(dateTimeFormat.parse(startDateString + " " + 
                                         startTimeString));
                                 hours.setEndTime(dateTimeFormat.parse(endDateString + " " + 
                                         endTimeString));
                                 hours.setEvent(event);
                                 listHours.add(hours);
                                 
                                 hours = new Hours();
                             }
                             
                             JSONArray links = (JSONArray)jsonFest.get("SocialNetworks");
                             
                             for (Object objIt : links)
                             {
                                 JSONObject curLink = (JSONObject)objIt;
                                 link.setName((String)curLink.get("Name"));
                                 link.setLink((String)curLink.get("Link"));
                                 link.setEvent(event);
                                 listLink.add(link);
                                 
                                 link = new Link();
                             }
                             setActiveIndex(idx);
                             errorMessage = "";
                             
                             
                         } catch (IOException ex) {
                             Logger.getLogger(NewFestivalFileController.class.getName()).log(Level.SEVERE, null, ex);
                         } catch (ParseException ex) {
                             Logger.getLogger(NewFestivalFileController.class.getName()).log(Level.SEVERE, null, ex);
                             errorMessage = "Neuspesno parsiranje";
                         } catch (java.text.ParseException ex) {
                             Logger.getLogger(NewFestivalFileController.class.getName()).log(Level.SEVERE, null, ex);
                         }
                     }
                     else if (CSV_EXT.equals(ext))
                     {
                        try {
                            CSVReader reader = new CSVReader(new BufferedReader(
                                     new InputStreamReader(uploadedFile.getInputstream())));
                            int i;
                            String[] indx = reader.readNext(); // "Festival","Place","StartDate","EndDate"
                            HashMap<String, Integer> mapIdx = new HashMap<>();
                            for (i = 0; i < indx.length; i ++)
                            {
                                mapIdx.put(indx[i], i);
                            }
                            
                            
                            String[] festival = reader.readNext();
                            event = new Event();
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            SimpleDateFormat dateTimeFormat = new SimpleDateFormat(
                                     "dd/MM/yyyy hh:mm:ss a");
                            event.setName(festival[mapIdx.get(CSV_FESTIVAL)]);
                            event.setPlace(festival[mapIdx.get(CSV_PLACE)]);
                            beginDateTime = dateFormat.parse(festival[mapIdx.get(CSV_START_DATE)]);
                            endDateTime =  dateFormat.parse(festival[mapIdx.get(CSV_END_DATE)]);
                            endDateTime.setHours(23);
                            endDateTime.setMinutes(59); 
                            endDateTime.setSeconds(59);
                            event.setBeginDateTime(beginDateTime);
                            event.setEndDateTime(endDateTime);
                            event.setCntClick(0);
                            event.setTicketPerDay(PER_DAY);
                            event.setTicketPerUser(PER_USER);
                            
                            
                            indx = reader.readNext(); // "TicketType", "Price"
                            mapIdx = new HashMap<>();
                            for (i = 0; i < indx.length; i ++)
                            {
                                mapIdx.put(indx[i], i);
                            }
                            
                            String [] indStrArray = reader.readNext();
                            ticketPriceInd = Integer.parseInt(indStrArray[mapIdx.get(CSV_PRICE)]);
                            String [] fulStrArray = reader.readNext();
                            ticketPriceFull = Integer.parseInt(fulStrArray[mapIdx.get(CSV_PRICE)]);
                            event.setTicketPriceInd(ticketPriceInd);
                            event.setTicketPriceFull(ticketPriceFull);
                            indx = reader.readNext(); // "Performer","StartDate","EndDate","StartTime","EndTime"
                            mapIdx = new HashMap<>();
                            for (i = 0; i < indx.length; i ++)
                            {
                                mapIdx.put(indx[i], i);
                            }
                            
                            String[] performers = reader.readNext();
                            while (performers.length == 5)
                            {
                                String startDateString, endDateString;
                                String startTimeString, endTimeString;
                                hours.setPerformer(performers[mapIdx.get(CSV_PERFORMER)]);
                                startDateString = performers[mapIdx.get(CSV_PERFORMER_START_DATE)];
                                endDateString = performers[mapIdx.get(CSV_PERFORMER_END_DATE)];
                                startTimeString = performers[mapIdx.get(CSV_PERFORMER_START_TIME)];
                                endTimeString = performers[mapIdx.get(CSV_PERFORMER_END_TIME)]; 
                                hours.setStartTime(dateTimeFormat.parse(startDateString + " " + 
                                         startTimeString));
                                hours.setEndTime(dateTimeFormat.parse(endDateString + " " + 
                                         endTimeString));
                                hours.setEvent(event);
                                listHours.add(hours);
                                 
                                hours = new Hours();
                                performers = reader.readNext();
                            }
                            
                            indx = performers;
                            mapIdx = new HashMap<>();
                            for (i = 0; i < indx.length; i ++)
                            {
                                mapIdx.put(indx[i], i);
                            }
                            
                            String[] links = reader.readNext();
                            while (links != null) 
                            {
                                link.setEvent(event);
                                link.setLink(links[mapIdx.get(CSV_LINK)]);
                                link.setName(links[mapIdx.get(CSV_SOCIAL_NETWORK)]);
                                listLink.add(link);
                                link = new Link();
                                links = reader.readNext();
                            }
                            
                            setActiveIndex(idx);
                            errorMessage = "";
                            } catch (IOException ex) {
                             Logger.getLogger(NewFestivalFileController.class.getName()).log(Level.SEVERE, null, ex);
                         } catch (java.text.ParseException ex) {
                             Logger.getLogger(NewFestivalFileController.class.getName()).log(Level.SEVERE, null, ex);
                         }
                         
                         
                     }
                    
                }
                break;
            case 2:
                setActiveIndex(idx);
                break;
            case 0:
                setActiveIndex(idx);
                break;
        }
        
        
        return beginPage();
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
        
        event.setInfo("");
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
    
    public String cancelAdd()
    {
        resetData();
        return AdminPageController.HOME_ADMIN + "?faces-redirect=true";
    }
}
