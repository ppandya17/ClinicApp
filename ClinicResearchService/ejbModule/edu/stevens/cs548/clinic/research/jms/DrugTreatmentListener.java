package edu.stevens.cs548.clinic.research.jms;

import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import edu.stevens.cs548.clinic.research.domain.ResearchDAO;
import edu.stevens.cs548.clinic.service.dto.treatment.TreatmentDto;

/**
 * Message-Driven Bean implementation class for: DrugTreatmentListener
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
		@ActivationConfigProperty(propertyName = "messageSelector", propertyValue = "treatmentType='Drug'") }, mappedName = "jmsTreatment")
public class DrugTreatmentListener implements MessageListener {

	/**
	 * Default constructor.
	 */
	Logger logger = Logger.getLogger("edu.stevens.cs548.clinic.service.research");

	public DrugTreatmentListener() {
		// TODO Auto-generated constructor stub
	}

	@PersistenceContext(unitName = "ClinicDomain")
	private EntityManager em;

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		// TODO Auto-generated method stub
		ObjectMessage objMessage = (ObjectMessage) message;
		try {
			TreatmentDto treatment = (TreatmentDto) objMessage.getObject();
			if (treatment.getDrugTreatment() != null) {
				ResearchDAO rdao = new ResearchDAO(em);
				rdao.addResearchInfo(treatment.getId(), treatment.getDrugTreatment().getName(), treatment.getDrugTreatment().getDosage());
			} else
				logger.severe("JMS null treatment error");
		} catch (JMSException e) {
			logger.severe("JMS error: " + e);
		}
	}

}
