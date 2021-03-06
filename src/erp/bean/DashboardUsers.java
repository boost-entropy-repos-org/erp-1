package erp.bean;

import erp.dao.UsrDAO;
import erp.entities.Company;
import erp.entities.Department;
import erp.entities.Role;
import erp.entities.Sector;
import erp.entities.Usr;
import erp.exception.ERPCustomException;
import erp.util.AccessControl;
import erp.util.MessageBundleLoader;
import erp.util.SystemParameters;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.primefaces.event.SelectEvent;

/**
 *
 * @author peukianm
 */
@Named("dbUsers")
@ViewScoped
public class DashboardUsers implements Serializable {

    private static final Logger logger = LogManager.getLogger(DashboardUsers.class);

    @Inject
    private SessionBean sessionBean;

    @Inject
    private UsrDAO userDao;

    Usr user;

    private Usr searchUser;
    private Department selectedDepartment;
    private Sector selectedSector;
    private Role selectedRole;
    private Company selectedCompany;
    private String surname;
    private boolean active = true;
    private Usr viewUser;
    private Usr passwordUpdateUser;
    private String password;

    List<Usr> availableUsers;

    List<Usr> searchUsers = new ArrayList<>(0);

    String showUsers = "";
    String showNewUser = "hidden='true'";

    public void preRenderView() {
        if (sessionBean.getUsers().getDepartment() != null && sessionBean.getUsers().getDepartment().getDepartmentid() != Integer.parseInt(SystemParameters.getInstance().getProperty("itID"))) {
            if (!AccessControl.control(sessionBean.getUsers(), SystemParameters.getInstance().getProperty("PAGE_USER_ADMIN"), null, 1)) {
                return;
            }
        }
        sessionBean.setPageCode(SystemParameters.getInstance().getProperty("PAGE_USER_ADMIN"));
        sessionBean.setPageName(MessageBundleLoader.getMessage("usersPage"));
    }

    @PostConstruct
    public void init() {
    }

    @PreDestroy
    public void reset() {

    }

    public void resetSearchUsersForm() {
        searchUser = null;
        selectedDepartment = null;
        selectedSector = null;
        selectedRole = null;
        selectedCompany = null;
        surname = null;
        active = true;
        searchUsers = new ArrayList<>(0);
    }

    public List<Usr> completeUser(String username) throws ERPCustomException {
        try {
            user = sessionBean.getUsers();
            if (username != null && !username.trim().isEmpty() && username.trim().length() >= 1) {
                username = username.trim();
                availableUsers = userDao.fetchUserAutoCompleteUsername(username);
                return availableUsers;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sessionBean.setErrorMsgKey("errMsg_GeneralError");
            throw new ERPCustomException("Throw From Autocomplete User Action", e, sessionBean.getUsers(), "errMsg_GeneralError");
        }
    }

    public void autocompleteUsernameSelectUser(SelectEvent event) {
        if (!searchUsers.contains(searchUser)) {
            searchUsers.add(searchUser);
        }
        searchUser = null;
    }

    public Usr getViewUser() {
        return viewUser;
    }

    public void setViewUser(Usr viewUser) {
        this.viewUser = viewUser;
    }

    public Usr getPasswordUpdateUser() {
        return passwordUpdateUser;
    }

    public void setPasswordUpdateUser(Usr passwordUpdateUser) {
        this.passwordUpdateUser = passwordUpdateUser;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    
    

    public void goAddNewUser() {
        showUsers = "hidden='true'";
        showNewUser = "";
    }

    public String getShowUsers() {
        return showUsers;
    }

    public void setShowUsers(String showUsers) {
        this.showUsers = showUsers;
    }

    public String getShowNewUser() {
        return showNewUser;
    }

    public void setShowNewUser(String showNewUser) {
        this.showNewUser = showNewUser;
    }

    public List<Usr> getAvailableUsers() {
        return availableUsers;
    }

    public void setAvailableUsers(List<Usr> availableUsers) {
        this.availableUsers = availableUsers;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public Company getSelectedCompany() {
        return selectedCompany;
    }

    public void setSelectedCompany(Company selectedCompany) {
        this.selectedCompany = selectedCompany;
    }

    public List<Usr> getSearchUsers() {
        return searchUsers;
    }

    public void setSearchUsers(List<Usr> searchUsers) {
        this.searchUsers = searchUsers;
    }

    public Usr getSearchUser() {
        return searchUser;
    }

    public void setSearchUser(Usr searchUser) {
        this.searchUser = searchUser;
    }

    public Department getSelectedDepartment() {
        return selectedDepartment;
    }

    public void setSelectedDepartment(Department selectedDepartment) {
        this.selectedDepartment = selectedDepartment;
    }

    public Sector getSelectedSector() {
        return selectedSector;
    }

    public void setSelectedSector(Sector selectedSector) {
        this.selectedSector = selectedSector;
    }

    public Role getSelectedRole() {
        return selectedRole;
    }

    public void setSelectedRole(Role selectedRole) {
        this.selectedRole = selectedRole;
    }
}
