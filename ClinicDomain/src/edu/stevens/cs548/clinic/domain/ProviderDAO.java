package edu.stevens.cs548.clinic.domain;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

public class ProviderDAO implements IProviderDAO {

	private EntityManager em;
	private TreatmentDAO treatmentDAO;

	public ProviderDAO(EntityManager em) {
		this.em = em;
		this.setTreatmentDAO(new TreatmentDAO(em));
	}

	public Provider getProvider(long id) throws ProviderExn {
		// TODO Auto-generated method stub
		Provider prov = em.find(Provider.class, id);
		if (prov == null) {
			throw new ProviderExn("Provider not found: primary key = " + id);
		} else {
			return prov;
		}
	}

	public Provider getProviderByNPI(long npi) throws ProviderExn {
		// TODO Auto-generated method stub
		TypedQuery<Provider> query = em.createNamedQuery("SearchProviderByNPI", Provider.class).setParameter("npi",
				npi);
		List<Provider> providers = query.getResultList();
		if (providers.size() > 1) {
			throw new ProviderExn("Duplicate patient records: " + npi);
		} else if (providers.size() < 1) {
			throw new ProviderExn("Provider not found: npi = " + npi);
		} else {
			Provider prov = providers.get(0);
			return prov;
		}
	}

	public void addProvider(Provider prov) throws ProviderExn {
		long npi = prov.getNpi();
		TypedQuery<Provider> query = em.createNamedQuery("SearchProviderByNPI", Provider.class).setParameter("npi",
				npi);
		List<Provider> providers = query.getResultList();
		if (providers.size() < 1) {
			em.persist(prov);
			prov.setTreatmentDAO(new TreatmentDAO(em));
		} else {
			throw new ProviderExn("Provider (" + npi + ") already exists.");
		}
	}

	public void deleteProvider(Provider prov) throws ProviderExn {
		em.remove(prov);
	}

	public TreatmentDAO getTreatmentDAO() {
		return treatmentDAO;
	}

	public void setTreatmentDAO(TreatmentDAO treatmentDAO) {
		this.treatmentDAO = treatmentDAO;
	}

}
