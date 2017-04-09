package edu.stevens.cs548.clinic.domain;

import java.io.Serializable;
import javax.persistence.*;

/**
 * Entity implementation class for Entity: Treatment
 *
 */
/*
 * TODO
 */
@Entity
@NamedQueries({ @NamedQuery(name = "RemoveAllTreatments", query = "delete from Treatment t") })
@Inheritance(strategy = InheritanceType.JOINED)
@DiscriminatorColumn(name = "TTYPE")
@Table(name = "TREATMENT")
public abstract class Treatment implements Serializable {

	private static final long serialVersionUID = 1L;

	/*
	 * TODO
	 */
	@Id
	@GeneratedValue
	private long id;
	private String diagnosis;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	/*
	 * TODO
	 */
	@Column(name = "TTYPE", length = 2)
	private String ttype;

	public String getTreatmentType() {
		return ttype;
	}

	public void setTreatmentType(String ttype) {
		this.ttype = ttype;
	}

	public String getDiagnosis() {
		return diagnosis;
	}

	public void setDiagnosis(String diagnosis) {
		this.diagnosis = diagnosis;
	}

	/*
	 * TODO
	 */
	@ManyToOne
	@JoinColumn(name = "patient_fk", referencedColumnName = "id")
	private Patient patient;

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
		if (!patient.getTreatments().contains(this))
			patient.addTreatment(this);
	}

	@ManyToOne
	@JoinColumn(name = "provider_fk", referencedColumnName = "id")
	private Provider provider;

	public Provider getProvider() {
		return provider;
	}

	public void setProvider(Provider provider) {
		this.provider = provider;
	}

	public abstract void visit(ITreatmentVisitor visitor);

	public abstract <T> T export(ITreatmentExporter<T> visitor);

	public Treatment() {
		super();
	}

}
