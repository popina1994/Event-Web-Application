/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import entity.Message;
import entity.User;
import hibernate.HibernateUtil;
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
@SessionScoped
public class MessagePageController {
    public static final String MESSAGE = "messages";
        List<Message> listMessage  = new LinkedList<>();
    public String beginPageStatic()
    {
        return MESSAGE + HomePageController.REDIRECT_EXT;
    }
    
    public String beginPage()
    {
        return beginPageStatic();
    }
    
    
    public List<Message> getListMessage() {
        return listMessage;
    }

    public void setListMessage(List<Message> listMessage) {
        this.listMessage = listMessage;
    }
    
    public String viewMessages(User u)
    {
          Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("From Message m WHERE m.reservation.user.iduser= :userId ");
            query.setParameter("userId", u.getIduser());
            listMessage = query.list();
            List<Message> tmpList = new LinkedList<>();
            for (Message it : listMessage)
            {
                if (!it.isShown())
                {
                    it.setShown(true);
                    session.update(it);
                    tmpList.add(it);
                }
            }
            listMessage = tmpList;
            tr.commit();
        }catch (Exception e) {
            if (tr != null) {
                tr.rollback();
            }
        }
        finally {
            session.close();
        }
        return beginPage();
    }
            
}
