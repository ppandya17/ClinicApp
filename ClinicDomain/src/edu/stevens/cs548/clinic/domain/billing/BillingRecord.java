package edu.stevens.cs548.clinic.domain.billing;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import edu.stevens.cs548.clinic.domain.Treatment;

/**
 * Entity implementation class for Entity: TreatmentBilling
 *
 */
@Entity
@NamedQueries
		(
			{ 
				@NamedQuery(name = "SearchBillingRecords", query = "select b from BillingRecord b"),
				@NamedQuery(name = "DeleteBillingRecords", query = "delete from BillingRecord b") 
			}
		)
@Table(name = "BillingRecord")
public class BillingRecord implements Serializable {

	private static final long serialVersionUID = 1L;

	public BillingRecord() {
		super();
	}

	@Id
	@GeneratedValue
	private long id;

	private float amount;

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@OneToOne
	@PrimaryKeyJoinColumn
	private Treatment treatments;

	@Temporal(TemporalType.DATE)
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public float getAmount() {
		return amount;
	}

	public void setAmount(float amount) {
		this.amount = amount;
	}

	public Treatment getTreatments() {
		return treatments;
	}

	public void setTreatments(Treatment treatments) {
		this.treatments = treatments;
	}

}
