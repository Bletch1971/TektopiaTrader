package bletch.tektopiatrader.utils;

import bletch.common.utils.LoggerBase;
import bletch.tektopiatrader.core.ModConfig;
import bletch.tektopiatrader.core.ModDetails;

public class LoggerUtils extends LoggerBase {
	
	public static void Initialise(String debugLogFile) {
		LoggerBase.Initialise(new LoggerUtils(), ModDetails.MOD_NAME, debugLogFile);
	}
	
	@Override
	public void debug(String message, Boolean checkConfig) {
		if (checkConfig && !ModConfig.debug.enableDebug)
			return;
		
		debug(message);
	}
	
	@Override
	public void info(String message, Boolean checkConfig) {
		if (checkConfig && !ModConfig.debug.enableDebug)
			return;
		
		info(message);
	}

}
