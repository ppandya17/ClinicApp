package edu.stevens.cs548.clinic.service.ejb;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;

import edu.stevens.cs548.clinic.domain.IPatientDAO;
import edu.stevens.cs548.clinic.domain.IPatientDAO.PatientExn;
import edu.stevens.cs548.clinic.domain.ITreatmentDAO.TreatmentExn;
import edu.stevens.cs548.clinic.domain.ITreatmentExporter;
import edu.stevens.cs548.clinic.domain.Patient;
import edu.stevens.cs548.clinic.domain.PatientDAO;
import edu.stevens.cs548.clinic.domain.PatientFactory;
import edu.stevens.cs548.clinic.domain.Provider;
import edu.stevens.cs548.clinic.service.dto.patient.PatientDto;
import edu.stevens.cs548.clinic.service.dto.treatment.*;
import edu.stevens.cs548.clinic.service.dto.util.ObjectFactory;
import edu.stevens.cs548.clinic.service.dto.util.PatientDtoFactory;

/**
 * Session Bean implementation class PatientService
 */
@Stateless(name = "PatientServiceBean")
public class PatientService implements IPatientServiceLocal, IPatientServiceRemote {

	@SuppressWarnings("unused")
	private Logger logger = Logger.getLogger(PatientService.class.getCanonicalName());

	private PatientFactory patientFactory;

	@SuppressWarnings("unused")
	private PatientDtoFactory patientDtoFactory;

	private IPatientDAO patientDAO;

	/**
	 * Default constructor.
	 */
	public PatientService() {
		// TODO initialize factories
		patientFactory = new PatientFactory();
	}

	@Inject
	@ClinicDomain
	EntityManager em;

	@PostConstruct
	private void initialize() {
		patientDAO = new PatientDAO(em);
	}

	@Override
	public long addPatient(PatientDto dto) throws PatientServiceExn {
		// Use factory to create patient entity, and persist with DAO
		try {
			Patient patient = patientFactory.createPatient(dto.getPatientId(), dto.getName(), dto.getDob(),
					dto.getAge());
			patientDAO.addPatient(patient);
			return patient.getId();
		} catch (PatientExn e) {
			throw new PatientServiceExn(e.toString());
		}
	}

	@Override
	public PatientDto getPatient(long id) throws PatientServiceExn {
		try {
			Patient patient = patientDAO.getPatient(id);
			return new PatientDto(patient);
		} catch (PatientExn e) {
			throw new PatientServiceExn(e.toString());
		}
	}

	@Override
	public PatientDto getPatientByPatId(long pid) throws PatientServiceExn {
		try {
			Patient patient = patientDAO.getPatientByPatientId(pid);
			return new PatientDto(patient);
		} catch (PatientExn e) {
			throw new PatientServiceExn(e.toString());
		}
	}

	@Resource(mappedName = "jms/TreatmentPool")
	private ConnectionFactory treatmentConnFactory;

	@Resource(mappedName = "jmsTreatment")
	private Topic treatmentTopic;

	Logger logger1 = Logger.getLogger("edu.stevens.cs548.clinic.service.ejb");

	@Override
	public void addDrugTreatment(long id, String diagnosis, String drug, float dosage, Provider provider)
			throws PatientNotFoundExn {
		Connection treatmentConn = null;
		try {
			Patient patient = patientDAO.getPatient(id);
			long tid = patient.addDrugTreatment(diagnosis, drug, dosage, provider);
			logger1.info("drug treatment of patient service added, jms function incoming");
			treatmentConn = treatmentConnFactory.createConnection();
			Session session = treatmentConn.createSession(true, Session.AUTO_ACKNOWLEDGE);
			MessageProducer producer = session.createProducer(treatmentTopic);

			TreatmentDto treatment = new TreatmentDto();
			treatment.setId(tid);
			treatment.setPatient(id);
			treatment.setDiagnosis(diagnosis);
			treatment.setProvider(provider.getId());
			DrugTreatmentType dt = new DrugTreatmentType();
			dt.setName(drug);
			dt.setDosage(dosage);
			// dt.setPrescribingPhysician("Dr. Max");
			treatment.setDrugTreatment(dt);
			ObjectMessage message = session.createObjectMessage();
			message.setObject(treatment);
			message.setStringProperty("treatmentType", "Drug");
			producer.send(message);
		} catch (PatientExn e) {
			throw new PatientNotFoundExn(e.toString() + "jms problem in patient service");
		} catch (JMSException e) {
			logger1.severe("JMS Error: " + e);
		} finally {
			try {
				if (treatmentConn != null)
					treatmentConn.close();
			} catch (JMSException e) {
				logger1.severe("Error closing JMS connection " + e);
			}

		}
	}

	public class TreatmentExporter implements ITreatmentExporter<TreatmentDto> {

		private ObjectFactory factory = new ObjectFactory();

		@Override
		public TreatmentDto exportDrugTreatment(long tid, String diagnosis, String drug, float dosage, Provider prov) {
			TreatmentDto dto = factory.createTreatmentDto();
			dto.setDiagnosis(diagnosis);
			dto.setId(tid);
			dto.setProvider(prov.getId());
			DrugTreatmentType drugInfo = factory.createDrugTreatmentType();
			drugInfo.setDosage(dosage);
			drugInfo.setName(drug);
			dto.setDrugTreatment(drugInfo);
			return dto;
		}

		@Override
		public TreatmentDto exportRadiology(long tid, String diagnosis, List<Date> dates, Provider prov) {
			TreatmentDto dto = factory.createTreatmentDto();
			dto.setDiagnosis(diagnosis);
			dto.setId(tid);
			dto.setProvider(prov.getId());
			RadiologyType radiology = factory.createRadiologyType();
			radiology.setDate(dates);
			dto.setRadiology(radiology);
			return dto;
		}

		@Override
		public TreatmentDto exportSurgery(long tid, String diagnosis, Date date, Provider prov) {
			TreatmentDto dto = factory.createTreatmentDto();
			dto.setDiagnosis(diagnosis);
			dto.setId(tid);
			dto.setProvider(prov.getId());
			SurgeryType surgery = new SurgeryType();
			surgery.setDate(date);
			dto.setSurgery(surgery);
			return dto;
		}

	}

	@Override
	public TreatmentDto getTreatment(long id, long tid)
			throws PatientNotFoundExn, TreatmentNotFoundExn, PatientServiceExn {
		try {
			Patient patient = patientDAO.getPatient(id);
			TreatmentExporter visitor = new TreatmentExporter();
			return patient.exportTreatment(tid, visitor);
		} catch (PatientExn e) {
			throw new PatientNotFoundExn(e.toString());
		} catch (TreatmentExn e) {
			throw new PatientServiceExn(e.toString());
		}
	}

	@Resource(name = "SiteInfo")
	private String siteInformation;

	@Override
	public String siteInfo() {
		return siteInformation;
	}

}
