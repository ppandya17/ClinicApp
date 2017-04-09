package edu.stevens.cs548.clinic.domain;


public interface IProviderDAO {

	public static class ProviderExn extends Exception{
		private static final long serialVersionUID = 1L;
		public ProviderExn (String msg) {
			super(msg);
		}
	}
	
	public Provider getProviderByNPI(long npi) throws ProviderExn;
	
	public Provider getProvider(long id) throws ProviderExn;
	
	public void addProvider(Provider prov) throws ProviderExn;
	
	public void deleteProvider(Provider prov) throws ProviderExn;
	
}
