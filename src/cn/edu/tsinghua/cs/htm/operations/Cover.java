package cn.edu.tsinghua.cs.htm.operations;

import java.util.*;

import cn.edu.tsinghua.cs.htm.HTM;
import cn.edu.tsinghua.cs.htm.shapes.Convex;
import cn.edu.tsinghua.cs.htm.shapes.Trixel;
import cn.edu.tsinghua.cs.htm.utils.HTMid;
import cn.edu.tsinghua.cs.htm.utils.HTMRanges;
import cn.edu.tsinghua.cs.htm.utils.Markup;
import cn.edu.tsinghua.cs.htm.utils.Pair;

/**
 * Cover a Convex with Trixels
 * @author Haojia Zuo
 *
 */
public class Cover {
	
	private Convex convex;
	
	private List<Trixel> listOfInners;
	
	private Stack<List<Trixel> > stackOfPartialLists;
	
	private Queue<Trixel> queue;
	
	private int maxLevel;
	
	private int currentLevel, previousLevel;
	
	private boolean halt, alreadyRun;
	
	public Cover(Convex convex, int maxLevel) {
		this.convex = convex;
		this.maxLevel = maxLevel;
		init();
	}
	
	private void init() {
		listOfInners = new ArrayList<Trixel>();
		stackOfPartialLists = new Stack<List<Trixel> >();
		stackOfPartialLists.push(new ArrayList<Trixel>());
		queue = new LinkedList<Trixel>();
		currentLevel = 0;
		previousLevel = 1;
		
		HTM htm = HTM.getInstance();
		
		for (int i = 0; i < 8; i++) {
			queue.add(htm.getTopTrixel(i));
		}
		
		alreadyRun = false;
	}
	
	private void evaluateCurrentLevel() {
		if (currentLevel >= maxLevel) {
			halt = true;
		}
	}
	
	/**
	 * Calculate the Trixel cover
	 * Call this method before calling getTrixels(), getRanges(), etc.
	 */
	public void run() {
		halt = false;
		if (convex == null) {
			return;
		}
		while (!halt && queue.size() > 0) {
			step();
			evaluateCurrentLevel();
		}
		alreadyRun = true;
	}
	
	private void step() {
		boolean levelFinished = false;
		
		stackOfPartialLists.push(new ArrayList<Trixel>());
		currentLevel++;
		
		while (!levelFinished && queue.size() > 0) {
			Trixel t = queue.peek();
			if (t.getHTMid().getLevel() != previousLevel) {
				if (previousLevel < 0) {
					currentLevel = 0;
				} else {
					levelFinished = true;
				}
				previousLevel = t.getHTMid().getLevel();
			}
			if (!levelFinished) {
				queue.remove();
				boolean terminal = false;
				
				switch (t.getMarkup(convex)) {
				case Full:
					listOfInners.add(t);
					terminal = true;
					break;
				case Outside:
					terminal = true;
					break;
				case Partial:
					stackOfPartialLists.peek().add(t);
				default:
					terminal = true;
				}
				
				if (!terminal) {
					queue.addAll(t.expand());
				}
			}
		}
	}
	
	public List<Pair<HTMid, HTMid> > getHTMidPairs() {
		if (!alreadyRun) {
			return null;
		}
		List<Trixel> trixelList = getTrixels();
		if (trixelList == null) {
			return null;
		}
		HTMRanges htmRanges = new HTMRanges(trixelList, maxLevel);
		return htmRanges.getPairList();
	}
	
	public List<Pair<HTMid, HTMid> > getHTMidPairs(Markup markup) {
		if (!alreadyRun) {
			return null;
		}
		List<Trixel> trixelList = getTrixels(markup);
		if (trixelList == null) {
			return null;
		}
		HTMRanges htmRanges = new HTMRanges(trixelList, maxLevel);
		return htmRanges.getPairList();
	}
	
	/**
	 * Get all covering Trixels, no matter Full or Partial
	 * @return List of covering Trixels, null if failed
	 */
	public List<Trixel> getTrixels() {
		if (!alreadyRun) {
			return null;
		}
		return concatenate(listOfInners, stackOfPartialLists.peek());
	}
	
	/**
	 * Get specific kind of covering Trixels
	 * @param markUp Full or Partial
	 * @return List of covering Trixels, null if failed
	 */
	public List<Trixel> getTrixels(Markup markUp) {
		if (!alreadyRun) {
			return null;
		}
		switch (markUp) {
		case Full:
			return concatenate(listOfInners, null);
		case Partial:
			return concatenate(null, stackOfPartialLists.peek());
		default:
			return null;
		}
	}
	
	private List<Trixel> concatenate(List<Trixel> innerList, List<Trixel> partialList) {
		List<Trixel> result = new ArrayList<Trixel>();
		if (innerList != null) {
			result.addAll(innerList);
		}
		if (partialList != null) {
			result.addAll(partialList);
		}
		return result;
	}
	
	public static void main(String[] args) {
		Convex convex = Convex.parseVertices(args);
		if (convex == null) {
			System.out.println("Illegal arguments!");
			return;
		}
		Cover cover = new Cover(convex, 3);
		cover.run();
		List<Pair<HTMid, HTMid> > pairs = cover.getHTMidPairs();
		System.out.println("Trixels in the coverage:");
		for (Pair<HTMid, HTMid> pair : pairs) {
			System.out.println("[" + pair.a.toString() + ", " +
								pair.b.toString() + "]");
		}
	}
	
}