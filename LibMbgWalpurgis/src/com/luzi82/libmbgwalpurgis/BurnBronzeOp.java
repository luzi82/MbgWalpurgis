package com.luzi82.libmbgwalpurgis;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class BurnBronzeOp {

	final public MwClient mMwClient;
	final public HttpClient mHttpClient;

	final String[] ALL_RANK = { "N", "N+", "R", "R+", "SR", "SR+" };
	final String[] UPGRADE_OK = { "R+", "SR", "SR+" };
	final String[] EAT_OK = { "N+", "N" };
	final List<String> UPGRADE_OK_LIST = Arrays.asList(UPGRADE_OK);
	final List<String> EAT_OK_LIST = Arrays.asList(EAT_OK);

	// public ICallback<Exception> mExceptionCallback;

	public BurnBronzeOp(MwClient aMwClient, HttpClient aHttpClient) {
		super();
		this.mMwClient = aMwClient;
		this.mHttpClient = aHttpClient;
	}

	public class Part {

		public final int mGirl;
		public String mMainGirlCid = null;

		public Part(int aGirl) {
			mGirl = aGirl;
		}

		public void start(final ICallback<Void> aCallback, final ICallback<Exception> aExceptionCallback) {
			final AsynList al = new AsynList();

			al.addRunnable(new Runnable() {
				@Override
				public void run() {
					// filter girl, sort by atk up
					HttpUriRequest request = new HttpGet("http://sp.pf.mbga.jp/12012090/?guid=ON&url=http%3A%2F%2Fmadoka2.sp.nextory.co.jp%2Fcardadd_change.php%3Fsort%3D7%26kind%3D0%26chara%3D" + mGirl + "%26equip%3D0");
					al.startNext(request);
				}
			});
			al.addCallback(mMwClient.httpDocCallback(mHttpClient, al.createStartNextCallback(new Document[0]), aExceptionCallback));
			al.addCallback(new CallbackE<Document>(aExceptionCallback) {
				@Override
				public void cb(Document aDocument) throws Exception {
					Elements cardElements = aDocument.select("div[class=basic-bg]");
					Element cardElement = null;
					int okRate = -1;
					for (Element e : cardElements) {
						Elements rateElements = e.select("a span");
						for (Element rateElement : rateElements) {
							String rateText = rateElement.text();
							int rate = UPGRADE_OK_LIST.indexOf(rateText);
							if (rate > okRate) {
								System.out.println(rateText);
								cardElement = e;
								okRate = rate;
							}
						}
					}
					if (cardElement == null) {
						Utils.startCallback(aCallback, null, mMwClient.mExecutor);
						return;
					}

					System.out.println("(cardElement != null)");
					mMainGirlCid = getCid(cardElement);

					Elements link = cardElement.select("a[href*=cardadd_change.php]");
					Utils.parseCheck(1, link.size());
					String href = link.get(0).absUrl("href");
					System.out.println(href);

					HttpGet httpGet = new HttpGet(href);
					al.startNext(httpGet);
				}
			});
			al.addCallback(mMwClient.httpDocCallback(mHttpClient, al.createStartNextCallback(new Document[0]), aExceptionCallback));
			al.addRunnable(new Runnable() {
				@Override
				public void run() {
					// filter girl, sort by atk down
					String cardadd_list_url = "http://sp.pf.mbga.jp/12012090/?url=http%3A%2F%2Fmadoka2.sp.nextory.co.jp%2Fcardadd_list.php%3Foff%3D%26chara%3D+" + mGirl + "%26set%3D1%26sort%3D1";
					HttpGet httpGet = new HttpGet(cardadd_list_url);
					al.startNext(httpGet);
				}
			});
			al.addCallback(mMwClient.httpDocCallback(mHttpClient, al.createStartNextCallback(new Document[0]), aExceptionCallback));
			al.addCallback(new CallbackE<Document>(aExceptionCallback) {
				@Override
				public void cb(Document aDoc) throws Exception {
					// check first appear card
					Elements cardElements = aDoc.select("div[class=basic-bg]");
					Utils.parseCheck(cardElements.size() >= 1);
					Utils.parseCheck(getCid(cardElements.get(0)).equals(mMainGirlCid));

					LinkedList<String> selectedCid = new LinkedList<String>();

					cardElements = aDoc.select("form div[class=basic-bg]");
					for (Element e : cardElements) {
						Elements checkboxElements = e.select("input[type=checkbox][name^=cards]");
						if (checkboxElements.size() != 1)
							continue;
						String cid = checkboxElements.get(0).attr("value");
						Utils.parseCheck(getCid(e), cid);

						Elements rankElements = e.select("a span");
						for (Element rateElement : rankElements) {
							String rankText = rateElement.text();
							int offset = EAT_OK_LIST.indexOf(rankText);
							if (offset != -1) {
								selectedCid.add(cid);
							}
						}
					}

					for (String cid : selectedCid) {
						System.out.println(cid);
					}

					if (selectedCid.size() <= 0) {
						Utils.startCallback(aCallback, null, mMwClient.mExecutor);
						return;
					}

					Elements formElements = aDoc.select("form[action*=cardadd_list.php]:has(input[type=submit])");
					Utils.parseCheck(1, formElements.size());
					Element formElement = formElements.get(0);
					String formUrl = formElement.absUrl("action");
					System.out.println(formUrl);

					Elements submitElements = formElement.select("input[type=submit][name=add][value=強化する]");
					Utils.parseCheck(2, submitElements.size());
					Element submitElement = submitElements.get(0);

					HttpPost httpPost = new HttpPost(formUrl);
					httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
					LinkedList<NameValuePair> nvpList = new LinkedList<NameValuePair>();
					nvpList.add(new BasicNameValuePair(submitElement.attr("name"), submitElement.attr("value")));
					for (String cid : selectedCid) {
						nvpList.add(new BasicNameValuePair("cards[]", cid));
					}
					String entityString = URLEncodedUtils.format(nvpList, "UTF-8");
					System.out.println(entityString);
					StringEntity entity = new StringEntity(entityString);
					httpPost.setEntity(entity);

					al.startNext(httpPost);
				}
			});
			al.addCallback(mMwClient.httpDocCallback(mHttpClient, al.createStartNextCallback(new Document[0]), aExceptionCallback));
			al.addCallback(new CallbackE<Document>(aExceptionCallback) {
				@Override
				public void cb(Document aDoc) throws Exception {
					// FINAL CHECK
					checkFinalPage(aDoc);

					Elements formElements = aDoc.select("form[action*=cardadd_exe.php]:has(input[type=submit][name=cardadd])");
					System.out.println(formElements.size());
					Element formElement = formElements.get(0);
					String formUrl = formElement.absUrl("action");

					Elements hiddenElements = formElement.select("input[type=hidden]");

					Elements submitElements = formElement.select("input[type=submit][name=cardadd]");
					Utils.parseCheck(1, submitElements.size());
					Element submitElement = submitElements.get(0);

					System.out.println(formUrl);
					HttpPost httpPost = new HttpPost(formUrl);
					httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded");
					LinkedList<NameValuePair> nvpList = new LinkedList<NameValuePair>();
					nvpList.add(new BasicNameValuePair(submitElement.attr("name"), submitElement.attr("value")));
					for (Element e : hiddenElements) {
						nvpList.add(new BasicNameValuePair(e.attr("name"), e.attr("value")));
					}
					String entityString = URLEncodedUtils.format(nvpList, "UTF-8");
					System.out.println(entityString);
					StringEntity entity = new StringEntity(entityString);
					httpPost.setEntity(entity);

					al.startNext(httpPost);
					//					 al.startNext(null);
				}
			});
			al.addCallback(mMwClient.httpDocCallback(mHttpClient, al.createStartNextCallback(new Document[0]), aExceptionCallback));
			al.addRunnable(new Runnable() {
				@Override
				public void run() {
					al.startNext(null);
				}
			});
			al.addCallback(aCallback);

			al.start(mMwClient.mExecutor);
		}

		public ICallback<Void> callback(final ICallback<Void> aCallback, final ICallback<Exception> aExceptionCallback) {
			return new ICallback<Void>() {
				@Override
				public void callback(Void aResult) {
					start(aCallback, aExceptionCallback);
				}
			};
		}

		public void checkFinalPage(Document aDoc) throws ParseException {
			Elements cardElements = aDoc.select("div[class=basic-bg]");
			System.out.println("cardElements.size()=" + cardElements.size());
			boolean first = true;
			for (Element cardElement : cardElements) {
				if (first) {
					Utils.parseCheck(mMainGirlCid, getCid(cardElement));
					checkCardGirl(cardElement, mGirl);
					checkCardRank(cardElement, UPGRADE_OK);
				} else {
					checkCardGirl(cardElement, mGirl);
					checkCardRank(cardElement, EAT_OK);
				}
				first = false;
			}
			Elements blinks = aDoc.select("div[class=blink]");
			for (Element blink : blinks) {
				Utils.parseCheck(!blink.text().contains("レアカード"));
			}
		}

	}

	public void start(ICallback<Void> aCallback, ICallback<Exception> aExceptionCallback) {
		AsynList al = new AsynList();
		for (int i = 0; i < 5; ++i) {
			Part part = new Part(i + 1);
			al.addCallback(part.callback(al.createStartNextCallback(new Void[0]), aExceptionCallback));
		}
		al.addCallback(aCallback);
		al.start(mMwClient.mExecutor);
	}

	public String getCid(Element aElement) throws ParseException {
		Elements labelElements = aElement.select("label[for]");
		Utils.parseCheck(labelElements.size() >= 1);
		String cbString = null;
		for (Element labelElement : labelElements) {
			String labelString = labelElement.attr("for");
			if (cbString == null) {
				cbString = labelString;
			}
			Utils.parseCheck(labelString.equals(cbString));
		}
		Utils.parseCheck(cbString.startsWith("cb"));
		String cid = cbString.substring(2);

		Elements aes = aElement.select("a[href*=card_detail.php][href*=card_sid]");
		Utils.parseCheck(1, aes.size());
		Utils.parseCheck(aes.attr("href").contains("card_sid%3D" + cid));

		return cid;
	}

	public void checkCardGirl(Element aElement, int aGirl) throws ParseException {
		Elements img = aElement.select("img[src*=http%3A%2F%2Fmadoka2.sp.nextory.co.jp%2Fimg%2Fcard%2F]");
		Utils.parseCheck(1, img.size());
		Utils.parseCheck(img.attr("src").contains("card_" + aGirl));
	}

	public void checkCardRank(Element aElement, String[] aRank) throws ParseException {
		List<String> rankList = Arrays.asList(aRank);
		Elements aes = aElement.select("a[href*=card_detail.php][href*=card_sid]");
		Utils.parseCheck(1, aes.size());
		Elements spans = aes.select("span");
		for (Element span : spans) {
			String txt = span.text().trim();
			if (rankList.contains(txt))
				return;
		}
		for (Element span : spans) {
			String txt = span.text().trim();
			System.err.println(txt);
		}
		Utils.parseCheck(false);
	}

}
