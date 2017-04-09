package edu.stevens.cs548.clinic.billing.domain;

import edu.stevens.cs548.clinic.domain.billing.BillingRecord;

public class BillingRecordFactory implements IBillingRecordFactory {

	@Override
	public BillingRecord createBillingRecord() {
		return new BillingRecord();
	}

}
