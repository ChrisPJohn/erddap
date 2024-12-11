public class String2 {

  /**
   * Finds the first instance of s at or after fromIndex (0.. ) in sb.
   *
   * @param sb a StringBuilder
   * @param s the String you want to find
   * @param fromIndex the index number of the position to start the search
   * @return The starting position of s. If s is null or not found, it returns -1.
   */
  public static int indexOf(final StringBuilder sb, final String s, final int fromIndex) {
    if (s == null) return -1;
    final int sLength = s.length();
    if (sLength == 0) return -1;

    final char ch = s.charAt(0);
    int index = Math.max(fromIndex, 0);
    final int tSize = sb.length() - sLength + 1; // no point in searching last few char
    while (index < tSize) {
      if (sb.charAt(index) == ch) {
        int nCharsMatched = 1;
        while ((nCharsMatched < sLength)
            && (sb.charAt(index + nCharsMatched) == s.charAt(nCharsMatched))) nCharsMatched++;
        if (nCharsMatched == sLength) return index;
      }

      index++;
    }

    return -1;
  }

  /**
   * This creates a hashset of the unique acronyms in a string. An acronym here is defined by the
   * regular expression: [^a-zA-Z0-9][A-Z]{2,}[^a-zA-Z0-9]
   *
   * @param text
   * @return hashset of the unique acronyms in text.
   */
  public static Set<String> findAcronyms(final String text) {
    final HashSet<String> hs = new HashSet<>();
    if (text == null || text.length() < 2) return hs;
    final Pattern pattern = Pattern.compile("[^a-zA-Z0-9]([A-Z]{2,})[^a-zA-Z0-9]");
    final Matcher matcher = pattern.matcher(text);
    int po = 0;
    while (po < text.length()) {
      if (matcher.find(po)) {
        hs.add(matcher.group(1));
        po = matcher.end();
      } else {
        return hs;
      }
    }
    return hs;
  }

   /**
   * This returns the index of the first value that matches the regex.
   *
   * @param ar an array of objects which will be tested via ar[i].toString()
   * @param regex
   * @return the index of the first value that matches the regex, or -1 if none matches.
   * @throws RuntimeException if regex won't compile.
   */
  public int firstMatch(final Object ar[], final String regex) {
    return firstMatch(ar, Pattern.compile(regex));
  }

  /**
   * This returns the index of the first value that matches the regex pattern p.
   *
   * @param ar an array of objects which will be tested via ar[i].toString()
   * @param p
   * @return the index of the first value that matches the regex pattern p, or -1 if none matches.
   */
  public int firstMatch(final Object ar[], final Pattern p) {
    if (ar == null) return -1;
    for (int i = 0; i < ar.length; i++) {
      final Object s = ar[i];
      if (s != null && p.matcher(s.toString()).matches()) return i;
    }
    return -1;
  }

   /**
   * This counts all occurrences of <tt>findS</tt> in sb. if (sb == null || findS == null ||
   * findS.length() == 0) return 0;
   *
   * @param sb the source StringBuilder
   * @param findS the string to be searched for
   */
  public static int countAll(final StringBuilder sb, final String findS) {
    if (sb == null || findS == null || findS.length() == 0) return 0;
    int n = 0;
    int sLength = findS.length();
    int po = sb.indexOf(findS, 0);
    while (po >= 0) {
      n++;
      po = sb.indexOf(findS, po + sLength);
    }
    return n;
  }

  /**
   * This repeatedly replaces the text matched in the capture group with the replacement text.
   *
   * @param sb a StringBuilder
   * @param regex a regular Expression
   * @param captureGroup a capture in the regex
   * @param replacement the replacement string
   * @return sb for convenience
   */
  public static StringBuilder regexReplaceAll(
      final StringBuilder sb,
      final String regex,
      final int captureGroup,
      final String replacement) {

    final Pattern pattern = Pattern.compile(regex);
    Matcher matcher = pattern.matcher(sb);
    while (matcher.find()) {
      sb.replace(matcher.start(1), matcher.end(1), replacement);
      matcher = pattern.matcher(sb); // sb has changed, so need new matcher
    }
    return sb;
  }

   /**
   * This creates an ArrayList from an Object[].
   *
   * @param objectArray an Object[]
   * @return arrayList with the objects
   */
  public static List<Object> toArrayList(final Object objectArray[]) {
    final int n = objectArray.length;
    final ArrayList<Object> al = new ArrayList<>(n);
    al.addAll(Arrays.asList(objectArray));
    return al;
  }

 /** This returns a CSV (not CSSV) String. */
  public static String toCSVString(final Enumeration<String> en) {
    return toSVString(toArrayList(en).toArray(), ",", false);
  }

  public static String toCSVString(final List<Object> al) {
    return toSVString(al.toArray(), ",", false);
  }

  /**
   * Generates a Comma-Space-Separated-Value (CSSV) string.
   *
   * <p>WARNING: This does not have a sychronized block: if your enumeration needs thread-safety,
   * wrap this call in something like <tt>synchronized(enum) {String2.toArrayList(enum); }</tt>.
   *
   * <p>CHANGED: before 2011-03-06, this didn't do anything special for strings with internal commas
   * or quotes. Now it uses toJson for that string.
   *
   * <p>CHANGED: before 2011-09-04, this was called toCSVString.
   *
   * @param en an enumeration of objects
   * @return a CSSV String with the values with ", " after all but the last value. Returns null if
   *     ar is null. null elements are represented as "[null]".
   */
  public static String toCSSVString(final Enumeration<String> en) {
    return toSVString(toArrayList(en).toArray(), ", ", false);
  }

  /**
   * Generates a Comma-Space-Separated-Value (CSSV) string.
   *
   * <p>CHANGED: before 2011-03-06, this didn't do anything special for strings with internal commas
   * or quotes. Now it uses toJson for that string.
   *
   * <p>CHANGED: before 2011-09-04, this was called toCSVString.
   *
   * @param al an arrayList of objects
   * @return a CSV String with the values with ", " after all but the last value. Returns null if ar
   *     is null. null elements are represented as "[null]".
   */
  public static String toCSSVString(final List<Object> al) {
    return toSVString(al.toArray(), ", ", false);
  }

   /**
   * Add the items in the array (if any) to the arrayList.
   *
   * @param arrayList
   * @param ar the items to be added
   */
  public static void add(final List<Object> arrayList, final Object ar[]) {
    if (arrayList == null || ar == null) return;
    arrayList.addAll(Arrays.asList(ar));
  }

  /**
   * This displays the contents of a bitSet as a String.
   *
   * @param bitSet
   * @return the corresponding String (the 'true' bits, comma separated)
   */
  public static String toString(final BitSet bitSet) {
    if (bitSet == null) return null;
    final StringBuilder sb = new StringBuilder(1024);

    String separator = "";
    int i = bitSet.nextSetBit(0);
    while (i >= 0) {
      sb.append(separator + i);
      separator = ", ";
      i = bitSet.nextSetBit(i + 1);
    }
    return sb.toString();
  }

  /**
   * From an arrayList which alternates attributeName (a String) and attributeValue (an object),
   * this generates a String with " name=value" on each line. If arrayList == null, this returns "
   * [null]\n".
   *
   * @param arrayList
   * @return the desired string representation
   */
  public static String alternateToString(final List<Object> arrayList) {
    if (arrayList == null) return "    [null]\n";
    final int n = arrayList.size();
    // estimate 32 bytes/element
    final StringBuilder sb = new StringBuilder(32 * Math.min(n, (Integer.MAX_VALUE - 8192) / 32));
    for (int i = 0; i < n; i += 2) {
      sb.append("    ");
      sb.append(arrayList.get(i).toString());
      sb.append('=');
      sb.append(arrayToCSSVString(arrayList.get(i + 1)));
      sb.append('\n');
    }
    return sb.toString();
  }

  /**
   * From an arrayList which alternates attributeName (a String) and attributeValue (an object),
   * this an array of attributeNames. If arrayList == null, this returns " [null]\n".
   *
   * @param arrayList
   * @return the attributeNames in the arrayList
   */
  public static String[] alternateGetNames(final List<Object> arrayList) {
    if (arrayList == null) return null;
    final int n = arrayList.size();
    final String[] sar = new String[n / 2];
    int i2 = 0;
    for (int i = 0; i < n / 2; i++) {
      sar[i] = arrayList.get(i2).toString();
      i2 += 2;
    }
    return sar;
  }

  /**
   * From an arrayList which alternates attributeName (a String) and attributeValue (an object),
   * this returns the attributeValue associated with the supplied attributeName. If array == null or
   * there is no matching value, this returns null.
   *
   * @param arrayList
   * @param attributeName
   * @return the associated value
   */
  public static Object alternateGetValue(final List<Object> arrayList, final String attributeName) {
    if (arrayList == null) return null;
    final int n = arrayList.size();
    for (int i = 0; i < n; i += 2) {
      if (arrayList.get(i).toString().equals(attributeName)) return arrayList.get(i + 1);
    }
    return null;
  }

  /**
   * Given an arrayList which alternates attributeName (a String) and attributeValue (an object),
   * this either removes the attribute (if value == null), adds the attribute and value (if it isn't
   * in the list), or changes the value (if the attriubte is in the list).
   *
   * @param arrayList
   * @param attributeName
   * @param value the value associated with the attributeName
   * @return the previous value for the attribute (or null)
   * @throws RuntimeException of trouble (e.g., if arrayList is null)
   */
  public static Object alternateSetValue(
      final List<Object> arrayList, final String attributeName, final Object value) {
    if (arrayList == null)
      throw new SimpleException(ERROR + " in String2.alternateSetValue: arrayList is null.");
    final int n = arrayList.size();
    for (int i = 0; i < n; i += 2) {
      if (arrayList.get(i).toString().equals(attributeName)) {
        final Object oldValue = arrayList.get(i + 1);
        if (value == null) {
          arrayList.remove(i + 1); // order of removal is important
          arrayList.remove(i);
        } else arrayList.set(i + 1, value);
        return oldValue;
      }
    }

    // attributeName not found?
    if (value == null) {
    } else {
      // add it
      arrayList.add(attributeName);
      arrayList.add(value);
    }
    return null;
  }

  /**
   * DON'T USE THIS; RELY ON THE FIXES AVAILABLE FOR JAVA: EITHER THE LATEST VERSION OF JAVA OR THE
   * JAVA UPDATER TO FIX THE BUG ON EXISTING OLDER JAVA INSTALLATIONS
   * https://www.oracle.com/technetwork/java/javase/fpupdater-tool-readme-305936.html
   *
   * <p>This returns true if s is a value that causes Java to hang. Avoid java hang. 2011-02-09
   * http://www.exploringbinary.com/java-hangs-when-converting-2-2250738585072012e-308 This was
   * Bob's work-around to avoid the Java bug.
   *
   * @param s a string representing a double value
   * @return true if the value is the troublesome value. If true, the value can be interpreted as
   *     either +/-Double.MIN_VALUE (not sure which) or (crudely) 0.
   */
  public static boolean isDoubleTrouble(String s) {
    if (s == null || s.length() < 22) // this is a good quick reject
    return false;

    // all variants are relevant, so look for the mantissa
    return replaceAll(s, ".", "").indexOf("2225073858507201") >= 0;
  }

  /**
   * This returns a string with the keys and values of the Map (sorted by the keys, ignoreCase).
   *
   * @param map (keys and values are objects with good toString methods). If it needs to be
   *     thead-safe, use ConcurrentHashMap.
   * @return a string with the sorted (ignoreCase) keys and their values ("key1: value1\nkey2:
   *     value2\n")
   */
  public static String getKeysAndValuesString(Map<String, String> map) {
    ArrayList<String> al = new ArrayList<>();

    // synchronize so protected from changes in other threads
    for (Map.Entry<String, String> entry : map.entrySet()) {
      al.add(entry.getKey() + ": " + entry.getValue());
    }
    al.sort(STRING_COMPARATOR_IGNORE_CASE);
    return toNewlineString(al.toArray(new String[0]));
  }

  /** This returns the text to make n system beeps if printed to the console. */
  public static String beep(int n) {
    return makeString('\u0007', n);
  }

  /** This lists the methods for a given object's class. */
  public static void listMethods(Object v) {
    Class<?> tClass = v.getClass();
    Method[] methods = tClass.getMethods();
    for (int i = 0; i < methods.length; i++) {
      String2.log("public method #" + i + ": " + methods[i]);
    }
  }

  /**
   * This encodes special regex characters to turn a string into a regex for that string. I'm not at
   * all sure this is perfect!
   */
  public static String encodeAsRegex(String s) {
    int length = s.length();
    StringBuilder sb = new StringBuilder(length * 3 / 2);
    for (int po = 0; po < length; po++) {
      char ch = s.charAt(po);
      if (".^$[](){}?*+\\".indexOf(ch) >= 0) sb.append('\\');
      sb.append(ch);
    }
    return sb.toString();
  }
}
