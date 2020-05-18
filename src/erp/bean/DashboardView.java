/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package erp.bean;

import com.sun.javafx.scene.control.skin.VirtualFlow;
import erp.action.AdministrationAction;
import erp.dao.StaffDAO;
import erp.entities.Attendance;
import erp.entities.Department;
import erp.entities.Sector;
import erp.entities.Staff;
import erp.entities.Usr;
import erp.util.FacesUtils;
import erp.util.MessageBundleLoader;
import erp.util.SystemParameters;
import java.io.IOException;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
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
@Named("dbView")
@ViewScoped
public class DashboardView implements Serializable {

    private static final Logger logger = LogManager.getLogger(AdministrationAction.class);

    @Inject
    private SessionBean sessionBean;

    @Inject
    private StaffDAO staffDao;
    
     @Inject
    private ApplicationBean applicationBean;

    private Attendance dayAttendance;
    private String entryTime = "N/A";
    private String exitTime = "N/A";
    private String attendanceDate = "N/A";
    
     private List<Department> departments;

    private List<Department> selectedDepartments;
    private List<Sector> selectedSectors;

    private Date fromAttendanceDate;
    private Date toAttendanceDate;

    private List<Staff> selectedStaff = new ArrayList<>(0);

    private Staff searchStaff;
    private List<Staff> availableStaff;

    private Boolean enableDepartment;
    private Boolean enableSector;
    private Boolean enableStaff;
    
    private String lastExecution;

    Usr user;

    @PostConstruct
    public void init() {
        user = sessionBean.getUsers();
        fromAttendanceDate = new java.util.Date();
        toAttendanceDate = new java.util.Date();
        System.out.println("User=" + user.getStaff().getEmprank().getRankid() + " " + user.getStaff().getDepartment().getName() + " " + user.getStaff().getSector().getName());
        dayAttendance = staffDao.getDayAttendance(user.getStaff(), false);
        if (dayAttendance != null) {
            attendanceDate = dayAttendance.getEntrance().toString().substring(0, 10);
            entryTime = dayAttendance.getEntrance().toString().substring(11, 16);
            if (dayAttendance.getExit() != null) {
                exitTime = dayAttendance.getExit().toString().substring(11, 16);
            }
        }
        departments = applicationBean.getDepartments();
        System.out.println("Starting switch");
        switch ((int) user.getRole().getRoleid()) {
            case 1:
                enableDepartment = true;
                enableSector = true;
                enableStaff = true;               
                break;
            case 2:
                enableDepartment = true;
                enableSector = true;
                enableStaff = true;
                break;
            case 3: {
                switch ((int) user.getStaff().getEmprank().getRankid()) {
                    case 15: //YPALLHLOI
                    case 16:
                    case 17:
                    case 6:
                    case 7:
                    case 8: {
                        enableDepartment = false;
                        enableSector = false;
                        enableStaff = false;
                        selectedStaff = new ArrayList<>(0);
                        selectedStaff.add(user.getStaff());
                        break;
                    }
                    case 3: //DIEYYHYNTES
                    case 14:
                    case 13:
                    case 11: {
                        System.out.println("INSIDE DIEUYNTES");
                        enableDepartment = true;
                        enableSector = false;
                        enableStaff = true;
                        selectedSectors = new ArrayList<>(0);
                        selectedSectors.add(user.getStaff().getSector());
                        departments = staffDao.getSectorDepartments(user.getCompany(), user.getStaff().getSector());
                        break;
                    }
                    case 12://PROISTAMENOI
                    case 10:
                    case 9: {
                        System.out.println("INSIDE PROSTAMENOI");
                        enableDepartment = false;
                        enableSector = false;
                        enableStaff = true;
                        selectedDepartments = new ArrayList<>(0);
                        selectedDepartments.add(user.getStaff().getDepartment());
                        break;
                    }
                }
            }
        }
        
        lastExecution = staffDao.getTaskLastExecutionTime(user.getCompany(), Long.parseLong(SystemParameters.getInstance().getProperty("SCHEDULE_TASK_READ_LOGGERS")));

//        if (user.getStaff().getDepartment().getDepartmentid() == 67 || user.getStaff().getDepartment().getDepartmentid() == 64) { //Prosopokou Plhroforikh
//            enableDepartment = true;
//            enableSector = true;
//            enableStaff = true;
//            selectedSectors = new ArrayList<>(0);
//            selectedDepartments = new ArrayList<>(0);
//
//        }
        System.out.println(enableSector);
        System.out.println(enableDepartment);
        System.out.println(enableStaff);
    }

    @PreDestroy
    public void reset() {

    }

    public void resetSearchStaffForm() {
        selectedStaff = new ArrayList<>(0);
        selectedDepartments = null;
        selectedSectors = null;

        fromAttendanceDate = new java.util.Date();
        toAttendanceDate = new java.util.Date();

    }

    public void onSectorChange() {
        try {
            if (selectedSectors != null) {
                selectedDepartments = new ArrayList<>(0);
                selectedStaff = new ArrayList<>(0);
                searchStaff = null;
                selectedSectors.forEach((temp) -> {
                    selectedDepartments.addAll(staffDao.getSectorDepartments(sessionBean.getUsers().getCompany(), temp));
                });

            } else {
                selectedDepartments = new ArrayList<>();
            }
        } catch (Exception e) {
            e.printStackTrace();
            sessionBean.setErrorMsgKey("errMsg_GeneralError");
            goError(e);
        }
    }

