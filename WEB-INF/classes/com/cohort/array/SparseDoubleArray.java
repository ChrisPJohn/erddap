/* This file is part of the EMA project and is 
 * Copyright (c) 2005 Robert Simons (CoHortSoftware@gmail.com).
 * See the MIT/X-like license in LICENSE.txt.
 * For more information visit www.cohort.com or contact CoHortSoftware@gmail.com.
 */
package com.cohort.array;

import com.cohort.util.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.rmi.UnexpectedException;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import ucar.ma2.StructureData;

/**
 * DoubleArray is a thin shell over a hashmap with methods like ArrayList's 
 * methods; it extends PrimitiveArray.
 */
public class SparseDoubleArray extends PrimitiveArray {

    public final static SparseDoubleArray MV9 = new SparseDoubleArray(Math2.COMMON_MV9);


    /**
     * This returns the number of bytes per element for this PrimitiveArray.
     * The value for "String" isn't a constant, so this returns 20.
     *
     * @return the number of bytes per element for this PrimitiveArray.
     * The value for "String" isn't a constant, so this returns 20.
     */
    public int elementSize() {
        return 8;
    }

    /** 
     * This returns for cohort missing value for this class (e.g., Integer.MAX_VALUE), 
     * expressed as a double. FloatArray and StringArray return Double.NaN. 
     */
    public double missingValueAsDouble() {
        return Double.NaN;
    }

    /**
     * This tests if the value at the specified index equals the data type's MAX_VALUE 
     * (for integerTypes, which may or may not indicate a missing value,
     * depending on maxIsMV), NaN (for Float and Double), \\uffff (for CharArray),
     * or "" (for StringArray).
     *
     * @param index The index in question
     * @return true if the value is a missing value.
     */
    public boolean isMaxValue(int index) {
        return !Double.isFinite(get(index));
    }

    /**
     * This tests if the value at the specified index is a missing value.
     * For integerTypes, isMissingValue can only be true if maxIsMv is 'true'.
     *
     * @param index The index in question
     * @return true if the value is a missing value.
     */
    public boolean isMissingValue(int index) {
        return isMaxValue(index);
    }

    /**
     * This is the main data structure.
     */
    private Map<Integer, Double> map;

    /**
     * A constructor for a capacity of 8 elements. The initial 'size' will be 0.
     */
    public SparseDoubleArray() {
        map = new HashMap<>(8);
    }

    /**
     * This constructs a DoubleArray by copying elements from the incoming
     * PrimitiveArray (using append()).
     *
     * @param primitiveArray a primitiveArray of any type 
     */
    public SparseDoubleArray(PrimitiveArray primitiveArray) {
        
        map = new HashMap<>(8);
        append(primitiveArray);
        System.out.print("***********************************************************");
        System.out.println("Made SparseDouble from PA. Size: " + size + " mapCount: " + map.size());
        System.out.println("Input missing entry: " + primitiveArray.missingValueAsDouble() + " First entry: " + get(0));
        System.out.print("***********************************************************");
    }

    /**
     * A constructor for a specified number of elements. The initial 'size' will be 0.
     *
     * @param capacity creates an DoubleArray with the specified initial capacity.
     * @param active if true, size will be set to capacity and all elements 
     *    will equal 0; else size = 0.
     */
    public SparseDoubleArray(int capacity, boolean active) {
        Math2.ensureMemoryAvailable(8L * capacity, "SparseDoubleArray");
        map = new HashMap<>(capacity);
        if (active) {
            addN(capacity, 0);
        }
    }

    /**
     * A constructor which (at least initially) uses the array and all 
     * its elements ('size' will equal anArray.length).
     *
     * @param anArray the array to be used as this object's array.
     */
    public SparseDoubleArray(double[] anArray) {
        map = new HashMap<>();
        add(anArray);
    }

    /**
     * This makes a DoubleArray from the comma-separated values.
     * <br>null becomes pa.length() == 0.
     * <br>"" becomes pa.length() == 0.
     * <br>" " becomes pa.length() == 1.
     * <br>See also PrimitiveArray.csvFactory(paType, csv);
     *
     * @param csv the comma-separated-value string
     * @return a DoubleArray from the comma-separated values.
     */
    public static SparseDoubleArray fromCSV(String csv) {
        return (SparseDoubleArray)PrimitiveArray.csvFactory(PAType.SPARSEDOUBLE, csv);
    }

    /** This returns a new PAOne with the minimum value that can be held by this class. 
     *
     * @return a new PAOne with the minimum value that can be held by this class, e.g., -128b for ByteArray. 
     */
    public PAOne MINEST_VALUE() {return PAOne.fromDouble(-Double.MAX_VALUE);}

    /** This returns a new PAOne with the maximum value that can be held by this class 
     *   (not including the cohort missing value). 
     *
     * @return a new PAOne with the maximum value that can be held by this class, e.g., 126 for ByteArray. 
     */
    public PAOne MAXEST_VALUE() {return PAOne.fromDouble(Double.MAX_VALUE);}

    /**
     * This returns the current capacity (number of elements) of the internal data array.
     * 
     * @return the current capacity (number of elements) of the internal data array.
     */
    public int capacity() {
        return size;
    }

    /** This indicates if this class' type is PAType.FLOAT or PAType.DOUBLE. 
     */
    public boolean isFloatingPointType() {
        return true;
    }

    /**
     * This returns the hashcode for this DoubleArray (dependent only on values,
     * not capacity).
     * WARNING: the algorithm used may change in future versions.
     *
     * @return the hashcode for this DoubleArray (dependent only on values,
     * not capacity)
     */
    public int hashCode() {
        //see https://docs.oracle.com/javase/8/docs/api/java/util/List.html#hashCode()
        //and https://stackoverflow.com/questions/299304/why-does-javas-hashcode-in-string-use-31-as-a-multiplier
        //and java docs for Double.hashCode
        int code = 0;
        for (Map.Entry<Integer,Double> entry : map.entrySet()) {
            long lv = Double.doubleToLongBits(entry.getValue());
            code = 31*code + ((int)(lv ^ lv>>>32));  //safe (only want low 32 bits)
        }
        
        return code;
    }

    /**
     * This makes a new subset of this PrimitiveArray based on startIndex, stride,
     * and stopIndex.
     *
     * @param pa the pa to be filled (may be null). If not null, must be of same type as this class. 
     * @param startIndex must be a valid index
     * @param stride   must be at least 1
     * @param stopIndex (inclusive) If &gt;= size, it will be changed to size-1.
     * @return The same pa (or a new PrimitiveArray if it was null) with the desired subset.
     *    If new, it will have a backing array with a capacity equal to its size.
     *    If stopIndex &lt; startIndex, this returns PrimitiveArray with size=0;
     */
    public PrimitiveArray subset(PrimitiveArray pa, int startIndex, int stride, int stopIndex) {
        if (pa != null)
            pa.clear();
        if (startIndex < 0)
            throw new IndexOutOfBoundsException(MessageFormat.format(
                ArraySubsetStart, getClass().getSimpleName(), "" + startIndex));
        if (stride < 1)
            throw new IllegalArgumentException(MessageFormat.format(
                ArraySubsetStride, getClass().getSimpleName(), "" + stride));
        if (stopIndex >= size)
            stopIndex = size - 1;
        if (stopIndex < startIndex)
            return pa == null? new SparseDoubleArray(new double[0]) : pa;

        int willFind = strideWillFind(stopIndex - startIndex + 1, stride);
        SparseDoubleArray da = null;
        if (pa == null) {
            da = new SparseDoubleArray(willFind, false);
        } else {
            da = (SparseDoubleArray)pa;
            da.ensureCapacity(willFind);
        }
        for (int i = startIndex; i <= stopIndex; i+=stride) 
            da.add(get(i));
        return da;
    }

    /**
     * This returns the PAType (PAType.DOUBLE) of the element type.
     *
     * @return the PAType (PAType.DOUBLE) of the element type.
     */
    public PAType elementType() {
        return PAType.DOUBLE;
    }

    /**
     * This returns the minimum PAType needed to completely and precisely contain
     * the values in this PA's PAType and tPAType (e.g., when merging two PrimitiveArrays).
     *
     * @return the minimum PAType needed to completely and precisely contain
     * the values in this PA's PAType and tPAType (e.g., when merging two PrimitiveArrays).
     */
    public PAType needPAType(PAType tPAType) {
        //if tPAType is smaller or same, return this.PAType
        if (tPAType == PAType.BYTE   ||
            tPAType == PAType.UBYTE  ||
            tPAType == PAType.SHORT  ||
            tPAType == PAType.USHORT ||
            tPAType == PAType.INT    ||
            tPAType == PAType.UINT   ||
            tPAType == PAType.FLOAT  ||
            tPAType == PAType.DOUBLE ||
            tPAType == PAType.SPARSEDOUBLE)  return PAType.DOUBLE;

        //if sideways           //LONG, ULONG, CHAR, STRING
        return PAType.STRING;
    }

    /**
     * This adds an item to the array (increasing 'size' by 1).
     *
     * @param value the value to be added to the array
     */
    public void add(double value) {
        if (!Double.isNaN(value)) {
            map.put(size, value);
        }
        size++;
    }

    /**
     * This adds an item to the array (increasing 'size' by 1).
     *
     * @param value the value to be added to the array.
     *    If value instanceof Number, this uses Number.doubleValue().
     *    If null or not a Number, this adds Double.NaN.
     */
    public void addObject(Object value) {
        add(value != null && value instanceof Number?
                ((Number)value).doubleValue() :
                    Double.NaN);
    }

