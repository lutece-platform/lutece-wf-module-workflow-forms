package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponseHome;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.QuestionHome;
import fr.paris.lutece.plugins.forms.business.StepHome;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeCheckBox;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeRadioButton;
import fr.paris.lutece.plugins.forms.service.entrytype.EntryTypeSelect;
import fr.paris.lutece.plugins.genericattributes.business.Field;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.EntryTypeServiceManager;
import fr.paris.lutece.plugins.genericattributes.service.entrytype.IEntryTypeService;
import fr.paris.lutece.plugins.workflow.modules.forms.business.FormResponseValueStateControllerConfig;
import fr.paris.lutece.plugins.workflow.modules.forms.business.FormResponseValueStateControllerConfigHome;
import fr.paris.lutece.plugins.workflow.modules.state.service.IChooseStateController;
import fr.paris.lutece.plugins.workflowcore.business.config.ITaskConfig;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

public class FormResponseValueStateController implements IChooseStateController
{
    private static final String BEAN_NAME = "workflow-forms.formResponseValueStateController";
    
    private static final String PROPERTY_KEY_LABEL = "module.workflow.forms.state.control.forms.response";
    private static final String PROPERTY_KEY_HELP = "module.workflow.forms.state.control.forms.response.help";
    
    // Mark
    private static final String MARK_FORM_LIST = "form_list";
    private static final String MARK_ID_FORM = "id_form";
    private static final String MARK_STEP_LIST = "list_step";
    private static final String MARK_ID_STEP = "id_step";
    private static final String MARK_QUESTION_LIST = "question_list";
    private static final String MARK_ID_QUESTION = "id_question";
    private static final String MARK_VALUE_LIST = "value_list";
    private static final String MARK_RESPONSE_VALUE = "response_value";
    
    // Parameters
    private static final String PARAMETER_ACTION = "apply";
    private static final String PARAMETER_FORM = "form_select";
    private static final String PARAMETER_STEP = "step_select";
    private static final String PARAMETER_QUESTION = "question_select";
    private static final String PARAMETER_VALUE = "response_select";
    
    // Actions
    private static final String ACTION_SELECT_FORM = "select_form_config";
    private static final String ACTION_SELECT_STEP = "select_step_config";
    private static final String ACTION_SELECT_QUESTION = "select_question_config";
    
    private static final String TEMPLATE_TASK_CONFIG = "admin/plugins/workflow/modules/forms/state_control_form_response_value.html";
    
    @Override
    public String getLabelKey( )
    {
        return PROPERTY_KEY_LABEL;
    }

    @Override
    public String getHelpKey( )
    {
        return PROPERTY_KEY_HELP;
    }

    @Override
    public String getName( )
    {
        return BEAN_NAME;
    }
    
    @Override
    public boolean control( ITask task, int nIdResource, String strResourceType )
    {
        FormResponseValueStateControllerConfig config = loadConfig( task.getId( ) );
        if ( StringUtils.isEmpty( config.getValue( ) ) )
        {
            return false;
        }
        List<FormQuestionResponse> responseList = FormQuestionResponseHome.findFormQuestionResponseByResponseQuestion( nIdResource,
                config.getQuestion( ).getId( ) );
        
        if ( CollectionUtils.isEmpty( responseList ) )
        {
            return false;
        }
        List<Response> entryResponseList = responseList.get( 0 ).getEntryResponse( );
        if ( CollectionUtils.isEmpty( entryResponseList ) )
        {
            return false;
        }
        Response response = entryResponseList.get( 0 );
        if ( response == null )
        {
            return false;
        }
        return config.getValue( ).equals( response.getResponseValue( ) );
    }
    
    @Override
    public boolean hasConfig( )
    {
        return true;
    }
    
    @Override
    public void doSaveConfig( HttpServletRequest request, Locale locale, ITask task )
    {
        FormResponseValueStateControllerConfig controllerConfig = loadConfig( task.getId( ) );
        String action = request.getParameter( PARAMETER_ACTION );
        if ( action != null )
        {
            switch( action )
            {
                case ACTION_SELECT_FORM:
                    controllerConfig.setForm( FormHome.findByPrimaryKey( Integer.valueOf( request.getParameter( PARAMETER_FORM ) ) ) );
                    controllerConfig.setStep( null );
                    controllerConfig.setQuestion( null );
                    break;
                case ACTION_SELECT_STEP:
                    controllerConfig.setStep( StepHome.findByPrimaryKey( Integer.parseInt( request.getParameter( PARAMETER_STEP ) ) ) );
                    controllerConfig.setQuestion( null );
                    break;
                case ACTION_SELECT_QUESTION:
                    controllerConfig.setQuestion( QuestionHome.findByPrimaryKey( Integer.parseInt( request.getParameter( PARAMETER_QUESTION ) ) ) );
                    break;
                default:
                    break;
            }
        }
        controllerConfig.setValue( request.getParameter( PARAMETER_VALUE ) );
        FormResponseValueStateControllerConfigHome.update( controllerConfig );
    }
    
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITaskConfig config )
    {
        FormResponseValueStateControllerConfig controllerConfig = loadConfig( config.getIdTask( ) );

        Map<String, Object> model = new HashMap<>( );
        model.put( MARK_FORM_LIST, FormHome.getFormsReferenceList( ) );

        if ( controllerConfig.getForm( ) != null )
        {
            model.put( MARK_ID_FORM, controllerConfig.getForm( ).getId( ) );
            model.put( MARK_STEP_LIST, StepHome.getStepReferenceListByForm( controllerConfig.getForm( ).getId( ) ) );
        }
        if ( controllerConfig.getStep( ) != null )
        {
            model.put( MARK_ID_STEP, controllerConfig.getStep( ).getId( ) );
            model.put( MARK_QUESTION_LIST, getQuestionReferenceList( controllerConfig.getStep( ).getId( ) ) );
        }
        if( controllerConfig.getQuestion( ) != null )
        {
            model.put( MARK_ID_QUESTION, controllerConfig.getQuestion( ).getId( ) );
            model.put( MARK_VALUE_LIST, getResponseReferenceList( controllerConfig.getQuestion( ).getId( ) ) );
        }
        if ( StringUtils.isNotEmpty( controllerConfig.getValue( ) ) )
        {
            model.put( MARK_RESPONSE_VALUE, controllerConfig.getValue( ) );
        }
        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_CONFIG, locale, model );
        return template.getHtml( );
    }
    
    private FormResponseValueStateControllerConfig loadConfig( int idTask )
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
   
    private ReferenceList getQuestionReferenceList( int idStep )
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
    
    private boolean canQuestionBeCondition( Question question )
    {
        IEntryTypeService entryTypeService = EntryTypeServiceManager.getEntryTypeService( question.getEntry( ) );

        return entryTypeService instanceof EntryTypeSelect || entryTypeService instanceof EntryTypeCheckBox || entryTypeService instanceof EntryTypeRadioButton;
    }
    
    private ReferenceList getResponseReferenceList( int idQuestion )
    {
        ReferenceList refList = new ReferenceList( );
        refList.addItem( "", "" );
        if ( idQuestion != -1 )
        {
            Question question = QuestionHome.findByPrimaryKey( idQuestion );
            for ( Field field : question.getEntry( ).getFields( ) )
            {
                if ( IEntryTypeService.FIELD_ANSWER_CHOICE.equals( field.getCode( ) ) )
                {
                    refList.addItem( field.getValue( ), field.getTitle( ) );
                }
            }
        }
        return refList;
    }
}
