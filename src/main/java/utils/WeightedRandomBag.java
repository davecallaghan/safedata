package utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

//https://gamedev.stackexchange.com/questions/162976/how-do-i-create-a-weighted-collection-and-then-pick-a-random-element-from-it
public class WeightedRandomBag<T extends Object> {

    private class Entry {
        double accumulatedWeight;
        T object;
    }

    private List<Entry> entries = new ArrayList<>();
    private double accumulatedWeight;
    private Random random = new Random();

    public void addEntry(T object, double weight) {
        accumulatedWeight += weight;
        Entry entry = new Entry();
        entry.object = object;
        entry.accumulatedWeight = accumulatedWeight;
        entries.add(entry);
    }

    public T getRandom() {
        double r = random.nextDouble() * accumulatedWeight;

        for (Entry entry: entries) {
            if (entry.accumulatedWeight >= r) {
                return entry.object;
            }
        }
        return null;
    }


}
