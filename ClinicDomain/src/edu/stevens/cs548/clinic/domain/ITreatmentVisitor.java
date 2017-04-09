package edu.stevens.cs548.clinic.domain;

import java.util.Date;
import java.util.List;

public interface ITreatmentVisitor {

	public void visitDrugTreatment(long tid, String diagonsis, String drug, float dosage, Provider prov);

	public void visitRadiology(long tid, String diagnosis, List<Date> dates, Provider prov);

	public void visitSurgery(long tid, String diagnosis, Date date, Provider prov);

}
