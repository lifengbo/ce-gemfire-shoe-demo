/**
 * 
 */
package io.pivotal.ce.gemfire.fastfootshoes.serverside.listeners;

import io.pivotal.ce.gemfire.fastfootshoes.model.Alert;
import io.pivotal.ce.gemfire.fastfootshoes.serverside.ReferenceHelper;

import java.util.List;

import com.gemstone.gemfire.cache.asyncqueue.AsyncEvent;
import com.gemstone.gemfire.cache.asyncqueue.AsyncEventListener;
import com.gemstone.gemfire.pdx.internal.PdxInstanceImpl;

/**
 * @author lshannon
 *
 */
public class AsyncAlertWriter implements AsyncEventListener {
	
	private AlertsDAO alertsDAO;

	public void close() {
	}

	@SuppressWarnings("rawtypes")
	public boolean processEvents(List<AsyncEvent> events) {
		for (AsyncEvent asyncEvent : events) {
			Alert alert = null;
			if (asyncEvent.getDeserializedValue() instanceof PdxInstanceImpl) {
				alert = ReferenceHelper.toObject(asyncEvent.getDeserializedValue(), Alert.class);
			}
			else {
				System.out.println(asyncEvent.getDeserializedValue().getClass().getName());
				alert = (Alert) asyncEvent.getDeserializedValue();
			}
			if (asyncEvent.getOperation().isCreate()) {
				alertsDAO.insert(alert);
			}
		}
		return true;
	}

	public AlertsDAO getAlertsDAO() {
		return alertsDAO;
	}

	public void setAlertsDAO(AlertsDAO alertsDAO) {
		this.alertsDAO = alertsDAO;
	}
	
	

}
