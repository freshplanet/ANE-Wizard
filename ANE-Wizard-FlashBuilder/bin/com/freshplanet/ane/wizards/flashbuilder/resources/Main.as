package ${PACKAGE_NAME}
{
	import flash.events.EventDispatcher;
	import flash.events.StatusEvent;
	import flash.external.ExtensionContext;

	public class ${PROJECT_NAME} extends EventDispatcher
	{
		private static var _instance:${PROJECT_NAME};
		
		private var extCtx:ExtensionContext = null;
				
		public function ${PROJECT_NAME}()
		{
			if (!_instance)
			{
				extCtx = ExtensionContext.createExtensionContext("${PACKAGE_NAME}.${PROJECT_NAME}", null);
				if (extCtx != null)
				{
					extCtx.addEventListener(StatusEvent.STATUS, onStatus);
				} 
				else
				{
					trace('[${PROJECT_NAME}] Error - Extension Context is null.');
				}
				_instance = this;
			}
			else
			{
				throw Error('This is a singleton, use getInstance(), do not call the constructor directly.');
			}
		}
		
		public static function getInstance() : ${PROJECT_NAME}
		{
			return _instance ? _instance : new ${PROJECT_NAME}();
		}
		
		
		/**
		 * Example function.
		 * Define your own API and use extCtx.call() to communicate with the native part of the ANE.
		 */
		public function isSupported() : Boolean
		{
			return extCtx.call('isSupported');
		}
		
		
		/**
		 * Status events allow the native part of the ANE to communicate with the ActionScript part.
		 * We use event.code to represent the type of event, and event.level to carry the data.
		 */
		private function onStatus( event : StatusEvent ) : void
		{
			if (event.code == "LOGGING")
			{
				trace('[${PROJECT_NAME}] ' + event.level);
			}
		}
	}
}