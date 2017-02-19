/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;
import entity.User;
import hibernate.HibernateUtil;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.ResultTransformer;
import org.primefaces.model.UploadedFile;
import org.hibernate.SessionFactory;
/**
 *
 * @author popina
 */
@SessionScoped
@ManagedBean
public class HomePageController implements Serializable {
    
    public static final String REDIRECT_EXT = ".xhtml?faces-redirect=true";
    private String userName;
    private String password;
    private String passwordConfirm;
    private String passwordNew;
    private String passwordNewConfirm;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Boolean showRegister = false;
    private Boolean showChangePassword = false;
    private Boolean showLogin = true;
    
    public static final int ADMIN = 1;
    public static final int REG = 0;
    public static final int UNREG = 2;
    
    public static final String HOME_PAGE = "index";

    public int getADMIN() {
        return ADMIN;
    }

    public int getREG() {
        return REG;
    }

    public  int getUNREG() {
        return UNREG;
    }
    
    
    public String beginPage() 
    {
        return beginPageStatic();
    }
    
    public static String beginPageStatic() 
    {
        return HOME_PAGE + REDIRECT_EXT;
    }
    
    // 0 - registered confirmed user
    // 1 - administrator
    // 2 - registered not confirmed user
    //private Integer userType; 

    public User getCurUser() {
        return curUser;
    }

    public void setCurUser(User curUser) {
        this.curUser = curUser;
    }
    
    
    
    public String getPasswordNew() {
        return passwordNew;
    }

    public void setPasswordNew(String passwordNew) {
        this.passwordNew = passwordNew;
    }

    public String getPasswordNewConfirm() {
        return passwordNewConfirm;
    }

    public void setPasswordNewConfirm(String passwordNewConfirm) {
        this.passwordNewConfirm = passwordNewConfirm;
    }

    public Boolean getShowRegister() {
        return showRegister;
    }

    public void setShowRegister(Boolean showRegister) {
        this.showRegister = showRegister;
    }

    public Boolean getShowChangePassword() {
        return showChangePassword;
    }

    public void setShowChangePassword(Boolean showChangePassword) {
        this.showChangePassword = showChangePassword;
    }

    public Boolean getShowLogin() {
        return showLogin;
    }

    public void setShowLogin(Boolean showLogin) {
        this.showLogin = showLogin;
    }

    public String getPasswordConfirm() {
        return passwordConfirm;
    }

    public void setPasswordConfirm(String passwordConfirm) {
        this.passwordConfirm = passwordConfirm;
    }
    
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    
    private String errorMessage;
    private User curUser;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    
    public Boolean isAdmin()
    {
        if ( (curUser != null) && curUser.getUserType() == User.ADMINISTRATOR)
        {
            return true;
        }
        return false;
    }
    
