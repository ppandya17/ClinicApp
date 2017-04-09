package edu.stevens.cs548.clinic.test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.persistence.EntityManager;

import edu.stevens.cs548.clinic.billing.domain.BillingRecordDAO;
import edu.stevens.cs548.clinic.billing.domain.IBillingRecordDAO;
import edu.stevens.cs548.clinic.domain.IPatientDAO;
import edu.stevens.cs548.clinic.domain.IPatientDAO.PatientExn;
import edu.stevens.cs548.clinic.domain.IProviderDAO;
import edu.stevens.cs548.clinic.domain.IProviderDAO.ProviderExn;
import edu.stevens.cs548.clinic.domain.ITreatmentDAO;
import edu.stevens.cs548.clinic.domain.Patient;
import edu.stevens.cs548.clinic.domain.PatientDAO;
import edu.stevens.cs548.clinic.domain.PatientFactory;
import edu.stevens.cs548.clinic.domain.Provider;
import edu.stevens.cs548.clinic.domain.ProviderDAO;
import edu.stevens.cs548.clinic.domain.ProviderFactory;
import edu.stevens.cs548.clinic.domain.Treatment;
import edu.stevens.cs548.clinic.domain.TreatmentDAO;
import edu.stevens.cs548.clinic.domain.TreatmentFactory;
import edu.stevens.cs548.clinic.research.domain.IResearchDAO;
import edu.stevens.cs548.clinic.research.domain.ResearchDAO;
import edu.stevens.cs548.clinic.service.dto.patient.PatientDto;
import edu.stevens.cs548.clinic.service.dto.provider.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.treatment.DrugTreatmentType;
import edu.stevens.cs548.clinic.service.dto.treatment.TreatmentDto;
import edu.stevens.cs548.clinic.service.dto.util.PatientDtoFactory;
import edu.stevens.cs548.clinic.service.dto.util.ProviderDtoFactory;
import edu.stevens.cs548.clinic.service.dto.util.TreatmentDtoFactory;
import edu.stevens.cs548.clinic.service.ejb.ClinicDomain;
import edu.stevens.cs548.clinic.service.ejb.IPatientService.PatientServiceExn;
import edu.stevens.cs548.clinic.service.ejb.IPatientServiceLocal;
import edu.stevens.cs548.clinic.service.ejb.IProviderService.ProviderServiceExn;
import edu.stevens.cs548.clinic.service.ejb.IProviderServiceLocal;

/**
 * Session Bean implementation class TestBean
 */
@Singleton
@LocalBean
@Startup
public class InitBean {

	private static Logger logger = Logger.getLogger(InitBean.class.getCanonicalName());

	/**
	 * Default constructor.
	 */
	public InitBean() {
	}

	private Date setDate(String stringDate) {
		Date date = null;
		try {
			String pattern = "MM/dd/yyyy";
			SimpleDateFormat format = new SimpleDateFormat(pattern);
			date = format.parse(stringDate);
		} catch (Exception e) {
			IllegalStateException ex = new IllegalStateException("Error while setting state");
			ex.initCause(e);
			throw ex;
		}
		return date;
	}
	
	@Inject
	@ClinicDomain
	EntityManager em;

	@Inject
	IPatientServiceLocal patientService;
	@Inject
	IProviderServiceLocal providerService;

