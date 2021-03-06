package erp.bean;

import erp.dao.StaffDAO;
import erp.entities.Department;
import erp.entities.Sector;
import erp.entities.Staff;
import erp.entities.Usr;
import erp.exception.ERPCustomException;
import erp.util.AccessControl;
import erp.util.MessageBundleLoader;
import erp.util.SystemParameters;
import java.io.Serializable;
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
@Named("dbAttendance")
@ViewScoped
public class DashboardAttendance implements Serializable {

    private static final Logger logger = LogManager.getLogger(DashboardAttendance.class);

    @Inject
    private SessionBean sessionBean;

    @Inject
    private StaffDAO staffDao;

    @Inject
    private ApplicationBean applicationBean;

    private List<Department> departments;
    private List<Department> selectedDepartments = new ArrayList<>(0);
    ;
    private List<Sector> selectedSectors = new ArrayList<>(0);
    ;

    private Date fromAttendanceDate;
    private Date toAttendanceDate;

    private List<Staff> selectedStaff = new ArrayList<>(0);

    private Staff searchStaff;
    private List<Staff> availableStaff;

    private Boolean enableDepartment;
    private Boolean enableSector;
    private Boolean enableStaff;

    private List<AttendanceBean> attendances = new ArrayList<>(0);

    private String lastExecution;

    Usr user;
    private boolean all = false;

    public void preRenderView() {
        user = sessionBean.getUsers();
        if (!AccessControl.control(user, SystemParameters.getInstance().getProperty("PAGE_ATTENDANCE_ADMIN"), null, 1)) {
            return;
        }
        sessionBean.setPageCode(SystemParameters.getInstance().getProperty("PAGE_ATTENDANCE_ADMIN"));
        sessionBean.setPageName(MessageBundleLoader.getMessage("attendancePage"));

    }

    @PostConstruct
    public void init() {
        user = sessionBean.getUsers();
        lastExecution = staffDao.getTaskLastExecutionTime(user.getCompany(), Long.parseLong(SystemParameters.getInstance().getProperty("SCHEDULE_TASK_READ_LOGGERS")));
        fromAttendanceDate = new java.util.Date();
        toAttendanceDate = new java.util.Date();
        departments = applicationBean.getDepartments();
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
            case 3:
                if (user.getStaff() != null) {
                    enableDepartment = false;
                    enableSector = false;
                    enableStaff = false;
                    selectedStaff = new ArrayList<>(0);
                    selectedStaff.add(user.getStaff());
                    break;
                }
            case 4:
                if (user.getStaff() != null) {
                    enableDepartment = true;
                    enableSector = false;
                    enableStaff = true;
                    selectedDepartments = new ArrayList<>(0);
                    selectedDepartments.addAll(user.getDepartments());
                    departments = user.getDepartments();
                    break;
                }
            case 5:
                if (user.getStaff() != null) {
                    enableSector = false;
                    enableDepartment = true;
                    enableStaff = true;
                    selectedSectors = new ArrayList<>(0);
                    selectedSectors.add(user.getSector());
                    departments = staffDao.getSectorDepartments(user.getCompany(), user.getSector());
                    selectedDepartments.addAll(departments);
                    break;
                }
            case 6:
                if (user.getStaff() != null) {
                    enableDepartment = false;
                    enableSector = false;
                    enableStaff = false;
                    selectedStaff = new ArrayList<>(0);
                    selectedStaff.add(user.getStaff());
                    break;
                }
            case 7:
                if (user.getStaff() != null) {
                    enableSector = false;
                    enableStaff = true;
                    enableDepartment = true;
                    selectedSectors = new ArrayList<>(0);
                    selectedSectors.add(user.getSector());
                    departments = staffDao.getSectorDepartments(user.getCompany(), user.getSector());
                    selectedDepartments.addAll(departments);
                    break;
                }
            default:
                if (user.getStaff() != null) {
                    enableDepartment = false;
                    enableSector = false;
                    enableStaff = false;
                    selectedStaff = new ArrayList<>(0);
                    selectedStaff.add(user.getStaff());
                    break;
                }
        }

