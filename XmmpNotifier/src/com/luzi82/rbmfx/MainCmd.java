package com.luzi82.rbmfx;

import com.luzi82.nagatoquery.NqExec.CommandHandler;

public class MainCmd {

	public static void cmd_pollstart(CommandHandler aHandler) {
		Main.mMain.mMw.start();
		aHandler.mCommandListener.commandReturn(null);
	}

	public static void cmd_pollstop(CommandHandler aHandler) {
		Main.mMain.mMw.stop();
		aHandler.mCommandListener.commandReturn(null);
	}

	public static void cmd_pollfreq(CommandHandler aHandler) {
		int freq = Main.mMain.mMw.getPollFreq();
		aHandler.mCommandListener.commandReturn(Integer.toString(freq));
	}

	public static void cmd_msgstart(CommandHandler aHandler) {
		Main.mMain.mMsgEnabled = true;
		aHandler.mCommandListener.commandReturn(null);
	}

	public static void cmd_msgstop(CommandHandler aHandler) {
		Main.mMain.mMsgEnabled = false;
		aHandler.mCommandListener.commandReturn(null);
	}

}
