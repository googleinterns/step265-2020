package com.google.cloudassets.discovery.assetobjects;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class AssetObjectTest {
    /**
     * This function validates that the getLastSeg function in the AssetObject class
     * returns null (invalid value returned due to an exception being encountered) when provided
     * a Boolean value that can't be converted into String with regular casting.
     */
    @Test
    public void testGetLastSegBoolean() {
        assertNull(AssetObject.getLastSeg(true));
    }

    /**
     * This function validates that the getLastSeg function returns the right value when
     * called with a valid argument.
     */
    @Test
    public void testGetLastSegValid() {
        assertEquals("shown", AssetObject.getLastSeg("not_shown/also_not_shown/shown"));
        assertEquals("everything", AssetObject.getLastSeg("everything"));
    }

    /**
     * This function validates that the convertListToLastSegList function in the AssetObject class
     * returns null (invalid value returned due to an exception being encountered) when provided
     * a Boolean value that can't be converted into a List of Strings.
     */
    @Test
    public void testConvertListToLastSegListBoolean() {
        assertNull(AssetObject.convertListToLastSegList(true));
    }

    /**
     * This function validates that the convertListToLastSegList function in the AssetObject class
     * returns null (invalid value returned due to an exception being encountered) when provided
     * a String value that can't be converted into a List of Strings.
     */
    @Test
    public void testConvertListToLastSegListString() {
        assertNull(AssetObject.convertListToLastSegList("not a list"));
    }

    /**
     * This function validates that the convertListToLastSegList function in the AssetObject class
     * returns null (invalid value returned due to an exception being encountered) when provided
     * a List of Boolean values that can't be converted into a List of Strings with regular casting.
     */
    @Test
    public void testConvertListToLastSegListBooleanList() {
        List<Boolean> invalidInput = new ArrayList<>();
        invalidInput.add(true);
        assertNull(AssetObject.convertListToLastSegList(invalidInput));
    }

    /**
     * This function validates that the convertListToLastSegList function returns the right value
     * when called with an empty List of Strings.
     */
    @Test
    public void testConvertListToLastSegListEmptyList() {
        List<String> emptyList = new ArrayList<>();
        assertEquals(emptyList, AssetObject.convertListToLastSegList(emptyList));
    }

    /**
     * This function validates that the convertListToLastSegList function returns the right value
     * when called with a valid argument.
     */
    @Test
    public void testConvertListToLastSegListValid() {
        List<String> validInput = new ArrayList<>();
        validInput.add("not_shown/shown");
        validInput.add("everything");
        assertEquals("shown", AssetObject.convertListToLastSegList(validInput).get(0));
        assertEquals("everything", AssetObject.convertListToLastSegList(validInput).get(1));
    }

    /**
     * This function validates that the convertStringToDate function in the AssetObject class
     * returns null (invalid value returned due to an exception being encountered) when provided
     * a Boolean value that can't be converted into Timestamp.
     */
    @Test
    public void testConvertStringToDateBoolean() {
        assertNull(AssetObject.convertStringToDate(true));
    }

    /**
     * This function validates that the convertStringToInt function in the AssetObject class
     * returns null (invalid value returned due to an exception being encountered) when provided
     * a String that does not match the proper date format.
     */
    @Test
    public void testConvertStringToDateString() {
        assertNull(AssetObject.convertStringToDate("2020-02-02"));
    }

    /**
     * This function validates that the convertStringToInt function in the AssetObject class
     * returns null (invalid value returned due to an exception being encountered) when provided
     * a Boolean value that can't be converted into String with regular casting.
     */
    @Test
    public void testConvertStringToIntBoolean() {
        assertNull(AssetObject.convertStringToInt(true));
    }

    /**
     * This function validates that the convertStringToInt function in the AssetObject class
     * returns null (invalid value returned due to an exception being encountered) when provided
     * a String value that can't be converted into an int.
     */
    @Test
    public void testConvertStringToIntString() {
        assertNull(AssetObject.convertStringToInt("not a number"));
    }

    /**
     * This function validates that the convertStringToInt function returns the right value when
     * called with a valid argument.
     */
    @Test
    public void testConvertStringToIntValid() {
        assertEquals(24, AssetObject.convertStringToInt("24"));
    }

    /**
     * This function validates that the convertObjectToString function in the AssetObject class
     * returns null (invalid value returned due to an exception being encountered) when provided
     * a Boolean value that can't be converted into String with regular casting.
     */
    @Test
    public void testConvertObjectToStringBoolean() {
        assertNull(AssetObject.convertObjectToString(true));
    }

    /**
     * This function validates that the convertObjectToBoolean function in the AssetObject class
     * returns null (invalid value returned due to an exception being encountered) when provided
     * a String value that can't be converted into a Boolean with regular casting.
     */
    @Test
    public void testConvertObjectToBooleanString() {
        assertNull(AssetObject.convertObjectToBoolean("invalid"));
    }

    /**
     * This function validates that the convertObjectToMap function in the AssetObject class
     * returns null (invalid value returned due to an exception being encountered) when provided
     * a String value that can't be converted into a HashMap<String, Object> with regular casting.
     */
    @Test
    public void testConvertObjectToMapString() {
        assertNull(AssetObject.convertObjectToMap("invalid"));
    }
}
