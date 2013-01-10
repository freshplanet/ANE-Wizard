package ${PACKAGE_NAME}.${PROJECT_NAME};

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import ${PACKAGE_NAME}.${PROJECT_NAME}.functions.IsSupportedFunction;

public class ${PROJECT_NAME}ExtensionContext extends FREContext 
{
	private static String TAG = "[${PROJECT_NAME}]";
	
	public ${PROJECT_NAME}ExtensionContext()
	{
		Log.d(TAG, "Creating Extension Context");
	}
	
	@Override
	public void dispose() 
	{
		Log.d(TAG, "Disposing Extension Context");
		${PROJECT_NAME}Extension.context = null;
	}

	/**
	 * Registers AS function name to Java Function Class
	 */
	@Override
	public Map<String, FREFunction> getFunctions() 
	{
		Log.d(TAG, "Registering Extension Functions");
		Map<String, FREFunction> functionMap = new HashMap<String, FREFunction>();
		functionMap.put("isSupported", new IsSupportedFunction());
		// add other functions here
		return functionMap;	
	}
}
