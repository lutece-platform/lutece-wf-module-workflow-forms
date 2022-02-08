package fr.paris.lutece.plugins.workflow.modules.forms.business;

import fr.paris.lutece.plugins.workflowcore.business.config.TaskConfig;

public class ModifyFormResponseUpdateStatusTaskConfig extends TaskConfig 
{
	private boolean _bPublished = false;

	public boolean isPublished() {
		return _bPublished;
	}

	public void setPublished(boolean _bPublished) {
		this._bPublished = _bPublished;
	}
}
