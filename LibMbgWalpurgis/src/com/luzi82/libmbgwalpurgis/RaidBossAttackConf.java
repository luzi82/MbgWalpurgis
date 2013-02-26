package com.luzi82.libmbgwalpurgis;

import java.util.concurrent.Executor;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class RaidBossAttackConf {

	public boolean mBp0Ok;

	public String mBp1Url;

	public String mBp3Url;

	public static RaidBossAttackConf toRaidBossAttackConf(Document aDocument) throws ParseException {
		RaidBossAttackConf ret = new RaidBossAttackConf();

		Elements eV = aDocument.select("a[href*=raid_boss_battle_exe][href*=bp%3D1]");
		if (eV.size() > 1) {
			throw new ParseException();
		} else if (eV.size() == 1) {
			Element e = eV.get(0);
			ret.mBp1Url = e.absUrl("href");
			ret.mBp0Ok = ret.mBp1Url.contains("first%3D1");
		}

		eV = aDocument.select("a[href*=raid_boss_battle_exe][href*=bp%3D3]");
		if (eV.size() > 1) {
			throw new ParseException();
		} else if (eV.size() == 1) {
			Element e = eV.get(0);
			ret.mBp3Url = e.absUrl("href");
		}

		return ret;
	}

	public static ICallback<Document> toRaidBossAttackConf(final ICallback<RaidBossAttackConf> aCallback, final ICallback<Exception> aException, final Executor aExecutor) {
		return new ICallback<Document>() {
			@Override
			public void callback(Document aResult) {
				try {
					Utils.startCallback(aCallback, toRaidBossAttackConf(aResult), aExecutor);
				} catch (Exception e) {
					Utils.startCallback(aException, e, aExecutor);
				}
			}
		};
	}

}
