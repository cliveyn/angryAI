package uc;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

/**
 * Created by keltp on 2017-05-16.
 */
public class Test {
    public static void main(String args[]) {
        Map<String, Integer> left = ImmutableMap.of("a", 1, "b", 2, "c", 3);
        Map<String, Integer> right = ImmutableMap.of("b", 2, "c", 4, "d", 5);
        MapDifference<String, Integer> diff = Maps.difference(left, right);
    }
}
