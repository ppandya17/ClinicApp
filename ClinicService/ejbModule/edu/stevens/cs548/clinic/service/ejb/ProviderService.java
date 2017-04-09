package edu.stevens.cs548.clinic.service.ejb;

import java.nio.file.ProviderNotFoundException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.jms.Topic;
import javax.persistence.EntityManager;

import edu.stevens.cs548.clinic.domain.IProviderDAO.ProviderExn;
import edu.stevens.cs548.clinic.domain.ITreatmentDAO.TreatmentExn;
import edu.stevens.cs548.clinic.domain.ITreatmentExporter;
import edu.stevens.cs548.clinic.domain.Patient;
import edu.stevens.cs548.clinic.domain.PatientDAO;
import edu.stevens.cs548.clinic.domain.Provider;
import edu.stevens.cs548.clinic.domain.ProviderDAO;
import edu.stevens.cs548.clinic.domain.ProviderFactory;
import edu.stevens.cs548.clinic.domain.Treatment;
import edu.stevens.cs548.clinic.domain.TreatmentFactory;
import edu.stevens.cs548.clinic.domain.IPatientDAO;
import edu.stevens.cs548.clinic.domain.IPatientDAO.PatientExn;
import edu.stevens.cs548.clinic.domain.IProviderDAO;
import edu.stevens.cs548.clinic.service.dto.util.ObjectFactory;
import edu.stevens.cs548.clinic.service.ejb.IPatientService.PatientNotFoundExn;
import edu.stevens.cs548.clinic.service.dto.provider.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.treatment.DrugTreatmentType;
import edu.stevens.cs548.clinic.service.dto.treatment.RadiologyType;
import edu.stevens.cs548.clinic.service.dto.treatment.SurgeryType;
import edu.stevens.cs548.clinic.service.dto.treatment.TreatmentDto;

@SuppressWarnings("unused")
@Stateless(name = "ProviderServiceBean")
public class ProviderService implements IProviderServiceLocal, IProviderServiceRemote {

	private Logger logger = Logger.getLogger(ProviderService.class.getCanonicalName());

	private ProviderFactory providerFactory;

	private IProviderDAO providerDAO;
	private IPatientDAO patientDAO;

	public ProviderService() {
		providerFactory = new ProviderFactory();
	}

	@Inject
	@ClinicDomain
	EntityManager em;

	@PostConstruct
	private void initialize() {
		providerDAO = new ProviderDAO(em);
		patientDAO = new PatientDAO(em);
	}

	@Override
	public long addProvider(ProviderDto prov) throws ProviderServiceExn {
		try {
			Provider provider = providerFactory.createProvider(prov.getNpi(), prov.getName(), prov.getSpecialization());
			providerDAO.addProvider(provider);
			return provider.getId();
		} catch (ProviderExn e) {
			throw new ProviderServiceExn(e.toString());
		}
	}

	@Override
	public ProviderDto getProvider(long id) throws ProviderServiceExn {
		try {
			Provider prov = providerDAO.getProvider(id);
			return new ProviderDto(prov);
		} catch (ProviderExn e) {
			throw new ProviderServiceExn(e.toString());
		}
	}

	@Override
	public ProviderDto getProviderByNPI(long npi) throws ProviderServiceExn {
		try {
			Provider prov = providerDAO.getProviderByNPI(npi);
			return new ProviderDto(prov);
		} catch (ProviderExn e) {
			throw new ProviderServiceExn(e.toString());
		}
	}

	@Resource(mappedName = "jms/TreatmentPool")
	private ConnectionFactory treatmentConnFactory;

	@Resource(mappedName = "jmsTreatment")
	private Topic treatmentTopic;

