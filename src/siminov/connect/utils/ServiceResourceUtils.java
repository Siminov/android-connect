package siminov.connect.utils;

import java.util.Iterator;

import siminov.connect.model.ServiceDescriptor;
import siminov.connect.model.ServiceDescriptor.API;
import siminov.connect.model.ServiceDescriptor.API.HeaderParameter;
import siminov.connect.model.ServiceDescriptor.API.QueryParameter;
import siminov.connect.service.design.IService;
import siminov.orm.exception.SiminovException;

public class ServiceResourceUtils {

	public static void resolve(final IService service) throws SiminovException {

		/*
		 * Resolve Service Descriptor Properties
		 */
		ServiceDescriptor serviceDescriptor = service.getServiceDescriptor();
		
		Iterator<String> inlineResources = service.getInlineResources();
		while(inlineResources.hasNext()) {
			String inlineResource = inlineResources.next();
			serviceDescriptor.addProperty(inlineResource, service.getInlineResource(inlineResource));
		}
		
		
		Iterator<String> serviceDescriptorProperties = serviceDescriptor.getProperties();
		while(serviceDescriptorProperties.hasNext()) {
			
			String serviceDescriptorProperty = serviceDescriptorProperties.next();
			String serviceDescriptorValue = serviceDescriptor.getProperty(serviceDescriptorProperty);
			serviceDescriptorValue = ResourceUtils.resolve(serviceDescriptorValue, serviceDescriptor);
			
			serviceDescriptor.addProperty(serviceDescriptorProperty, serviceDescriptorValue);
		}
		
		
		/*
		 * Resolve API Properties
		 */
		API api = serviceDescriptor.getApi(service.getApi());
		Iterator<String> apiProperties = api.getProperties();
		while(apiProperties.hasNext()) {
			
			String apiProperty = apiProperties.next();
			String apiValue = api.getProperty(apiProperty);
			apiValue = ResourceUtils.resolve(apiValue, serviceDescriptor, api);
			
			api.addProperty(apiProperty, apiValue);
		}

		
		/*
		 * Resolve API Query Parameters
		 */
		Iterator<QueryParameter> queryParameters = api.getQueryParameters();
		while(queryParameters.hasNext()) {
			
			QueryParameter queryParameter = queryParameters.next();
			
			String queryValue = queryParameter.getValue();
			queryValue = ResourceUtils.resolve(queryValue, serviceDescriptor, api, queryParameter);
			
			queryParameter.setValue(queryValue);
		}

		
		/*
		 * Resolve API Query Parameters
		 */
		Iterator<HeaderParameter> headerParameters = api.getHeaderParameters();
		while(headerParameters.hasNext()) {
			
			HeaderParameter headerParameter = headerParameters.next();
			
			String headerValue = headerParameter.getValue();
			headerValue = ResourceUtils.resolve(headerValue, serviceDescriptor, api, headerParameter);
			
			headerParameter.setValue(headerValue);
		}
	}
}
