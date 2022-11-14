package org.ayakaji.prism.pipeline;

import java.util.Random;

/**
 * 随机mac地址 代码源于网络 由kingYiFan整理 create2019/05/24
 */
public class MacRandom {
	private static String SEPARATOR_OF_MAC = ":";

	public static String randomMac4Qemu() {
		Random random = new Random();
		String[] mac = { String.format("%02x", 0x52), String.format("%02x", 0x54), String.format("%02x", 0x00),
				String.format("%02x", random.nextInt(0xff)), String.format("%02x", random.nextInt(0xff)),
				String.format("%02x", random.nextInt(0xff)) };
		return String.join(SEPARATOR_OF_MAC, mac);
	}

	public static void main(String[] args) {

		System.out.println(randomMac4Qemu());
	}
}