/* Generated By:JavaCC: Do not edit this line. DDSParserTokenManager.java */
package dods.dap.parser;

import com.google.common.collect.ImmutableList;

public class DDSParserTokenManager implements DDSParserConstants {
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
    switch (curChar) {
      case 58:
        return jjStopAtPos(0, 24);
      case 59:
        return jjStopAtPos(0, 23);
      case 61:
        return jjStopAtPos(0, 27);
      case 91:
        return jjStopAtPos(0, 25);
      case 93:
        return jjStopAtPos(0, 26);
      case 123:
        return jjStopAtPos(0, 21);
      case 125:
        return jjStopAtPos(0, 22);
      default:
        return jjMoveNfa_0(0, 0);
    }
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

  static final long[] jjbitVec0 = {0x0L, 0x0L, 0xffffffffffffffffL, 0xffffffffffffffffL};

  private final int jjMoveNfa_0(int startState, int curPos) {
    int startsAt = 0;
    jjnewStateCnt = 205;
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
              if ((0x3ffe82000000000L & l) != 0L) {
                if (kind > 20) kind = 20;
                jjCheckNAdd(22);
              } else if (curChar == 35) {
                if (kind > 5) kind = 5;
                jjCheckNAdd(1);
              }
              break;
            case 1:
              if ((0xffffffffffffdbffL & l) == 0L) break;
              if (kind > 5) kind = 5;
              jjCheckNAdd(1);
              break;
            case 21:
              if ((0x3ffe82000000000L & l) == 0L) break;
              if (kind > 20) kind = 20;
              jjCheckNAdd(22);
              break;
            case 22:
              if ((0x3ffe82800000000L & l) == 0L) break;
              if (kind > 20) kind = 20;
              jjCheckNAdd(22);
              break;
            case 75:
              if (curChar == 54 && kind > 12) kind = 12;
              break;
            case 76:
            case 193:
            case 196:
              if (curChar == 49) jjCheckNAdd(75);
              break;
            case 79:
              if (curChar == 50 && kind > 14) kind = 14;
              break;
            case 80:
            case 199:
            case 202:
              if (curChar == 51) jjCheckNAdd(79);
              break;
            case 84:
              if (curChar == 54 && kind > 13) kind = 13;
              break;
            case 85:
            case 110:
            case 114:
              if (curChar == 49) jjCheckNAdd(84);
              break;
            case 89:
              if (curChar == 50 && kind > 15) kind = 15;
              break;
            case 90:
            case 118:
            case 122:
            case 126:
              if (curChar == 51) jjCheckNAdd(89);
              break;
            case 97:
              if (curChar == 50 && kind > 16) kind = 16;
              break;
            case 98:
            case 172:
            case 177:
              if (curChar == 51) jjCheckNAdd(97);
              break;
            case 103:
              if (curChar == 52 && kind > 17) kind = 17;
              break;
            case 104:
            case 182:
            case 187:
              if (curChar == 54) jjCheckNAdd(103);
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
                if (kind > 20) kind = 20;
                jjCheckNAdd(22);
              }
              if (curChar == 73) jjAddStates(0, 3);
              else if (curChar == 70) jjAddStates(4, 7);
              else if (curChar == 83) jjAddStates(8, 13);
              else if (curChar == 85) jjAddStates(14, 20);
              else if (curChar == 102) jjAddStates(21, 22);
              else if (curChar == 117) jjAddStates(23, 25);
              else if (curChar == 105) jjAddStates(26, 27);
              else if (curChar == 115) jjAddStates(28, 30);
              else if (curChar == 68) jjAddStates(31, 32);
              else if (curChar == 76) jjAddStates(33, 34);
              else if (curChar == 71) jjAddStates(35, 36);
              else if (curChar == 66) jjAddStates(37, 38);
              else if (curChar == 98) jjstateSet[jjnewStateCnt++] = 19;
              else if (curChar == 103) jjstateSet[jjnewStateCnt++] = 15;
              else if (curChar == 108) jjstateSet[jjnewStateCnt++] = 11;
              else if (curChar == 100) jjstateSet[jjnewStateCnt++] = 7;
              break;
            case 1:
              if (kind > 5) kind = 5;
              jjstateSet[jjnewStateCnt++] = 1;
              break;
            case 2:
              if (curChar == 116 && kind > 6) kind = 6;
              break;
            case 3:
            case 48:
              if (curChar == 101) jjCheckNAdd(2);
              break;
            case 4:
              if (curChar == 115) jjstateSet[jjnewStateCnt++] = 3;
              break;
            case 5:
              if (curChar == 97) jjstateSet[jjnewStateCnt++] = 4;
              break;
            case 6:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 5;
              break;
            case 7:
              if (curChar == 97) jjstateSet[jjnewStateCnt++] = 6;
              break;
            case 8:
              if (curChar == 100) jjstateSet[jjnewStateCnt++] = 7;
              break;
            case 9:
              if (curChar == 116 && kind > 7) kind = 7;
              break;
            case 10:
            case 39:
              if (curChar == 115) jjCheckNAdd(9);
              break;
            case 11:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 10;
              break;
            case 12:
              if (curChar == 108) jjstateSet[jjnewStateCnt++] = 11;
              break;
            case 13:
              if (curChar == 100 && kind > 10) kind = 10;
              break;
            case 14:
            case 33:
              if (curChar == 105) jjCheckNAdd(13);
              break;
            case 15:
              if (curChar == 114) jjstateSet[jjnewStateCnt++] = 14;
              break;
            case 16:
              if (curChar == 103) jjstateSet[jjnewStateCnt++] = 15;
              break;
            case 17:
              if (curChar == 101 && kind > 11) kind = 11;
              break;
            case 18:
            case 27:
              if (curChar == 116) jjCheckNAdd(17);
              break;
            case 19:
              if (curChar == 121) jjstateSet[jjnewStateCnt++] = 18;
              break;
            case 20:
              if (curChar == 98) jjstateSet[jjnewStateCnt++] = 19;
              break;
            case 21:
            case 22:
              if ((0x7fffffe97fffffeL & l) == 0L) break;
              if (kind > 20) kind = 20;
              jjCheckNAdd(22);
              break;
            case 23:
              if (curChar == 66) jjAddStates(37, 38);
              break;
            case 24:
              if (curChar == 69 && kind > 11) kind = 11;
              break;
            case 25:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 24;
              break;
            case 26:
              if (curChar == 89) jjstateSet[jjnewStateCnt++] = 25;
              break;
            case 28:
              if (curChar == 121) jjstateSet[jjnewStateCnt++] = 27;
              break;
            case 29:
              if (curChar == 71) jjAddStates(35, 36);
              break;
            case 30:
              if (curChar == 68 && kind > 10) kind = 10;
              break;
            case 31:
              if (curChar == 73) jjstateSet[jjnewStateCnt++] = 30;
              break;
            case 32:
              if (curChar == 82) jjstateSet[jjnewStateCnt++] = 31;
              break;
            case 34:
              if (curChar == 114) jjstateSet[jjnewStateCnt++] = 33;
              break;
            case 35:
              if (curChar == 76) jjAddStates(33, 34);
              break;
            case 36:
              if (curChar == 84 && kind > 7) kind = 7;
              break;
            case 37:
              if (curChar == 83) jjstateSet[jjnewStateCnt++] = 36;
              break;
            case 38:
              if (curChar == 73) jjstateSet[jjnewStateCnt++] = 37;
              break;
            case 40:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 39;
              break;
            case 41:
              if (curChar == 68) jjAddStates(31, 32);
              break;
            case 42:
              if (curChar == 84 && kind > 6) kind = 6;
              break;
            case 43:
              if (curChar == 69) jjstateSet[jjnewStateCnt++] = 42;
              break;
            case 44:
              if (curChar == 83) jjstateSet[jjnewStateCnt++] = 43;
              break;
            case 45:
              if (curChar == 65) jjstateSet[jjnewStateCnt++] = 44;
              break;
            case 46:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 45;
              break;
            case 47:
              if (curChar == 65) jjstateSet[jjnewStateCnt++] = 46;
              break;
            case 49:
              if (curChar == 115) jjstateSet[jjnewStateCnt++] = 48;
              break;
            case 50:
              if (curChar == 97) jjstateSet[jjnewStateCnt++] = 49;
              break;
            case 51:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 50;
              break;
            case 52:
              if (curChar == 97) jjstateSet[jjnewStateCnt++] = 51;
              break;
            case 53:
              if (curChar == 115) jjAddStates(28, 30);
              break;
            case 54:
              if (curChar == 101 && kind > 8) kind = 8;
              break;
            case 55:
            case 141:
              if (curChar == 99) jjCheckNAdd(54);
              break;
            case 56:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 55;
              break;
            case 57:
              if (curChar == 101) jjstateSet[jjnewStateCnt++] = 56;
              break;
            case 58:
              if (curChar == 117) jjstateSet[jjnewStateCnt++] = 57;
              break;
            case 59:
              if (curChar == 113) jjstateSet[jjnewStateCnt++] = 58;
              break;
            case 60:
              if (curChar == 101) jjstateSet[jjnewStateCnt++] = 59;
              break;
            case 61:
              if (curChar == 101 && kind > 9) kind = 9;
              break;
            case 62:
            case 155:
              if (curChar == 114) jjCheckNAdd(61);
              break;
            case 63:
              if (curChar == 117) jjstateSet[jjnewStateCnt++] = 62;
              break;
            case 64:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 63;
              break;
            case 65:
              if (curChar == 99) jjstateSet[jjnewStateCnt++] = 64;
              break;
            case 66:
              if (curChar == 117) jjstateSet[jjnewStateCnt++] = 65;
              break;
            case 67:
              if (curChar == 114) jjstateSet[jjnewStateCnt++] = 66;
              break;
            case 68:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 67;
              break;
            case 69:
              if (curChar == 103 && kind > 18) kind = 18;
              break;
            case 70:
            case 167:
              if (curChar == 110) jjCheckNAdd(69);
              break;
            case 71:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 70;
              break;
            case 72:
              if (curChar == 114) jjstateSet[jjnewStateCnt++] = 71;
              break;
            case 73:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 72;
              break;
            case 74:
              if (curChar == 105) jjAddStates(26, 27);
              break;
            case 77:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 76;
              break;
            case 78:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 77;
              break;
            case 81:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 80;
              break;
            case 82:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 81;
              break;
            case 83:
              if (curChar == 117) jjAddStates(23, 25);
              break;
            case 86:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 85;
              break;
            case 87:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 86;
              break;
            case 88:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 87;
              break;
            case 91:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 90;
              break;
            case 92:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 91;
              break;
            case 93:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 92;
              break;
            case 94:
              if (curChar == 108 && kind > 19) kind = 19;
              break;
            case 95:
            case 132:
              if (curChar == 114) jjCheckNAdd(94);
              break;
            case 96:
              if (curChar == 102) jjAddStates(21, 22);
              break;
            case 99:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 98;
              break;
            case 100:
              if (curChar == 97) jjstateSet[jjnewStateCnt++] = 99;
              break;
            case 101:
              if (curChar == 111) jjstateSet[jjnewStateCnt++] = 100;
              break;
            case 102:
              if (curChar == 108) jjstateSet[jjnewStateCnt++] = 101;
              break;
            case 105:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 104;
              break;
            case 106:
              if (curChar == 97) jjstateSet[jjnewStateCnt++] = 105;
              break;
            case 107:
              if (curChar == 111) jjstateSet[jjnewStateCnt++] = 106;
              break;
            case 108:
              if (curChar == 108) jjstateSet[jjnewStateCnt++] = 107;
              break;
            case 109:
              if (curChar == 85) jjAddStates(14, 20);
              break;
            case 111:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 110;
              break;
            case 112:
              if (curChar == 78) jjstateSet[jjnewStateCnt++] = 111;
              break;
            case 113:
              if (curChar == 73) jjstateSet[jjnewStateCnt++] = 112;
              break;
            case 115:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 114;
              break;
            case 116:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 115;
              break;
            case 117:
              if (curChar == 73) jjstateSet[jjnewStateCnt++] = 116;
              break;
            case 119:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 118;
              break;
            case 120:
              if (curChar == 78) jjstateSet[jjnewStateCnt++] = 119;
              break;
            case 121:
              if (curChar == 73) jjstateSet[jjnewStateCnt++] = 120;
              break;
            case 123:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 122;
              break;
            case 124:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 123;
              break;
            case 125:
              if (curChar == 73) jjstateSet[jjnewStateCnt++] = 124;
              break;
            case 127:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 126;
              break;
            case 128:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 127;
              break;
            case 129:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 128;
              break;
            case 130:
              if (curChar == 76 && kind > 19) kind = 19;
              break;
            case 131:
              if (curChar == 82) jjstateSet[jjnewStateCnt++] = 130;
              break;
            case 133:
              if (curChar == 83) jjAddStates(8, 13);
              break;
            case 134:
              if (curChar == 69 && kind > 8) kind = 8;
              break;
            case 135:
              if (curChar == 67) jjstateSet[jjnewStateCnt++] = 134;
              break;
            case 136:
              if (curChar == 78) jjstateSet[jjnewStateCnt++] = 135;
              break;
            case 137:
              if (curChar == 69) jjstateSet[jjnewStateCnt++] = 136;
              break;
            case 138:
              if (curChar == 85) jjstateSet[jjnewStateCnt++] = 137;
              break;
            case 139:
              if (curChar == 81) jjstateSet[jjnewStateCnt++] = 138;
              break;
            case 140:
              if (curChar == 69) jjstateSet[jjnewStateCnt++] = 139;
              break;
            case 142:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 141;
              break;
            case 143:
              if (curChar == 101) jjstateSet[jjnewStateCnt++] = 142;
              break;
            case 144:
              if (curChar == 117) jjstateSet[jjnewStateCnt++] = 143;
              break;
            case 145:
              if (curChar == 113) jjstateSet[jjnewStateCnt++] = 144;
              break;
            case 146:
              if (curChar == 101) jjstateSet[jjnewStateCnt++] = 145;
              break;
            case 147:
              if (curChar == 69 && kind > 9) kind = 9;
              break;
            case 148:
              if (curChar == 82) jjstateSet[jjnewStateCnt++] = 147;
              break;
            case 149:
              if (curChar == 85) jjstateSet[jjnewStateCnt++] = 148;
              break;
            case 150:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 149;
              break;
            case 151:
              if (curChar == 67) jjstateSet[jjnewStateCnt++] = 150;
              break;
            case 152:
              if (curChar == 85) jjstateSet[jjnewStateCnt++] = 151;
              break;
            case 153:
              if (curChar == 82) jjstateSet[jjnewStateCnt++] = 152;
              break;
            case 154:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 153;
              break;
            case 156:
              if (curChar == 117) jjstateSet[jjnewStateCnt++] = 155;
              break;
            case 157:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 156;
              break;
            case 158:
              if (curChar == 99) jjstateSet[jjnewStateCnt++] = 157;
              break;
            case 159:
              if (curChar == 117) jjstateSet[jjnewStateCnt++] = 158;
              break;
            case 160:
              if (curChar == 114) jjstateSet[jjnewStateCnt++] = 159;
              break;
            case 161:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 160;
              break;
            case 162:
              if (curChar == 71 && kind > 18) kind = 18;
              break;
            case 163:
              if (curChar == 78) jjstateSet[jjnewStateCnt++] = 162;
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
            case 168:
              if (curChar == 105) jjstateSet[jjnewStateCnt++] = 167;
              break;
            case 169:
              if (curChar == 114) jjstateSet[jjnewStateCnt++] = 168;
              break;
            case 170:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 169;
              break;
            case 171:
              if (curChar == 70) jjAddStates(4, 7);
              break;
            case 173:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 172;
              break;
            case 174:
              if (curChar == 65) jjstateSet[jjnewStateCnt++] = 173;
              break;
            case 175:
              if (curChar == 79) jjstateSet[jjnewStateCnt++] = 174;
              break;
            case 176:
              if (curChar == 76) jjstateSet[jjnewStateCnt++] = 175;
              break;
            case 178:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 177;
              break;
            case 179:
              if (curChar == 97) jjstateSet[jjnewStateCnt++] = 178;
              break;
            case 180:
              if (curChar == 111) jjstateSet[jjnewStateCnt++] = 179;
              break;
            case 181:
              if (curChar == 108) jjstateSet[jjnewStateCnt++] = 180;
              break;
            case 183:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 182;
              break;
            case 184:
              if (curChar == 65) jjstateSet[jjnewStateCnt++] = 183;
              break;
            case 185:
              if (curChar == 79) jjstateSet[jjnewStateCnt++] = 184;
              break;
            case 186:
              if (curChar == 76) jjstateSet[jjnewStateCnt++] = 185;
              break;
            case 188:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 187;
              break;
            case 189:
              if (curChar == 97) jjstateSet[jjnewStateCnt++] = 188;
              break;
            case 190:
              if (curChar == 111) jjstateSet[jjnewStateCnt++] = 189;
              break;
            case 191:
              if (curChar == 108) jjstateSet[jjnewStateCnt++] = 190;
              break;
            case 192:
              if (curChar == 73) jjAddStates(0, 3);
              break;
            case 194:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 193;
              break;
            case 195:
              if (curChar == 78) jjstateSet[jjnewStateCnt++] = 194;
              break;
            case 197:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 196;
              break;
            case 198:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 197;
              break;
            case 200:
              if (curChar == 84) jjstateSet[jjnewStateCnt++] = 199;
              break;
            case 201:
              if (curChar == 78) jjstateSet[jjnewStateCnt++] = 200;
              break;
            case 203:
              if (curChar == 116) jjstateSet[jjnewStateCnt++] = 202;
              break;
            case 204:
              if (curChar == 110) jjstateSet[jjnewStateCnt++] = 203;
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
      if ((i = jjnewStateCnt) == (startsAt = 205 - (jjnewStateCnt = startsAt))) return curPos;
      try {
        curChar = input_stream.readChar();
      } catch (java.io.IOException e) {
        return curPos;
      }
    }
  }

  static final ImmutableList<Integer> jjnextStates =
      ImmutableList.of(
          195, 198, 201, 204, 176, 181, 186, 191, 140, 146, 154, 161, 166, 170, 113, 117, 121, 125,
          129, 131, 132, 102, 108, 88, 93, 95, 78, 82, 60, 68, 73, 47, 52, 38, 40, 32, 34, 26, 28);
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
          jjstrLiteralImagesNull,
          jjstrLiteralImagesNull,
          "\173",
          "\175",
          "\73",
          "\72",
          "\133",
          "\135",
          "\75");
  static final ImmutableList<Long> jjtoToken = ImmutableList.of(0xfffffc1L);
  private SimpleCharStream input_stream;
  private final int[] jjrounds = new int[205];
  private final int[] jjstateSet = new int[410];
  protected char curChar;

  public DDSParserTokenManager(SimpleCharStream stream) {
    if (SimpleCharStream.staticFlag)
      throw new Error(
          "ERROR: Cannot use a static CharStream class with a non-static lexical analyzer.");
    input_stream = stream;
  }

  public DDSParserTokenManager(SimpleCharStream stream, int lexState) {
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
    for (i = 205; i-- > 0; ) jjrounds[i] = 0x80000000;
  }

  public void ReInit(SimpleCharStream stream, int lexState) {
    ReInit(stream);
    SwitchTo(lexState);
  }

  public void SwitchTo(int lexState) {
    if (lexState >= 1 || lexState < 0)
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
  int defaultLexState = 0;
  int jjnewStateCnt;
  int jjround;
  int jjmatchedPos;
  int jjmatchedKind;

  public final Token getNextToken() {
    Token matchedToken;
    int curPos = 0;

    EOFLoop:
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
        continue EOFLoop;
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
          continue EOFLoop;
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
