/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package hibernate;


import entity.*;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.primefaces.model.UploadedFile;
import org.hibernate.cfg.Configuration;
/**
 *
 * @author popina
 */
public class HibernateUtil {
    public static final SessionFactory sessionFactory;
    
    static {
        try {
            Configuration conf = new Configuration().configure().addPackage("entity");
            conf.addAnnotatedClass(Comment.class);
            conf.addAnnotatedClass(Event.class);
            conf.addAnnotatedClass(Hours.class);
            conf.addAnnotatedClass(Link.class);
            conf.addAnnotatedClass(Picture.class);
            conf.addAnnotatedClass(Rating.class);
            conf.addAnnotatedClass(Reservation.class);
            conf.addAnnotatedClass(User.class);
            conf.addAnnotatedClass(Video.class);
            
            sessionFactory = conf.buildSessionFactory();
        }
        catch (Throwable ex){
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }
    
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }
}
