/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package erp.bean;

import erp.dao.UsrDAO;
import erp.entities.Usr;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;

import javax.inject.Named;

/**
 *
 * @author peukianm
 */
@Named(value = "resetBean")
@ViewScoped
public class ResetBean implements Serializable {

    @Inject
    private SessionBean sessionBean;

    @Inject
    private UsrDAO userDAO;

    private String password;
    private String rePassword;
    private String email;
    private String sessionid;
    private Usr user;
    private String userid;
    private boolean show = false;

    @PostConstruct
    public void pc() {
    }

    public void init() {
        if (userid != null && !userid.equals("") && sessionid != null && !sessionid.equals("")) {
            user = userDAO.get(Long.parseLong(userid));            
            if (user.getPassword().equals(sessionid)) {
                show = true;
            } else {
                show = false;
            }
        } else {
            show = false;
        }
    }

    @PreDestroy
    public void reset() {
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public boolean isShow() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public Usr getUser() {
        return user;
    }

    public void setUser(Usr user) {
        this.user = user;
    }

    public String getSessionid() {
        return sessionid;
    }

    public void setSessionid(String sessionid) {
        this.sessionid = sessionid;
    }

    public SessionBean getSessionBean() {
        return sessionBean;
    }

    public void setSessionBean(SessionBean sessionBean) {
        this.sessionBean = sessionBean;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRePassword() {
        return rePassword;
    }

    public void setRePassword(String rePassword) {
        this.rePassword = rePassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
