/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Picture;
import entity.Video;
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
@ManagedBean
@ViewScoped
public class VideoApprovalController implements Serializable{
     public static final String VIDEO_APPROVAL = "videoApprove";
    private List<Video> listVideos;
    
    public static String beginPageStatic()
    {
        return VIDEO_APPROVAL + HomePageController.REDIRECT_EXT;
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
        Query query = session.createQuery("FROM Video v WHERE "
                + "v.needsApproval = true AND v.approved = false");
        listVideos = query.list();
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

    public List<Video> getListVideos() {
        return listVideos;
    }

    public void setListVideos(List<Video> listVideos) {
        this.listVideos = listVideos;
    }
    
    

    public void addVideo(Video video, boolean approved)
    {
          Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
        tr = session.beginTransaction();
            video.setApproved(approved);
            video.setNeedsApproval(false);
            session.saveOrUpdate(video);
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
