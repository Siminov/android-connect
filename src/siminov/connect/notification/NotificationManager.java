package siminov.connect.notification;

import siminov.connect.design.notification.IMessage;
import siminov.connect.design.notification.INotification;
import siminov.connect.design.notification.IRegistration;
import siminov.connect.events.INotificationEvents;
import siminov.connect.exception.NotificationException;
import siminov.connect.model.ApplicationDescriptor;
import siminov.connect.model.NotificationDescriptor;
import siminov.connect.resource.Resources;
import siminov.orm.exception.SiminovCriticalException;
import siminov.orm.log.Log;
import android.content.Context;

import com.google.android.gcm.GCMRegistrar;

public class NotificationManager implements INotification {

	private static NotificationManager notificationManager = null;

	private siminov.orm.resource.Resources ormResources = siminov.orm.resource.Resources.getInstance();
	private Resources connectResources = Resources.getInstance();
	
	
	private NotificationManager() {

	}
	
	public static NotificationManager getInstance() {
		
		if(notificationManager == null) {
			notificationManager = new NotificationManager();
		}
		
		return notificationManager;
	}
	
	public void doRegistration() {

		ApplicationDescriptor applicationDescriptor = connectResources.getApplicationDescriptor();
		NotificationDescriptor notificationDescriptor = applicationDescriptor.getNotificationDescriptor();

		Context applicationContext = ormResources.getApplicationContext();
		
        GCMRegistrar.checkDevice(applicationContext);
        GCMRegistrar.checkManifest(applicationContext);
        
        String registrationId = GCMRegistrar.getRegistrationId(applicationContext);
        if (registrationId == null || registrationId.equals("")) {
        	
        	String senderId = notificationDescriptor.getProperty(SENDER_ID);
        	if(senderId == null || senderId.length() <= 0) {
        		Log.loge(NotificationManager.class.getName(), "doRegistration", "INVALID SENDER ID.");
        		throw new SiminovCriticalException(NotificationManager.class.getName(), "doRegistration", "INVALID SENDER ID.");
        	}
        	
        	
            GCMRegistrar.register(applicationContext, senderId);
        } else {

        	IRegistration registration = new Registration();
        	registration.setRegistrationId(GCMRegistrar.getRegistrationId(applicationContext));
        	
        	onRegistration(registration);
        }
	}

	public void onRegistration(IRegistration registration) {

		INotificationEvents notificationEventsHandler = connectResources.getNotificationEventHandler();
		if(notificationEventsHandler != null) {
			notificationEventsHandler.onRegistration(registration);
		}
	}

	public void doUnregistration() {
		onUnregistration(new Registration());
	}


	public void onUnregistration(IRegistration registration) {
		
		INotificationEvents notificationEventsHandler = connectResources.getNotificationEventHandler();
		if(notificationEventsHandler != null) {
			notificationEventsHandler.onUnregistration(registration);
		}
	}

	public void onNotification(IMessage message) {
		
		INotificationEvents notificationEventsHandler = connectResources.getNotificationEventHandler();
		if(notificationEventsHandler != null) {
			notificationEventsHandler.onNotification(message);
		}
	}
	
	public void onError(NotificationException notificationException) {
		
		INotificationEvents notificationEventsHandler = connectResources.getNotificationEventHandler();
		if(notificationEventsHandler != null) {
			notificationEventsHandler.onError(notificationException);
		}
	}
}
