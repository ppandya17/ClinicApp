package edu.stevens.cs548.clinic.domain.research;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import edu.stevens.cs548.clinic.domain.DrugTreatment;

/**
 * Entity implementation class for Entity: Drug
 *
 */
@Entity
@NamedQueries(
		{ 
			@NamedQuery(name = "SearchDrugTreatmentRecords", query = "select dt from DrugTreatmentRecord dt"),
			@NamedQuery(name = "DeleteDrugTreatmentRecords", query = "delete from DrugTreatmentRecord dt") 
		}
	)
@Table(name = "DrugTreatmentRecord")
public class DrugTreatmentRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	public DrugTreatmentRecord() {
		super();
	}

	@Id
	@GeneratedValue
	private long id;

	private String drugName;

	@Temporal(TemporalType.DATE)
	private Date date;
	private float dosage;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getDrugName() {
		return drugName;
	}

	public void setDrugName(String drugName) {
		this.drugName = drugName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public float getDosage() {
		return dosage;
	}

	public void setDosage(float dosage) {
		this.dosage = dosage;
	}

	public List<DrugTreatment> getTreatments() {
		return treatments;
	}

	public void setTreatments(List<DrugTreatment> treatments) {
		this.treatments = treatments;
	}

	public Subject getSubject() {
		return subject;
	}

	public void setSubject(Subject subject) {
		this.subject = subject;
	}

	@OneToMany
	@OrderBy
	private List<DrugTreatment> treatments;
	@ManyToOne
	private Subject subject;

}
