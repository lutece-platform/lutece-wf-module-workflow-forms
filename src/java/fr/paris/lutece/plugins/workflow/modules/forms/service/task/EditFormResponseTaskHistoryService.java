package fr.paris.lutece.plugins.workflow.modules.forms.service.task;

import java.util.List;

import javax.inject.Inject;

import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.workflow.modules.forms.business.EditFormResponseTaskHistory;
import fr.paris.lutece.plugins.workflow.modules.forms.business.IEditFormResponseTaskHistoryDAO;

public class EditFormResponseTaskHistoryService implements IEditFormResponseTaskHistoryService
{

    private final IEditFormResponseTaskHistoryDAO _editFormResponseTaskHistoryDAO;

    /**
     * Constructor
     * 
     * @param IEditFormResponseTaskHistoryDAO
     *            the edit form response task history dao
     */
    @Inject
    public EditFormResponseTaskHistoryService( IEditFormResponseTaskHistoryDAO editFormResponseTaskHistoryDAO )
    {
        _editFormResponseTaskHistoryDAO = editFormResponseTaskHistoryDAO;
    }

    @Override
    public void create( EditFormResponseTaskHistory editFormResponseTaskHistory )
    {
        _editFormResponseTaskHistoryDAO.insert( editFormResponseTaskHistory );
    }

    @Override
    public List<EditFormResponseTaskHistory> load( int nIdHistory, int nIdTask )
    {
        List<EditFormResponseTaskHistory> listEditFormResponseTaskHistory = _editFormResponseTaskHistoryDAO.selectEditFormResponseHistoryByIdHistoryAndIdTask(
                nIdHistory, nIdTask );
        listEditFormResponseTaskHistory.forEach( history -> {
            Question question = QuestionHome.findByPrimaryKey( history.getQuestion( ).getId( ) );
            question.setIterationNumber( history.getQuestion( ).getIterationNumber( ) );
            history.setQuestion( question );
        } );
        return listEditFormResponseTaskHistory;
    }

}
