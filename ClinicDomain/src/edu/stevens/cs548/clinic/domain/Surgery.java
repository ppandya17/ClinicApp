package edu.stevens.cs548.clinic.domain;

import edu.stevens.cs548.clinic.domain.Treatment;
import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Surgery
 *
 */
@Entity
@NamedQueries({ @NamedQuery(name = "RemoveAllSurgery", query = "delete from Surgery s") })
@Table(name = "Surgery")
@DiscriminatorValue("S")

public class Surgery extends Treatment implements Serializable {

	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.DATE)
	private Date date;

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public void visit(ITreatmentVisitor visitor) {
		visitor.visitSurgery(this.getId(), this.getDiagnosis(), this.getDate(), this.getProvider());
	}

	public <T> T export(ITreatmentExporter<T> visitor) {
		return visitor.exportSurgery(this.getId(), this.getDiagnosis(), this.getDate(), this.getProvider());
	}

	public Surgery() {
		super();
		this.setTreatmentType("S");
	}

}