    /**
     * This reads one value from the StrutureData and adds it to this PA.
     *
     * @param sd from an .nc file
     * @param memberName
     */
    public void add(StructureData sd, String memberName) {
        add(sd.getScalarDouble(memberName));
    }

    /**
     * This adds all the values from ar.
     *
     * @param ar an array
     */
    public void add(double ar[]) {
        int arSize = ar.length;
        for (int i = 0; i < arSize; i++) {
            add(ar[i]);
        }
    }    

    /**
     * This adds n copies of value to the array (increasing 'size' by n).
     *
     * @param n  If less than 0, this throws Exception.
     * @param value the value to be added to the array.
     *    n &lt; 0 throws an Exception.
     */
    public void addN(int n, double value) {
        if (n == 0) return;
        if (n < 0)
            throw new IllegalArgumentException(MessageFormat.format(
                ArrayAddN, getClass().getSimpleName(), "" + n));

        for (int i = 0; i < n; i++) {
            add(value);
        }
    }
    
    private void shift(int from, int to, int amount) {
        if (amount < 0) {
            for (int i = from; i <= to; i++) {
                if (map.containsKey(i)) {
                    map.put(i+amount, map.get(i));
                    map.remove(i);
                }
            }
        } else {
            for (int i = to; i >= from; i--) {
                if (map.containsKey(i)) {
                    map.put(i+amount, map.get(i));
                    map.remove(i);
                }
            }
        }
    }

    /**
     * This inserts an item into the array at the specified index, 
     * pushing subsequent items to oldIndex+1 and increasing 'size' by 1.
     *
     * @param index the position where the value should be inserted.
     * @param value the value to be inserted into the array
     */
    public void atInsert(int index, double value) {
        if (index < 0 || index > size)
            throw new IllegalArgumentException(MessageFormat.format(
                ArrayAtInsert, getClass().getSimpleName(), "" + index, "" + size));
        shift(index, size, 1);
        
        map.put(index, value);
        size++;
    }

    /**
     * This inserts an item into the array at the specified index, 
     * pushing subsequent items to oldIndex+1 and increasing 'size' by 1.
     *
     * @param index 0..
     * @param value the value, as a String.
     */
    public void atInsertString(int index, String value) {
        atInsert(index, String2.parseDouble(value));
    }

    /**
     * This adds n PAOne's to the array.
     *
     * @param n the number of times 'value' should be added.
     *    If less than 0, this throws Exception.
     * @param value the value, as a PAOne (or null).
     */
    public void addNPAOnes(int n, PAOne value) {
        addN(n, value == null? Double.NaN : value.getNiceDouble());
    }

    /**
     * This adds n Strings to the array.
     *
     * @param n the number of times 'value' should be added.
     *    If less than 0, this throws Exception.
     * @param value the value, as a String.
     */
    public void addNStrings(int n, String value) {
        addN(n, String2.parseDouble(value));
    }

    /**
     * This adds n floats to the array.
     *
     * @param n the number of times 'value' should be added.
     *    If less than 0, this throws Exception.
     * @param value the value, as a float.
     */
    public void addNFloats(int n, float value) {
        addN(n, Math2.floatToDoubleNaN(value));
    }

    /**
     * This adds n doubles to the array.
     *
     * @param n the number of times 'value' should be added.
     *    If less than 0, this throws Exception.
     * @param value the value, as a double.
     */
    public void addNDoubles(int n, double value) {
        addN(n, value);
    }

    /**
     * This adds n ints to the array.
     *
     * @param n the number of times 'value' should be added
     * @param value the value, as an int.
     */
    public void addNInts(int n, int value) {
        addN(n, value); //! assumes value=Integer.MAX_VALUE isn't maxIsMV
    }

    /**
     * This adds n longs to the array.
     *
     * @param n the number of times 'value' should be added
     * @param value the value, as an int.
     */
    public void addNLongs(int n, long value) {
        addN(n, value); //! assumes value=Integer.MAX_VALUE isn't maxIsMV
    }

    /**
     * This adds an element from another PrimitiveArray.
     *
     * @param otherPA the source PA
     * @param otherIndex the start index in otherPA
     * @param nValues the number of values to be added
     * @return 'this' for convenience
     */
    public PrimitiveArray addFromPA(PrimitiveArray otherPA, int otherIndex, int nValues) {
        //add from different type
        for (int i = 0; i < nValues; i++)
            add(otherPA.getNiceDouble(otherIndex++)); //does error checking    'nice' just affects float->double
        return this;
    }

    /**
     * This sets an element from another PrimitiveArray.
     *
     * @param index the index to be set
     * @param otherPA the other PrimitiveArray
     * @param otherIndex the index of the item in otherPA
     */
    public void setFromPA(int index, PrimitiveArray otherPA, int otherIndex) {
        set(index, otherPA.getNiceDouble(otherIndex));  //'nice' just affects float->double
    }

    /**
     * This removes the specified element.
     *
     * @param index the element to be removed, 0 ... size-1
     */
    public void remove(int index) {
        if (index >= size)
            throw new IllegalArgumentException(MessageFormat.format(
                ArrayRemove, getClass().getSimpleName(), "" + index, "" + size));
        map.remove(index);
        shift(index+1, size, -1);
        size--;

        //for object types, nullify the object at the end
    }

    /**
     * This removes the specified range of elements.
     *
     * @param from the first element to be removed, 0 ... size
     * @param to one after the last element to be removed, from ... size
     */
    public void removeRange(int from, int to) {
        if (to > size)
            throw new IllegalArgumentException(String2.ERROR + " in DoubleArray.removeRange: to (" + 
                to + ") > size (" + size + ").");
        if (from >= to) {
            if (from == to) 
                return;
            throw new IllegalArgumentException(String2.ERROR + " in DoubleArray.removeRange: from (" + 
                from + ") > to (" + to + ").");
        }
        for (int i = from; i < to; i++) {
            map.remove(i);
        }
        int shiftAmount = to - from;
        shift(to, size, -shiftAmount);
        size -= shiftAmount;

        //for object types, nullify the objects at the end
    }

    /**
     * Moves elements 'first' through 'last' (inclusive)
     *   to 'destination'.
     *
     * @param first  the first to be move
     * @param last  (exclusive)
     * @param destination the destination, can't be in the range 'first+1..last-1'.
     */
    public void move(int first, int last, int destination) {
        String errorIn = String2.ERROR + " in DoubleArray.move:\n";

        if (first < 0) 
            throw new RuntimeException(errorIn + "first (" + first + ") must be >= 0.");
        if (last < first || last > size)
            throw new RuntimeException( 
                errorIn + "last (" + last + ") must be >= first (" + first + 
                ") and <= size (" + size + ").");
        if (destination < 0 || destination > size)
            throw new RuntimeException( 
                errorIn + "destination (" + destination + 
                ") must be between 0 and size (" + size + ").");
        if (destination > first && destination < last)
            throw new RuntimeException(
              errorIn + "destination (" + destination + ") must be <= first (" + 
              first + ") or >= last (" + last + ").");
        if (first == last || destination == first || destination == last) 
            return; //nothing to do
        //String2.log("move first=" + first + " last=" + last + " dest=" + destination);
        //String2.log("move initial " + String2.toCSSVString(array));

        double[] array = toArray();
        //store the range to be moved
        int nToMove = last - first;
        double[] temp = new double[nToMove];
        System.arraycopy(array, first, temp, 0, nToMove);

        //if moving to left...    (draw diagram to visualize this)
        if (destination < first) {
            System.arraycopy(array, destination, array, destination + nToMove, first - destination);
            //String2.log("move after shift " + String2.toCSSVString(array));

            //copy temp data into place
            System.arraycopy(temp, 0, array, destination, nToMove);
        } else {
            //moving to right
            System.arraycopy(array, last, array, first, destination - last);
            //String2.log("move after shift " + String2.toCSSVString(array));

            //copy temp data into place
            System.arraycopy(temp, 0, array, destination - nToMove, nToMove);
        }
        //String2.log("move done " + String2.toCSSVString(array));
        clear();
        add(array);
    }

    /**
     * This just keeps the rows for the 'true' values in the bitset.
     * Rows that aren't kept are removed.
     * The resulting PrimitiveArray is compacted (i.e., it has a smaller size()).
     *
     * @param bitset The BitSet indicating which rows (indices) should be kept.
     */
    public void justKeep(BitSet bitset) {
        int newSize = 0;
        Map<Integer, Double> newMap = new HashMap<>();
        for (int row = 0; row < size; row++) {
            if ( bitset.get(row)) {
                double value = get(row);
                if (!Double.isNaN(value)) {
                   newMap.put(newSize, value);
                }
                newSize++;
            }
        }
        size = newSize;
        map = newMap;
    }    

    /**
     * This ensures that the capacity is at least 'minCapacity'.
     *
     * @param minCapacity the minimum acceptable capacity.
     *    minCapacity is type long, but &gt;= Integer.MAX_VALUE will throw exception.
     */
    public void ensureCapacity(long minCapacity) {
        // Do nothing here. The capacity needed for this data structure is unlikely to be actually known.
    }

    /**
     * This returns an array (perhaps 'array') which has 'size' elements.
     *
     * @return an array (perhaps 'array') which has 'size' elements.
     *   Unsigned integer types will return an array with their storage type
     *   e.g., ULongArray returns a long[].
     */
    public double[] toArray() {
        
        Math2.ensureMemoryAvailable(8L * size, "SparseDoubleArray.toArray");
        double[] dar = new double[size];
        for (int i = 0; i < size; i++) {
            dar[i] = get(i);
        }
        return dar;

    }
   
