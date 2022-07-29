import java.util.*;

public class TestClass {	
	final static double explorationProbability = 0.2;

	public static void main(String[] args) {
		// world
		World world = new World(new String[] {	"#.#####################",
												"#...####.#............#",
												"#.#......#.########.###",
												"#.#.####.#........#...#",
												"#.#......#.##########.#",
												"#.####................#",
												"#....############...#.#",
												"#.####..###...#.#.###.#",
												"#.............#.#.#.#.#",
												"#.#############.###.#.#",
												"#...................#.#",
												"#####################.#"});
		System.out.println(world);
		
		// create world
		Location entry = new Location(1, 0);
		Location exit = new Location(21, 11);
		int maximumMovementCount = 100000;
		
		System.out.println("Entry is " + entry);
		System.out.println("Exit is " + exit);
		System.out.println("Maximum allowed movement count to find the exit is " + maximumMovementCount);
		System.out.println();
		
		System.out.println("------------------------------------------------------------------------------------------");
		
		/// *** spaghetti code (or bad code, or code that smells) *** 
		// random movement strategy
		System.out.println("findPathWithRandomMovement dummy code:");
		int moveCount = findPathWithRandomMovement(world, entry, exit, maximumMovementCount);
		if (moveCount >= 0)
			System.out.println("Exit is founded with findPathWithRandomMovement strategy in " + moveCount + " movements.");
		else
			System.out.println("Exit is not founded with findPathWithRandomMovement strategy");
		System.out.println();
		
		// random movement combined with heuristic strategy
		System.out.println("findPathWithRandomAndHeuristicMovement dummy code:");
		moveCount = findPathWithRandomAndHeuristicMovement(world, entry, exit, maximumMovementCount);
		if (moveCount >= 0)
			System.out.println("Exit is founded with findPathWithRandomAndHeuristicMovement strategy in " + moveCount + " movements.");
		else
			System.out.println("Exit is not founded with findPathWithRandomAndHeuristicMovement strategy");
		System.out.println();	
				
		System.out.println("------------------------------------------------------------------------------------------");		
		System.out.println();
		
		/// *** with template method pattern ***
		findPathWithTemplateMethod(world, entry, exit, maximumMovementCount);

		/// *** with strategy pattern ***
		findPathWithStrategy(world, entry, exit, maximumMovementCount);
	}

	/// *** spaghetti code (or bad code, or code that smells) *** 
	static int findPathWithRandomMovement(World world, Location entry, Location exit, int maximumMovementCount) {
		Random rand = new Random(RandomSeed.currentTimeMillis); 
		
		// search procedure
		Location position = new Location(entry);		
		for (int k=0; k<maximumMovementCount; k++) {
			if (position.equals(exit)) {
				return k;
			}			
			Location[] neighbours = position.getNeighbours();
			
			// random with heuristic movement
			Location candidateLocation = neighbours[rand.nextInt(neighbours.length)];
			if (world.visitable(candidateLocation)) {
				position = candidateLocation;
			}
		}
		
		return -1;
	}
	
	static int findPathWithRandomAndHeuristicMovement(World world, Location entry, Location exit, int maximumMovementCount) {
		Random rand = new Random(RandomSeed.currentTimeMillis); 
		
		// search procedure
		Location position = new Location(entry);		
		for (int k=0; k<maximumMovementCount; k++) {
			if (position.equals(exit)) {
				return k;
			}			
			Location[] neighbours = position.getNeighbours();
			
			// random with heuristic movement
			if (rand.nextDouble() < explorationProbability) {
				Location candidateLocation = neighbours[rand.nextInt(neighbours.length)];
				if (world.visitable(candidateLocation)) {
					position = candidateLocation;
				}
			}
			else {
				int minimumDistance = Integer.MAX_VALUE;
				Location bestNeighbour = null;
				for (Location candidateLocation: neighbours) {
					if (world.visitable(candidateLocation)) {
						int distance = candidateLocation.distanceTo(exit);
						if (distance < minimumDistance) {
							minimumDistance = distance;
							bestNeighbour = candidateLocation;
						}
					}					
				}
				
				if (bestNeighbour != null) {
					position = bestNeighbour;
				}
			}
			
		}
		
		return -1;
	}
	
