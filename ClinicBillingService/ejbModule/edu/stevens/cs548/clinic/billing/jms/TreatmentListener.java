package edu.stevens.cs548.clinic.billing.jms;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.logging.Logger;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import edu.stevens.cs548.clinic.billing.domain.BillingRecordDAO;
import edu.stevens.cs548.clinic.service.dto.treatment.TreatmentDto;

/**
 * Message-Driven Bean implementation class for: TreatmentListener
 */
@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic") }, mappedName = "jmsTreatment")
public class TreatmentListener implements MessageListener {

	/**
	 * Default constructor.
	 */
	Logger logger = Logger.getLogger("edu.stevens.cs548.clinic.billing.jms");

	public TreatmentListener() {
		// TODO Auto-generated constructor stub
	}

	@PersistenceContext(unitName = "ClinicDomain")
	private EntityManager em;

	/**
	 * @see MessageListener#onMessage(Message)
	 */
	public void onMessage(Message message) {
		ObjectMessage objMessage = (ObjectMessage) message;
		try {
			TreatmentDto treatment = (TreatmentDto) objMessage.getObject();
			BillingRecordDAO tbd = new BillingRecordDAO(em);
			Random generator = new Random();
			float amount = generator.nextFloat() * 500;
			SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
			Date date = fmt.parse("2016-10-10");
			tbd.addBillingInfo(treatment.getId(), amount, date);
		} catch (JMSException e) {
			logger.severe("JMS error: " + e);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
