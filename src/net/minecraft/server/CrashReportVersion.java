package net.minecraft.server;

import java.util.concurrent.Callable;

public class CrashReportVersion
implements Callable
{
	public CrashReportVersion(CrashReport paramCrashReport)
	{
	}

	public String a()
	{
		return "1.4.2";
	}

	@Override
	public Object call() throws Exception {
		throw new Exception("CrashReportVersion callable? - the fuck?");
	}
}