package com.soffid.iam.addons.passrecover.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.springframework.beans.BeansException;

import com.soffid.iam.ServiceLocator;

public class SignalGenerator {
	final String RISC_RECOVERY_CHANGED = "https://schemas.openid.net/secevent/risc/event-type/recovery-information-changed";
	final String RISC_RECOVERY_ACTIVATED = "https://schemas.openid.net/secevent/risc/event-type/recovery-activated";
	
	public void generateRecoveryActivationEvent(String name, String system) throws Exception {
		try {
			Object ssvc = ServiceLocator.instance().getService("signalService-v2");
			Method m = ssvc.getClass().getMethod("signalAccount", String.class, String.class, String.class, String[].class);
			m.invoke(ssvc, RISC_RECOVERY_ACTIVATED, name, system,
					new String[]{});
		} catch (BeansException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
			// Console version older than 3.5.10
		} catch (InvocationTargetException e) {
			if ( e.getCause() instanceof Exception)
				throw (Exception)e.getCause();
			else
				throw new RuntimeException(e);
		}
	}

	public void generateRecoveryChangeEvent(String name) throws Exception {
		try {
			Object ssvc = ServiceLocator.instance().getService("signalService-v2");
			Method m = ssvc.getClass().getMethod("signalUser", String.class, String.class, String[].class);
			m.invoke(ssvc, RISC_RECOVERY_CHANGED, name,
					new String[]{});
		} catch (BeansException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
			// Console version older than 3.5.10
		} catch (InvocationTargetException e) {
			if ( e.getCause() instanceof Exception)
				throw (Exception)e.getCause();
			else
				throw new RuntimeException(e);
		}
	}
}
