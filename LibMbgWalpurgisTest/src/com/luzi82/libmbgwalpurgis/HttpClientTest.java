package com.luzi82.libmbgwalpurgis;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.DefaultRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.junit.Test;

public class HttpClientTest {

	@Test
	public void testLoginPage() {
		try {
			Properties props = new Properties();
			FileInputStream authFileIn = new FileInputStream("auth.properties");
			props.load(authFileIn);
			authFileIn.close();

			HttpParams httpParams = new BasicHttpParams();
			httpParams.setParameter(CoreProtocolPNames.USER_AGENT, "Mozilla/5.0 (Linux; U; Android 4.0.2; en-us; Galaxy Nexus Build/ICL53F) AppleWebKit/534.30 (KHTML, like Gecko) Version/4.0 Mobile Safari/534.30");
			HttpClientParams.setRedirecting(httpParams, true);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
			httpClient.setRedirectStrategy(new DefaultRedirectStrategy() {
				@Override
				public boolean isRedirected(HttpRequest request, HttpResponse response, HttpContext context) throws ProtocolException {
					if (super.isRedirected(request, response, context))
						return true;
					int code = response.getStatusLine().getStatusCode();
					if (code == 302)
						return true;
					return false;
				}
			});

			HttpGet httpGet = new HttpGet("https://ssl.sp.mbga.jp/_lg");
			HttpResponse response = httpClient.execute(httpGet);
			System.out.println(response.getStatusLine());
			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			HttpEntity httpEntity = response.getEntity();
			String contentType = httpEntity.getContentType().getValue();
			System.out.println(contentType);
			Assert.assertEquals("text/html; charset=UTF-8", contentType);

			InputStream is = httpEntity.getContent();
			Document doc = Jsoup.parse(is, "UTF-8", "https://ssl.sp.mbga.jp/_lg");

			Elements formElements = doc.select("form[action*=https://ssl.sp.mbga.jp/_lg]");
			Assert.assertEquals(1, formElements.size());
			Element formElement = formElements.get(0);

			Elements inputElements = formElement.select("input");
			for (Element inputElement : inputElements) {
				System.out.println(inputElement.attr("name") + ": " + inputElement.attr("value"));
			}
			Assert.assertEquals(1, inputElements.select("[name*=login_id]").size());
			Assert.assertEquals(1, inputElements.select("[name*=login_pw]").size());
			Assert.assertEquals(1, inputElements.select("[type*=submit]").size());
			for (Element inputElement : inputElements) {
				String type = inputElement.attr("type");
				if (type.equals("hidden"))
					continue;
				if ((type.equals("email")) && (inputElement.id().equals("login_id")))
					continue;
				if ((type.equals("password")) && (inputElement.id().equals("login_pw")))
					continue;
				if (type.equals("submit"))
					continue;
				Assert.fail("type=" + type);
			}

			HttpPost httpPost = new HttpPost("https://ssl.sp.mbga.jp/_lg");
			List<NameValuePair> nvpList = new LinkedList<NameValuePair>();
			nvpList.add(new BasicNameValuePair("login_id", props.getProperty("login_id")));
			nvpList.add(new BasicNameValuePair("login_pw", props.getProperty("login_pw")));
			Elements hiddenInputElements = formElement.select("[type*=hidden]");
			for (Element hiddenInputElement : hiddenInputElements) {
				nvpList.add(new BasicNameValuePair(hiddenInputElement.attr("name"), hiddenInputElement.attr("value")));
			}
			Element submitElement = inputElements.select("[type*=submit]").get(0);
			nvpList.add(new BasicNameValuePair(submitElement.attr("name"), submitElement.attr("value")));
			StringEntity entity = new StringEntity(URLEncodedUtils.format(nvpList, "UTF-8"));
			httpPost.setEntity(entity);
			response = httpClient.execute(httpPost);

			System.out.println(response.getStatusLine());
			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			httpEntity = response.getEntity();
			is = httpEntity.getContent();
			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			char[] buf = new char[1 << 16];
			while (true) {
				int len = isr.read(buf);
				if (len == -1) {
					break;
				}
				System.out.print(Arrays.copyOf(buf, len));
			}

			CookieStore cookieStore = httpClient.getCookieStore();
			List<Cookie> cookieList = cookieStore.getCookies();
			for (Cookie cookie : cookieList) {
				System.out.println(cookie.getDomain() + ":" + cookie.getName() + ":" + cookie.getValue());
			}

			httpGet = new HttpGet("http://sp.pf.mbga.jp/12012090");
			response = httpClient.execute(httpGet);
			System.out.println(response.getStatusLine());
			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			httpEntity = response.getEntity();
			is = httpEntity.getContent();
			isr = new InputStreamReader(is, "UTF-8");
			while (true) {
				int len = isr.read(buf);
				if (len == -1) {
					break;
				}
				System.out.print(Arrays.copyOf(buf, len));
			}

			httpGet = new HttpGet("http://sp.pf.mbga.jp/12012090/?url=http%3A%2F%2Fmadoka2.sp.nextory.co.jp%2Fraid_boss_matching_feed.php");
			response = httpClient.execute(httpGet);
			System.out.println(response.getStatusLine());
			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			httpEntity = response.getEntity();
			is = httpEntity.getContent();
			doc = Jsoup.parse(is, "UTF-8", "http://sp.pf.mbga.jp/12012090/?url=http%3A%2F%2Fmadoka2.sp.nextory.co.jp%2Fraid_boss_matching_feed.php");

			Elements unitElements = doc.select("div[class*=basic-bg] td[valign*=top]");
			for (Element unitElement : unitElements) {
				List<Node> chileNodes = unitElement.childNodes();
				for (int i = 0; i < chileNodes.size(); ++i) {
					Node unitElementChild = chileNodes.get(i);
					if (unitElementChild instanceof Element) {
						Element unitElementChildElement = (Element) unitElementChild;
						System.out.println(i + ": <" + unitElementChildElement.tagName() + ">");
					} else if (unitElementChild instanceof TextNode) {
						TextNode unitElementChildTextNode = (TextNode) unitElementChild;
						String txt = unitElementChildTextNode.text();
						txt = txt.trim();
						if (!txt.isEmpty()) {
							System.out.println(i + ": " + txt);
						}
					}
				}
				Assert.assertEquals(10, chileNodes.size());

				Element e = (Element) chileNodes.get(1);
				Assert.assertEquals("img", e.tagName());
				String srcattr = e.attr("src");
				System.out.println(srcattr);
				Assert.assertTrue(srcattr.contains("icon01") || srcattr.contains("icon02"));

				TextNode tn = (TextNode) chileNodes.get(2);
				System.out.println(tn.text().trim());

				e = (Element) chileNodes.get(5);
				Assert.assertEquals("a", e.tagName());
				Assert.assertEquals(1, e.children().size());
				e = e.child(0);
				Assert.assertEquals("span", e.tagName());
				System.out.println(e.ownText().trim());

				tn = (TextNode) chileNodes.get(7);
				System.out.println(tn.text().trim());
			}

			httpGet = new HttpGet("http://sp.pf.mbga.jp/12012090/?url=http%3A%2F%2Fmadoka2.sp.nextory.co.jp%2Fmypage.php");
			response = httpClient.execute(httpGet);
			System.out.println(response.getStatusLine());
			Assert.assertEquals(200, response.getStatusLine().getStatusCode());

			httpEntity = response.getEntity();
			is = httpEntity.getContent();
			doc = Jsoup.parse(is, "UTF-8", "http://sp.pf.mbga.jp/12012090/?url=http%3A%2F%2Fmadoka2.sp.nextory.co.jp%2Fmypage.php");

			Elements mainStatusElement = doc.select("div[class*=main-status] span");
			for (Element e : mainStatusElement) {
				String t = e.ownText().trim();
				if (!t.contains("でレベルアップ"))
					continue;
				Elements eev = e.select("span");
				if (eev.size() != 2)
					continue;
				t = eev.get(1).ownText().trim();
				t = t.replaceAll(Pattern.quote(","), "");
				System.out.println(Integer.parseInt(t));
			}

			Elements cardElements = doc.select("div[class*=status-exsample] td div");
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
				System.out.println(ess.get(0).ownText().trim());
				System.out.println(e.ownText().trim().replaceAll(Pattern.quote("/"), ""));
			}

			Pattern lpPattern = Pattern.compile("LP:([\\d]+)/([\\d]+)");
			Pattern bpPattern = Pattern.compile("BP:([\\d]+)/([\\d]+)");
			Elements mmlElements = doc.select("span[class*=mypage-mode-label]");
			for (Element e : mmlElements) {
				String t = e.ownText().trim();
				Matcher m;
				m = lpPattern.matcher(t);
				if (m.find()) {
					System.out.println(m.group(1));
					System.out.println(m.group(2));
				}
				m = bpPattern.matcher(t);
				if (m.find()) {
					System.out.println(m.group(1));
					System.out.println(m.group(2));
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
