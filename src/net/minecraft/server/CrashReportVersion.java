package net.minecraft.server;

import java.util.concurrent.Callable;

public class CrashReportVersion
implements Callable<String>
{
	public CrashReportVersion(CrashReport paramCrashReport)
	{
	}

	public String a()
	{
		return "1.4.2";
	}

	@Override
	public String call() throws Exception {
		return a();
	}
}