import java.util.*;

// https://www.youtube.com/watch?v=MGEx35FjBuo
// https://www.coursera.org/lecture/design-patterns/2-2-3-state-pattern-ggJze
// https://www.journaldev.com/1751/state-design-pattern-java

public class StatePattern {

	public static void main(String[] args) {
		// elevator test case
		int minLevel = -2;
		int maxLevel = 10;
		Elevator elevator = new Elevator(minLevel, maxLevel);

		System.out.println(elevator + "\n");
		
		// people who want to use elevator (name, surname, current level, target level)		
		elevator.elevatorWaitQueue.add(new ElevatorUser(new Person("Anas", "Abugofa"), 2, 4));
		elevator.elevatorWaitQueue.add(new ElevatorUser(new Person("Büsra", "Kaya"), 5, 0));
		elevator.elevatorWaitQueue.add(new ElevatorUser(new Person("Murat Can", "Yilmaz"), 3, 9));
		elevator.elevatorWaitQueue.add(new ElevatorUser(new Person("Okan", "Ulker"), 0, 8));
		
		// people call the elevator almost at the same time
		for (ElevatorUser elevatorUser : elevator.elevatorWaitQueue) {
			elevator.callToLevel(elevatorUser.currentLevel);
			System.out.println(elevatorUser.person + " calls elevator from level " + elevatorUser.currentLevel);			
		}
		System.out.println();

		System.out.println(elevator + "\n");
		
		// continue if 
		//   there are people waiting for the elevator
		//   or there is at least one person in the elevator
		while (elevator.isBusy()) {			
			elevator.proceed();		// calls the state.handle
			
			System.out.println(elevator + "\n");
		}	
	}

}

class ElevatorUser {
	final Person person;
	final int currentLevel;
	final int targetLevel;
	
	ElevatorUser(Person person, int currentLevel, int targetLevel) {
		this.person = person;
		this.currentLevel = currentLevel;
		this.targetLevel = targetLevel;		
	}
	
	@Override 
	public String toString() {
		return person + " goes to " + targetLevel;
	}	
}

class Person {
	final String name;
	final String surname;
	
	Person(String name, String surname) {
		this.name = name;
		this.surname = surname;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof Person) { 
			Person person = (Person)obj;
			return name.equals(person.name) && surname.equals(person.surname);
		}
		else
			return false;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, surname);
	}
	
	@Override 
	public String toString() {
		return name + " " + surname;
	}	
}


// State interface (abstract)
interface ElevatorState {
	void handle();				// instead of single method you can also create different methods for different tasks
}

// Context class
class Elevator {
	private ElevatorState idleState = new IdleState();
	private ElevatorState preparingToMoveUpState = new PreparingToMoveUpState();
	private ElevatorState preparingToMoveDownState = new PreparingToMoveDownState();
	private ElevatorState movingToFloorState = new MovingToFloorState();
	private ElevatorState checkingNextDestinationState = new CheckingNextDestinationState();
	private ElevatorState state = idleState;		// state
	
	final int minLevel;
	final int maxLevel;
	private int currentLevel;
	private int elevatorDirection = 0;
	private Set<Integer> requestedLevels = new TreeSet<>();

	final List<ElevatorUser> elevatorWaitQueue = new LinkedList<>();
	final List<ElevatorUser> users = new ArrayList<>();
	
	Elevator(int maxLevel) {
		this.minLevel = 0;
		this.maxLevel = maxLevel;
		
		currentLevel = getFloorLevel(); 
	}

	Elevator(int minLevel, int maxLevel) {
		this.minLevel = minLevel;
		this.maxLevel = maxLevel;
		
		currentLevel = getFloorLevel();
	}
	
	boolean isBusy() {
		return (!elevatorWaitQueue.isEmpty() || hasPeople() || state != idleState);
	}
	
	public int getFloorLevel() {
		return Math.max(0, minLevel);
	}
	
	int getCurrentLevel() {
		return currentLevel;
	}
	
	// a person can call the elevator independent from its current state
	void callToLevel(int level) {
		requestedLevels.add(level);
	}
	
