package fr.paris.lutece.plugins.workflow.modules.forms.business;

import java.util.List;

public interface IEditFormResponseTaskHistoryDAO
{

    /**
     * insert an EditFormResponseHistory with a editFormResponseHistory
     * 
     * @param editFormResponseTaskHistory
     */
    public void insert( EditFormResponseTaskHistory editFormResponseTaskHistory );

    /**
     * select some EditFormResponseHistory with an idHistory and an idTask
     * 
     * @param idHistory
     * @param idTask
     * @return a list of EditFormResponseHistory
     */
    public List<EditFormResponseTaskHistory> selectEditFormResponseHistoryByIdHistoryAndIdTask( int nIdHistory, int nIdTask );
}
