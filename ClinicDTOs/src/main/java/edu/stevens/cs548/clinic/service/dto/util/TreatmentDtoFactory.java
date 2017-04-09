package edu.stevens.cs548.clinic.service.dto.util;

import edu.stevens.cs548.clinic.domain.DrugTreatment;
import edu.stevens.cs548.clinic.service.dto.patient.PatientDto;
import edu.stevens.cs548.clinic.service.dto.treatment.*;

public class TreatmentDtoFactory {

	ObjectFactory factory;

	public TreatmentDtoFactory() {
		factory = new ObjectFactory();
	}

	public TreatmentDto createDrugTreatmentDto() {
		TreatmentDto tdto = factory.createTreatmentDto();
		DrugTreatmentType drug = factory.createDrugTreatmentType();
		tdto.setDrugTreatment(drug);
		return tdto;
	}

	public TreatmentDto createSurgeryDto() {
		TreatmentDto tdto = factory.createTreatmentDto();
		SurgeryType surgery = factory.createSurgeryType();
		tdto.setSurgery(surgery);
		return tdto;
	}

	public TreatmentDto createRadiologyDto() {
		TreatmentDto tdto = factory.createTreatmentDto();
		RadiologyType radiology = factory.createRadiologyType();
		tdto.setRadiology(radiology);
		return tdto;
	}

	public PatientDto createTreatmentDto(DrugTreatment t) {
		return null;
	}

	/*
	 * TODO: Repeat for other treatments.
	 */

}
