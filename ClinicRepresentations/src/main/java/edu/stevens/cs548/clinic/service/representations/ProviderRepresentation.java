package edu.stevens.cs548.clinic.service.representations;

import java.util.List;

import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import javax.xml.bind.annotation.XmlRootElement;

import edu.stevens.cs548.clinic.service.dto.provider.ProviderDto;
import edu.stevens.cs548.clinic.service.dto.util.ProviderDtoFactory;
import edu.stevens.cs548.clinic.service.web.rest.data.LinkType;
import edu.stevens.cs548.clinic.service.web.rest.data.ProviderType;

@XmlRootElement
public class ProviderRepresentation extends ProviderType {

	public List<LinkType> getLinksTreatments() {
		return this.getTreatments();
	}

	public static LinkType getProviderLink(long id, UriInfo uriInfo) {
		UriBuilder ub = uriInfo.getBaseUriBuilder();
		ub.path("provider").path("{id}");
		String providerURI = ub.build(Long.toString(id)).toString();

		LinkType link = new LinkType();
		link.setUrl(providerURI);
		link.setRelation(Representation.RELATION_PROVIDER);
		link.setMediaType(Representation.MEDIA_TYPE);
		return link;
	}

	private ProviderDtoFactory providerDtoFactory;

	public ProviderRepresentation() {
		super();
		this.providerDtoFactory = new ProviderDtoFactory();
	}

	public ProviderRepresentation(ProviderDto dto, UriInfo uriInfo) {
		this();
		this.id = getProviderLink(dto.getId(), uriInfo);
		this.npi = dto.getNpi();
		this.name = dto.getName();
		this.specialization = dto.getSpecialization();
		List<LinkType> links = this.getTreatments();
		for (long t : dto.getTreatments()) {
			links.add(TreatmentRepresentation.getTreatmentLink(t, uriInfo));
		}
	}

	public ProviderDto getProviderDto() {
		ProviderDto prov = providerDtoFactory.createProviderDto();
		prov.setId(Representation.getId(this.id));
		prov.setNpi(this.npi);
		prov.setName(this.name);
		prov.setSpecialization(this.specialization);
		return prov;
	}

}