	/// *** with template method pattern ***
	static void findPathWithTemplateMethod(World world, Location entry, Location exit, int maximumMovementCount) {
		System.out.println("PathFinderUsingRandomMovement Template Method is in use:");
		PathFinderUsingRandomMovement pathFinder1 = new PathFinderUsingRandomMovement();
		int moveCount = pathFinder1.findPath(world, entry, exit, maximumMovementCount);
		if (moveCount >= 0)
			System.out.println("Exit is founded with Random Movement strategy in " + moveCount + " movements.");
		else
			System.out.println("Exit is not founded with Random Movement strategy");
		System.out.println();
		
		System.out.println("PathFinderUsingRandomAndHeuristicMovement Template Method is in use:");
		PathFinderUsingRandomAndHeuristicMovement pathFinder2 = new PathFinderUsingRandomAndHeuristicMovement(explorationProbability, exit);
		moveCount = pathFinder2.findPath(world, entry, exit, maximumMovementCount);
		if (moveCount >= 0)
			System.out.println("Exit is founded with Random And Heuristic Movement strategy in " + moveCount + " movements.");
		else
			System.out.println("Exit is not founded with Random And Heuristic Movement strategy");
		System.out.println();		
	}

	/// *** with strategy pattern ***
	static void findPathWithStrategy(World world, Location entry, Location exit, int maximumMovementCount) {
		System.out.println("PathFinderUsingStrategy with RandomMovementStrategy (Strategy Pattern) is in use:");
		PathFinderUsingStrategy pathFinder1 = new PathFinderUsingStrategy(new RandomMovementStrategy());
		int moveCount = pathFinder1.findPath(world, entry, exit, maximumMovementCount);
		if (moveCount >= 0)
			System.out.println("Exit is founded with Random Movement strategy in " + moveCount + " movements.");
		else
			System.out.println("Exit is not founded with Random Movement strategy");
		System.out.println();
		
		System.out.println("PathFinderUsingStrategy with RandomAndHeuristicMovementStrategy (Strategy Pattern) is in use:");
		MovementStrategy strategy = new RandomAndHeuristicMovementStrategy(explorationProbability, exit);
		PathFinderUsingStrategy pathFinder2 = new PathFinderUsingStrategy(strategy);
		moveCount = pathFinder2.findPath(world, entry, exit, maximumMovementCount);
		if (moveCount >= 0)
			System.out.println("Exit is founded with Random And Heuristic Movement strategy in " + moveCount + " movements.");
		else
			System.out.println("Exit is not founded with Random And Heuristic Movement strategy");
		System.out.println();				
	}
	
}


// this is just to guarantee that each path-finder code is running with same random numbers (normally you do not do it)
abstract class RandomSeed {
	static final long currentTimeMillis = System.currentTimeMillis();	
}


// Template Method Pattern
abstract class PathFinderUsingTemplate {
	int findPath(World world, Location entry, Location exit, int maximumMovementCount) {
		Location position = new Location(entry);		
		for (int k=0; k<maximumMovementCount; k++) {
			if (position.equals(exit)) {
				return k;
			}			
			position = getNextPosition(world, position);
		}
		
		return -1;		
	}
	
	abstract Location getNextPosition(World world, Location position);	
}

class PathFinderUsingRandomMovement extends PathFinderUsingTemplate {
	static final Random rand = new Random(RandomSeed.currentTimeMillis); 

	@Override
	Location getNextPosition(World world, Location position) {
		Location[] neighbours = position.getNeighbours();
		
		Location candidateLocation = neighbours[rand.nextInt(neighbours.length)];
		if (world.visitable(candidateLocation)) {
			return candidateLocation;
		}
		
		return position;
	}	
}

class PathFinderUsingRandomAndHeuristicMovement extends PathFinderUsingTemplate {
	static final Random rand = new Random(RandomSeed.currentTimeMillis); 

	final double explorationProbability;
	private Location exit;
	
	PathFinderUsingRandomAndHeuristicMovement(double explorationProbability, Location exit) {
		this.explorationProbability = explorationProbability;
		this.exit = exit;
	}
	
