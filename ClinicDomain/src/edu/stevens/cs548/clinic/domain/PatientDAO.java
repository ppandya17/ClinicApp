package edu.stevens.cs548.clinic.domain;

import java.util.List;
import java.util.logging.Logger;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

public class PatientDAO implements IPatientDAO {

	private EntityManager em;

	public PatientDAO(EntityManager em) {
		this.em = em;
		new TreatmentDAO(em);
	}

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(PatientDAO.class.getCanonicalName());

	@Override
	public long addPatient(Patient patient) throws PatientExn {
		long pid = patient.getPatientId();
		Query query = em.createNamedQuery("CountPatientByPatientID").setParameter("pid", pid);
		Long numExisting = (Long) query.getSingleResult();
		if (numExisting < 1) {
			em.persist(patient);
			patient.setTreatmentDAO(new TreatmentDAO(em));
			return pid;
		} else {
			throw new PatientExn("Patient with patient id (" + pid + ") already exists.");
		}
	}

	@Override
	public Patient getPatient(long id) throws PatientExn {

		Patient patient = em.find(Patient.class, id);
		if (patient == null) {
			throw new PatientExn("Patient not found: primary key = " + id);
		} else {
			patient.setTreatmentDAO(new TreatmentDAO(em));
			return patient;
		}
	}

	@Override
	public Patient getPatientByPatientId(long pid) throws PatientExn {

		TypedQuery<Patient> query = em.createNamedQuery("SearchPatientByPatientID", Patient.class).setParameter("pid",
				pid);
		List<Patient> patients = query.getResultList();
		if (patients.size() > 1)
			throw new PatientExn("Duplicate patient records: patient id = " + pid);
		else if (patients.size() < 1)
			throw new PatientExn("Patient not found: patient id = " + pid);
		else {
			Patient p = patients.get(0);
			p.setTreatmentDAO(new TreatmentDAO(em));
			return p;
		}
	}

	@Override
	public void deletePatients() {

		Query deleteDrugTreatment = em.createNamedQuery("RemoveAllDrugTreatments", DrugTreatment.class);
		deleteDrugTreatment.executeUpdate();
		Query deleteSurgery = em.createNamedQuery("RemoveAllSurgery", Surgery.class);
		deleteSurgery.executeUpdate();
		Query deleteRadiology = em.createNamedQuery("RemoveAllRadiology", Radiology.class);
		deleteRadiology.executeUpdate();
		Query deleteTreatment = em.createNamedQuery("RemoveAllTreatments", Treatment.class);
		deleteTreatment.executeUpdate();
		Query deleteProvider = em.createNamedQuery("RemoveAllProviders", Provider.class);
		deleteProvider.executeUpdate();
		Query update = em.createNamedQuery("RemoveAllPatients", Patient.class);
		update.executeUpdate();
	}

}
