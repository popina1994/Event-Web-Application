/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Picture;
import entity.User;
import hibernate.HibernateUtil;
import java.io.Serializable;
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
@ManagedBean
@ViewScoped
public class PictureApprovalController implements Serializable{
    public static final String PICTURE_APPROVAL = "approvePictures";
    private List<Picture> listPictures;
    
    public static String beginPageStatic()
    {
        return PICTURE_APPROVAL + HomePageController.REDIRECT_EXT;
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
        Query query = session.createQuery("FROM Picture p WHERE "
                + "p.needsApproval = true AND p.approved = false");
        listPictures = query.list();
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

    public List<Picture> getListPictures() {
        return listPictures;
    }

    public void setListPictures(List<Picture> listPictures) {
        this.listPictures = listPictures;
    }
    
    public void addPicture(Picture picture, boolean approved)
    {
          Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
        tr = session.beginTransaction();
            picture.setApproved(approved);
            picture.setNeedsApproval(false);
            session.saveOrUpdate(picture);
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