	@Override
	Location getNextPosition(World world, Location position) {
		Location[] neighbours = position.getNeighbours();
		
		// random with heuristic movement
		if (rand.nextDouble() < explorationProbability) {
			Location candidateLocation = neighbours[rand.nextInt(neighbours.length)];
			if (world.visitable(candidateLocation)) {
				position = candidateLocation;
			}
		}
		else {
			int minimumDistance = Integer.MAX_VALUE;
			Location bestNeighbour = null;
			for (Location candidateLocation: neighbours) {
				if (world.visitable(candidateLocation)) {
					int distance = candidateLocation.distanceTo(exit);
					if (distance < minimumDistance) {
						minimumDistance = distance;
						bestNeighbour = candidateLocation;
					}
				}					
			}
			
			if (bestNeighbour != null) {
				position = bestNeighbour;
			}
		}
		
		return position;
	}	
}


// Strategy Pattern
class PathFinderUsingStrategy {
	private MovementStrategy strategy;

	PathFinderUsingStrategy(MovementStrategy strategy) {
		this.strategy = strategy;
	}
	
	int findPath(World world, Location entry, Location exit, int maximumMovementCount) {
		Location position = new Location(entry);		
		for (int k=0; k<maximumMovementCount; k++) {
			if (position.equals(exit)) {
				return k;
			}			
			position = strategy.getNextPosition(world, position);
		}
		
		return -1;		
	}
}

abstract class MovementStrategy {
	abstract Location getNextPosition(World world, Location position);	
}

class RandomMovementStrategy extends MovementStrategy {
	static final Random rand = new Random(RandomSeed.currentTimeMillis); 

	@Override
	Location getNextPosition(World world, Location position) {
		Location[] neighbours = position.getNeighbours();
		
		Location candidateLocation = neighbours[rand.nextInt(neighbours.length)];
		if (world.visitable(candidateLocation)) {
			return candidateLocation;
		}
		
		return position;
	}	
}

class RandomAndHeuristicMovementStrategy extends MovementStrategy {
	static final Random rand = new Random(RandomSeed.currentTimeMillis); 

	final double explorationProbability;
	private Location exit;
	
	RandomAndHeuristicMovementStrategy(double explorationProbability, Location exit) {
		this.explorationProbability = explorationProbability;
		this.exit = exit;
	}
	
	@Override
	Location getNextPosition(World world, Location position) {
		Location[] neighbours = position.getNeighbours();
		
		// random with heuristic movement
		if (rand.nextDouble() < explorationProbability) {
			Location candidateLocation = neighbours[rand.nextInt(neighbours.length)];
			if (world.visitable(candidateLocation)) {
				position = candidateLocation;
			}
		}
		else {
			int minimumDistance = Integer.MAX_VALUE;
			Location bestNeighbour = null;
			for (Location candidateLocation: neighbours) {
				if (world.visitable(candidateLocation)) {
					int distance = candidateLocation.distanceTo(exit);
					if (distance < minimumDistance) {
						minimumDistance = distance;
						bestNeighbour = candidateLocation;
					}
				}					
			}
			
			if (bestNeighbour != null) {
				position = bestNeighbour;
			}
		}
		
		return position;
	}	
}


// Common classes: Location and World
class Location {
	final int x;
	final int y;
	
	static int[][] neighbourDirections = {{+1, 0}, {0, +1}, {-1, 0}, {0, -1}};
	
	Location(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	Location(Location location) {
		this.x = location.x;
		this.y = location.y;
	}
	
	int distanceTo(Location target) {
		return Math.abs(x - target.x) + Math.abs(y - target.y);
	}
	
	Location[] getNeighbours() {
		Location[] neighbours = new Location[neighbourDirections.length];
		for (int i=0; i<neighbourDirections.length; i++) {
			neighbours[i] = new Location(x + neighbourDirections[i][0], y + neighbourDirections[i][1]);
		}
			
		return neighbours;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		
		if (obj == this) {
			return true;
		}
		
		if (obj instanceof Location) {
			Location locationObject = (Location)obj;
			return (x == locationObject.x && y == locationObject.y);
		}
		else
			return false;
	}
	
	@Override
	public String toString() {
		return "P(" + x + ", " + y + ")";
	}
}

class World {
	final String[] grid;  

	World(String[] grid) {
		this.grid = grid;
	}	
	
	boolean visitable(Location location) {
		if (location.y >= 0 && location.y < grid.length) {
			String row = grid[location.y];
			
			if (location.x >= 0 && location.x < row.length())
				return (row.charAt(location.x) == '.');
			else
				return false;
		}
		else
			return false;
	}
	
	@Override
	public String toString() {
		String gridWorld = "";
		for (String row : grid) {
			gridWorld += row + "\n";
		}
		return gridWorld;
	}
}


