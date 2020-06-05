/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package erp.bean;

import erp.dao.StaffDAO;
import erp.dao.UsrDAO;
import erp.entities.Attendance;
import erp.entities.Usr;
import erp.util.SystemParameters;
import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author peukianm
 */
@Named("dbTasks")
@ViewScoped
public class DashboardTasks implements Serializable {

    private static final Logger logger = LogManager.getLogger(DashboardTasks.class);

    private String lastExecution;

    @Inject
    private SessionBean sessionBean;

    @Inject
    private StaffDAO staffDao;

    Usr user;

    @PostConstruct
    public void init() {
        System.out.println("INITIALIZE DB TASKS BEAN");
        user = sessionBean.getUsers();
        lastExecution = staffDao.getTaskLastExecutionTime(user.getCompany(), Long.parseLong(SystemParameters.getInstance().getProperty("SCHEDULE_TASK_READ_LOGGERS")));
    }

    @PreDestroy
    public void reset() {

    }

    public String getLastExecution() {
        return lastExecution;
    }

    public void setLastExecution(String lastExecution) {
        this.lastExecution = lastExecution;
    }

}
