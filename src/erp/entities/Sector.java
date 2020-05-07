package erp.entities;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;


/**
 * The persistent class for the SECTOR database table.
 * 
 */
@Entity
@NamedQuery(name="Sector.findAll", query="SELECT s FROM Sector s")
public class Sector implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="SECTOR_SECTORID_GENERATOR", sequenceName="SECTOR_SEQ")
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SECTOR_SECTORID_GENERATOR")
	private long sectorid;

	private String name;

	private BigDecimal ordered;

	//bi-directional many-to-many association to Company
	@ManyToMany(mappedBy="sectors")
	private List<Company> companies;

	//bi-directional many-to-one association to Companysector
	@OneToMany(mappedBy="sector")
	private List<Companysector> companysectors;

	//bi-directional many-to-many association to Department
	@ManyToMany
	@JoinTable(
		name="SECTORDEPARTMENT"
		, joinColumns={
			@JoinColumn(name="SECTORID")
			}
		, inverseJoinColumns={
			@JoinColumn(name="DEPARTMENTID")
			}
		)
	private List<Department> departments;

	//bi-directional many-to-one association to Sectordepartment
	@OneToMany(mappedBy="sector")
	private List<Sectordepartment> sectordepartments;

	//bi-directional many-to-one association to Staff
	@OneToMany(mappedBy="sector")
	private List<Staff> staffs;

	public Sector() {
	}

	public long getSectorid() {
		return this.sectorid;
	}

	public void setSectorid(long sectorid) {
		this.sectorid = sectorid;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getOrdered() {
		return this.ordered;
	}

	public void setOrdered(BigDecimal ordered) {
		this.ordered = ordered;
	}

	public List<Company> getCompanies() {
		return this.companies;
	}

	public void setCompanies(List<Company> companies) {
		this.companies = companies;
	}

	public List<Companysector> getCompanysectors() {
		return this.companysectors;
	}

	public void setCompanysectors(List<Companysector> companysectors) {
		this.companysectors = companysectors;
	}

	public Companysector addCompanysector(Companysector companysector) {
		getCompanysectors().add(companysector);
		companysector.setSector(this);

		return companysector;
	}

	public Companysector removeCompanysector(Companysector companysector) {
		getCompanysectors().remove(companysector);
		companysector.setSector(null);

		return companysector;
	}

	public List<Department> getDepartments() {
		return this.departments;
	}

	public void setDepartments(List<Department> departments) {
		this.departments = departments;
	}

	public List<Sectordepartment> getSectordepartments() {
		return this.sectordepartments;
	}

	public void setSectordepartments(List<Sectordepartment> sectordepartments) {
		this.sectordepartments = sectordepartments;
	}

	public Sectordepartment addSectordepartment(Sectordepartment sectordepartment) {
		getSectordepartments().add(sectordepartment);
		sectordepartment.setSector(this);

		return sectordepartment;
	}

	public Sectordepartment removeSectordepartment(Sectordepartment sectordepartment) {
		getSectordepartments().remove(sectordepartment);
		sectordepartment.setSector(null);

		return sectordepartment;
	}

	public List<Staff> getStaffs() {
		return this.staffs;
	}

	public void setStaffs(List<Staff> staffs) {
		this.staffs = staffs;
	}

	public Staff addStaff(Staff staff) {
		getStaffs().add(staff);
		staff.setSector(this);

		return staff;
	}

	public Staff removeStaff(Staff staff) {
		getStaffs().remove(staff);
		staff.setSector(null);

		return staff;
	}

}