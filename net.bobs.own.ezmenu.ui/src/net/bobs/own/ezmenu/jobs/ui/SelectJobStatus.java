package net.bobs.own.ezmenu.jobs.ui;

import java.util.List;

import org.eclipse.core.runtime.Status;

import net.bobs.own.db.rundml.mapper.ITable;

  class SelectJobStatus extends Status {
	 
	 private List<ITable> results = null;
	 
	 public SelectJobStatus (int severity,String pluginId,String message, List<ITable> results) {
		 super(severity,pluginId,message);
		 this.results = results;
	 }
	 
	 public SelectJobStatus(int severity,String id,String message, Throwable exception) {
		 super(severity,id,message,exception);
	 }
	 
	 public List<ITable> getSelectResults() {
		 return this.results;
	 }

}
