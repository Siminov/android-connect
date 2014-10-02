/** 
 * [SIMINOV FRAMEWORK]
 * Copyright [2015] [Siminov Software Solution LLP|support@siminov.com]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 **/

package siminov.connect.connection;

import java.util.Iterator;

import siminov.connect.Constants;
import siminov.connect.connection.design.IConnectionRequest;
import siminov.connect.model.ServiceDescriptor;
import siminov.connect.model.ServiceDescriptor.API;
import siminov.connect.model.ServiceDescriptor.API.HeaderParameter;
import siminov.connect.model.ServiceDescriptor.API.QueryParameter;
import siminov.connect.service.design.IService;


/**
 * It provides Utility APIs for the communication
 */
public class ConnectionHelper {

	/**
	 * It build connection request instance based on the service descriptor object
	 * @param service Instance of IService
	 * @return IConnectionRequest instance
	 */
	public static IConnectionRequest prepareConnectionRequest(final IService service) {

		/*
		 * Resolve All Referring Resources
		 */
		ServiceDescriptor serviceDescriptor = service.getServiceDescriptor();
		API api = serviceDescriptor.getApi(service.getApi());
		
		String url = formUrl(service);
		
		Iterator<QueryParameter> queryParameters = formQueryParameters(service);
		Iterator<HeaderParameter> headerParameters = formHeaderParameters(service);
		
		byte[] dataStream = formDataStream(service);
		
		IConnectionRequest connectionRequest = new ConnectionRequest();
		connectionRequest.setUrl(url);
		connectionRequest.setProtocol(serviceDescriptor.getProtocol());
		connectionRequest.setType(api.getType());
		
		while(queryParameters.hasNext()) {
			connectionRequest.addQueryParameter(queryParameters.next());
		}
		
		while(headerParameters.hasNext()) {
			connectionRequest.addHeaderParameter(headerParameters.next());
		}

		connectionRequest.setDataStream(dataStream);

		return connectionRequest;
	}


	private static String formUrl(final IService service) {
		
		ServiceDescriptor serviceDescriptor = service.getServiceDescriptor();
		
		String apiName = service.getApi();
		API api = serviceDescriptor.getApi(apiName);
		
		String protocol = serviceDescriptor.getProtocol();
		
		String instance = serviceDescriptor.getInstance();
		String port = serviceDescriptor.getPort();
		
		String context = serviceDescriptor.getContext();
		String apiPath = api.getApi();
		
		StringBuilder url = new StringBuilder();

			if(protocol != null) {

				if(protocol.equalsIgnoreCase(Constants.SERVICE_DESCRIPTOR_HTTP_PROTOCOL)) {
					url.append(Constants.CONNECTION_HTTP);
				} else if(protocol.equalsIgnoreCase(Constants.SERVICE_DESCRIPTOR_HTTPS_PROTOCOL)) {
					url.append(Constants.CONNECTION_HTTPS);
				}

				url.append("://");
			}
				
			url.append(instance);

			if(port != null && port.length() > 0) {
				url.append(":" + port);
			}
			
			if(context != null && context.length() > 0) {
				url.append("/" + context);
			}
			
			if(apiPath != null && apiPath.length() > 0) {
				url.append("/" + apiPath);
			}
			
		return url.toString();
	}
	
	private static Iterator<QueryParameter> formQueryParameters(final IService service) {
		
		ServiceDescriptor serviceDescriptor = service.getServiceDescriptor();
		
		String apiName = service.getApi();
		API api = serviceDescriptor.getApi(apiName);
		
		return api.getQueryParameters();
	}
	
	private static Iterator<HeaderParameter> formHeaderParameters(final IService service) {

		ServiceDescriptor serviceDescriptor = service.getServiceDescriptor();
		
		String apiName = service.getApi();
		API api = serviceDescriptor.getApi(apiName);
		
		return api.getHeaderParameters();
	}
	
	private static byte[] formDataStream(final IService service) {
		
		ServiceDescriptor serviceDescriptor = service.getServiceDescriptor();
		
		String apiName = service.getApi();
		API api = serviceDescriptor.getApi(apiName);

		if(api.getDataStream() == null) {
			return new byte[0];
		}
		
		
		return api.getDataStream().getBytes();
	}
}
