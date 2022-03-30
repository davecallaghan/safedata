package utils;

import java.util.HashMap;
import java.util.Map;

public class BiMap<K, V> extends HashMap<K, V> {

    //https://self-learning-java-tutorial.blogspot.com/2019/05/java-implement-bidirectional-map.html
    //https://www.foreach.be/blog/parallel-and-asynchronous-programming-in-java-8
    //https://www.callicoder.com/java-8-completablefuture-tutorial/

    public Map<V, K> inverseMap = new HashMap<V, K>();

    public K getKey(V value) {
        return inverseMap.get(value);
    }

    @Override
    public int size() {
        return this.size();
    }

    @Override
    public boolean isEmpty() {
        return this.size() > 0;
    }

    @Override
    public V remove(Object key) {
        V value = super.remove(key);
        inverseMap.remove(value);
        return value;
    }

    @Override
    public V put(K key, V value) {
        inverseMap.put(value, key);
        return super.put(key, value);
    }
}