    /**
     * This returns a primitive[] (perhaps 'array') which has 'size' 
     * elements.
     *
     * @return a primitive[] (perhaps 'array') which has 'size' elements.
     *   Unsigned integer types will return an array with their storage type
     *   e.g., ULongArray returns a long[].
     */
    public Object toObjectArray() {
        return toArray();
    }

    /**
     * This returns a double[] which has 'size' elements.
     *
     * @return a double[] which has 'size' elements.
     */
    public double[] toDoubleArray() {
        return toArray();
    }

    /**
     * This returns a String[] which has 'size' elements.
     *
     * @return a String[] which has 'size' elements.
     *    If a value isn't finite, it appears as "".
     */
    public String[] toStringArray() {
        Math2.ensureMemoryAvailable(12L * size, "DoubleArray.toStringArray"); //12L is feeble minimal estimate
        String[] sar = new String[size];
        for (int i = 0; i < size; i++) {
            double d = get(i);
            sar[i] = Double.isFinite(d)? String.valueOf(d) : "";
        }
        return sar;
    }

    /**
     * This gets a specified element.
     *
     * @param index 0 ... size-1
     * @return the specified element
     */
    public double get(int index) {
        if (index >= size)
            throw new IllegalArgumentException(String2.ERROR + " in DoubleArray.get: index (" + 
                index + ") >= size (" + size + ").");
        return map.getOrDefault(index, Double.NaN);
    }

    /**
     * This sets a specified element.
     *
     * @param index 0 ... size-1
     * @param value the value for that element
     */
    public void set(int index, double value) {
        if (index >= size)
            throw new IllegalArgumentException(String2.ERROR + " in DoubleArray.set: index (" + 
                index + ") >= size (" + size + ").");
        if (Double.isNaN(value)) {
            map.remove(index);
        } else {
            map.put(index, value);
        }
    }


    /**
     * Return a value from the array as an int.
     * Floating point values are rounded.
     * 
     * @param index the index number 0 ... size-1
     * @return the value as an int. This may return Integer.MAX_VALUE.
     */
    public int getInt(int index) {
        return Math2.roundToInt(get(index));
    }

    //getRawInt(index) uses default getInt(index) since missingValue must be converted

    /**
     * Set a value in the array as an int.
     * 
     * @param index the index number 0 .. size-1
     * @param i the value. Integer.MAX_VALUE is NOT converted
     *   to this Double.NaN.
     */
    public void setInt(int index, int i) {
        set(index, i);
    }

    /**
     * Return a value from the array as a long.
     * 
     * @param index the index number 0 ... size-1
     * @return the value as a long. 
     *   This may return Long.MAX_VALUE.
     */
    public long getLong(int index) {
        return Math2.roundToLong(get(index));
    }

    /**
     * Set a value in the array as a long.
     * 
     * @param index the index number 0 .. size-1
     * @param i the value. Long.MAX_VALUE is NOT converted
     *   to Double.NaN.
     */
    public void setLong(int index, long i) {
        set(index, i);
    }

    /**
     * Return a value from the array as a ulong.
     * 
     * @param index the index number 0 ... size-1
     * @return the value as a ulong. 
     *   NaN is returned as null.
     */
    public BigInteger getULong(int index) {
        double d = get(index);
        return Double.isFinite(d)? Math2.roundToULongOrNull(new BigDecimal(d)) : null;
    }

    /**
     * Set a value in the array as a ulong.
     * 
     * @param index the index number 0 .. size-1
     * @param i the value. For numeric PrimitiveArray's, it is narrowed 
     *   if needed by methods like Math2.narrowToByte(long).
     */
    public void setULong(int index, BigInteger i) {
        set(index, Math2.ulongToDoubleNaN(i));
    }

    /**
     * Return a value from the array as a float.
     * 
     * @param index the index number 0 .. size-1
     * @return the value as a float. String values are parsed
     *   with String2.parseFloat and so may return Float.NaN.
     *   Large values like 1e100 are returned as Float.NaN, not Float.POSITIVE_INFINITY.
     */
    public float getFloat(int index) {
        return Math2.doubleToFloatNaN(get(index));
    }

    /**
     * Set a value in the array as a float.
     * 
     * @param index the index number 0 .. size-1
     * @param d the value. For numeric PrimitiveArray, it is narrowed 
     *   if needed by methods like Math2.roundToFloat(d).
     */
    public void setFloat(int index, float d) {
        set(index, (double)d);
    }

    /**
     * Return a value from the array as a double.
     * 
     * @param index the index number 0 .. size-1
     * @return the value as a double. String values are parsed
     *   with String2.parseDouble and so may return Double.NaN.
     */
    public double getDouble(int index) {
        return get(index);
    }

    /**
     * Set a value in the array as a double.
     * 
     * @param index the index number 0 .. size-1
     * @param d the value. For numeric PrimitiveArray, it is narrowed 
     *   if needed by methods like Math2.roundToDouble(d).
     */
    public void setDouble(int index, double d) {
        set(index, d);
    }

    /**
     * Return a value from the array as a String (where the cohort missing value
     * appears as "", not a value).
     * 
     * @param index the index number 0 .. 
     * @return For numeric types, this returns (String.valueOf(ar[index])), or "" for NaN or infinity.
     *   If this PA is unsigned, this method returns the unsigned value.
     */
    public String getString(int index) {
        double b = get(index);
        return Double.isFinite(b)? String.valueOf(b) : "";
    }

    /**
     * Return a value from the array as a String suitable for a JSON file. 
     * char returns a String with 1 character.
     * String returns a json String with chars above 127 encoded as \\udddd.
     * 
     * @param index the index number 0 ... size-1 
     * @return For numeric types, this returns ("" + ar[index]), or null for NaN or infinity.
     */
    public String getJsonString(int index) {
        return String2.toJson(get(index));
    }

    /**
     * Return a value from the array as a String.
     * This "raw" variant leaves missingValue from integer data types 
     * (e.g., ByteArray missingValue=127) AS IS, regardless of maxIsMV.
     * FloatArray and DoubleArray return "NaN" if the stored value is NaN.  That's different than getRawString!!!
     *
     * <p>Float and DoubleArray overwrite this.
     * 
     * @param index the index number 0 ... size-1
     * @return the value as a String. 
     */
    public String getRawestString(int index) {
        return String.valueOf(get(index));
    }


    /**
     * Set a value in the array as a String.
     * 
     * @param index the index number 0 .. 
     * @param s the value. For numeric PrimitiveArray's, it is parsed
     *   with String2.parseDouble.
     */
    public void setString(int index, String s) {
        set(index, String2.parseDouble(s));
    }

    /**
     * This finds the first value which equals 'lookFor' starting at index 'startIndex'.
     *
     * @param lookFor the value to be looked for.
     *    This correctly searches for NaN.
     * @return the index where 'lookFor' is found, or -1 if not found.
     */
    public int indexOf(double lookFor) {
        return indexOf(lookFor, 0);
    }


    /**
     * This finds the first value which equals 'lookFor' starting at index 'startIndex'.
     *
     * @param lookFor the value to be looked for.
     *    This correctly searches for NaN.
     * @param startIndex 0 ... size-1
     * @return the index where 'lookFor' is found, or -1 if not found.
     */
    public int indexOf(double lookFor, int startIndex) {
        if (Double.isNaN(lookFor)) {
            for (int i = startIndex; i < size; i++) 
                if (Double.isNaN(get(i))) 
                    return i;
            return -1;
        }

        for (int i = startIndex; i < size; i++) 
            if (get(i) == lookFor) 
                return i;
        return -1;
    }

    /**
     * This finds the first value which equals 'lookFor' starting at index 'startIndex'.
     *
     * @param lookFor the value to be looked for
     * @param startIndex 0 ... size-1
     * @return the index where 'lookFor' is found, or -1 if not found.
     */
    public int indexOf(String lookFor, int startIndex) {
        if (startIndex >= size)
            return -1;
        return indexOf(String2.parseDouble(lookFor), startIndex);
    }

    /**
     * This finds the last value which equals 'lookFor' starting at index 'startIndex'.
     *
     * @param lookFor the value to be looked for
     * @param startIndex 0 ... size-1. The search progresses towards 0.
     * @return the index where 'lookFor' is found, or -1 if not found.
     */
    public int lastIndexOf(double lookFor, int startIndex) {
        if (startIndex >= size)
            throw new IllegalArgumentException(String2.ERROR + " in DoubleArray.get: startIndex (" + 
                startIndex + ") >= size (" + size + ").");
        for (int i = startIndex; i >= 0; i--) 
            if (get(i) == lookFor) 
                return i;
        return -1;
    }

    /**
     * This finds the last value which equals 'lookFor' starting at index 'startIndex'.
     *
     * @param lookFor the value to be looked for
     * @param startIndex 0 ... size-1. The search progresses towards 0.
     * @return the index where 'lookFor' is found, or -1 if not found.
     */
    public int lastIndexOf(String lookFor, int startIndex) {
        return lastIndexOf(String2.parseDouble(lookFor), startIndex);
    }

    /**
     * If size != capacity, this makes a new 'array' of size 'size'
     * so capacity will equal size.
     */
    public void trimToSize() {
        // No implementation for sparse.
    }

    /**
     * Test if o is an DoubleArray with the same size and values.
     *
     * @param o the object that will be compared to this DoubleArray
     * @return true if equal.  o=null returns false.
     */
    public boolean equals(Object o) {
        return testEquals(o).length() == 0;
    }

