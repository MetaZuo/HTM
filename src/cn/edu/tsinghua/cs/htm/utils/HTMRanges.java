package cn.edu.tsinghua.cs.htm.utils;

import java.util.*;

import cn.edu.tsinghua.cs.htm.shapes.Trixel;

/**
 * A list of HTMid ranges of the same depth
 * Used as the result of Trixel covering
 * @author Haojia Zuo
 *
 */
public class HTMRanges {
	
	private List<Pair<HTMid, HTMid> > pairList;
	
	/**
	 * All HTMids will be at this same level
	 */
	private int level;
	
	{
		pairList = new ArrayList<Pair<HTMid, HTMid> >();
	}
	
	public HTMRanges(int level) {
		this.level = level;
	}
	
	public HTMRanges(List<Trixel> trixelList, int level) {
		this.level = level;
		if (trixelList != null) {
			for (Trixel trixel : trixelList) {
				HTMid htmId = trixel.getHTMid();
				Pair<HTMid, HTMid> range = htmId.extend(this.level);
				pairList.add(range);
			}
			sort();
			compact();
		}
	}
	
	public List<Pair<HTMid, HTMid> > getPairList() {
		List<Pair<HTMid, HTMid> > duplicate = new ArrayList<Pair<HTMid, HTMid> >();
		duplicate.addAll(pairList);
		return duplicate;
	}
	
	/**
	 * Sort the pairs under these rules:
	 * 1. Sort in ascending order of lower bounds
	 * 2. If having same lower bounds, sort in ascending order of upper bounds
	 */
	private void sort() {
		Collections.sort(pairList, new Comparator<Pair<HTMid, HTMid> >() {
			@Override
			public int compare(Pair<HTMid, HTMid> o1, Pair<HTMid, HTMid> o2) {
				int cmp = o1.a.compareTo(o2.a);
				if (cmp != 0) {
					return cmp;
				}
				return o1.b.compareTo(o2.b);
			}
		});
	}
	
	/**
	 * Rearrange the pair list
	 * Merge pairs who are overlapping or adjacent with each other
	 * Must be called after sort
	 */
	private void compact() {
		List<Pair<HTMid, HTMid> > newList = new ArrayList<Pair<HTMid, HTMid> >();
		Iterator<Pair<HTMid, HTMid> > iter = pairList.iterator();
		if (!iter.hasNext()) {
			return;
		}
		Pair<HTMid, HTMid> prev = iter.next();
		while (iter.hasNext()) {
			Pair<HTMid, HTMid> now = iter.next();
			if (prev.b.hid >= now.a.hid) {
				Pair<HTMid, HTMid> merged = new Pair<HTMid, HTMid>(prev.a, now.b);
				newList.add(merged);
				if (iter.hasNext()) {
					prev = iter.next();
				} else {
					prev = null;
				}
			} else {
				newList.add(prev);
				prev = now;
			}
		}
		if (prev != null) {
			newList.add(prev);
		}
		pairList = newList;
	}

}
