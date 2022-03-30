package utils;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BiMapTest {

    @Test
    public void givenABiMap_WhenAddingUniqueKeyValuePairs_EachItemCanBeAccessedAsAKeyOrAValue(){
        BiMap<String, Integer> map = new BiMap<>();
        map.put("a", 1);
        map.put("b", 2);
        // expected map behavior
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertTrue(map.containsValue(1));
        assertTrue(map.containsValue(2));

        //additional bi-map behavior
        assertEquals(map.getKey(1), "a");
        assertEquals(map.getKey(2), "b");
    }

    @Test
    public void givenABiMap_WhenAddingDuplicateValuesForDifferentKeys_AUniqueButSimilarValueMustBeProvided(){
        BiMap<String, Integer> map = new BiMap<>();
        map.put("a", 10);
        map.put("b", 20);
        map.put("c", 30);
        map.put("d", 10);

        // expected map behavior
        assertTrue(map.containsKey("a"));
        assertTrue(map.containsKey("b"));
        assertTrue(map.containsKey("c"));
        assertTrue(map.containsKey("d"));

        //additional bi-map behavior
        //assertEquals(map.getKey(10), "a");
        assertEquals(map.getKey(20), "b");
        assertEquals(map.getKey(30), "c");
        System.out.println(map.get("a"));
        System.out.println(map.get("d"));




    }
}
