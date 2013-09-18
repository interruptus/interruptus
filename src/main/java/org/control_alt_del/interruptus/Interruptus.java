package org.control_alt_del.interruptus;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPServiceDestroyedException;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esperio.amqp.AMQPSource;
import com.espertech.esper.client.dataflow.EPDataFlowInstantiationException;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;

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
