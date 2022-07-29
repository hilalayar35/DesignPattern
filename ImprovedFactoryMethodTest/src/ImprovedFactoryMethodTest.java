
public class ImprovedFactoryMethodTest {

	public static void main(String[] args) {
		traditionalFactoryMethod();

		System.out.println();
		System.out.println();
		
		improvedFactoryMethod();
	}

	// traditional Factory Method is not type-safe
	static void traditionalFactoryMethod() {
		System.out.println("Traditional Factory Method");
		System.out.println("--------------------------");
		
		TraditionalPlanFactory factory = new TraditionalPlanFactory();
		Plan plan1 = factory.getPlan("DOMESTICPLAN");
		Plan plan2 = factory.getPlan("COMMERCIALPLAN");
		Plan plan3 = factory.getPlan("INSTITUTIONALPLAN");
		Plan plan4 = factory.getPlan("DOMESTOCPLAN");
		
		try {
			int units = 10;
			plan1.calculateBill(units);
			plan2.calculateBill(units);
			plan3.calculateBill(units);
			plan4.calculateBill(units);
		}
		catch(Exception e) {
			System.out.println("Error occured with plan4! There is no such a thing as DOMESTOCPLAN");
		}
	}
	
	// improved Factory Method is type-safe
	static void improvedFactoryMethod() {
		System.out.println("Improved Factory Method");
		System.out.println("-----------------------");
		
		Plan plan1 = ImprovedPlanFactory.DomesticPlan.get();
		Plan plan2 = ImprovedPlanFactory.CommercialPlan.get();
		Plan plan3 = ImprovedPlanFactory.InstitutionalPlan.get();
		
		int units = 10;
		plan1.calculateBill(units);
		plan2.calculateBill(units);
		plan3.calculateBill(units);
	}	
}


// Improved Plan Factory
// student code ...

enum ImprovedPlanFactory
{
	

	DomesticPlan
	{
		public Plan get()
		{
			return new DomesticPlan();
		}
	},
	
	CommercialPlan
	{
		public Plan get()
		{
			return new CommercialPlan();
		}
	},
	
	InstitutionalPlan
	{
		public Plan get()
		{
			return new InstitutionalPlan();
		}
	};

	public abstract Plan get();
		

}


// Traditional Plan Factory
class TraditionalPlanFactory {
	public Plan getPlan(String planType) {
		if (planType != null) {
			switch (planType.toUpperCase()) {
			case "DOMESTICPLAN": return new DomesticPlan(); 
			case "COMMERCIALPLAN": return new CommercialPlan(); 
			case "INSTITUTIONALPLAN": return new InstitutionalPlan();
			}
		}
		
		return null;
	}
}

abstract class Plan {
	abstract double getRate();

	final public void calculateBill(int units) {
		System.out.println("Bill using " + getClass().getName() + " = " + units * getRate());
	}
}

class DomesticPlan extends Plan {
	@Override
	public double getRate() {
		return 3.50;
	}
}

class CommercialPlan extends Plan {
	@Override
	public double getRate() {
		return 7.50;
	}
}

class InstitutionalPlan extends Plan {
	@Override
	public double getRate() {
		return 5.50;
	}
}