	void getIn() {
		Iterator<ElevatorUser> iterator = elevatorWaitQueue.iterator();
		while (iterator.hasNext()) {
			ElevatorUser elevatorUser = iterator.next();
			
			if (getIn(elevatorUser)) {
				iterator.remove();
			}
		}						
	}
	
	boolean getIn(ElevatorUser elevatorUser) {
		if (elevatorUser.currentLevel == getCurrentLevel()) {		
			users.add(elevatorUser);					// person gets in to elevator
			callToLevel(elevatorUser.targetLevel);		// person push the level he wants to go
			
			System.out.println(elevatorUser.person + " gets in the elevator at level " + elevatorUser.currentLevel);

			return true;
		}
		else
			return false;
	}
	
	void getOut() {
		Iterator<ElevatorUser> iterator = users.iterator();
		while (iterator.hasNext()) {
			ElevatorUser elevatorUser = iterator.next();
			
			if (elevatorUser.targetLevel == getCurrentLevel()) {
				iterator.remove();		// person is not in elevator anymore
				
				System.out.println(elevatorUser.person + " gets out from elevator at level " + elevatorUser.targetLevel);
			}
		}		
	}
	
	boolean hasTasks() {
		return !requestedLevels.isEmpty() || !users.isEmpty();
	}
	
	boolean hasPeople() {
		return !users.isEmpty();
	}
	
	boolean reachedToTargetLevel() {
		boolean isReached = requestedLevels.contains(currentLevel);
		if (isReached) {
			requestedLevels.remove(currentLevel);
		}
		
		return isReached;
	}
	
	void moveElevator(int direction) {
		currentLevel = Math.min(Math.max(currentLevel + direction, minLevel), maxLevel);		
	}
	
	void proceed() {
		state.handle();		
	}
	
	@Override 
	public String toString() {
		return "Current level = " + currentLevel + (currentLevel == getFloorLevel() ? " (at floor)" : "") + "\n"
				+ "Current state = " + state + "\n"
				+ "List of levels to go = " + requestedLevels + "\n"
				+ (users.isEmpty() ? "Elevator is empty" : "Elevator contains " + users);
	}
	
	// Concrete states
	class IdleState implements ElevatorState {
		@Override
		public void handle() {
			if (hasTasks())  {
 				getIn();		// waiting people get in to the elevator
 				
 				getOut();		// people reached their target get out from the elevator

				state = checkingNextDestinationState;
 			}
			else
 				elevatorDirection = 0;
		}

		@Override 
		public String toString() {
			return "Idle";
		}
	}

	class PreparingToMoveUpState implements ElevatorState {
		@Override
		public void handle() {
			elevatorDirection = 1;
			state = movingToFloorState;
		}

		@Override 
		public String toString() {
			return "Preparing to move up";
		}		
	}

	class PreparingToMoveDownState implements ElevatorState {
		@Override
		public void handle() {
			elevatorDirection = -1;
			state = movingToFloorState;
		}

		@Override 
		public String toString() {
			return "Preparing to move down";
		}		
	}

	class MovingToFloorState implements ElevatorState {
		@Override
		public void handle() {
			moveElevator(elevatorDirection);
			
			// a level that is person called the elevator or a level that a person wants to go 
			if (reachedToTargetLevel()) {
				state = idleState;
			}
		}

		@Override 
		public String toString() {
			return "Moving to floor";
		}		
	}

	class CheckingNextDestinationState implements ElevatorState {
		@Override
		public void handle() {
			Integer closestLevel = findClosestLevel();
			
			if (closestLevel == null) {
				state = idleState;				
				return;
			}
			
			if (closestLevel > getCurrentLevel()) {
				state = preparingToMoveUpState;
			}
			
			if (closestLevel < getCurrentLevel()) {
				state = preparingToMoveDownState;
			}
		}
		
		Integer findClosestLevel() {
			Integer closestLevel = null;
			
			int minimumDistance = Integer.MAX_VALUE;
			for (Integer requestedLevel : requestedLevels) {
				if (closestLevel == null || Math.abs(getCurrentLevel() - requestedLevel) < minimumDistance) {
					closestLevel = requestedLevel;
				}
			}			
			
			return closestLevel;
		}

		@Override 
		public String toString() {
			return "Checking next destination";
		}		
	}	
}


