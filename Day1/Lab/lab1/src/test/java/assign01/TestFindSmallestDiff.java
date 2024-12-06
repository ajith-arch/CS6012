package assign01;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TestFindSmallestDiff {
    private int[] arr1, arr2, arr3, arr4;

    @BeforeEach
    void setUp() {
        arr1 = new int[0];
        arr2 = new int[] { 3, 3, 3 };
        arr3 = new int[] { 52, 4, -8, 0, -17 };
        arr4 = new int[] {-10,-5,-15,-4};
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    public void emptyArray() {
        assertEquals(-1, DiffUtil.findSmallestDiff(arr1));
    }

    @Test
    public void allArrayElementsEqual() {
        assertEquals(0, DiffUtil.findSmallestDiff(arr2));
    }

    @Test
    public void smallRandomArrayElements() {
        assertEquals(-8, DiffUtil.findSmallestDiff(arr3));
    }

    @Test
    public void randomNegativeElements(){
        assertEquals(-11, DiffUtil.findSmallestDiff(arr4));
    }

}