    /**
     * Test if o is an DoubleArray with the same size and values,
     * but returns a String describing the difference (or "" if equal).
     * Here NaN in one array equals NaN in another array (whereas Java would say false).
     *
     * @param o
     * @return a String describing the difference (or "" if equal).
     *   o=null doesn't throw an exception.
     */
    public String testEquals(Object o) {
        if (!(o instanceof SparseDoubleArray))
            return "The two objects aren't equal: this object is a DoubleArray; the other is a " + 
                (o == null? "null" : o.getClass().getName()) + ".";
        SparseDoubleArray other = (SparseDoubleArray)o;
        if (other.size() != size) 
            return "The two DoubleArrays aren't equal: one has " + size + 
               " value(s); the other has " + other.size() + " value(s).";
        for (int i = 0; i < size; i++)
            if (!Math2.equalsIncludingNanOrInfinite(get(i), other.get(i)))
                return "The two DoubleArrays aren't equal: this[" + i + "]=" + get(i) + 
                                                       "; other[" + i + "]=" + other.get(i) + ".";
        return "";
    }


    /** 
     * This converts the elements into a Comma-Space-Separated-Value (CSSV) String.
     *
     * @return a Comma-Space-Separated-Value (CSSV) String representation 
     */
    public String toString() {
        return String2.toCSSVString(toArray()); //toArray() gets just 'size' elements
    }

    /** 
     * This converts the elements into an NCCSV attribute String, e.g.,: -128b, 127b
     *
     * @return an NCCSV attribute String
     */
    public String toNccsvAttString() {
        StringBuilder sb = new StringBuilder(size * 15);
        for (int i = 0; i < size; i++) 
            sb.append((i == 0? "" : ",") + String.valueOf(get(i)) + "d");
        return sb.toString();
    }

    /** 
     * This sorts the elements in ascending order.
     * To get the elements in reverse order, just read from the end of the list
     * to the beginning.
     */
    public void sort() {
        //see switchover point and speed comparison in 
        //  https://www.baeldung.com/java-arrays-sort-vs-parallelsort
        double[] array = toArray();
        if (size < 8192)
             Arrays.sort(array, 0, size);
        else Arrays.parallelSort(array, 0, size);
        clear();
        add(array);
        
    }
    
    @Override
    public void clear() {
        size = 0;
        maxIsMV = false;
        map.clear();
    }


    /**
     * This compares the values in this.row1 and otherPA.row2
     * and returns a negative integer, zero, or a positive integer if the 
     * value at index1 is less than, equal to, or greater than 
     * the value at index2.
     * NaN sorts highest.
     * Currently, this does not range check index1 and index2,
     * so the caller should be careful.
     *
     * @param index1 an index number 0 ... size-1
     * @param otherPA the other PrimitiveArray which must be the same (or close) PAType.
     * @param index2 an index number 0 ... size-1
     * @return returns a negative integer, zero, or a positive integer if the 
     *   value at index1 is less than, equal to, or greater than 
     *   the value at index2.  
     *   Think "array[index1] - array[index2]".
     */
    public int compare(int index1, PrimitiveArray otherPA, int index2) {
        return Double.compare(getDouble(index1), otherPA.getNiceDouble(index2));
    }

    /**
     * This copies the value in row 'from' to row 'to'.
     * This does not check that 'from' and 'to' are valid;
     * the caller should be careful.
     * The value for 'from' is unchanged.
     *
     * @param from an index number 0 ... size-1
     * @param to an index number 0 ... size-1
     */
    public void copy(int from, int to) {
        set(to, get(from));
    }

    /**
     * This reorders the values in 'array' based on rank.
     *
     * @param rank is an int with values (0 ... size-1) 
     * which points to the row number for a row with a specific 
     * rank (e.g., rank[0] is the row number of the first item 
     * in the sorted list, rank[1] is the row number of the
     * second item in the sorted list, ...).
     */
    public void reorder(int rank[]) {
        Map<Integer, Double> newMap = new HashMap<>();
        for (int i = 0; i < size; i++) {
            double value = get(rank[i]);
            if (!Double.isNaN(value)) {
                newMap.put(i, value);
            }
        }
        map = newMap;
    }

    /**
     * This reverses the order of the bytes in each value,
     * e.g., if the data was read from a little-endian source.
     */
    public void reverseBytes() {
        Object[] entries = map.entrySet().toArray();
        for (int i = 0; i < entries.length; i++) {
            Entry<Integer, Double> entry = (Entry<Integer,Double>)entries[i];
            map.put(entry.getKey(), Double.longBitsToDouble(Long.reverseBytes(
                    Double.doubleToLongBits(entry.getValue()))));
        }
    }

    /**
     * This writes 'size' elements to a DataOutputStream.
     *
     * @param dos the DataOutputStream
     * @return the number of bytes used per element (for Strings, this is
     *    the size of one of the strings, not others, and so is useless;
     *    for other types the value is consistent).
     *    But if size=0, this returns 0.
     * @throws Exception if trouble
     */
    public int writeDos(DataOutputStream dos) throws Exception {
        for (int i = 0; i < size; i++)
            dos.writeDouble(get(i));
        return size == 0? 0 : 8;
    }

    /**
     * This writes one element to a DataOutputStream.
     *
     * @param dos the DataOutputStream
     * @param i the index of the element to be written
     * @return the number of bytes used for this element
     *    (for Strings, this varies; for others it is consistent)
     * @throws Exception if trouble
     */
    public int writeDos(DataOutputStream dos, int i) throws Exception {
        dos.writeDouble(get(i));
        return 8;
    }

    /**
     * This reads/adds n elements from a DataInputStream.
     *
     * @param dis the DataInputStream
     * @param n the number of elements to be read/added
     * @throws Exception if trouble
     */
    public void readDis(DataInputStream dis, int n) throws Exception {
        for (int i = 0; i < n; i++)
            add(dis.readDouble());
    }

    /**
     * This reads/appends double values to this PrimitiveArray from a DODS DataInputStream,
     * and is thus the complement of externalizeForDODS.
     *
     * @param dis
     * @throws IOException if trouble
     */
    public void internalizeFromDODS(DataInputStream dis) throws java.io.IOException {
        int nValues = dis.readInt();
        dis.readInt(); //skip duplicate of nValues
        for (int i = 0; i < nValues; i++) 
            add(dis.readDouble());
    }

    /** 
     * This writes array[index] to a randomAccessFile at the current position.
     *
     * @param raf the RandomAccessFile
     * @param index
     * @throws Exception if trouble
     */
    public void writeToRAF(RandomAccessFile raf, int index) throws Exception {
        raf.writeDouble(get(index));
    }

    /** 
     * This reads one value from a randomAccessFile at the current position
     * and adds it to the PrimitiveArraay.
     *
     * @param raf the RandomAccessFile
     * @throws Exception if trouble
     */
    public void readFromRAF(RandomAccessFile raf) throws Exception {
        add(raf.readDouble());
    }

    /**
     * This appends the data in another pa to the current data.
     * WARNING: information may be lost from the incoming pa if this
     * primitiveArray is of a smaller type; see needPAType().
     *
     * @param pa pa must be the same or a narrower 
     *  data type, or the data will be narrowed with pa.getDouble.
     */
    public void append(PrimitiveArray pa) {
        int otherSize = pa.size(); 

        for (int i = 0; i < otherSize; i++) {
            double value = pa.getNiceDouble(i); // this converts mv's
            if (!Double.isNaN(value)) {
                map.put(size+i, value);
            }
        }
        size += otherSize; //do last to minimize concurrency problems
    }    

    /**
     * This appends the data in another pa to the current data.
     * This "raw" variant leaves missingValue from smaller data types 
     * (e.g., ByteArray missingValue=127) AS IS (even if maxIsMV=true).
     * WARNING: information may be lost from the incoming pa if this
     * primitiveArray is of a simpler type.
     *
     * @param pa pa must be the same or a narrower 
     *  data type, or the data will be narrowed with pa.getDouble.
     */
    public void rawAppend(PrimitiveArray pa) {
        int otherSize = pa.size(); 

        for (int i = 0; i < otherSize; i++) {
            double value = pa.getRawDouble(i); // this converts mv's
            if (!Double.isNaN(value)) {
                map.put(size+i, value);
            }
        }
        size += otherSize; //do last to minimize concurrency problems
    }
    
    /**
     * For all values, this unpacks the values by multiplying by scale and then adding addOffset.
     * DoubleArray overrides the PrimitiveArray version for optimization purposes.
     *
     * @param scale
     * @param addOffset
     */
    @Override
    public void scaleAddOffset(double scale, double addOffset) {
        if (scale == 1 && addOffset == 0)
            return;
        // NaNs remain NaNs 
        Object[] entries = map.entrySet().toArray();
        for (int i = 0; i < entries.length; i++) {
            Entry<Integer, Double> entry = (Entry<Integer,Double>)entries[i];
            map.put(entry.getKey(), entry.getValue() * scale + addOffset);
        } 
    }
     
    /**
     * For all values, this packs the values by adding addOffset then multiplying by scale.
     * DoubleArray overrides the PrimitiveArray version for optimization purposes.
     *
     * @param scale
     * @param addOffset
     */
    @Override
    public void addOffsetScale(double addOffset, double scale) {
        if (scale == 1 && addOffset == 0)
            return;
        // NaNs remain NaNs 
        Object[] entries = map.entrySet().toArray();
        for (int i = 0; i < entries.length; i++) {
            Entry<Integer, Double> entry = (Entry<Integer,Double>)entries[i];
            map.put(entry.getKey(), (entry.getValue() + addOffset) * scale);
        }
    }

