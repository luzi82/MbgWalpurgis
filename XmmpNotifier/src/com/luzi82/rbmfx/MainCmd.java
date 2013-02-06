package com.luzi82.rbmfx;

import com.luzi82.libmbgwalpurgis.ICallback;
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

	public static void cmd_poll(CommandHandler aHandler) {
		aHandler.mCommandListener.commandReturn(Boolean.toString(Main.mMain.mMw.mMaintainScheduledFuture != null));
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

	public static void cmd_msg(CommandHandler aHandler) {
		aHandler.mCommandListener.commandReturn(Boolean.toString(Main.mMain.mMsgEnabled));
	}

	public static void cmd_lpexpset(CommandHandler aHandler, float aValue) {
		Main.mMain.mMw.mLp2Exp = aValue;
		aHandler.mCommandListener.commandReturn(null);
	}

	public static void cmd_lpexp(CommandHandler aHandler) {
		aHandler.mCommandListener.commandReturn(Float.toString(Main.mMain.mMw.mLp2Exp));
	}

	public static void cmd_burn(final CommandHandler aHandler) {
		Main.mMain.mMw.burnBronze(new ICallback<Void>() {
			@Override
			public void callback(Void aResult) {
				aHandler.mCommandListener.commandTrace("done");
				aHandler.mCommandListener.commandReturn(null);
			}
		}, new ICallback<Exception>() {
			@Override
			public void callback(Exception aResult) {
				aHandler.mCommandListener.commandError(aResult.getMessage());
				aResult.printStackTrace();
			}
		});
	}

}
