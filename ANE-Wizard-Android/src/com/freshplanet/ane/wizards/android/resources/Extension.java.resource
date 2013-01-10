package ${PACKAGE_NAME}.${PROJECT_NAME};

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREExtension;

public class ${PROJECT_NAME}Extension implements FREExtension 
{
	private static String TAG = "[${PROJECT_NAME}]";

	public static FREContext context;
	
	/**
	 * Create the context (AS to Java).
	 */
	public FREContext createContext(String extId)
	{
		Log.d(TAG, "Extension.createContext extId: " + extId);
		return context = new ${PROJECT_NAME}ExtensionContext();
	}

	/**
	 * Dispose the context.
	 */
	public void dispose() 
	{
		Log.d(TAG, "Extension.dispose");
		context = null;
	}
	
	/**
	 * Initialize the context.
	 * Doesn't do anything for now.
	 */
	public void initialize() 
	{
		Log.d(TAG, "Extension.initialize");
	}
}
