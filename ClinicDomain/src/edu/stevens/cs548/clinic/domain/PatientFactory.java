package edu.stevens.cs548.clinic.domain;

import java.util.Calendar;
import java.util.Date;

public class PatientFactory implements IPatientFactory {

	@Override
	public Patient createPatient(long id, String name, Date dob, int age) throws IPatientDAO.PatientExn {
		if (age != computeAge(dob)) {
			throw new IPatientDAO.PatientExn("Age and date of birth do not match." + age + "=" + computeAge(dob));
		} else {
			Patient p = new Patient();
			p.setPatientId(id);
			p.setName(name);
			p.setBirthDate(dob);
			return p;
		}
	}

	public static int computeAge(Date dob) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(dob);
		Calendar today = Calendar.getInstance();
		int age = today.get(Calendar.YEAR) - cal.get(Calendar.YEAR);
		if (today.get(Calendar.MONTH) < cal.get(Calendar.MONTH)) {
			age--;
		} else if (today.get(Calendar.MONTH) == cal.get(Calendar.MONTH)
				&& today.get(Calendar.DAY_OF_MONTH) < cal.get(Calendar.DAY_OF_MONTH)) {
			age--;
		}
		return age;
	}

}
