/* Generated By:JavaCC: Do not edit this line. ErrorParserConstants.java */
package dods.dap.parser;

import com.google.common.collect.ImmutableList;

public interface ErrorParserConstants {

  int EOF = 0;
  int ERROR = 6;
  int CODE = 7;
  int MSG = 8;
  int PTYPE = 9;
  int PROGRAM = 10;
  int INT = 11;
  int STR = 12;
  int UNQUOTED_STR = 13;
  int QUOTED_STR = 14;
  int UNTERM_QUOTE = 15;

  int DEFAULT = 0;

  ImmutableList<String> tokenImage =
      ImmutableList.of(
          "<EOF>",
          "\" \"",
          "\"\\t\"",
          "\"\\n\"",
          "\"\\r\"",
          "<token of kind 5>",
          "<ERROR>",
          "<CODE>",
          "<MSG>",
          "<PTYPE>",
          "<PROGRAM>",
          "<INT>",
          "<STR>",
          "<UNQUOTED_STR>",
          "<QUOTED_STR>",
          "<UNTERM_QUOTE>",
          "\"{\"",
          "\"}\"",
          "\";\"",
          "\"=\"");
}
