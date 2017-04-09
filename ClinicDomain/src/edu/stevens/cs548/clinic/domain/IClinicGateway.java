package edu.stevens.cs548.clinic.domain;

public interface IClinicGateway {

	public IPatientFactory gePatientFaceory();
	
	public IPatientDAO getPatientDAO();
	
}