    /**
     * This populates 'indices' with the indices (ranks) of the values in this DoubleArray
     * (ties get the same index). For example, 10,10,25,3 returns 1,1,2,0.
     *
     * @param indices the intArray that will capture the indices of the values 
     *  (ties get the same index). For example, 10,10,25,3 returns 1,1,2,0.
     * @return a PrimitveArray (the same type as this class) with the unique values, sorted.
     *     If all the values are unique and already sorted, this returns 'this'.
     */
    public PrimitiveArray makeIndices(IntArray indices) {
        indices.clear();
        if (size == 0) {
            return new SparseDoubleArray();
        }

        //make a hashMap with all the unique values (associated values are initially all dummy)
        Integer dummy = -1;
        HashMap<Double, Integer> hashMap = new HashMap<>(Math2.roundToInt(1.4 * size));
        double lastValue = get(0); //since lastValue often equals currentValue, cache it
        hashMap.put(lastValue, dummy);
        boolean alreadySorted = true;
        for (int i = 1; i < size; i++) {
            double currentValue = get(i);
            if (currentValue != lastValue) {
                if (currentValue < lastValue) 
                    alreadySorted = false;
                lastValue = currentValue;
                hashMap.put(lastValue, dummy);
            }
        }

        //quickly deal with: all unique and already sorted
        Set<Double> keySet = hashMap.keySet();
        int nUnique = keySet.size();
        if (nUnique == size && alreadySorted) {
            indices.ensureCapacity(size);
            for (int i = 0; i < size; i++)
                indices.add(i); 
            //String2.log("DoubleArray.makeIndices all unique and already sorted.");
            return this; //the PrimitiveArray with unique values
        }


        //store all the elements in an array
        double[] unique = new double[nUnique];
        Iterator<Double> iterator = keySet.iterator();
        int count = 0;
        while (iterator.hasNext())
            unique[count++] = iterator.next();
        if (nUnique != count)
            throw new RuntimeException("DoubleArray.makeRankArray nUnique(" + nUnique +
                ") != count(" + count + ")!");

        //sort them
        Arrays.sort(unique);

        //put the unique values back in the hashMap with the ranks as the associated values
        //and make tUnique 
        double[] tUnique = new double[nUnique];
        for (int i = 0; i < count; i++) {
            hashMap.put(unique[i], i);
            tUnique[i] = ((Double)unique[i]).doubleValue();
        }

        //convert original values to ranks
        int[] ranks = new int[size];
        lastValue = get(0);
        ranks[0] = (hashMap.get(lastValue)).intValue();
        int lastRank = ranks[0];
        for (int i = 1; i < size; i++) {
            if (get(i) == lastValue) {
                ranks[i] = lastRank;
            } else {
                lastValue = get(i);
                ranks[i] = (hashMap.get(lastValue)).intValue();
                lastRank = ranks[i];
            }
        }

        //store the results in ranked
        indices.append(new IntArray(ranks));

        return new SparseDoubleArray(tUnique);

    }

    /**
     * This changes all instances of the first value to the second value.
     *
     * @param tFrom the original value (use "" or "NaN"  for standard missingValue)
     * @param tTo   the new value (use "" or "NaN"  for standard missingValue)
     * @return the number of values switched
     */
    public int switchFromTo(String tFrom, String tTo) {
        System.out.println("SwitchFromTo: " + tFrom + " " + tTo);
        double from = String2.parseDouble(tFrom);
        double to   = String2.parseDouble(tTo);
        if ((Double.isNaN(from) && Double.isNaN(to)) ||
            (from == to))
            return 0;
        int count = 0;
        // This would be really bad for a sparse array. Consider changing the sparse value to 'to'.
        if (Double.isNaN(from)) {
            for (int i = 0; i < size; i++) {
                if (!map.containsKey(i)) {
                    map.put(i, to);
                    count++;
                } else {
                }
            }
        } else {
            Object[] entries = map.entrySet().toArray();
            for (int i = 0; i < entries.length; i++) {
                Entry<Integer, Double> entry = (Entry<Integer,Double>)entries[i];
                if (Math2.almostEqual(9, entry.getValue(), from)) {
                    if (Double.isNaN(to)) {
                        map.remove(entry.getKey());
                    } else {
                        map.put(entry.getKey(), to);
                    }
                    count++;
                }
            }
        }
        
        System.out.print("***********************************************************");
        System.out.println("Post switchFromTo SparseDouble. Size: " + size + " mapCount: " + map.size());
        System.out.print("***********************************************************");
        return count;
    }

    /** 
     * Assuming this array is sorted, this removes duplicates,
     * including Math2.almostEqual5 values.
     */
    public void removeDuplicatesAE5() {
        if (size <= 1)
            return;
        int nValid = 1;
        Map<Integer, Double> newMap = new HashMap<>();
        newMap.put(0, get(0));
        for (int i = 1; i < size; i++) 
            if (!Math2.almostEqual(5, get(i), newMap.get(nValid - 1))) {
                newMap.put(nValid++, get(i));
            }
        map = newMap;
        size = nValid;
    }


    /**
     * This tests for adjacent tied values and returns the index of the first tied value.
     * Adjacent NaNs are treated as ties.
     *
     * @return the index of the first tied value (or -1 if none).
     */
    public int firstTie() {
        for (int i = 1; i < size; i++) {
            if (Double.isNaN(get(i - 1))) {
                if (Double.isNaN(get(i)))
                    return i - 1;
            } else if (get(i - 1) == get(i)) {
                return i - 1;
            }
        }
        return -1;
    }

    /**
     * This tests if the values in the array are evenly spaced (ascending or descending)
     * (via Math2.almostEqual9, or easier test if first 12 digits are same).
     *
     * @return "" if the values in the array are evenly spaced;
     *   or an error message if not.
     *   If size is 0 or 1, this returns "".
     */
    public String isEvenlySpaced() {
        if (size <= 2)
            return "";
        //This diff is closer to exact 
        //and usually detects not-evenly-spaced anywhere in the array on first test!
        double diff = (get(size-1) - get(0)) / (size - 1);
        for (int i = 1; i < size; i++) {
            //This is a difficult test to do well. See tests below.
            //1e7 avoids dEps test in almostEqual
            if (Math2.almostEqual( 9, (get(i) - get(i - 1)) * 1e7, diff * 1e7)) { 
                //String2.log(i + " passed first test");
            } else if (
                //do easier test if first 12 digits are same
                Math2.almostEqual(12, get(i - 1) + diff, get(i)) && 
                Math2.almostEqual( 2, (get(i) - get(i - 1)) * 1e7, diff * 1e7)) { 
                //String2.log(i + " passed second test " + (array[i] - array[i - 1]) + " " + diff);
            } else {
                return MessageFormat.format(ArrayNotEvenlySpaced, getClass().getSimpleName(),
                    "" + (i - 1), "" + get(i - 1), "" + i, "" + get(i),
                    "" + (get(i) - get(i-1)), "" + diff);
            }
        }
        return "";
    }

    /**
     * This finds the number of non-missing values, and the index of the min and
     *    max value.
     *
     * @return int[3], [0]=the number of non-missing values, 
     *    [1]=index of min value (if tie, index of last found; -1 if all mv),
     *    [2]=index of max value (if tie, index of last found; -1 if all mv).
     */
    public int[] getNMinMaxIndex() {
        int n = 0, tmini = -1, tmaxi = -1;
        double tmin =  Double.MAX_VALUE;
        double tmax = -Double.MAX_VALUE;
        for (Map.Entry<Integer,Double> entry : map.entrySet()) {
            double v = entry.getValue();
            if (Double.isFinite(v)) {
                n++;
                if (v <= tmin) {tmini = entry.getKey(); tmin = v; }
                if (v >= tmax) {tmaxi = entry.getKey(); tmax = v; }
            }
        }
        return new int[]{n, tmini, tmaxi};
    }


    /**
     * Assuming this is an ascending sorted array of "seconds since 1970-01-01T00:00:00Z" values,
     * this returns a String with a list of gaps larger than the median or gap=NaN (one per line).
     * The values of this array won't be changed.
     *
     * @return a descriptive String with a list of gaps larger than the median 
     *    (one per line, with info at the top and with a trailing newline),
     *  There will be a results line for any gaps that are NaN.
     *  If the median is NaN, this will return a list of gaps that are NaN.
     */
    public String findTimeGaps() {

        if (size <= 2)
            return "Time gaps: (none, because nTimeValues=" + size + ")\n" +
                   "nGaps=0\n";

        //find median 
        DoubleArray gaps = new DoubleArray(size - 1, false);
        for (int i = 1; i < size; i++)  //1 because looking back
            gaps.add(get(i) - get(i-1));
        gaps.sort();
        int size1o2 = (size / 2) - 1;
        double median = 
            (size-1) % 2 == 0?  //even number of gaps?
                (gaps.get(size1o2) + gaps.get(size1o2 + 1)) / 2.0 : //average of 2 values
                gaps.get(size1o2);
        gaps = null; //allow gc

        //look for gaps that are NaN or > median
        StringBuilder sb = new StringBuilder("Time gaps greater than the median (" + 
            Calendar2.elapsedTimeString(median * 1000)+ "):");
        int count = 0;
        for (int i = 1; i < size; i++) { //1 because looking back
            double gap = get(i) - get(i-1);
            if (!Double.isFinite(gap) || gap > median) {
                if (count++ == 0) 
                    sb.append('\n');
                sb.append("[" + (i-1) + "]=" + 
                    Calendar2.safeEpochSecondsToIsoStringTZ(get(i-1), "(NaN)") +
                    " -> [" + i + "]=" + 
                    Calendar2.safeEpochSecondsToIsoStringTZ(get(i), "(NaN)") +
                    ", gap=" + Calendar2.elapsedTimeString(gap * 1000) + "\n");
            }
        }
        if (count == 0)
            sb.append(" (none)\n");
        sb.append("nGaps=" + count + "\n");
        return sb.toString();
    }
       

