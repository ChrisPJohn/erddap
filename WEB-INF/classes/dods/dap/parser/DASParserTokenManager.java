/* Generated By:JavaCC: Do not edit this line. DASParserTokenManager.java */
package dods.dap.parser;

import com.google.common.collect.ImmutableList;

public class DASParserTokenManager implements DASParserConstants {
  public java.io.PrintStream debugStream = System.out;

  public void setDebugStream(java.io.PrintStream ds) {
    debugStream = ds;
  }

  private final int jjStopAtPos(int pos, int kind) {
    jjmatchedKind = kind;
    jjmatchedPos = pos;
    return pos + 1;
  }

  private final int jjMoveStringLiteralDfa0_0() {
    return switch (curChar) {
      case 44 -> jjStopAtPos(0, 21);
      case 59 -> jjStopAtPos(0, 22);
      case 123 -> jjStopAtPos(0, 19);
      case 125 -> jjStopAtPos(0, 20);
      default -> jjMoveNfa_0(0, 0);
    };
  }

  private final void jjCheckNAdd(int state) {
    if (jjrounds[state] != jjround) {
      jjstateSet[jjnewStateCnt++] = state;
      jjrounds[state] = jjround;
    }
  }

  private final void jjAddStates(int start, int end) {
    do {
      jjstateSet[jjnewStateCnt++] = jjnextStates.get(start);
    } while (start++ != end);
  }

  private final void jjCheckNAddStates(int start, int end) {
    do {
      jjCheckNAdd(jjnextStates.get(start));
    } while (start++ != end);
  }

  static final long[] jjbitVec0 = {0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL};

