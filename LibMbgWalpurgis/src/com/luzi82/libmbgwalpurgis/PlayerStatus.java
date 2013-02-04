package com.luzi82.libmbgwalpurgis;

import java.util.concurrent.Executor;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class PlayerStatus {

	public static final int NOT_SET = -999;

	public int mLp = NOT_SET;

	public int mLpMax = NOT_SET;

	public int mBp = NOT_SET;

	public int mBpMax = NOT_SET;

	public int mExpToUp = NOT_SET;

	public int mCard = NOT_SET;

	public int mCardMax = NOT_SET;

	public static PlayerStatus toStatus(Document aDoc) throws ParseException {
		PlayerStatus ret = new PlayerStatus();

		Elements mainStatusElement = aDoc.select("div[class*=main-status] span");
		for (Element e : mainStatusElement) {
			String t = e.ownText().trim();
			if (!t.contains("でレベルアップ"))
				continue;
			Elements eev = e.select("span");
			if (eev.size() != 2)
				continue;
			t = eev.get(1).ownText().trim();
			t = t.replaceAll(Pattern.quote(","), "");
			ret.mExpToUp = Integer.parseInt(t);
		}

		Elements cardElements = aDoc.select("div[class*=status-exsample] td div");
		for (Element e : cardElements) {
			Elements eas = e.select("a");
			if (eas.size() != 1)
				continue;
			Element ea = eas.get(0);
			if (!ea.attr("href").contains("card_list"))
				continue;
			Elements ess = ea.select("span");
			if (ess.size() != 1)
				continue;
			// System.out.println(ess.get(0).ownText().trim());
			// System.out.println(e.ownText().trim().replaceAll(Pattern.quote("/"),
			// ""));
			ret.mCard = Integer.parseInt(ess.get(0).ownText().trim());
			ret.mCardMax = Integer.parseInt(e.ownText().trim().replaceAll(Pattern.quote("/"), ""));
		}

		Pattern lpPattern = Pattern.compile("LP:([\\d]+)/([\\d]+)");
		Pattern bpPattern = Pattern.compile("BP:([\\d]+)/([\\d]+)");
		Elements mmlElements = aDoc.select("span[class*=mypage-mode-label]");
		for (Element e : mmlElements) {
			String t = e.ownText().trim();
			Matcher m;
			m = lpPattern.matcher(t);
			if (m.find()) {
				// System.out.println(m.group(1));
				// System.out.println(m.group(2));
				ret.mLp = Integer.parseInt(m.group(1));
				ret.mLpMax = Integer.parseInt(m.group(2));
			}
			m = bpPattern.matcher(t);
			if (m.find()) {
				// System.out.println(m.group(1));
				// System.out.println(m.group(2));
				ret.mBp = Integer.parseInt(m.group(1));
				ret.mBpMax = Integer.parseInt(m.group(2));
			}
		}

		Utils.parseCheck(ret.mLp != NOT_SET);
		Utils.parseCheck(ret.mLpMax != NOT_SET);
		Utils.parseCheck(ret.mBp != NOT_SET);
		Utils.parseCheck(ret.mBpMax != NOT_SET);
		Utils.parseCheck(ret.mExpToUp != NOT_SET);
		Utils.parseCheck(ret.mCard != NOT_SET);
		Utils.parseCheck(ret.mCardMax != NOT_SET);

		return ret;
	}

	public static ICallback<Document> toStatus(final ICallback<PlayerStatus> aCallback, final ICallback<Exception> aException, final Executor aExecutor) {
		return new ICallback<Document>() {
			@Override
			public void callback(Document aResult) {
				try {
					Utils.startCallback(aCallback, toStatus(aResult), aExecutor);
				} catch (Exception e) {
					Utils.startCallback(aException, e, aExecutor);
				}
			}
		};
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(String.format("LP: %d/%d\n", mLp, mLpMax));
		sb.append(String.format("BP: %d/%d\n", mBp, mBpMax));
		sb.append(String.format("EXP: %d\n", mExpToUp));
		sb.append(String.format("card: %d/%d\n", mCard, mCardMax));
		return sb.toString();
	}

}
