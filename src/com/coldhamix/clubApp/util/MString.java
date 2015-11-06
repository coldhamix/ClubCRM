package com.coldhamix.clubApp.util;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;

public class MString implements Comparable<MString> {
	
	private String delegate;
	
	private Object userData;
	
	private SimpleBooleanProperty on = new SimpleBooleanProperty();
	
	public MString(String delegate, boolean on) {
		this.delegate = delegate;
		this.on.set(on);
	}
	
	public void setUserData(Object o) {
		userData = o;
	}
	
	public BooleanProperty onProperty() {
		return on;
	}
	
	public Object getUserData() {
		return userData;
	}
	
	public String toString() { 
		return delegate;
	}

	public int compareTo(MString s) {
		if (on.get() == s.on.get()) {
			return delegate.compareTo(s.delegate);
		}
		
		if (!on.get() && s.on.get()) {
			return 1;
		} else {
			return -1;
		}	
	}

}
