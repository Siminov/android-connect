package siminov.connect.service;

import java.util.Iterator;

import siminov.connect.Constants;
import siminov.connect.design.service.IService;
import siminov.connect.design.service.IServiceWorker;
import siminov.connect.exception.ServiceException;
import siminov.connect.model.ServiceDescriptor;
import siminov.connect.model.ServiceDescriptor.API;
import siminov.connect.resource.ResourceUtils;
import siminov.connect.resource.Resources;
import siminov.connect.resource.ServiceResourceUtils;
import siminov.connect.worker.service.AsyncServiceWorker;
import siminov.connect.worker.service.SyncServiceWorker;


public class ServiceHandler {

	private static ServiceHandler serviceHandler = null;
	
	private IServiceWorker syncServiceWorker = null;
	private IServiceWorker asyncServiceWorker = null;
	
	private Resources resources = null;
	
	private ServiceHandler() {
		
		syncServiceWorker =  new SyncServiceWorker();
		asyncServiceWorker = AsyncServiceWorker.getInstance();
		
		resources = Resources.getInstance();
	}
	
	public static ServiceHandler getInstance() {
		
		if(serviceHandler == null) {
			serviceHandler = new ServiceHandler();
		}
		
		return serviceHandler;
	}
	
	public void handle(final IService service) throws ServiceException {

		ServiceDescriptor serviceDescriptor = service.getServiceDescriptor();
		if(serviceDescriptor == null) {
			serviceDescriptor = resources.requiredServiceDescriptorBasedOnName(service.getService());
			service.setServiceDescriptor(serviceDescriptor);
		}


		Iterator<String> inlineResources = service.getResources();
		while(inlineResources.hasNext()) {
			String inlineResource = inlineResources.next();
			
			if(service.getResource(inlineResource) instanceof String) {
				serviceDescriptor.addProperty(inlineResource, (String) service.getResource(inlineResource));
			}
		}

		
		API api = serviceDescriptor.getApi(service.getApi());
		String mode = ResourceUtils.resolve(api.getMode(), serviceDescriptor);
		
		if(mode.equalsIgnoreCase(Constants.SERVICE_DESCRIPTOR_API_SYNC_REQUEST_MODE)) {

			ServiceResourceUtils.resolve(service);
			syncServiceWorker.process(service);
		} else if(mode.equalsIgnoreCase(Constants.SERVICE_DESCRIPTOR_API_ASYNC_REQUEST_MODE)) {
			asyncServiceWorker.process(service);
		}
	}
}
