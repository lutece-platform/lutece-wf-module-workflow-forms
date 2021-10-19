package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflow.modules.forms.business.FormResponseValueStateControllerConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.FormResponseValueStateControllerConfigHome;
import fr.paris.lutece.plugins.workflow.modules.state.service.IChooseStateController;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.util.ReferenceList;

public abstract class AbstractFormResponseStateController implements IChooseStateController
{

    @Override
    public boolean hasConfig( )
    {
        return true;
    }
    
    @Override
    public void doRemoveConfig( ITask task )
    {
        FormResponseValueStateControllerConfigHome.removeByTask( task.getId( ) );
    }
    
    protected FormResponseValueStateControllerConfig loadConfig( int idTask )
    {
        FormResponseValueStateControllerConfig controllerConfig = FormResponseValueStateControllerConfigHome.findByTask( idTask );
        if ( controllerConfig == null )
        {
            controllerConfig = new FormResponseValueStateControllerConfig( );
            controllerConfig.setIdTask( idTask );
            FormResponseValueStateControllerConfigHome.create( controllerConfig );
        }
        return controllerConfig;
    }
    
    protected ReferenceList getQuestionReferenceList( int idStep )
    {
        ReferenceList refList = new ReferenceList( );
        refList.addItem( -1, "" );
        if ( idStep != -1 )
        {
            List<Question> questionList = QuestionHome.getQuestionsListByStep( idStep );
            for ( Question question : questionList )
            {
                if ( canQuestionBeCondition( question ) )
                {
                    refList.addItem( question.getId( ), question.getTitle( ) );
                }
            }
        }

        return refList;
    }
    
    protected abstract boolean canQuestionBeCondition( Question question );
    
    protected Response getResponseFromQuestionAndFormResponse( int IdQuestion, int idResponse )
    {
        List<FormQuestionResponse> responseList = FormQuestionResponseHome.findFormQuestionResponseByResponseQuestion( idResponse,
                IdQuestion );

        if ( CollectionUtils.isEmpty( responseList ) )
        {
            return null;
        }
        List<Response> entryResponseList = responseList.get( 0 ).getEntryResponse( );
        if ( CollectionUtils.isEmpty( entryResponseList ) )
        {
            return null;
        }
        return entryResponseList.get( 0 );
    }
}