    public void autocompleteSurnameSelectStaff(SelectEvent event) {
        try {
            selectedDepartments = new ArrayList<>(0);
            selectedSectors = new ArrayList<>(0);
            selectedStaff.add(searchStaff);
            searchStaff = null;
        } catch (Exception e) {
            e.printStackTrace();
            sessionBean.setErrorMsgKey("errMsg_GeneralError");
            goError(e);
        }
    }

    public List<Staff> completeStaff(String surname) {
        try {
            user = sessionBean.getUsers();
            if (surname != null && !surname.trim().isEmpty() && surname.trim().length() >= 1) {
                surname = surname.trim();

                if (enableSector) {
                    availableStaff = staffDao.fetchStaffAutoCompleteSurname(surname, null, null);
                } else if (enableDepartment) {
                    availableStaff = staffDao.fetchStaffAutoCompleteSurname(surname, user.getStaff().getSector(), null);
                } else {
                    availableStaff = staffDao.fetchStaffAutoCompleteSurname(surname, user.getStaff().getSector(), user.getStaff().getDepartment());
                }
                return availableStaff;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sessionBean.setErrorMsgKey("errMsg_GeneralError");
            goError(e);
            return null;
        }
    }

    public void removeStaff(int index) {
        try {

            System.out.println(index);
            System.out.println(selectedStaff.size());
            if (selectedStaff != null && selectedStaff.size() > 0 && selectedStaff.size() > index) {
                selectedStaff.remove(index);
            }
            System.out.println(selectedStaff.size());
        } catch (Exception e) {
            e.printStackTrace();
            sessionBean.setErrorMsgKey("errMsg_GeneralError");
            goError(e);
        }
    }

    public String getLastExecution() {
        return lastExecution;
    }

    public void setLastExecution(String lastExecution) {
        this.lastExecution = lastExecution;
    }

    
    public List<Department> getDepartments() {
        return departments;
    }

    public void setDepartments(List<Department> departments) {
        this.departments = departments;
    }

    
    public Boolean getEnableDepartment() {
        return enableDepartment;
    }

    public void setEnableDepartment(Boolean enableDepartment) {
        this.enableDepartment = enableDepartment;
    }

    public Boolean getEnableSector() {
        return enableSector;
    }

    public void setEnableSector(Boolean enableSector) {
        this.enableSector = enableSector;
    }

    public Boolean getEnableStaff() {
        return enableStaff;
    }

    public void setEnableStaff(Boolean enableStaff) {
        this.enableStaff = enableStaff;
    }

    public Staff getSearchStaff() {
        return searchStaff;
    }

    public void setSearchStaff(Staff searchStaff) {
        this.searchStaff = searchStaff;
    }

    public List<Staff> getAvailableStaff() {
        return availableStaff;
    }

    public void setAvailableStaff(List<Staff> availableStaff) {
        this.availableStaff = availableStaff;
    }

    public List<Staff> getSelectedStaff() {
        return selectedStaff;
    }

    public void setSelectedStaff(List<Staff> selectedStaff) {
        this.selectedStaff = selectedStaff;
    }

    public void onDepartmentChange() {
        selectedSectors = new ArrayList<Sector>();
        selectedStaff = new ArrayList<Staff>(0);
        searchStaff = null;
    }

    public Date getFromAttendanceDate() {
        return fromAttendanceDate;
    }

    public void setFromAttendanceDate(Date fromAttendanceDate) {
        this.fromAttendanceDate = fromAttendanceDate;
    }

    public Date getToAttendanceDate() {
        return toAttendanceDate;
    }

    public void setToAttendanceDate(Date toAttendanceDate) {
        this.toAttendanceDate = toAttendanceDate;
    }

    public List<Sector> getSelectedSectors() {
        return selectedSectors;
    }

    public void setSelectedSectors(List<Sector> selectedSectors) {
        this.selectedSectors = selectedSectors;
    }

    public List<Department> getSelectedDepartments() {
        return selectedDepartments;
    }

    public void setSelectedDepartments(List<Department> selectedDepartments) {
        this.selectedDepartments = selectedDepartments;
    }

    public String getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(String attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public String getEntryTime() {
        return entryTime;
    }

    public void setEntryTime(String entryTime) {
        this.entryTime = entryTime;
    }

    public String getExitTime() {
        return exitTime;
    }

    public void setExitTime(String exitTime) {
        this.exitTime = exitTime;
    }

    public Attendance getDayAttendance() {
        return dayAttendance;
    }

    public void setDayAttendance(Attendance dayAttendance) {
        this.dayAttendance = dayAttendance;
    }

    public void goError(Exception ex) {
        try {
            logger.error("-----------AN ERROR HAPPENED !!!! -------------------- : " + ex.toString());
            if (sessionBean.getUsers() != null) {
                logger.error("User=" + sessionBean.getUsers().getUsername());
            }
            logger.error("Cause=" + ex.getCause());
            logger.error("Class=" + ex.getClass());
            logger.error("Message=" + ex.getLocalizedMessage());
            logger.error(ex, ex);
            logger.error("--------------------- END OF ERROR --------------------------------------------------------\n\n");

            ErrorBean errorBean = (ErrorBean) FacesUtils.getManagedBean("errorBean");
            errorBean.reset();
            errorBean.setErrorMSG(MessageBundleLoader.getMessage(sessionBean.getErrorMsgKey()));
            //FacesUtils.redirectAJAX("./templates/error.jsf?faces-redirect=true");
            FacesUtils.redirectAJAX(FacesUtils.getContextPath() + "/error.jsf?faces-redirect=true");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}