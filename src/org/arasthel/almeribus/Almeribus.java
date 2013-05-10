package org.arasthel.almeribus;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import android.app.Application;

@ReportsCrashes(formKey = "", // will not be used
	mailTo = "angel.arasthel@gmail.com",
	customReportContent = { ReportField.ANDROID_VERSION, ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE, ReportField.LOGCAT },                
	mode = ReportingInteractionMode.TOAST,
	resToastText = R.string.error)

public class Almeribus extends Application{
	
	@Override
	public void onCreate() {
		ACRA.init(this);
		super.onCreate();
	}
	
}
