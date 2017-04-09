package edu.stevens.cs548.clinic.service.dto.util;

import edu.stevens.cs548.clinic.domain.Provider;
import edu.stevens.cs548.clinic.service.dto.util.ObjectFactory;
import edu.stevens.cs548.clinic.service.dto.provider.ProviderDto;

public class ProviderDtoFactory {

	ObjectFactory factory;

	public ProviderDtoFactory() {
		factory = new ObjectFactory();
	}

	public ProviderDto createProviderDto() {
		return factory.createProviderDto();
	}

	public ProviderDto createProviderDto(Provider prov) {
		ProviderDto d = factory.createProviderDto();
		d.setId(prov.getId());
		d.setNpi(prov.getNpi());
		d.setName(prov.getName());
		d.setSpecialization(prov.getSpecialization());
		return d;
	}

}
