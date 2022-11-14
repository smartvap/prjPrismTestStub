package org.ayakaji.prism.pipeline;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.logging.Logger;

import org.joda.time.DateTime;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

public class Simulator {
	
	private static final Logger logger = Logger.getLogger(Simulator.class.getName());
	
	private static String actionId = null;
	private static int uriSeqNo = 1;
	
	private static List<String> actions = new ArrayList<String>() {
		private static final long serialVersionUID = 8763032168965112116L;
		{
			add("Click");
			add("Keyboard");
			add("Drop");
		}
	};
	
	private static List<String> regions = new ArrayList<String>() {
		private static final long serialVersionUID = 8763032168965112116L;
		{
			add("531");
			add("532");
			add("533");
			add("534");
			add("535");
			add("536");
			add("537");
			add("538");
			add("539");
			add("530");
			add("543");
			add("546");
			add("631");
			add("632");
			add("633");
			add("634");
			add("635");
		}
	};

	/**
	 * 获取一个随机IP
	 */
	private static String getRandomIp() {

	    int[][] range = {
	            {607649792, 608174079}, // 36.56.0.0-36.63.255.255
	            {1038614528, 1039007743}, // 61.232.0.0-61.237.255.255
	            {1783627776, 1784676351}, // 106.80.0.0-106.95.255.255
	            {2035023872, 2035154943}, // 121.76.0.0-121.77.255.255
	            {2078801920, 2079064063}, // 123.232.0.0-123.235.255.255
	            {-1950089216, -1948778497}, // 139.196.0.0-139.215.255.255
	            {-1425539072, -1425014785}, // 171.8.0.0-171.15.255.255
	            {-1236271104, -1235419137}, // 182.80.0.0-182.92.255.255
	            {-770113536, -768606209}, // 210.25.0.0-210.47.255.255
	            {-569376768, -564133889}, // 222.16.0.0-222.95.255.255
	    };

	    Random random = new Random();
	    int index = random.nextInt(10);
	    String ip = num2ip(range[index][0] + random.nextInt(range[index][1] - range[index][0]));
	    return ip;
	}

	public static String num2ip(int ip) {
	    int[] b = new int[4];
	    b[0] = (ip >> 24) & 0xff;
	    b[1] = (ip >> 16) & 0xff;
	    b[2] = (ip >> 8) & 0xff;
	    b[3] = ip & 0xff;
	    String x = b[0] + "." + b[1] + "." + b[2] + "." + b[3];
	    return x;
	}

	private static void browser_tier() {
		JSONObject jsonObj = new JSONObject(new LinkedHashMap<String, Object>());
		jsonObj.put("USER_ACTION_ID", UUID.randomUUID().toString()); // 用户行为统一标识
		jsonObj.put("USER_ACTION_TYPE", actions.get(new Random().nextInt(2))); // 用户行为类型：鼠标（点击）、键盘（录入）、拖拽等
		jsonObj.put("IFRAME_TIER", "mainFrame >> rightFrame >> orderSubmit"); // 页面元素所述IFrame所在层级关系
		jsonObj.put("ELEM_XPATH", "//*[@id=\"toolBarBox\"]/div/div[1]/div[2]/a[1]"); // 页面元素定位符
		jsonObj.put("ENTER_TEXT", "Make it possible!"); // 录入内容
		jsonObj.put("URI", "https://www.sd.10086.cn/" + RandomUtil.getStringRandom(5)); // 动态链接
		jsonObj.put("URI_SEQ_NO", uriSeqNo++); // 动态链接在当前用户行为中的触发序号
		jsonObj.put("URI_STATUS_CODE", 200); // 状态码
		jsonObj.put("TIME_COMP", new JSONObject(new LinkedHashMap<String, Object>()) { // 时间构成项
			private static final long serialVersionUID = 1L;
			{
				put("DNS_RESOLVE_MS", new Random().nextInt(19));
				put("TCP_ESTAB", new Random().nextInt(29));
				put("UPSTREAM", new Random().nextInt(1199));
				put("TRANSFER", new Random().nextInt(119));
				put("DOM_PARSE", new Random().nextInt(17));
			}
		}); // 时间构成项
		jsonObj.put("REGION", regions.get(new Random().nextInt(16))); // 用户所述区号
		jsonObj.put("REAL_IP", getRandomIp()); // 真实用户IP
		jsonObj.put("MAC", MacRandom.randomMac4Qemu()); // 用户终端MAC
		jsonObj.put("OPER_NAME", NameRandom.getChineseName()); // 用户名称
		jsonObj.put("OPER_ID", RandomUtil.getStringRandom(8)); // alias: staff_id, user_id
		jsonObj.put("PHONE_NO", PhoneRandom.getTel()); // 电话号码
		jsonObj.put("EMAIL", EmailRandom.getEmail(5, 17)); // 邮箱
		jsonObj.put("GEO", LonLatRandom.randomLonLat(112.1212, 134.1212, 43.12, 65.123)); // 地理坐标
		jsonObj.put("DEV_SERNO", UUID.randomUUID().toString()); // 终端序列号
		jsonObj.put("USER_AGENT", UserAgentUtil.randomUserAgent()); // 浏览器版本
		jsonObj.put("TERM_CURR_DATE", new DateTime().toString("yyyy-MM-dd HH:mm:ss")); // 终端当前时间
		jsonObj.put("STACK_HIER", "1"); // 技术栈层级，向后累加
		jsonObj.put("SAMPLE_DATA", "TRUE"); // 是否采样数据
		logger.info(JSON.toJSONString(jsonObj, true));
	}
	
	private static void service_tier() {
		JSONObject jsonObj = new JSONObject(new LinkedHashMap<String, Object>());
		jsonObj.put("USER_ACTION_ID", actionId); // 用户行为统一标识
		jsonObj.put("URI_SEQ_NO", uriSeqNo); // 动态链接在当前用户行为中的触发序号
		jsonObj.put("CONTAINER_ID", RandomUtil.generateByRandom(64).toLowerCase()); // 容器ID
		jsonObj.put("CONTAINER_NAME", "k8s_default_crm-sd-c4-zk01-broker-2-67f57f745c-dpr6q_crm_072ee42f-2f69-4c91-bb98-" + RandomUtil.getStringRandom(12) + "_0"); // 容器名称
		jsonObj.put("PID", new Random().nextInt(65000));
		jsonObj.put("TID", new Random().nextInt(65000));
		jsonObj.put("PORT", 8080);
		jsonObj.put("THREAD_NAME", "Thread-" + new Random().nextInt(120));
		jsonObj.put("METHOD_NAME", "client.invoke()");
		logger.info(JSON.toJSONString(jsonObj, true));
	}

	public static void main(String[] args) {
		browser_tier();
		service_tier();
		service_tier();
	}

}
