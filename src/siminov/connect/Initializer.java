/** 
 * [SIMINOV FRAMEWORK]
 * Copyright [2013] [Siminov Software Solution LLP|support@siminov.com]
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

package siminov.connect;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import siminov.orm.IInitializer;
import siminov.orm.resource.Resources;
import android.content.Context;

public class Initializer implements IInitializer {

	private Resources ormResources = Resources.getInstance();
	
	private List<Object> parameters = new ArrayList<Object> ();
	
	public void addParameter(Object object) {
		parameters.add(object);
	}
	
	public void start() {
		
		Context context = null;
		
		Iterator<Object> iterator = parameters.iterator();
		while(iterator.hasNext()) {
			
			Object object = iterator.next();
			if(object instanceof Context) {
				context = (Context) object;
			}
			
		}
		
		ormResources.setApplicationContext(context);
		
		Siminov.start();
		
	}

}