    public Boolean isRegisteredUser()
    {
        if ( (curUser != null) && curUser.getUserType() == User.REGISTERED_CONFIRMED_USER)
        {
            return true;
        }
        return false;
    }
    
    
    public String login() {
        String address = beginPage();
        
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM User WHERE userName=:"
                    + "userName");
            query.setParameter("userName", userName);
            List<User> users = query.list();
            tr.commit();
            
            
            if (users.isEmpty()){
                errorMessage = "Pogresno korisnicko ime";
                address = beginPage();
            }
            else if (!users.get(0).getPassword().equals(password))
            {
                errorMessage = "Pogresna sifra!";
                address = beginPage();
            }
            else {
                curUser = users.get(0);
                int type = curUser.getUserType();
                if (type == User.REGISTERED_UNCONFIRMED_USER)
                {
                    errorMessage = "Nije vas potvrdio administrator";
                    address = beginPage();
                }
                else 
                {
                    if (type == User.REGISTERED_CONFIRMED_USER)
                    {
                        errorMessage = "";
                        address = StartPageController.beginPageStatic();
                        
                    }
                    else if (type == User.ADMINISTRATOR)
                    {
                        errorMessage = "";
                        address = AdminPageController.beginPageStatic();
                    }
                    else 
                    {
                        errorMessage = "Greska u bazi";
                        address = beginPage();
                    }
                }
            }
            
        }catch (Exception e) {
            if (tr != null) {
                tr.rollback();
            }
            errorMessage = "Greska u bazi";
        }
        finally {
            session.close();
        }
        System.out.print(errorMessage);
        if ( (errorMessage == null) || errorMessage == "")
        {
            updateLastAcccessTimeOfUser(curUser);
        }
        return address;
    }
    
    public String changePassword() {
        String address = beginPage();
        Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
            tr = session.beginTransaction();
            Query query = session.createQuery("FROM User WHERE userName=:"
                    + "userName");
            query.setParameter("userName", userName);
            List<User> users = query.list();
            tr.commit();
            
            
            if (users.isEmpty()){
                errorMessage = "Pogresno korisnicko ime";
                address = beginPage();
            }
            else if (!users.get(0).getPassword().equals(password))
            {
                errorMessage = "Pogresna sifra!";
                address = beginPage();
            }
            else {
                curUser = users.get(0);
                int type = curUser.getUserType();
                if (type == User.REGISTERED_UNCONFIRMED_USER)
                {
                    errorMessage = "Nije vas potvrdio administrator";
                    address = beginPage();
                }
                else 
                {
                    if (!passwordNew.equals(passwordNewConfirm))
                    {
                        errorMessage = "Sifre se ne poklapaju";
                        address = beginPage();
                    }
                    else 
                    {
                        curUser.setPassword(passwordNew);
                        tr = session.beginTransaction();
                        session.update(curUser);
                        tr.commit();
                        showLogin = true;
                        showChangePassword = false;
                        address = beginPage();
                        errorMessage = "";
                    }
                }
            }
            
        }catch (Exception e) {
            if (tr != null) {
                tr.rollback();
            }
            errorMessage = "Greska u bazi";
        }
        finally {
            session.close();
        }
        System.out.print(errorMessage);
        return address;
    }
    
    public String register() {
        String address = beginPage();
        if (password.equals(passwordConfirm))
        {
            User newUser = new User();
            newUser.setUserName(userName);
            newUser.setPassword(password);
            newUser.setFirstName(firstName);
            newUser.setLastName(lastName);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setUserType(User.REGISTERED_UNCONFIRMED_USER);
            newUser.setBlocked(false);
            newUser.setLastAccessTime(new Date());
            newUser.setUnsoldTickets(0);

            Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction transaction = null;
            // check if double exist
            //
            try {
                Query query = session.createQuery("FROM User WHERE username"
                    + "=:userName");
                query.setParameter("userName", userName);
                List<User> users = query.list();
                
                if (users.isEmpty())
                {
                    address = beginPage();
                    errorMessage = "";
                    showLogin = true;
                    showRegister = false;
                    transaction = session.beginTransaction();
                    session.save(newUser);
                    transaction.commit();
                }
                else {
                    address = beginPage();
                    errorMessage = "Vec postoji korisnicko ime";
                }
            } catch (Exception e)
            {
                if (transaction != null)
                {
                    transaction.rollback();
                }
            }
            finally{
                session.close();
            }
        }
        else {
            errorMessage = "Sifre se ne poklapaju";
            address = beginPage();
        }
        
        System.out.println("Registracija" + errorMessage);
        return address;
    }
    
    public String logout() {
        FacesContext.getCurrentInstance().getExternalContext().invalidateSession();
        return HOME_PAGE;
        
    }
    
    public String enableChangePassword() {
        String address = beginPage();
        showLogin = false;
        showRegister = false;
        showChangePassword = true;
        errorMessage = "";
        return address;
    }
    
    public String enableRegister() {
        String address = beginPage();
        showLogin = false;
        showChangePassword = false;
        showRegister = true;
        errorMessage = "";
        return address;
    }
    
    public String enableLogin() {
        String address = beginPage();
        showLogin = true;
        showChangePassword = false;
        showRegister = false;
        errorMessage = "";
        return address;
    }

    private void updateLastAcccessTimeOfUser(User curUser) {
        Session session = HibernateUtil.getSessionFactory().openSession();
            Transaction tr = null;

            try {
                tr = session.beginTransaction();
                curUser.setLastAccessTime(new Date());
                session.update(curUser);
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
    
    public void setGoogleId()
    {
        System.out.println("Pozvalo je");
        String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("name1");
        System.out.println(id);
        userName = id;
    }
    
    public void loginGoogle()
    {
        System.out.println("Pozvalo je login");
        String id = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("signInName");
        System.out.println(id);
        
          Session session = HibernateUtil.getSessionFactory().openSession();
        Transaction tr = null;
        
        try {
        tr = session.beginTransaction();
        Query query = session.createQuery("FROM User u WHERE u.userName=:USER_NAME");
        query.setParameter("USER_NAME", id);
        userName = id;
        List<User> listUser = query.list();
        password = listUser.get(0).getPassword();
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
