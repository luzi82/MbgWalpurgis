package com.luzi82.libmbgwalpurgis;

import java.io.FileInputStream;

import org.junit.Assert;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;

public class RaidBossAttackConfTest {

	@Test
	public void testToRaidBossAttackConf0() throws Exception {
		FileInputStream fis = new FileInputStream("testdata/conf0.txt");
		Document doc = Jsoup.parse(fis, "UTF-8", "http://sp.pf.mbga.jp/12012090/?url=http%3A%2F%2Fmadoka2.sp.nextory.co.jp%2Fraid_boss_matching_feed.php");
		fis.close();

		RaidBossAttackConf conf = RaidBossAttackConf.toRaidBossAttackConf(doc);

		Assert.assertEquals(true, conf.mBp0Ok);
		Assert.assertEquals("http://sp.pf.mbga.jp/12012090/?url=http%3A%2F%2Fmadoka2.sp.nextory.co.jp%2Fraid_boss_battle_exe.php%3FPHPSESSID%3Drpevgkaqv6q73utpobsgkfkin3%26all%3D0%26first%3D1%26ticket%3D7aca6fe4b25ef23e326b96f4bfb539c9%26bp%3D1", conf.mBp1Url);
		Assert.assertEquals(null, conf.mBp3Url);
	}

	@Test
	public void testToRaidBossAttackConf1() throws Exception {
		FileInputStream fis = new FileInputStream("testdata/conf1.txt");
		Document doc = Jsoup.parse(fis, "UTF-8", "http://sp.pf.mbga.jp/12012090/?url=http%3A%2F%2Fmadoka2.sp.nextory.co.jp%2Fraid_boss_matching_feed.php");
		fis.close();

		RaidBossAttackConf conf = RaidBossAttackConf.toRaidBossAttackConf(doc);

		Assert.assertEquals(false, conf.mBp0Ok);
		Assert.assertEquals("http://sp.pf.mbga.jp/12012090/?url=http%3A%2F%2Fmadoka2.sp.nextory.co.jp%2Fraid_boss_battle_exe.php%3FPHPSESSID%3Dbue5k087fvbnqblvodl184uv27%26all%3D0%26first%3D0%26ticket%3Ddc8782bdf89fc0e0f643dcd1829a99fb%26bp%3D1", conf.mBp1Url);
		Assert.assertEquals(null, conf.mBp3Url);
	}

}
