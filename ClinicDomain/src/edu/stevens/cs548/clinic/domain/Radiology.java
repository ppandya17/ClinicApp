package edu.stevens.cs548.clinic.domain;

import edu.stevens.cs548.clinic.domain.Treatment;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.*;

/**
 * Entity implementation class for Entity: Radiology
 *
 */
@Entity
@NamedQueries({ @NamedQuery(name = "RemoveAllRadiology", query = "delete from Radiology r") })
@Table(name = "Radiology")
@DiscriminatorValue("R")

public class Radiology extends Treatment implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<Date> dates;

	public List<Date> getDates() {
		return dates;
	}

	public void setDates(List<Date> dates) {
		this.dates = dates;
	}

	public void visit(ITreatmentVisitor visitor) {
		visitor.visitRadiology(this.getId(), this.getDiagnosis(), this.getDates(), this.getProvider());
	}

	public <T> T export(ITreatmentExporter<T> visitor) {
		return visitor.exportRadiology(this.getId(), this.getDiagnosis(), this.getDates(), this.getProvider());
	}

	public Radiology() {
		super();
		this.setTreatmentType("R");
	}

}
