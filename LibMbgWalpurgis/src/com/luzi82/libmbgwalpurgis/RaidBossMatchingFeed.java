package com.luzi82.libmbgwalpurgis;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;

public class RaidBossMatchingFeed {

	enum Side {
		ALLY, ENEMY
	}

	public static class Unit {

		public Side mSide;

		public String mTime;

		public String mPlayer;

		public String mMessage;
	}

	final LinkedList<Unit> mUnitList = new LinkedList<RaidBossMatchingFeed.Unit>();

	public static RaidBossMatchingFeed toFeed(Document aDoc) throws ParseException {
		RaidBossMatchingFeed ret = new RaidBossMatchingFeed();

		Elements unitElements = aDoc.select("div[class*=basic-bg] td[valign*=top]");
		for (Element unitElement : unitElements) {
			List<Node> chileNodes = unitElement.childNodes();
			Unit u = new Unit();

			Utils.parseCheck(10, chileNodes.size());

			Element e = (Element) chileNodes.get(1);
			Utils.parseCheck("img", e.tagName());
			String srcattr = e.attr("src");

			Utils.parseCheck(srcattr.contains("icon01") || srcattr.contains("icon02"));
			u.mSide = srcattr.contains("icon01") ? Side.ALLY : Side.ENEMY;

			TextNode tn = (TextNode) chileNodes.get(2);
			u.mTime = tn.text().trim();

			e = (Element) chileNodes.get(5);
			Utils.parseCheck("a", e.tagName());
			Utils.parseCheck(1, e.children().size());
			e = e.child(0);
			Utils.parseCheck("span", e.tagName());
			u.mPlayer = e.ownText().trim();

			tn = (TextNode) chileNodes.get(7);
			// System.out.println(tn.text().trim());
			u.mMessage = tn.text().trim();

			ret.mUnitList.add(u);
		}

		return ret;
	}

	public static ICallback<Document> toFeed(final ICallback<RaidBossMatchingFeed> aCallback, final ICallback<Exception> aException, final Executor aExecutor) {
		return new ICallback<Document>() {
			@Override
			public void callback(Document aResult) {
				try {
					Utils.startCallback(aCallback, toFeed(aResult), aExecutor);
				} catch (Exception e) {
					Utils.startCallback(aException, e, aExecutor);
				}
			}
		};
	}

}
