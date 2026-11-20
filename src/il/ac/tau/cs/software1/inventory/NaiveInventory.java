package il.ac.tau.cs.software1.inventory;

/*
 * A simple array-based Inventory (Question 6).
 */
public class NaiveInventory implements Inventory {

	private static final int INITIAL_CAPACITY = 100;
	private Collectible[] collectibles;
	private int actuallyStored = 0;

	public NaiveInventory() {
		collectibles = new Collectible[INITIAL_CAPACITY];
	}

	@Override
	public void collect(Collectible collectible) {
		if (actuallyStored == collectibles.length){
			Collectible[] newCollectibles = new Collectible[collectibles.length * 2];
			for (int i = 0; i < actuallyStored; i++){
				newCollectibles[i] = collectibles[i];
			}
			collectibles = newCollectibles;
		}
		collectibles[actuallyStored] = collectible;
		actuallyStored++;
	}

	@Override
	public float getTotalWeight() {
		float weight = 0;
		for (int i = 0; i < actuallyStored; i++){
			if (collectibles[i] != null) weight += collectibles[i].getWeight();
		} 
		return weight;
	}

	@Override
	public int getCurrentCount() {
		return actuallyStored;
	}

	// Returns the array of Collectibles that is stored in the field of this class
	public Collectible[] getCollArr() {
		return collectibles;
	}
}
