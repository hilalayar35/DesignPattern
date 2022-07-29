import java.util.*;

public class ObserverCompositeTest {

	public static void main(String[] args) {
		// see https://tr.wikipedia.org/wiki/Kategori:T%C3%BCrk_casus_filmleri
		
		PrincipalAgency principalAgency = PrincipalAgency.getInstance();
		principalAgency.registerObserver(new Agent("007"));
		principalAgency.registerObserver(new Agent("Süper Ajan K9"));

		BranchAgency branchAgencyMarmara = new BranchAgency("Marmara");
		principalAgency.registerObserver(branchAgencyMarmara);
		branchAgencyMarmara.registerObserver(new Agent("Disi Düsman"));
		branchAgencyMarmara.registerObserver(new Agent("Casus Kiran"));

		BranchAgency branchAgencyIcAnadolu = new BranchAgency("Iç Anadolu");
		branchAgencyIcAnadolu.registerObserver(new Agent("Yakut Gözlü Kedi"));
		principalAgency.registerObserver(branchAgencyIcAnadolu);
		BranchAgency branchAgencyAnkara = new BranchAgency("Ankara");
		branchAgencyIcAnadolu.registerObserver(branchAgencyAnkara);
		branchAgencyAnkara.registerObserver(new Agent("Çiçero"));
		branchAgencyAnkara.registerObserver(new Agent("Behzat Ç."));
	
		principalAgency.sendSecretMessage("There is a message!");
		
	}

}

interface Subject {
	void registerObserver(Observer observer);
	void removeObserver(Observer observer);
	void notifyObservers();
}

interface Observer {
	void update(String secretMessage);
}

abstract class IntelligenceUnit {
	final String name;
	
	IntelligenceUnit(String name) {
		this.name = name;
	}	
	
	@Override
	public String toString() {
		return name;
	}	
}

class Agent extends IntelligenceUnit implements Observer {
	
	Agent(String codeName) {
		super(codeName);
	}
	
	@Override
	public void update(String secretMessage) {
		System.out.println(toString() + " recieved the message : " + secretMessage);
	}
	
	@Override
	public String toString() {
		return "(Agent) " + super.toString();
	}	
}

abstract class Agency extends IntelligenceUnit implements Subject {
	protected final List<Observer> subordinates = new ArrayList<>();
	private String secretMessage;
	
	Agency(String agencyName) {
		super(agencyName);
	}
	
	@Override
	public void registerObserver(Observer observer) {
		subordinates.add(observer);
	}
	
	@Override
	public void removeObserver(Observer observer) {
		subordinates.remove(observer);
	}
	
	@Override
	public void notifyObservers() {
		System.out.println(toString() + " sends the message : " + secretMessage);
		
		for (Observer observer : subordinates) {
			observer.update(secretMessage);
		}
	}
	
	void sendSecretMessage(String secretMessage) {
		this.secretMessage = secretMessage;
		
		notifyObservers();
	}	
}

class PrincipalAgency extends Agency {
	private static PrincipalAgency instance;
	
	private PrincipalAgency() {
		super("Principal Agency");
	}
	
	public static synchronized PrincipalAgency getInstance() {
		if (instance == null) {
			instance = new PrincipalAgency();
		}
		
		return instance;
	}
}

class BranchAgency extends Agency implements Observer {
	BranchAgency(String agencyName) {
		super(agencyName);
	}
	
	@Override
	public void update(String secretMessage) {
		System.out.println(toString() + " recieved the message : " + secretMessage);
		
		sendSecretMessage(secretMessage);
	}
	
	@Override
	public String toString() {
		return "(Branch Agency) " + super.toString();
	}	
}