  private final int jjMoveNfa_0(int startState, int curPos) {
    int startsAt = 0;
    jjnewStateCnt = 175;
    int i = 1;
    jjstateSet[0] = startState;
    int kind = 0x7fffffff;
    for (; ; ) {
      if (++jjround == 0x7fffffff) ReInitRounds();
      if (curChar < 64) {
        long l = 1L << curChar;
        do {
          switch (jjstateSet[--i]) {
            case 0:
              if ((0x7ffeb2000000000L & l) != 0L) {
                if (kind > 17) kind = 17;
                jjCheckNAdd(13);
              } else if (curChar == 34) jjCheckNAddStates(0, 2);
              else if (curChar == 35) {
                if (kind > 5) kind = 5;
                jjCheckNAdd(1);
              }
              break;
            case 1:
              if ((0xffffffffffffdbffL & l) == 0L) break;
              if (kind > 5) kind = 5;
              jjCheckNAdd(1);
              break;
            case 12:
              if ((0x7ffeb2000000000L & l) == 0L) break;
              if (kind > 17) kind = 17;
              jjCheckNAdd(13);
              break;
            case 13:
              if ((0x7ffeb2800000000L & l) == 0L) break;
              if (kind > 17) kind = 17;
              jjCheckNAdd(13);
              break;
            case 14:
              if (curChar == 34) jjCheckNAddStates(0, 2);
              break;
            case 15:
              if ((0xfffffffbffffffffL & l) != 0L) jjCheckNAddStates(0, 2);
              break;
            case 17:
              if ((0x8400000000L & l) != 0L) jjCheckNAddStates(0, 2);
              break;
            case 18:
              if (curChar == 34 && kind > 18) kind = 18;
              break;
            case 19:
              if ((0xff000000000000L & l) != 0L) jjCheckNAddStates(3, 6);
              break;
            case 20:
              if ((0xff000000000000L & l) != 0L) jjCheckNAddStates(0, 2);
              break;
            case 21:
              if ((0xf000000000000L & l) != 0L) jjstateSet[jjnewStateCnt++] = 22;
              break;
            case 22:
              if ((0xff000000000000L & l) != 0L) jjCheckNAdd(20);
              break;
            case 54:
              if (curChar == 54 && kind > 9) kind = 9;
              break;
            case 55:
            case 138:
            case 141:
              if (curChar == 49) jjCheckNAdd(54);
              break;
            case 58:
              if (curChar == 50 && kind > 11) kind = 11;
              break;
            case 59:
            case 144:
            case 147:
              if (curChar == 51) jjCheckNAdd(58);
              break;
            case 63:
              if (curChar == 54 && kind > 10) kind = 10;
              break;
            case 64:
            case 89:
            case 93:
            case 97:
              if (curChar == 49) jjCheckNAdd(63);
              break;
            case 68:
              if (curChar == 50 && kind > 12) kind = 12;
              break;
            case 69:
            case 101:
            case 105:
            case 109:
              if (curChar == 51) jjCheckNAdd(68);
              break;
            case 76:
              if (curChar == 50 && kind > 13) kind = 13;
              break;
            case 77:
            case 117:
            case 122:
              if (curChar == 51) jjCheckNAdd(76);
              break;
            case 82:
              if (curChar == 52 && kind > 14) kind = 14;
              break;
            case 83:
            case 127:
            case 132:
              if (curChar == 54) jjCheckNAdd(82);
              break;
            default:
              break;
          }
        } while (i != startsAt);
      } else if (curChar < 128) {
        long l = 1L << (curChar & 077);
        do {
          switch (jjstateSet[--i]) {
            case 0:
              if ((0x7fffffe97fffffeL & l) != 0L) {
                if (kind > 17) kind = 17;
                jjCheckNAdd(13);
              }
              if (curChar == 65) jjAddStates(7, 10);
              else if (curChar == 73) jjAddStates(11, 14);
              else if (curChar == 70) jjAddStates(15, 18);
              else if (curChar == 85) jjAddStates(19, 26);
              else if (curChar == 102) jjAddStates(27, 28);
              else if (curChar == 117) jjAddStates(29, 31);
              else if (curChar == 105) jjAddStates(32, 33);
              else if (curChar == 97) jjAddStates(34, 35);
              else if (curChar == 66) jjAddStates(36, 37);
              else if (curChar == 83) jjAddStates(38, 39);
              else if (curChar == 115) jjstateSet[jjnewStateCnt++] = 10;
              else if (curChar == 98) jjstateSet[jjnewStateCnt++] = 4;
              break;
            case 1:
              if (kind > 5) kind = 5;
              jjstateSet[jjnewStateCnt++] = 1;
              break;
            case 2:
              if (curChar == 101 && kind > 8) kind = 8;
              break;
            case 3:
            case 37:
              if (curChar == 116) jjCheckNAdd(2);
              break;
            case 4:
              if (curChar == 121) jjstateSet[jjnewStateCnt++] = 3;
              break;
            case 5:
              if (curChar == 98) jjstateSet[jjnewStateCnt++] = 4;
              break;
            case 6:
              if (curChar == 103 && kind > 15) kind = 15;
              break;
            case 7:
            case 29:
              if (curChar == 110) jjCheckNAdd(6);
              break;
            case 8:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 7;
              break;
            case 9:
              if (curChar == 114) jjstateSet[jjnewStateCnt++] = 8;
              break;
            case 10:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 9;
              break;
            case 11:
              if (curChar == 115) jjstateSet[jjnewStateCnt++] = 10;
              break;
            case 12:
            case 13:
              if ((0x7fffffe97fffffeL & l) == 0L) break;
              if (kind > 17) kind = 17;
              jjCheckNAdd(13);
              break;
            case 15:
              if ((0xffffffffefffffffL & l) != 0L) jjCheckNAddStates(0, 2);
              break;
            case 16:
              if (curChar == 92) jjAddStates(40, 42);
              break;
            case 17:
              if ((0x14404410000000L & l) != 0L) jjCheckNAddStates(0, 2);
              break;
            case 23:
              if (curChar == 83) jjAddStates(38, 39);
              break;
            case 24:
              if (curChar == 71 && kind > 15) kind = 15;
              break;
            case 25:
              if (curChar == 78) jjstateSet[jjnewStateCnt++] = 24;
              break;
            case 26:
              if (curChar == 73) jjstateSet[jjnewStateCnt++] = 25;
              break;
            case 27:
              if (curChar == 82) jjstateSet[jjnewStateCnt++] = 26;
              break;
            case 28:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 27;
              break;
            case 30:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 29;
              break;
            case 31:
              if (curChar == 114) jjstateSet[jjnewStateCnt++] = 30;
              break;
            case 32:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 31;
              break;
            case 33:
              if (curChar == 66) jjAddStates(36, 37);
              break;
            case 34:
              if (curChar == 69 && kind > 8) kind = 8;
              break;
            case 35:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 34;
              break;
            case 36:
              if (curChar == 89) jjstateSet[jjnewStateCnt++] = 35;
              break;
            case 38:
              if (curChar == 121) jjstateSet[jjnewStateCnt++] = 37;
              break;
            case 39:
              if (curChar == 97) jjAddStates(34, 35);
              break;
            case 40:
              if (curChar == 115 && kind > 6) kind = 6;
              break;
            case 41:
            case 151:
              if (curChar == 101) jjCheckNAdd(40);
              break;
            case 42:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 41;
              break;
            case 43:
              if (curChar == 117) jjstateSet[jjnewStateCnt++] = 42;
              break;
            case 44:
              if (curChar == 98) jjstateSet[jjnewStateCnt++] = 43;
              break;
            case 45:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 44;
              break;
            case 46:
              if (curChar == 114) jjstateSet[jjnewStateCnt++] = 45;
              break;
            case 47:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 46;
              break;
            case 48:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 47;
              break;
            case 49:
              if (curChar == 115 && kind > 7) kind = 7;
              break;
            case 50:
            case 172:
              if (curChar == 97) jjCheckNAdd(49);
              break;
            case 51:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 50;
              break;
            case 52:
              if (curChar == 108) jjstateSet[jjnewStateCnt++] = 51;
              break;
            case 53:
              if (curChar == 105) jjAddStates(32, 33);
              break;
            case 56:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 55;
              break;
            case 57:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 56;
              break;
            case 60:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 59;
              break;
            case 61:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 60;
              break;
            case 62:
              if (curChar == 117) jjAddStates(29, 31);
              break;
            case 65:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 64;
              break;
            case 66:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 65;
              break;
            case 67:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 66;
              break;
            case 70:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 69;
              break;
            case 71:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 70;
              break;
            case 72:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 71;
              break;
            case 73:
              if (curChar == 108 && kind > 16) kind = 16;
              break;
            case 74:
            case 115:
              if (curChar == 114) jjCheckNAdd(73);
              break;
            case 75:
              if (curChar == 102) jjAddStates(27, 28);
              break;
            case 78:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 77;
              break;
            case 79:
              if (curChar == 97) jjstateSet[jjnewStateCnt++] = 78;
              break;
            case 80:
              if (curChar == 111) jjstateSet[jjnewStateCnt++] = 79;
              break;
            case 81:
              if (curChar == 108) jjstateSet[jjnewStateCnt++] = 80;
              break;
            case 84:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 83;
              break;
            case 85:
              if (curChar == 97) jjstateSet[jjnewStateCnt++] = 84;
              break;
            case 86:
              if (curChar == 111) jjstateSet[jjnewStateCnt++] = 85;
              break;
            case 87:
              if (curChar == 108) jjstateSet[jjnewStateCnt++] = 86;
              break;
            case 88:
              if (curChar == 85) jjAddStates(19, 26);
              break;
            case 90:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 89;
              break;
            case 91:
              if (curChar == 78) jjstateSet[jjnewStateCnt++] = 90;
              break;
            case 92:
              if (curChar == 73) jjstateSet[jjnewStateCnt++] = 91;
              break;
            case 94:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 93;
              break;
            case 95:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 94;
              break;
            case 96:
              if (curChar == 73) jjstateSet[jjnewStateCnt++] = 95;
              break;
            case 98:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 97;
              break;
            case 99:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 98;
              break;
            case 100:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 99;
              break;
            case 102:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 101;
              break;
            case 103:
              if (curChar == 78) jjstateSet[jjnewStateCnt++] = 102;
              break;
            case 104:
              if (curChar == 73) jjstateSet[jjnewStateCnt++] = 103;
              break;
            case 106:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 105;
              break;
            case 107:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 106;
              break;
            case 108:
              if (curChar == 73) jjstateSet[jjnewStateCnt++] = 107;
              break;
            case 110:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 109;
              break;
            case 111:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 110;
              break;
            case 112:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 111;
              break;
            case 113:
              if (curChar == 76 && kind > 16) kind = 16;
              break;
            case 114:
              if (curChar == 82) jjstateSet[jjnewStateCnt++] = 113;
              break;
            case 116:
              if (curChar == 70) jjAddStates(15, 18);
              break;
            case 118:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 117;
              break;
            case 119:
              if (curChar == 65) jjstateSet[jjnewStateCnt++] = 118;
              break;
            case 120:
              if (curChar == 79) jjstateSet[jjnewStateCnt++] = 119;
              break;
            case 121:
              if (curChar == 76) jjstateSet[jjnewStateCnt++] = 120;
              break;
            case 123:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 122;
              break;
            case 124:
              if (curChar == 97) jjstateSet[jjnewStateCnt++] = 123;
              break;
            case 125:
              if (curChar == 111) jjstateSet[jjnewStateCnt++] = 124;
              break;
            case 126:
              if (curChar == 108) jjstateSet[jjnewStateCnt++] = 125;
              break;
            case 128:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 127;
              break;
            case 129:
              if (curChar == 65) jjstateSet[jjnewStateCnt++] = 128;
              break;
            case 130:
              if (curChar == 79) jjstateSet[jjnewStateCnt++] = 129;
              break;
            case 131:
              if (curChar == 76) jjstateSet[jjnewStateCnt++] = 130;
              break;
            case 133:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 132;
              break;
            case 134:
              if (curChar == 97) jjstateSet[jjnewStateCnt++] = 133;
              break;
            case 135:
              if (curChar == 111) jjstateSet[jjnewStateCnt++] = 134;
              break;
            case 136:
              if (curChar == 108) jjstateSet[jjnewStateCnt++] = 135;
              break;
            case 137:
              if (curChar == 73) jjAddStates(11, 14);
              break;
            case 139:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 138;
              break;
            case 140:
              if (curChar == 78) jjstateSet[jjnewStateCnt++] = 139;
              break;
            case 142:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 141;
              break;
            case 143:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 142;
              break;
            case 145:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 144;
              break;
            case 146:
              if (curChar == 78) jjstateSet[jjnewStateCnt++] = 145;
              break;
            case 148:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 147;
              break;
            case 149:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 148;
              break;
            case 150:
              if (curChar == 65) jjAddStates(7, 10);
              break;
            case 152:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 151;
              break;
            case 153:
              if (curChar == 117) jjstateSet[jjnewStateCnt++] = 152;
              break;
            case 154:
              if (curChar == 98) jjstateSet[jjnewStateCnt++] = 153;
              break;
            case 155:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 154;
              break;
            case 156:
              if (curChar == 114) jjstateSet[jjnewStateCnt++] = 155;
              break;
            case 157:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 156;
              break;
            case 158:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 157;
              break;
            case 159:
              if (curChar == 83 && kind > 6) kind = 6;
              break;
            case 160:
              if (curChar == 69) jjstateSet[jjnewStateCnt++] = 159;
              break;
            case 161:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 160;
              break;
            case 162:
              if (curChar == 85) jjstateSet[jjnewStateCnt++] = 161;
              break;
            case 163:
              if (curChar == 66) jjstateSet[jjnewStateCnt++] = 162;
              break;
            case 164:
              if (curChar == 73) jjstateSet[jjnewStateCnt++] = 163;
              break;
            case 165:
              if (curChar == 82) jjstateSet[jjnewStateCnt++] = 164;
              break;
            case 166:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 165;
              break;
            case 167:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 166;
              break;
            case 168:
              if (curChar == 83 && kind > 7) kind = 7;
              break;
            case 169:
              if (curChar == 65) jjstateSet[jjnewStateCnt++] = 168;
              break;
            case 170:
              if (curChar == 73) jjstateSet[jjnewStateCnt++] = 169;
              break;
            case 171:
              if (curChar == 76) jjstateSet[jjnewStateCnt++] = 170;
              break;
            case 173:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 172;
              break;
            case 174:
              if (curChar == 108) jjstateSet[jjnewStateCnt++] = 173;
              break;
            default:
              break;
          }
        } while (i != startsAt);
      } else {
        int i2 = (curChar & 0xff) >> 6;
        long l2 = 1L << (curChar & 077);
        do {
          switch (jjstateSet[--i]) {
            case 1:
              if ((jjbitVec0[i2] & l2) == 0L) break;
              if (kind > 5) kind = 5;
              jjstateSet[jjnewStateCnt++] = 1;
              break;
            case 15:
              if ((jjbitVec0[i2] & l2) != 0L) jjAddStates(0, 2);
              break;
            default:
              break;
          }
        } while (i != startsAt);
      }
      if (kind != 0x7fffffff) {
        jjmatchedKind = kind;
        jjmatchedPos = curPos;
        kind = 0x7fffffff;
      }
      ++curPos;
      if ((i = jjnewStateCnt) == (startsAt = 175 - (jjnewStateCnt = startsAt))) return curPos;
      try {
        curChar = input_stream.readChar();
      } catch (java.io.IOException e) {
        return curPos;
      }
    }
  }

