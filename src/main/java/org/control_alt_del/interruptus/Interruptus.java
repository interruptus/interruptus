package org.control_alt_del.interruptus;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceDestroyedException;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esperio.amqp.AMQPSource;
import com.espertech.esper.client.dataflow.EPDataFlowInstantiationException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;


// @TODO Implement zookeeper master process election/configuration management
/* 

  Some thoughts...

  If we use ZK we could have all Interruptus nodes connect to the AMQP broker listening to the same
  exchanges/topics. Only the master would trigger listeners or outbound flows. On master failure, new master from election could instantiate 
  listeners and outbound data flows. Essentially, it would have a live state of master at the time it died and we wouldn't skip a beat.

  In the same vein, we could have all Interruptus nodes be available for managing config. Reload config state whenever it changes via
  watcher notifications.

  Makes it easier to manage the service when it's clustered (stick it behind a load balancer, don't need to worry about which node you hit)

*/

public class Interruptus {

	private static Configuration config;
	private static EPServiceProvider epService;

	public static void main(String[] args) throws Exception, InterruptedException {
		config = new Configuration();
		config.addImport(AMQPSource.class.getPackage().getName() + ".*");
		epService = EPServiceProviderManager.getDefaultProvider(config);
		epService.initialize();
		Server server = new Server(8080);
	        ServletHandler handler = new ServletHandler();
		server.setHandler(handler);
		server.start();
		server.join();
	}

}
