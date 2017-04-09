package edu.stevens.cs548.clinic.research.domain;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

import edu.stevens.cs548.clinic.domain.research.Subject;
import edu.stevens.cs548.clinic.domain.DrugTreatment;
import edu.stevens.cs548.clinic.domain.ITreatmentDAO;
import edu.stevens.cs548.clinic.domain.ITreatmentDAO.TreatmentExn;
import edu.stevens.cs548.clinic.domain.Treatment;
import edu.stevens.cs548.clinic.domain.TreatmentDAO;
import edu.stevens.cs548.clinic.domain.research.DrugTreatmentRecord;

public class ResearchDAO implements IResearchDAO {

	public ResearchDAO(EntityManager em) {
		this.em = em;
	}

	private EntityManager em;

	@Override
	public List<DrugTreatmentRecord> getDrugTreatmentRecords() {
		TypedQuery<DrugTreatmentRecord> query = em.createNamedQuery("SearchDrugTreatmentRecords",
				DrugTreatmentRecord.class);
		return query.getResultList();
	}

	@Override
	public DrugTreatmentRecord getDrugTreatmentRecord(long id) throws ResearchExn {
		DrugTreatmentRecord t = em.find(DrugTreatmentRecord.class, id);
		if (t == null) {
			throw new ResearchExn("Missing DrugTreatmentRecord: id = " + id);
		} else {
			return t;
		}
	}

	@Override
	public void addDrugTreatmentRecord(DrugTreatmentRecord t) {
		em.persist(t);
	}

	
	public void addResearchInfo(long tid, String name, float dosage) {
		DrugTreatmentRecord dtr = new DrugTreatmentRecord();
		dtr.setDrugName(name);
		dtr.setDosage(dosage);
//		ITreatmentDAO tdao = new TreatmentDAO(em);
//		try {
//			Treatment treat = tdao.getTreatment(tid);
//			
//
//		} catch (TreatmentExn e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		em.persist(dtr);
	}

	@Override
	public void deleteDrugTreatmentRecord(long id) {
		DrugTreatmentRecord m = em.find(DrugTreatmentRecord.class, id);
		if (m != null) {
			em.remove(m);
		} else {
			throw new IllegalArgumentException("No DrugTreatmentRecord with id " + id);
		}
	}

	@Override
	public void deleteDrugTreatmentRecords() {
		Query q = em.createNamedQuery("DeleteDrugTreatmentRecords");
		q.executeUpdate();
	}

	@Override
	public Subject getSubject(long id) throws ResearchExn {
		Subject subject = em.find(Subject.class, id);
		if (subject == null) {
			throw new ResearchExn("No such subject: " + id);
		} else {
			return subject;
		}
	}

	@Override
	public void addSubject(Subject s) {
		em.persist(s);
	}

}
