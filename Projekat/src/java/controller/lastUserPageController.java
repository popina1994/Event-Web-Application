/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.User;
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
public class lastUserPageController implements Serializable{
    public static final String LAST_USER = "lastUsers";
    private List<User> listUser;
    
    public static String beginPageStatic()
    {
        return LAST_USER + HomePageController.REDIRECT_EXT;
    }
    
    public String beginPage()
    {
        return beginPageStatic();
    }

    public List<User> getListUser() {
        return listUser;
    }

    public void setListUser(List<User> listUser) {
        this.listUser = listUser;
    }
    
    @PostConstruct
    public void init()
    {
         Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM User u ORDER by u.lastAccessTime DESC");
            listUser = query.list();
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