	@PostConstruct
	private void init() {

		logger.info("Your name here: Parth Pandya");
		
			try {

				
				Calendar calendar = Calendar.getInstance();
				calendar.set(1989, 4, 2);
				
				IBillingRecordDAO billingDAO=new BillingRecordDAO(em);
				billingDAO.deleteBillingRecords();
				
				IResearchDAO researchDAO=new ResearchDAO(em);
				researchDAO.deleteDrugTreatmentRecords();
				
				IPatientDAO patientDAO = new PatientDAO(em);
				IProviderDAO providerDAO = new ProviderDAO(em);
				@SuppressWarnings("unused")
				ITreatmentDAO treatmentDAO = new TreatmentDAO(em);

				PatientFactory patientFactory = new PatientFactory();
				ProviderFactory providerFactory = new ProviderFactory();
				TreatmentFactory treatmentFactory = new TreatmentFactory();

				patientDAO.deletePatients();

				Patient Malav = patientFactory.createPatient(1150, "Malav Shah", setDate("03/01/1992"), 24);
				patientDAO.addPatient(Malav);
				
				Patient Deep = patientFactory.createPatient(1155, "Deep Bhalavat", setDate("01/10/1990"), 26);
				patientDAO.addPatient(Deep);
				
				
				logger.info("Added patient " + Malav.getName() + " with id " + Malav.getId());
				logger.info("Added patient " + Deep.getName() + " with id " + Deep.getId());

				Provider Rahul = providerFactory.createProvider(11, "Rahul", "Surgery");
				providerDAO.addProvider(Rahul);

				Provider Amol = providerFactory.createProvider(22, "Amol", "Radiology");
				providerDAO.addProvider(Amol);

				
				logger.info("Added provider " + Rahul.getName() + " with id " + Rahul.getId());
				logger.info("Added provider " + Amol.getName() + " with id " + Amol.getId());
				
				Treatment drugTreatment = treatmentFactory.createDrugTreatment("ICU", "Vicks", 12);
				drugTreatment.setProvider(Rahul);

				Treatment surgeryTreatment = treatmentFactory.createSurgery("surgery", setDate("01/10/2014"));
				surgeryTreatment.setProvider(Rahul);

				Malav.addTreatment(drugTreatment);
				Malav.addTreatment(surgeryTreatment);
				
				logger.info("Added treatment " + drugTreatment.getTreatmentType() + "  with id " + drugTreatment.getId() + " to "
						+ drugTreatment.getPatient().getName());			
				logger.info("Added treatment " + surgeryTreatment.getTreatmentType() + "  with id " + surgeryTreatment.getId() + " to "
						+ surgeryTreatment.getPatient().getName());

				PatientDtoFactory patientdtoFactory = new PatientDtoFactory();
				PatientDto patientdto = patientdtoFactory.createPatientDto();
				patientdto.setName("Robin");
				patientdto.setPatientId(1245);
				patientdto.setDob(calendar.getTime());
				patientdto.setAge(27);

				patientService.addDrugTreatment(Malav.getId(), "APlastic Animiya", "Crocine", 2, Rahul);

				long patientId = patientService.addPatient(patientdto);
				String patientdtoName = patientService.getPatient(patientId).getName();
				long patientdtoId = patientService.getPatient(patientId).getId();
				logger.info("Added patient " + patientdtoName + " with id " + patientdtoId);

				ProviderDtoFactory proFac = new ProviderDtoFactory();
				ProviderDto Ted = proFac.createProviderDto();
				Ted.setName("Ted Mosby");
				Ted.setNpi(1215);
				Ted.setSpecialization("stomach ache");
				long proId = providerService.addProvider(Ted);

				String TedName = providerService.getProvider(proId).getName();
				long TedId = providerService.getProvider(proId).getId();
				logger.info("Added provider " + TedName + " with id " + TedId);

				TreatmentDtoFactory treatFac = new TreatmentDtoFactory();

				TreatmentDto treatDto = treatFac.createDrugTreatmentDto();
				treatDto.setDiagnosis("Stone");
				treatDto.setPatient(patientdtoId);
				treatDto.setProvider(TedId);
				
				DrugTreatmentType drugTreatmentT = new DrugTreatmentType();
				drugTreatmentT.setName("pain");
				drugTreatmentT.setDosage(25);
				treatDto.setDrugTreatment(drugTreatmentT);
				treatDto.setId(655);
				
				long trmtId = treatDto.getId();
				logger.info("Treatment Id before function call: " + trmtId);
				try {
					trmtId = providerService.addTreatmentForPat(treatDto, patientId, TedId);
				} catch (JMSException e) {
					logger.severe("Jms error");
					e.printStackTrace();
				}

				logger.info("patient's Id:" + patientId + " and provider's NPI:" + providerService.getProvider(proId).getNpi()
						+ " add drug treatment with id:" + trmtId);

				String diag = patientService.getTreatment(patientId, trmtId).getDiagnosis();
				logger.info("new treatment diagnosis is " + diag);
			} catch (ProviderExn | ProviderServiceExn e) {
				throw new IllegalStateException("Failed to add provider");
			} catch (PatientServiceExn | PatientExn e) {
				throw new IllegalStateException("Failed to add patient");
			}
	}
	
	
	
	
}