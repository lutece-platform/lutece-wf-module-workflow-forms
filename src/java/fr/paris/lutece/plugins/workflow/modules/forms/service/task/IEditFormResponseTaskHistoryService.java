package fr.paris.lutece.plugins.workflow.modules.forms.service.task;

import java.util.List;

import fr.paris.lutece.plugins.workflow.modules.forms.business.EditFormResponseTaskHistory;

public interface IEditFormResponseTaskHistoryService
{

    /**
     * Creation of an instance of editFormResponseTaskHistory value
     * 
     * @param editFormResponseTaskHistory
     *            The instance of editFormResponseTaskHistory value which contains the informations to store
     */
    void create( EditFormResponseTaskHistory editFormResponseTaskHistory );

    /**
     * Return a list of EditFormResponseTaskHistory with an idHistory and an idTask
     * 
     * @param idHistory
     * @param idTask
     * @return a list of EditFormResponseTaskHistory
     */
    List<EditFormResponseTaskHistory> load( int nIdHistory, int nIdTask );
}