	@Override
	public long addTreatmentForPat(TreatmentDto treatment, long patientId, long providerId)
			throws TreatmentNotFoundExn, PatientNotFoundExn, ProviderServiceExn, JMSException {
		Connection treatmentConn = null;
		try {

			Provider prov = providerDAO.getProvider(providerId);
			// long providerId=prov.getId();
			TreatmentFactory treatmentFactory = new TreatmentFactory();
			if (providerId != treatment.getProvider()) {
				throw new ProviderServiceExn("some thing wrong for provider with id = " + providerId);
			}
			if (treatment.getDrugTreatment() != null) {

				treatmentConn = treatmentConnFactory.createConnection();
				Session session = treatmentConn.createSession(true, Session.AUTO_ACKNOWLEDGE);
				MessageProducer producer = session.createProducer(treatmentTopic);

				Treatment t = treatmentFactory.createDrugTreatment(treatment.getDiagnosis(),
						treatment.getDrugTreatment().getName(), treatment.getDrugTreatment().getDosage());
				t.setProvider(prov);
				ObjectMessage message = session.createObjectMessage();
				message.setObject(treatment);
				message.setStringProperty("treatmentType", "Drug");
				producer.send(message);
				return patientDAO.getPatient(patientId).addTreatment(t);
			} else if (treatment.getSurgery() != null) {
				treatmentConn = treatmentConnFactory.createConnection();
				Session session = treatmentConn.createSession(true, Session.AUTO_ACKNOWLEDGE);
				MessageProducer producer = session.createProducer(treatmentTopic);

				Treatment t = treatmentFactory.createSurgery(treatment.getDiagnosis(),
						treatment.getSurgery().getDate());
				t.setProvider(prov);
				ObjectMessage message = session.createObjectMessage();
				message.setObject(treatment);
				message.setStringProperty("treatmentType", "Surgery");
				producer.send(message);
				return patientDAO.getPatient(patientId).addTreatment(t);
			} else if (treatment.getRadiology() != null) {
				treatmentConn = treatmentConnFactory.createConnection();
				Session session = treatmentConn.createSession(true, Session.AUTO_ACKNOWLEDGE);
				MessageProducer producer = session.createProducer(treatmentTopic);

				Treatment t = treatmentFactory.createRadiology(treatment.getDiagnosis(),
						treatment.getRadiology().getDate());
				t.setProvider(prov);
				ObjectMessage message = session.createObjectMessage();
				message.setObject(treatment);
				message.setStringProperty("treatmentType", "Radiology");
				producer.send(message);
				return patientDAO.getPatient(patientId).addTreatment(t);
			} else {
				throw new TreatmentNotFoundExn("treatment not found!");
			}
		} catch (ProviderExn e) {
			throw new ProviderServiceExn(e.toString());
		} catch (PatientExn e) {
			throw new ProviderServiceExn(e.toString());
		}

	}

	// @Resource(mappedName = "jms/TreatmentPool")
	// private ConnectionFactory treatmentConnFactory;
	//
	// @Resource(mappedName = "jmsTreatment")
	// private Topic treatmentTopic;
	//
	// Logger logger1 =
	// Logger.getLogger("edu.stevens.cs548.clinic.service.ejb");
	//
	// @Override
	// public void addDrugTreatment(long id, String diagnosis, String drug,
	// float dosage, Patient patient)
	// throws ProviderNotFoundException, PatientNotFoundExn {
	// Connection treatmentConn = null;
	// try {
	//// Patient patient = patientDAO.getPatient(id);
	// Provider provider= providerDAO.getProvider(id);
	// long tid = provider.addDrugTreatment(diagnosis, drug, dosage, patient);
	//
	// treatmentConn = treatmentConnFactory.createConnection();
	// Session session = treatmentConn.createSession(true,
	// Session.AUTO_ACKNOWLEDGE);
	// MessageProducer producer = session.createProducer(treatmentTopic);
	//
	// TreatmentDto treatment = new TreatmentDto();
	// treatment.setId(tid);
	// treatment.setPatient(id);
	// treatment.setDiagnosis(diagnosis);
	// treatment.setProvider(provider.getId());
	// DrugTreatmentType dt = new DrugTreatmentType();
	// dt.setName(drug);
	// dt.setDosage(dosage);
	//// dt.setPrescribingPhysician("Dr. Max");
	// treatment.setDrugTreatment(dt);
	// ObjectMessage message = session.createObjectMessage();
	// message.setObject(treatment);
	// message.setStringProperty("treatmentType", "Drug");
	// producer.send(message);
	// } catch (PatientExn e) {
	// throw new PatientNotFoundExn(e.toString() + "jms problem in patient
	// service");
	// } catch (JMSException e) {
	// logger1.severe("JMS Error: " + e);
	// } finally {
	// try {
	// if (treatmentConn != null)
	// treatmentConn.close();
	// } catch (JMSException e) {
	// logger1.severe("Error closing JMS connection " + e);
	// }
	//
	// }
	// }

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
	public TreatmentDto getTreatment(long id, long tid) throws TreatmentNotFoundExn, ProviderServiceExn {
		// Export treatment DTO from patient aggregate
		try {
			Provider prov = providerDAO.getProvider(id);
			TreatmentExporter visitor = new TreatmentExporter();
			return prov.exportTreatment(tid, visitor);
		} catch (ProviderExn e) {
			throw new ProviderServiceExn(e.toString());
		} catch (TreatmentExn e) {
			throw new ProviderServiceExn(e.toString());
		}
	}

	@Resource(name = "SiteInfo")
	private String siteInformation;

	@Override
	public String siteInfo() {
		return siteInformation;
	}

}
