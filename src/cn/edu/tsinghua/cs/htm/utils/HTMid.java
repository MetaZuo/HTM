package cn.edu.tsinghua.cs.htm.utils;

/**
 * Identifier for Trixels.
 * HTMids for 8 initial Trixels are: S0, S1, S2, S3, N0, N1, N2, N3.
 * 4 children of S1 are: S10, S11, S12, S13.
 * Representing 'S' by '10', 'N' by '11', we get the binary form of HTMids:
 * 1000 for S0, 1001 for S1, 111000 for N20, and so forth.
 * So an HTMid can be represented by both a long int and a string.
 * @author Haojia Zuo
 *
 */
public class HTMid implements Comparable<HTMid> {

	/**
	 * Binary representation in the form of long
	 * Note that the highest two bits are assumed 0
	 * Actually effective length of hid is 62
	 * Because Java doesn't support unsigned long
	 */
	protected long hid;
	
	/**
	 * String representation, e.g. S01
	 */
	protected String hidName;
	
	private static final int hidBitsMaxLen = 64;
	private static final long hidHighestBit = 0x2L << 60;
	private static final long hidSecondHighestBit = 0x1L << 60;
	
	public HTMid(long hid) {
		this.hid = hid;
		this.hidName = idToName(hid);
	}
	
	public HTMid(String hidName) {
		this.hidName = hidName;
		this.hid = nameToId(hidName);
	}
	
	/**
	 * Convert HTMid in long form to string form
	 * Returns null if illegal
	 * @param hid long form of HTMid
	 * @return string form of HTMid
	 */
	public static String idToName(long hid) {
		long shiftedBit, shiftedHid;
		
		if (hid < 0 || hid < 8) {
			return null;
		}
		
		int i;
		for (i = 2; i < hidBitsMaxLen; i += 2) {
			shiftedHid = hid << (i - 2);
			shiftedBit = shiftedHid & hidHighestBit;
			if (shiftedBit != 0) {
				break;
			}
			if ((shiftedHid & hidSecondHighestBit) != 0) {
				return null;
			}
		}
		
		int size = (hidBitsMaxLen - i) / 2;
		
		char[] name = new char[size];
		
		for (i = 0; i < size - 1; i++) {
			char ch = (char) ('0' + (int) ((hid >> (i * 2)) & 3));
			name[size - i - 1] = ch;
		}
		
		shiftedBit = (hid >> (size * 2 - 2)) & 1;
		if (shiftedBit != 0) {
			name[0] = 'N';
		} else {
			name[0] = 'S';
		}
		
		return String.valueOf(name);
	}
	
	/**
	 * Convert string form of HTMid to long int form.
	 * Returns 0 if illegal.
	 * @param hidName
	 * @return HTMid in long type
	 */
	public static long nameToId(String hidName) {
		long resultHid = 0;
		long shifted;
		long bits;
		int shift;
		
		if (hidName.length() < 2) {
			return 0;
		}
		if (hidName.length() > (hidBitsMaxLen - 2) * 2) {
			return 0;
		}
		if (hidName.charAt(0) != 'N' && hidName.charAt(0) != 'S') {
			return 0;
		}
		for (int i = hidName.length() - 1; i > 0; i--) {
			if (hidName.charAt(i) > '3' || hidName.charAt(i) < '0') {
				return 0;
			}
			bits = (int) (hidName.charAt(i) - '0');
			shift = 2 * (hidName.length() - i - 1);
			shifted = bits << shift;
			resultHid += shifted;
		}
		bits = 2;
		if (hidName.charAt(0) == 'N') {
			bits++;
		}
		shift = 2 * hidName.length() - 2;
		shifted = bits << shift;
		resultHid += shifted;
		return resultHid;
	}
	
	public int getLevel() {
		if (hid < 0) {
			return -1;
		}
		int i;
		for (i = 2; i < hidBitsMaxLen; i += 2) {
			if (((hid << (i - 2)) & hidHighestBit) != 0) {
				break;
			}
		}
		return (hidBitsMaxLen - i) / 2 - 2;
	}
	
	public HTMid getChild(int childNum) {
		if (childNum >= 0 && childNum <= 3) {
			return new HTMid(hid * 4 + childNum);
		} else {
			return this;
		}
	}
	
	public HTMid truncate(int level) {
		int currentLevel = getLevel();
		if (level < currentLevel) {
			return new HTMid(hid >> 2 * (currentLevel - level));
		} else {
			return this;
		}
	}
	
	public Pair<HTMid, HTMid> extend(int level) {
		int currentLevel = getLevel();
		long lo, hi;
		if (currentLevel < level) {
			int shiftBits = 2 * (level - currentLevel);
			lo = hid << shiftBits;
			hi = lo + ((1L << shiftBits) - 1);
		} else {
			// truncate
			int shiftBits = 2 * (currentLevel - level);
			lo = hid >> shiftBits;
			hi = lo;
		}
		return new Pair<HTMid, HTMid>(new HTMid(lo), new HTMid(hi));
	}
	
	public long getId() {
		return hid;
	}
	
	public String getName() {
		return hidName;
	}
	
	@Override
	public String toString() {
		return hidName;
	}

	@Override
	public int compareTo(HTMid o) {
		return Long.compare(hid, o.hid);
	}
}
