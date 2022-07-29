import java.util.*;

public class CompositeFoodMenuTest {

	public static void main(String[] args) {
		FoodMenu dinnerMenu = new FoodMenu("Dinner menu for 4 May 2020"); 
		dinnerMenu.addFood(new Karniyarik());		// Karniyarik is 135 calories
		dinnerMenu.addFood(new GenericFood("Salad of the day", 50));
		dinnerMenu.addFood(new GenericFood("Special Soup of Chief", 75));
		
		FoodMenu dessertMenu = new FoodMenu("Dessert menu");
		dessertMenu.addFood(new GenericFood("Dessert of the day", 220));
		dessertMenu.addFood(new TurkishCoffee());	// Turkish coffee is only 5 calories
		
		FoodMenu arabicDessertMenu = new FoodMenu("Arabic dessert menu");
		arabicDessertMenu.addFood(new GenericFood("Baklava", 300));
		arabicDessertMenu.addFood(new GenericFood("Iran syrup", 70));
		
		dessertMenu.addFood(arabicDessertMenu);
		dinnerMenu.addFood(dessertMenu);
		
		System.out.println(dinnerMenu);			// calls toString() method

		System.out.println("Total calorie of " + dinnerMenu.name + " is " + dinnerMenu.getCalorie());
		System.out.println("Number of menus in " + dinnerMenu.name + " is " + dinnerMenu.getCountOfFood(FoodMenu.class));
		System.out.println("Number of generic foods in " + dinnerMenu.name + " is " + dinnerMenu.getCountOfFood(GenericFood.class));		
	}

}

abstract class Food {
	final String name;

	Food(String name) {
		this.name = name;
	}
	
	abstract double getCalorie();
	
	int getCountOfFood(Class classType) {
		return (this.getClass() ==  classType ? 1 : 0);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof Food) {
			Food foodObj = (Food)obj;
			return name.equals(foodObj.name);
		}
		else
			return false;
	}
	
	@Override
	public String toString() {
		return name + " (calorie = " + getCalorie() + ") \n";
	}
}

class GenericFood extends Food {
	private final double calorie;
	
	GenericFood(String name, double calorie) {
		super(name);
		this.calorie = calorie;
	}
	
	@Override
	double getCalorie() {
		return calorie;
	}
}

class Karniyarik extends Food {
	
	Karniyarik() {
		super("Karniyarik");
	}
	
	@Override
	double getCalorie() {
		return 135.0;
	}
}

class TurkishCoffee extends Food {
	
	TurkishCoffee() {
		super("Türk Kahvesi");
	}
	
	@Override
	double getCalorie() {
		return 5.0;
	}
}

class FoodMenu extends Food {
	private List<Food> foods = new ArrayList<Food>();

	FoodMenu(String name) {
		super(name);
	}
	
	void addFood(Food food) {
		foods.add(food);
	}
	
	void removeFood(Food food) {
		foods.remove(food);
	}
	
	@Override
    double getCalorie() {
		double calorie = 0.0;
		for (Food food : foods) {
			calorie += food.getCalorie();
		}		
		return calorie;
    }
	
	@Override
	int getCountOfFood(Class classType) {
		int count = super.getCountOfFood(classType);
		for (Food food : foods) {
			count += food.getCountOfFood(classType);
		}		
		return count;		
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("* " + name + "\n");
		for (Food food : foods) {
			sb.append(food);
		}		
		
		return sb.toString();
	}	
	
}

