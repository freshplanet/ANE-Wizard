package ${PACKAGE_NAME}.${PROJECT_NAME}.functions;

import android.util.Log;

import com.adobe.fre.FREContext;
import com.adobe.fre.FREFunction;
import com.adobe.fre.FREObject;
import com.adobe.fre.FREWrongThreadException;

/**
 * Example function.
 *
 * Create a new class for each function in your API. Don't forget to add them in
 * ${PROJECT_NAME}ExtensionContext.getFunctions().
 */
public class IsSupportedFunction implements FREFunction 
{
	private static String TAG = "[${PROJECT_NAME}] IsSupported -";
	
	public FREObject call(FREContext context, FREObject[] args) 
	{
		Log.d(TAG, "true");
		try
		{
			return FREObject.newObject(true);
		}
		catch (FREWrongThreadException exception)
		{
			Log.d(TAG, exception.getLocalizedMessage());
			return null;
		}
	}
}