        if (user.getDepartment() != null && (user.getDepartment().getDepartmentid() == Integer.parseInt(SystemParameters.getInstance().getProperty("itID"))
                || user.getDepartment().getDepartmentid() == Integer.parseInt(SystemParameters.getInstance().getProperty("hrID")))) { //Prosopikou/Plhroforikh
            enableDepartment = true;
            enableSector = true;
            enableStaff = true;
            selectedSectors = new ArrayList<>(0);
            selectedDepartments = new ArrayList<>(0);
            departments = applicationBean.getDepartments();

        }
    }

    @PreDestroy
    public void reset() {

    }

    public void resetSearchStaffForm() {
        selectedStaff = new ArrayList<>(0);
        selectedDepartments = new ArrayList<>(0);;
        selectedSectors = new ArrayList<>(0);;

        fromAttendanceDate = new java.util.Date();
        toAttendanceDate = new java.util.Date();
        attendances = new ArrayList<>(0);

    }

    public void onSectorChange() {
        all = false;
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
    }

    public void onDepartmentChange() {
        all = false;
        selectedSectors = new ArrayList<Sector>();
        selectedStaff = new ArrayList<Staff>(0);
        searchStaff = null;
    }

    public void autocompleteSurnameSelectStaff(SelectEvent event) {
        all = false;
        selectedDepartments = new ArrayList<>(0);
        selectedSectors = new ArrayList<>(0);
        if (!selectedStaff.contains(searchStaff)) {
            selectedStaff.add(searchStaff);
        }
        searchStaff = null;
    }

    public List<Staff> completeStaff(String surname) throws ERPCustomException {
        try {
            user = sessionBean.getUsers();
            if (surname != null && !surname.trim().isEmpty() && surname.trim().length() >= 1) {
                surname = surname.trim();
                String srnm = surname;

                if (enableSector) {
                    availableStaff = staffDao.fetchStaffAutoCompleteSurname(surname, null, null);
                } else {
                    if (user.getRole().getRoleid() == 5 || user.getRole().getRoleid() == 7) {
                        availableStaff = staffDao.fetchStaffAutoCompleteSurname(surname, user.getSector(), null);
                    } else if (user.getRole().getRoleid() == 4) {
                        availableStaff = new ArrayList<>(0);
                        user.getDepartments().forEach((temp) -> {
                            availableStaff.addAll(staffDao.fetchStaffAutoCompleteSurname(srnm, null, temp));
                        });
                    } else {
                        availableStaff = staffDao.fetchStaffAutoCompleteSurname(surname, user.getSector(), user.getDepartment());
                    }
                }

//                if (enableSector) {
//                    availableStaff = staffDao.fetchStaffAutoCompleteSurname(surname, null, null);
//                } else if (enableDepartment) {
//                    availableStaff = staffDao.fetchStaffAutoCompleteSurname(surname, user.getSector(), null);
//                } else {
//                    availableStaff = staffDao.fetchStaffAutoCompleteSurname(surname, user.getSector(), user.getDepartment());
//                }
                return availableStaff;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            sessionBean.setErrorMsgKey("errMsg_GeneralError");
            throw new ERPCustomException("Throw From Autocomplete Staff Action", e, sessionBean.getUsers(), "errMsg_GeneralError");
        }
    }

    public void removeStaff(int index) {
        if (selectedStaff != null && selectedStaff.size() > 0 && selectedStaff.size() > index) {
            selectedStaff.remove(index);
            if (user.getRole().getRoleid()!=4 && user.getRole().getRoleid()!=6) {
                //selectedDepartments.addAll(user.getDepartments());
            }
//            if (selectedStaff.size() == 0 && selectedDepartments.size() == 0 && !enableSector && !enableDepartment && enableStaff && user.getStaff() != null) {
//                selectedDepartments.add(user.getDepartment());
//            }
//            if (selectedStaff.size() == 0 && selectedDepartments.size() == 0 && !enableSector && enableDepartment && enableStaff && user.getStaff() != null) {
//                selectedDepartments.addAll(user.getSector().getDepartments());
//            }

        }
    }

    public void onAllChange() {
        if (all) {
            selectedDepartments = new ArrayList<>(0);
            selectedSectors = new ArrayList<>(0);
            selectedStaff = new ArrayList<>(0);
            searchStaff = null;
        }
    }

    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }

    public String getLastExecution() {
        return lastExecution;
    }

    public void setLastExecution(String lastExecution) {
        this.lastExecution = lastExecution;
    }

    public List<AttendanceBean> getAttendances() {
        return attendances;
    }

    public void setAttendances(List<AttendanceBean> attendances) {
        this.attendances = attendances;
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
}

//if (user.getStaff() != null) {
//                    switch ((int) user.getStaff().getEmprank().getRankid()) {
//                        case 15: //YPALLHLOI
//                        case 16:
//                        case 17:
//                        case 6:
//                        case 7:
//                        case 8: {
//                            enableDepartment = false;
//                            enableSector = false;
//                            enableStaff = false;
//                            selectedStaff = new ArrayList<>(0);
//                            selectedStaff.add(user.getStaff());
//                            break;
//                        }
//                        case 3: //DIEYYHYNTES
//                        case 14:
//                        case 13:
//                        case 11: {
//                            enableDepartment = true;
//                            enableSector = false;
//                            enableStaff = true;
//                            selectedSectors = new ArrayList<>(0);
//                            selectedSectors.add(user.getStaff().getSector());
//                            departments = staffDao.getSectorDepartments(user.getCompany(), user.getStaff().getSector());
//                            break;
//                        }
//                        case 12://PROISTAMENOI
//                        case 10:
//                        case 9: {
//                            enableDepartment = false;
//                            enableSector = false;
//                            enableStaff = true;
//                            selectedDepartments = new ArrayList<>(0);
//                            selectedDepartments.add(user.getStaff().getDepartment());
//                            break;
//                        }
//                    }
//                }
