/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Event;
import entity.User;
import hibernate.HibernateUtil;
import java.io.Serializable;
import java.util.LinkedList;
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
public class NewUserController implements Serializable{
    public static final String NEW_USER = "userApprove";
    private List<User> listUser = new LinkedList<>();
    
    @PostConstruct
    public void init()
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
        tr = session.beginTransaction();
        Query query = session.createQuery("FROM User u WHERE u.userType = :type");
        query.setParameter("type", User.REGISTERED_UNCONFIRMED_USER);
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
    
    public static String beginPageStatic()
    {
        return NEW_USER + HomePageController.REDIRECT_EXT;
    }

    public List<User> getListUser() {
        return listUser;
    }

    public void setListUser(List<User> listUser) {
        this.listUser = listUser;
    }
    
    public String beginPage()
    {
        return beginPageStatic();
    }
    
    public void addUser(User user, boolean accept)
    {
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM User u WHERE u.iduser = :userId");
            query.setParameter("userId", user.getIduser());
            listUser = query.list();
            User findUser = listUser.get(0);
            if (accept)
            {
                findUser.setUserType(User.REGISTERED_CONFIRMED_USER);
            }
            else 
            {
                findUser.setUserType(User.REGISTERED_REJECTED_USER);
            }
            
            session.saveOrUpdate(findUser);

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