    /**
     * This tests the methods of this class.
     *
     * @throws Throwable if trouble.
     */
    public static void basicTest() throws Throwable{
        String2.log("*** SparseDoubleArray.basicTest");

        SparseDoubleArray anArray = SparseDoubleArray.fromCSV(-Double.MAX_VALUE + ", " + Double.MAX_VALUE + ", , NaN, 1e400 ");
        Test.ensureEqual(anArray.toString(),         "-1.7976931348623157E308, 1.7976931348623157E308, NaN, NaN, NaN", "");
        Test.ensureEqual(anArray.toNccsvAttString(), "-1.7976931348623157E308d,1.7976931348623157E308d,NaNd,NaNd,NaNd", "");

        //** test default constructor and many of the methods
        anArray = new SparseDoubleArray();
        Test.ensureEqual(anArray.isIntegerType(), false, "");
        Test.ensureEqual(anArray.missingValue().getRawDouble(), Double.NaN, "");
        anArray.addString("");
        Test.ensureEqual(anArray.get(0),               Double.NaN, "");
        Test.ensureEqual(anArray.getRawInt(0),         Integer.MAX_VALUE, "");
        Test.ensureEqual(anArray.getRawDouble(0),      Double.NaN, "");
        Test.ensureEqual(anArray.getUnsignedDouble(0), Double.NaN, "");
        Test.ensureEqual(anArray.getRawString(0),      "", "");
        Test.ensureEqual(anArray.getRawestString(0),   "NaN", "");
        Test.ensureEqual(anArray.getRawNiceDouble(0),  Double.NaN, "");
        Test.ensureEqual(anArray.getInt(0),            Integer.MAX_VALUE, "");
        Test.ensureEqual(anArray.getDouble(0),         Double.NaN, "");
        Test.ensureEqual(anArray.getString(0), "", "");
        anArray.clear();

        anArray.add(0.1);
        Test.ensureEqual(anArray.getDouble(0),         0.1, "");
        Test.ensureEqual(anArray.getNiceDouble(0),     0.1, "");
        Test.ensureEqual(anArray.getRawNiceDouble(0),  0.1, "");
        anArray.clear();


        String2.log("getClass().getName()=" + anArray.getClass().getName());
        String2.log("getClass().getSimpleName()=" + anArray.getClass().getSimpleName());
        Test.ensureEqual(anArray.size(), 0, "");
        anArray.add(1e307);
        Test.ensureEqual(anArray.size(), 1, "");
        Test.ensureEqual(anArray.get(0), 1e307, "");
        Test.ensureEqual(anArray.getInt(0), Integer.MAX_VALUE, "");
        Test.ensureEqual(anArray.getFloat(0), Float.NaN, "");
        Test.ensureEqual(anArray.getDouble(0), 1e307, "");
        Test.ensureEqual(anArray.getString(0), "1.0E307", "");
        Test.ensureEqual(anArray.elementType(), PAType.DOUBLE, "");
        double tArray[] = anArray.toArray();
        Test.ensureEqual(tArray, new double[]{1e307}, "");

        //intentional errors
        try {anArray.get(1);              throw new Throwable("It should have failed.");
        } catch (Exception e) {
            Test.ensureEqual(e.toString(), 
                "java.lang.IllegalArgumentException: ERROR in DoubleArray.get: index (1) >= size (1).", "");
        }
        try {anArray.set(1, 100);         throw new Throwable("It should have failed.");
        } catch (Exception e) {
            Test.ensureEqual(e.toString(), 
                "java.lang.IllegalArgumentException: ERROR in DoubleArray.set: index (1) >= size (1).", "");
        }
        try {anArray.getInt(1);           throw new Throwable("It should have failed.");
        } catch (Exception e) {
            Test.ensureEqual(e.toString(), 
                "java.lang.IllegalArgumentException: ERROR in DoubleArray.get: index (1) >= size (1).", "");
        }
        try {anArray.setInt(1, 100);      throw new Throwable("It should have failed.");
        } catch (Exception e) {
            Test.ensureEqual(e.toString(), 
                "java.lang.IllegalArgumentException: ERROR in DoubleArray.set: index (1) >= size (1).", "");
        }
        try {anArray.getLong(1);          throw new Throwable("It should have failed.");
        } catch (Exception e) {
            Test.ensureEqual(e.toString(), 
                "java.lang.IllegalArgumentException: ERROR in DoubleArray.get: index (1) >= size (1).", "");
        }
        try {anArray.setLong(1, 100);     throw new Throwable("It should have failed.");
        } catch (Exception e) {
            Test.ensureEqual(e.toString(), 
                "java.lang.IllegalArgumentException: ERROR in DoubleArray.set: index (1) >= size (1).", "");
        }
        try {anArray.getFloat(1);         throw new Throwable("It should have failed.");
        } catch (Exception e) {
            Test.ensureEqual(e.toString(), 
                "java.lang.IllegalArgumentException: ERROR in DoubleArray.get: index (1) >= size (1).", "");
        }
        try {anArray.setFloat(1, 100);    throw new Throwable("It should have failed.");
        } catch (Exception e) {
            Test.ensureEqual(e.toString(), 
                "java.lang.IllegalArgumentException: ERROR in DoubleArray.set: index (1) >= size (1).", "");
        }
        try {anArray.getDouble(1);        throw new Throwable("It should have failed.");
        } catch (Exception e) {
            Test.ensureEqual(e.toString(), 
                "java.lang.IllegalArgumentException: ERROR in DoubleArray.get: index (1) >= size (1).", "");
        }
        try {anArray.setDouble(1, 100);   throw new Throwable("It should have failed.");
        } catch (Exception e) {
            Test.ensureEqual(e.toString(), 
                "java.lang.IllegalArgumentException: ERROR in DoubleArray.set: index (1) >= size (1).", "");
        }
        try {anArray.getString(1);        throw new Throwable("It should have failed.");
        } catch (Exception e) {
            Test.ensureEqual(e.toString(), 
                "java.lang.IllegalArgumentException: ERROR in DoubleArray.get: index (1) >= size (1).", "");
        }
        try {anArray.setString(1, "100"); throw new Throwable("It should have failed.");
        } catch (Exception e) {
            Test.ensureEqual(e.toString(), 
                "java.lang.IllegalArgumentException: ERROR in DoubleArray.set: index (1) >= size (1).", "");
        }

        //set NaN returned as NaN
        anArray.setDouble(0, Double.NaN);   Test.ensureEqual(anArray.getDouble(0), Double.NaN, ""); 
        anArray.setDouble(0, -1e300);       Test.ensureEqual(anArray.getDouble(0), -1e300, ""); 
        anArray.setDouble(0, 2.2);          Test.ensureEqual(anArray.getDouble(0), 2.2,        ""); 
        anArray.setFloat( 0, Float.NaN);    Test.ensureEqual(anArray.getFloat(0),  Float.NaN,  ""); 
        anArray.setFloat( 0, -1e33f);       Test.ensureEqual(anArray.getFloat(0),  -1e33f,  ""); 
        anArray.setFloat( 0, 3.3f);         Test.ensureEqual(anArray.getFloat(0),  3.3f,       ""); 
        anArray.setLong(0, Long.MAX_VALUE); Test.ensureEqual(anArray.getLong(0),   Long.MAX_VALUE, ""); 
        anArray.setLong(0, 9123456789L);    Test.ensureEqual(anArray.getLong(0),   9123456789L, ""); 
        anArray.setLong(0, 4);              Test.ensureEqual(anArray.getLong(0),   4, ""); 
        anArray.setInt(0,Integer.MAX_VALUE);Test.ensureEqual(anArray.getInt(0),    Integer.MAX_VALUE, ""); 
        anArray.setInt(0, 1123456789);      Test.ensureEqual(anArray.getInt(0),    1123456789, ""); 
        anArray.setInt(0, 5);               Test.ensureEqual(anArray.getInt(0),    5, ""); 


        //** test capacity constructor, test expansion, test clear
        anArray = new SparseDoubleArray(2, false);
        Test.ensureEqual(anArray.size(), 0, "");
        for (int i = 0; i < 10; i++) {
            anArray.add(i);   
            Test.ensureEqual(anArray.get(i), i, "");
            Test.ensureEqual(anArray.size(), i+1, "");
        }
        Test.ensureEqual(anArray.size(), 10, "");
        anArray.clear();
        Test.ensureEqual(anArray.size(), 0, "");

        //active
        anArray = new SparseDoubleArray(3, true);
        Test.ensureEqual(anArray.size(), 3, "");
        Test.ensureEqual(anArray.get(2), 0, "");


        //** test array constructor
        anArray = new SparseDoubleArray(new double[]{0,2.2,4,6,8});
        Test.ensureEqual(anArray.size(), 5, "");
        Test.ensureEqual(anArray.get(0), 0, "");
        Test.ensureEqual(anArray.get(1), 2.2, "");
        Test.ensureEqual(anArray.get(2), 4, "");
        Test.ensureEqual(anArray.get(3), 6, "");
        Test.ensureEqual(anArray.get(4), 8, "");

        //test compare
        Test.ensureEqual(anArray.compare(1, 3), -1, "");
        Test.ensureEqual(anArray.compare(1, 1),  0, "");
        Test.ensureEqual(anArray.compare(3, 1),  1, "");

        //test toString
        Test.ensureEqual(anArray.toString(), "0.0, 2.2, 4.0, 6.0, 8.0", "");

        //test calculateStats
        anArray.addString("");
        double stats[] = anArray.calculateStats();
        anArray.remove(5);
        Test.ensureEqual(stats[STATS_N], 5, "");
        Test.ensureEqual(stats[STATS_MIN], 0, "");
        Test.ensureEqual(stats[STATS_MAX], 8, "");
        Test.ensureEqual(stats[STATS_SUM], 20.2, "");

        //test indexOf(int) indexOf(String)
        Test.ensureEqual(anArray.indexOf(0, 0),  0, "");
        Test.ensureEqual(anArray.indexOf(0, 1), -1, "");
        Test.ensureEqual(anArray.indexOf(8, 0),  4, "");
        Test.ensureEqual(anArray.indexOf(9, 0), -1, "");

        Test.ensureEqual(anArray.indexOf("0", 0),  0, "");
        Test.ensureEqual(anArray.indexOf("0", 1), -1, "");
        Test.ensureEqual(anArray.indexOf("8", 0),  4, "");
        Test.ensureEqual(anArray.indexOf("9", 0), -1, "");

        //test remove
        anArray.remove(1);
        Test.ensureEqual(anArray.size(), 4, "");
        Test.ensureEqual(anArray.get(0), 0, "");
        Test.ensureEqual(anArray.get(1), 4, "");
        Test.ensureEqual(anArray.get(3), 8, "");

        //test atInsert(index, value)
        anArray.atInsert(1, 22);
        Test.ensureEqual(anArray.size(), 5, "");
        Test.ensureEqual(anArray.get(0), 0, "");
        Test.ensureEqual(anArray.get(1),22, "");
        Test.ensureEqual(anArray.get(2), 4, "");
        Test.ensureEqual(anArray.get(4), 8, "");
        anArray.remove(1);

        //test removeRange
        anArray.removeRange(4, 4); //make sure it is allowed
        anArray.removeRange(1, 3);
        Test.ensureEqual(anArray.size(), 2, "");
        Test.ensureEqual(anArray.get(0), 0, "");
        Test.ensureEqual(anArray.get(1), 8, "");

        //test (before trimToSize) that toString, toDoubleArray, and toStringArray use 'size'
        Test.ensureEqual(anArray.toString(), "0.0, 8.0", "");
        Test.ensureEqual(anArray.toDoubleArray(), new double[]{0, 8}, "");
        Test.ensureEqual(anArray.toStringArray(), new String[]{"0.0", "8.0"}, "");

        //test trimToSize
        anArray.trimToSize();
        Test.ensureEqual(anArray.size, 2, "");

        //test equals
        SparseDoubleArray anArray2 = new SparseDoubleArray();
        anArray2.add(0); 
        Test.ensureEqual(anArray.testEquals(null), 
            "The two objects aren't equal: this object is a DoubleArray; the other is a null.", "");
        Test.ensureEqual(anArray.testEquals("A String"), 
            "The two objects aren't equal: this object is a DoubleArray; the other is a java.lang.String.", "");
        Test.ensureEqual(anArray.testEquals(anArray2), 
            "The two DoubleArrays aren't equal: one has 2 value(s); the other has 1 value(s).", "");
        Test.ensureTrue(!anArray.equals(anArray2), "");
        anArray2.addString("7");
        Test.ensureEqual(anArray.testEquals(anArray2), 
            "The two DoubleArrays aren't equal: this[1]=8.0; other[1]=7.0.", "");
        Test.ensureTrue(!anArray.equals(anArray2), "");
        anArray2.setString(1, "8");
        Test.ensureEqual(anArray.testEquals(anArray2), "", "");
        Test.ensureTrue(anArray.equals(anArray2), "");

        //test toObjectArray
        Test.ensureEqual(anArray.toArray(), anArray.toObjectArray(), "");

        //test toDoubleArray
        Test.ensureEqual(anArray.toDoubleArray(), new double[]{0, 8}, "");

        //test reorder
        int rank[] = {1, 0};
        anArray.reorder(rank);
        Test.ensureEqual(anArray.toDoubleArray(), new double[]{8, 0}, "");


        //** test append and clone
        anArray = new SparseDoubleArray(new double[]{1});
        anArray.append(new ByteArray(new byte[]{5, -5}));
        Test.ensureEqual(anArray.toDoubleArray(), new double[]{1, 5, -5}, "");
        anArray.append(new StringArray(new String[]{"a", "9"}));
        Test.ensureEqual(anArray.toDoubleArray(), new double[]{1, 5, -5, Double.NaN, 9}, "");
        anArray2 = (SparseDoubleArray)anArray.clone();
        Test.ensureEqual(anArray2.toDoubleArray(), new double[]{1, 5, -5, Double.NaN, 9}, "");

        //test sort: ensure mv sorts high
        anArray = new SparseDoubleArray(new double[]{-1, 1, Double.NaN});
        anArray.sort();
        Test.ensureEqual(anArray.toString(), "-1.0, 1.0, NaN", "");

        //test move
        anArray = new SparseDoubleArray(new double[]{0,1,2,3,4});
        anArray.move(1,3,0);
        Test.ensureEqual(anArray.toArray(), new double[]{1,2,0,3,4}, "");

        anArray = new SparseDoubleArray(new double[]{0,1,2,3,4});
        anArray.move(3,5,0);
        Test.ensureEqual(anArray.toArray(), new double[]{3,4,0,1,2}, "");

        anArray = new SparseDoubleArray(new double[]{0,1,2,3,4});
        anArray.move(1,2,4);
        Test.ensureEqual(anArray.toArray(), new double[]{0,2,3,1,4}, "");

        //move does nothing, but is allowed
        anArray = new SparseDoubleArray(new double[]{0,1,2,3,4});
        anArray.move(1,1,0);
        Test.ensureEqual(anArray.toArray(), new double[]{0,1,2,3,4}, "");
        anArray.move(1,2,1);
        Test.ensureEqual(anArray.toArray(), new double[]{0,1,2,3,4}, "");
        anArray.move(1,2,2);
        Test.ensureEqual(anArray.toArray(), new double[]{0,1,2,3,4}, "");
        anArray.move(5,5,0);
        Test.ensureEqual(anArray.toArray(), new double[]{0,1,2,3,4}, "");
        anArray.move(3,5,5);
        Test.ensureEqual(anArray.toArray(), new double[]{0,1,2,3,4}, "");

        //makeIndices
        anArray = new SparseDoubleArray(new double[] {25,1,1,10});
        IntArray indices = new IntArray();
        Test.ensureEqual(anArray.makeIndices(indices).toString(), "1.0, 10.0, 25.0", "");
        Test.ensureEqual(indices.toString(), "2, 0, 0, 1", "");

        anArray = new SparseDoubleArray(new double[] {35,35,Double.NaN,1,2});
        Test.ensureEqual(anArray.makeIndices(indices).toString(), "1.0, 2.0, 35.0, NaN", "");
        Test.ensureEqual(indices.toString(), "2, 2, 3, 0, 1", "");

        anArray = new SparseDoubleArray(new double[] {10,20,30,40});
        Test.ensureEqual(anArray.makeIndices(indices).toString(), "10.0, 20.0, 30.0, 40.0", "");
        Test.ensureEqual(indices.toString(), "0, 1, 2, 3", "");

        //switchToFakeMissingValue
        anArray = new SparseDoubleArray(new double[] {Double.NaN,1,2,Double.NaN,3,Double.NaN});
        Test.ensureEqual(anArray.switchFromTo("", "75"), 3, "");
        Test.ensureEqual(anArray.toString(), "75.0, 1.0, 2.0, 75.0, 3.0, 75.0", "");
        anArray.switchFromTo("75", "");
        Test.ensureEqual(anArray.toString(), "NaN, 1.0, 2.0, NaN, 3.0, NaN", "");
        Test.ensureEqual(anArray.getNMinMaxIndex(), new int[]{3, 1, 4}, "");

        //removeDuplicatesAE5
        anArray = new SparseDoubleArray(new double[] {1, 2, 2.0000001, 2.0000002, 3});
        anArray.removeDuplicatesAE5();
        Test.ensureEqual(anArray.toString(), "1.0, 2.0, 3.0", "");

        anArray = new SparseDoubleArray(new double[] {1, 0.9999999, 2, 2.0000001, 2.0000002, 3, 3.000000001});
        anArray.removeDuplicatesAE5();
        Test.ensureEqual(anArray.toString(), "1.0, 2.0, 3.0", "");

        //addN
        anArray = new SparseDoubleArray(new double[] {25});
        anArray.addN(2, 5.0);
        Test.ensureEqual(anArray.toString(), "25.0, 5.0, 5.0", "");
        Test.ensureEqual(anArray.getNMinMaxIndex(), new int[]{3, 2, 0}, "");

        //add array
        anArray.add(new double[]{17, 19});
        Test.ensureEqual(anArray.toString(), "25.0, 5.0, 5.0, 17.0, 19.0", "");

        //subset
        PrimitiveArray ss = anArray.subset(1, 3, 4);
        Test.ensureEqual(ss.toString(), "5.0, 19.0", "");
        ss = anArray.subset(0, 1, 0);
        Test.ensureEqual(ss.toString(), "25.0", "");
        ss = anArray.subset(0, 1, -1);
        Test.ensureEqual(ss.toString(), "", "");
        ss = anArray.subset(1, 1, 0);
        Test.ensureEqual(ss.toString(), "", "");

        ss.trimToSize();
        anArray.subset(ss, 1, 3, 4);
        Test.ensureEqual(ss.toString(), "5.0, 19.0", "");
        anArray.subset(ss, 0, 1, 0);
        Test.ensureEqual(ss.toString(), "25.0", "");
        anArray.subset(ss, 0, 1, -1);
        Test.ensureEqual(ss.toString(), "", "");
        anArray.subset(ss, 1, 1, 0);
        Test.ensureEqual(ss.toString(), "", "");

        //evenlySpaced
        String2.log("\nevenlySpaced test #1");
        anArray = new SparseDoubleArray(new double[] {10,20,30});
        Test.ensureEqual(anArray.isEvenlySpaced(), "", "");
        String2.log("\nevenlySpaced test #2");
        anArray.set(2, 30.1);
        Test.ensureEqual(anArray.isEvenlySpaced(), 
            "SparseDoubleArray isn't evenly spaced: [0]=10.0, [1]=20.0, spacing=10.0, average spacing=10.05.", "");
        Test.ensureEqual(anArray.smallestBiggestSpacing(),
            "    smallest spacing=10.0: [0]=10.0, [1]=20.0\n" +
            "    biggest  spacing=10.100000000000001: [1]=20.0, [2]=30.1", "");

        //these are unevenly spaced, but the secondary precision test allows it
        //should fail first test, but pass second test
        String2.log("\nevenlySpaced test #3");
        anArray = new SparseDoubleArray(new double[] {1.2345678906, 1.2345678907, 1.2345678908001});
        Test.ensureEqual(anArray.isEvenlySpaced(), "", "");
        //but this fails
        String2.log("\nevenlySpaced test #4");
        anArray.set(2, 1.23456789081); 
        Test.ensureEqual(anArray.isEvenlySpaced(),
            "SparseDoubleArray isn't evenly spaced: [0]=1.2345678906, [1]=1.2345678907, " +
            "spacing=1.000000082740371E-10, average spacing=1.0500000868773895E-10.", "");

        //isAscending
        anArray = new SparseDoubleArray(new double[] {10,10,30});
        Test.ensureEqual(anArray.isAscending(), "", "");
        anArray.set(2, Double.NaN);
        Test.ensureEqual(anArray.isAscending(), 
            "SparseDoubleArray isn't sorted in ascending order: [2]=(missing value).", "");
        anArray.set(1, 9);
        Test.ensureEqual(anArray.isAscending(), 
            "SparseDoubleArray isn't sorted in ascending order: [0]=10.0 > [1]=9.0.", "");

        //isDescending
        anArray = new SparseDoubleArray(new double[] {30,10,10});
        Test.ensureEqual(anArray.isDescending(), "", "");
        anArray.set(2, Double.NaN);
        Test.ensureEqual(anArray.isDescending(), 
            "SparseDoubleArray isn't sorted in descending order: [1]=10.0 < [2]=NaN.", "");
        anArray.set(1, 35);
        Test.ensureEqual(anArray.isDescending(), 
            "SparseDoubleArray isn't sorted in descending order: [0]=30.0 < [1]=35.0.", "");


        //firstTie
        anArray = new SparseDoubleArray(new double[] {30,35,10});
        Test.ensureEqual(anArray.firstTie(), -1, "");
        anArray.set(1, 30);
        Test.ensureEqual(anArray.firstTie(), 0, "");
        anArray.set(1, Double.NaN);
        Test.ensureEqual(anArray.firstTie(), -1, "");
        anArray.set(2, Double.NaN);
        Test.ensureEqual(anArray.firstTie(), 1, "");

        //hashcode
        anArray = new SparseDoubleArray();
        for (int i = 5; i < 1000; i++)
            anArray.add(i/100.0);
        String2.log("hashcode1=" + anArray.hashCode());
        anArray2 = (SparseDoubleArray)anArray.clone();
        Test.ensureEqual(anArray.hashCode(), anArray2.hashCode(), "");
        anArray.atInsert(0, (double)2);
        Test.ensureTrue(anArray.hashCode() != anArray2.hashCode(), "");

        //justKeep
        BitSet bitset = new BitSet();
        anArray = new SparseDoubleArray(new double[] {0, 11, 22, 33, 44});
        bitset.set(1);
        bitset.set(4);
        anArray.justKeep(bitset);
        Test.ensureEqual(anArray.toString(), "11.0, 44.0", "");

        //min max
        anArray = new SparseDoubleArray();
        anArray.addPAOne(anArray.MINEST_VALUE());
        anArray.addPAOne(anArray.MAXEST_VALUE());
        Test.ensureEqual(anArray.getString(0), anArray.MINEST_VALUE().toString(), "");
        Test.ensureEqual(anArray.getString(0), "-1.7976931348623157E308", "");
        Test.ensureEqual(anArray.getString(1), anArray.MAXEST_VALUE().toString(), "");
        Test.ensureEqual(anArray.getString(1), "1.7976931348623157E308", "");

        //tryToFindNumericMissingValue() 
        Test.ensureEqual((new SparseDoubleArray(new double[] {       })).tryToFindNumericMissingValue(), null, "");
        Test.ensureEqual((new SparseDoubleArray(new double[] {1, 2   })).tryToFindNumericMissingValue(), null, "");
        Test.ensureEqual((new SparseDoubleArray(new double[] {-1e300})).tryToFindNumericMissingValue(), -1e300, "");
        Test.ensureEqual((new SparseDoubleArray(new double[] {1e300 })).tryToFindNumericMissingValue(),  1e300, "");
        Test.ensureEqual((new SparseDoubleArray(new double[] {1, 99  })).tryToFindNumericMissingValue(),   99, "");

        //calculateStats2
        //test from https://en.wikipedia.org/wiki/Algorithms_for_calculating_variance mean=10, variance=s^2=30
        anArray = new SparseDoubleArray(new double[] {99, 4, 7, 13, 99, 16}); 
        Attributes atts99 = (new Attributes()).add("missing_value", 99.0);
        double stats2[] = anArray.calculateStats2(atts99);
                                           //n min max sum mean variance
        Test.ensureEqual(stats2, new double[]{4, 4, 16, 40, 10, 30}, "");

        //median
        Test.ensureEqual(anArray.calculateMedian(atts99), 10, "");
        Test.ensureEqual(anArray.calculateMedian(null), 14.5, "");  //when the 99's are data values
        anArray = new SparseDoubleArray(new double[] {99, 99}); 
        Test.ensureEqual(anArray.calculateMedian(atts99), Double.NaN, "");
        anArray = new SparseDoubleArray(new double[] {99, 4, 99}); 
        Test.ensureEqual(anArray.calculateMedian(atts99), 4, "");
        anArray = new SparseDoubleArray(new double[] {99, 4, 7, 99}); 
        Test.ensureEqual(anArray.calculateMedian(atts99), 5.5, "");
        anArray = new SparseDoubleArray(new double[] {99, 4, 13, 7, 99}); 
        Test.ensureEqual(anArray.calculateMedian(atts99), 7, "");

        //big numbers
        anArray = new SparseDoubleArray(new double[] {99, 1e12 + 4, 1e12 + 7, 1e12 + 13, 99, 1e12 + 16}); 
        stats2 = anArray.calculateStats2((new Attributes()).add("missing_value", 99.0));
        Test.ensureEqual(stats2, new double[]{4, 1e12 + 4, 1e12 + 16, 4e12 + 40, 1e12 + 10, 30}, "");

        //0 values
        anArray = new SparseDoubleArray(new double[] {99, 99}); 
        stats2 = anArray.calculateStats2((new Attributes()).add("missing_value", 99.0));
        Test.ensureEqual(stats2, new double[]{0, Double.NaN, Double.NaN, Double.NaN, Double.NaN, Double.NaN}, "");

        //1 value
        anArray = new SparseDoubleArray(new double[] {99, 4, 99}); 
        stats2 = anArray.calculateStats2((new Attributes()).add("missing_value", 99.0));
        Test.ensureEqual(stats2, new double[]{1, 4, 4, 4, 4, Double.NaN}, "");

    }

