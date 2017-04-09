package edu.stevens.cs548.clinic.service.ejb;

import edu.stevens.cs548.clinic.domain.Provider;
import edu.stevens.cs548.clinic.service.dto.patient.PatientDto;
import edu.stevens.cs548.clinic.service.dto.treatment.TreatmentDto;

public interface IPatientService {

	public class PatientServiceExn extends Exception {
		private static final long serialVersionUID = 1L;

		public PatientServiceExn(String m) {
			super(m);
		}
	}

	public class PatientNotFoundExn extends PatientServiceExn {
		private static final long serialVersionUID = 1L;

		public PatientNotFoundExn(String m) {
			super(m);
		}
	}

	public class TreatmentNotFoundExn extends PatientServiceExn {
		private static final long serialVersionUID = 1L;

		public TreatmentNotFoundExn(String m) {
			super(m);
		}
	}

	public long addPatient(PatientDto dto) throws PatientServiceExn;

	public PatientDto getPatient(long id) throws PatientServiceExn;

	public PatientDto getPatientByPatId(long pid) throws PatientServiceExn;

	public TreatmentDto getTreatment(long id, long tid)
			throws PatientNotFoundExn, TreatmentNotFoundExn, PatientServiceExn;

	public String siteInfo();

	void addDrugTreatment(long id, String diagnosis, String drug, float dosage, Provider provider)
			throws PatientNotFoundExn;

}