  static final ImmutableList<Integer> jjnextStates =
      ImmutableList.of(
          15, 16, 18, 15, 16, 20, 18, 158, 167, 171, 174, 140, 143, 146, 149, 121, 126, 131, 136,
          92, 96, 100, 104, 108, 112, 114, 115, 81, 87, 67, 72, 74, 57, 61, 48, 52, 36, 38, 28, 32,
          17, 19, 21);
  private static final String jjstrLiteralImagesNull = "null";
  public static final ImmutableList<String> jjstrLiteralImages =
      ImmutableList.of(
          "",
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          "\173",
          "\175",
          "\54",
          "\73");
  static final ImmutableList<Long> jjtoToken = ImmutableList.of(0x7fffc1L);
  private SimpleCharStream input_stream;
  private final int[] jjrounds = new int[175];
  private final int[] jjstateSet = new int[350];
  protected char curChar;

  public DASParserTokenManager(SimpleCharStream stream) {
    if (SimpleCharStream.staticFlag)
      throw new Error(
          "ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
    input_stream = stream;
  }

  public DASParserTokenManager(SimpleCharStream stream, int lexState) {
    this(stream);
    SwitchTo(lexState);
  }

  public void ReInit(SimpleCharStream stream) {
    jjmatchedPos = jjnewStateCnt = 0;
    curLexState = defaultLexState;
    input_stream = stream;
    ReInitRounds();
  }

  private final void ReInitRounds() {
    int i;
    jjround = 0x80000001;
    for (i = 175; i-- > 0; ) jjrounds[i] = 0x80000000;
  }

  public void ReInit(SimpleCharStream stream, int lexState) {
    ReInit(stream);
    SwitchTo(lexState);
  }

  public void SwitchTo(int lexState) {
    if (lexState != 0)
      throw new TokenMgrError(
          "Error: Ignoring invalid lexical state : " + lexState + ". State unchanged.",
          TokenMgrError.INVALID_LEXICAL_STATE);
    else curLexState = lexState;
  }

  private final Token jjFillToken() {
    Token t = Token.newToken(jjmatchedKind);
    t.kind = jjmatchedKind;
    String im = jjstrLiteralImages.get(jjmatchedKind);
    t.image = jjstrLiteralImagesNull.equals(im) ? input_stream.GetImage() : im;
    t.beginLine = input_stream.getBeginLine();
    t.beginColumn = input_stream.getBeginColumn();
    t.endLine = input_stream.getEndLine();
    t.endColumn = input_stream.getEndColumn();
    return t;
  }

  int curLexState = 0;
  final int defaultLexState = 0;
  int jjnewStateCnt;
  int jjround;
  int jjmatchedPos;
  int jjmatchedKind;

  public final Token getNextToken() {
    Token matchedToken;
    int curPos = 0;

    for (; ; ) {
      try {
        curChar = input_stream.BeginToken();
      } catch (java.io.IOException e) {
        jjmatchedKind = 0;
        matchedToken = jjFillToken();
        return matchedToken;
      }

      try {
        input_stream.backup(0);
        while (curChar <= 32 && (0x100002600L & (1L << curChar)) != 0L)
          curChar = input_stream.BeginToken();
      } catch (java.io.IOException e1) {
        continue;
      }
      jjmatchedKind = 0x7fffffff;
      jjmatchedPos = 0;
      curPos = jjMoveStringLiteralDfa0_0();
      if (jjmatchedKind != 0x7fffffff) {
        if (jjmatchedPos + 1 < curPos) input_stream.backup(curPos - jjmatchedPos - 1);
        if ((jjtoToken.get(jjmatchedKind >> 6) & (1L << (jjmatchedKind & 077))) != 0L) {
          matchedToken = jjFillToken();
          return matchedToken;
        } else {
          continue;
        }
      }
      int error_line = input_stream.getEndLine();
      int error_column = input_stream.getEndColumn();
      String error_after = null;
      boolean EOFSeen = false;
      try {
        input_stream.readChar();
        input_stream.backup(1);
      } catch (java.io.IOException e1) {
        EOFSeen = true;
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
        if (curChar == '\n' || curChar == '\r') {
          error_line++;
          error_column = 0;
        } else error_column++;
      }
      if (!EOFSeen) {
        input_stream.backup(1);
        error_after = curPos <= 1 ? "" : input_stream.GetImage();
      }
      throw new TokenMgrError(
          EOFSeen,
          curLexState,
          error_line,
          error_column,
          error_after,
          curChar,
          TokenMgrError.LEXICAL_ERROR);
    }
  }
}
