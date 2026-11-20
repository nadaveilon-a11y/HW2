package il.ac.tau.cs.software1.inventory;

/*
 * A simple array-based Inventory (Question 6).
 */
public class NaiveInventory implements Inventory {

	private static final int INITIAL_CAPACITY = 100;
	private Collectible[] collectibles;
	private int actually_stored = 0;

	public NaiveInventory() {
		collectibles = new Collectible[INITIAL_CAPACITY];
	}

	@Override
	public void collect(Collectible collectible) {
		if (actually_stored == collectibles.length){
			Collectible[] new_collectibles = new Collectible[actually_stored * 2];
			for (int i = 0; i < actually_stored; i++){
				new_collectibles[i] = collectibles[i];
			}
			collectibles = new_collectibles;
		}
		collectibles[actually_stored] = collectible;
		actually_stored++;
	}

	@Override
	public float getTotalWeight() {
		float weight = 0;
		for (Collectible c: collectibles){
			if (c != null) weight += c.getWeight();
		} 
		return weight;
	}

	@Override
	public int getCurrentCount() {
		return actually_stored;
	}

	// Returns the array of Collectibles that is stored in the field of this class
	public Collectible[] getCollArr() {
		return collectibles;
	}
}