    /**
     * This runs all of the interactive or not interactive tests for this class.
     *
     * @param errorSB all caught exceptions are logged to this.
     * @param interactive  If true, this runs all of the interactive tests; 
     *   otherwise, this runs all of the non-interactive tests.
     * @param doSlowTestsToo If true, this runs the slow tests, too.
     * @param firstTest The first test to be run (0...).  Test numbers may change.
     * @param lastTest The last test to be run, inclusive (0..., or -1 for the last test). 
     *   Test numbers may change.
     */
    public static void test(StringBuilder errorSB, boolean interactive, 
        boolean doSlowTestsToo, int firstTest, int lastTest) {
        if (lastTest < 0)
            lastTest = interactive? -1 : 0;
        String msg = "\n^^^ DoubleArray.test(" + interactive + ") test=";

        for (int test = firstTest; test <= lastTest; test++) {
            try {
                long time = System.currentTimeMillis();
                String2.log(msg + test);
            
                if (interactive) {
                    //if (test ==  0) ...;

                } else {
                    if (test ==  0) basicTest();
                }

                String2.log(msg + test + " finished successfully in " + (System.currentTimeMillis() - time) + " ms.");
            } catch (Throwable testThrowable) {
                String eMsg = msg + test + " caught throwable:\n" + 
                    MustBe.throwableToString(testThrowable);
                errorSB.append(eMsg);
                String2.log(eMsg);
                if (interactive) 
                    String2.pressEnterToContinue("");
            }
        }
    }

}


