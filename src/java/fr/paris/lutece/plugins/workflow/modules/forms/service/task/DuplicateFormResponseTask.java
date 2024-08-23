package fr.paris.lutece.plugins.workflow.modules.forms.service.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormHome;
import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.FormResponseStep;
import fr.paris.lutece.plugins.forms.service.FormService;
import fr.paris.lutece.plugins.genericattributes.business.Response;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.resource.ResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.business.file.File;
import fr.paris.lutece.portal.business.physicalfile.PhysicalFile;
import fr.paris.lutece.portal.service.file.FileService;
import fr.paris.lutece.portal.service.file.FileServiceException;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.util.AppLogService;

public class DuplicateFormResponseTask extends SimpleTask
{
	@Inject 
    @Named( "workflow-forms.taskDuplicateFormResponseConfigService" ) 
    private ITaskConfigService _config;
	
	@Inject
    @Named( ResourceHistoryService.BEAN_SERVICE )
    private IResourceHistoryService _resourceHistoryService;
	
	@Inject
    private FormService _formService;
	
	private final IFormsTaskService _formsTaskService;
	
	@Inject
    public DuplicateFormResponseTask( IFormsTaskService formsTaskService )
    {
        _formsTaskService = formsTaskService;
    }
	
	@Override 
	public String getTitle( Locale locale ) 
	{ 
		return I18nService.getLocalizedString( getTaskType( ).getTitleI18nKey( ), locale );
	}
	
	@Override 
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
		ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
		if ( resourceHistory != null )
        {
			FormResponse formResponse = _formsTaskService.findFormResponseFrom( resourceHistory );
			if ( formResponse != null )
			{
				FormResponse formResponseDuplicated = duplicateFormResponse( formResponse );
				_formService.saveFormResponse( formResponseDuplicated );
				_formService.processFormAction( FormHome.findByPrimaryKey( formResponseDuplicated.getFormId( ) ), formResponseDuplicated );
			}
        }
    }
	
	private FormResponse duplicateFormResponse( FormResponse formResponse )
	{
		FormResponse formResponseDuplicate = new FormResponse( );
		
		formResponseDuplicate.setAdmin( formResponse.getAdmin( ) );
		formResponseDuplicate.setDateCreation( formResponse.getCreation( ) );
		formResponseDuplicate.setFormId( formResponse.getFormId( ) );
		formResponseDuplicate.setFromSave( formResponse.isFromSave( ) );
		formResponseDuplicate.setGuid( formResponse.getGuid( ) );
		formResponseDuplicate.setPublished( formResponse.isPublished( ) );
		formResponseDuplicate.setRole( formResponse.getRole( ) );
		formResponseDuplicate.setUpdate( formResponse.getUpdate( ) );
		formResponseDuplicate.setUpdateStatus( formResponse.getUpdateStatus( ) );
		List<FormResponseStep> listStepsDuplicate = new ArrayList<>( );
		
		for ( FormResponseStep formResponseStep : formResponse.getSteps( ) )
		{
			listStepsDuplicate.add( duplicateFormResponseStep( formResponseStep ) );
		}
		formResponseDuplicate.setSteps( listStepsDuplicate );
		
		return formResponseDuplicate;
	}
	
	private FormResponseStep duplicateFormResponseStep( FormResponseStep formResponseStep )
	{
		FormResponseStep formResponseStepDuplicate = new FormResponseStep( );
		formResponseStepDuplicate.setOrder( formResponseStep.getOrder( ) );
		formResponseStepDuplicate.setStep( formResponseStep.getStep( ) );
		List<FormQuestionResponse> listFormQuestionResponseDuplicate = new ArrayList<>( );
		
		for ( FormQuestionResponse questionResponse : formResponseStep.getQuestions( ) )
		{
			listFormQuestionResponseDuplicate.add( duplicateFormQuestionResponse( questionResponse ) );
		}
		
		formResponseStepDuplicate.setQuestions( listFormQuestionResponseDuplicate );

		return formResponseStepDuplicate;
	}
	
	private FormQuestionResponse duplicateFormQuestionResponse( FormQuestionResponse formQuestionResponse )
	{
		FormQuestionResponse questionDuplicate = new FormQuestionResponse( );
		questionDuplicate.setQuestion( formQuestionResponse.getQuestion( ) );
		questionDuplicate.getQuestion().setIsVisible( true );
		questionDuplicate.setIdStep( formQuestionResponse.getIdStep( ) );
		List<Response> listResponseDuplicate = new ArrayList<>( );
		
		for ( Response response : formQuestionResponse.getEntryResponse( ) )
		{
			Response duplicateResponse = duplicateResponse( response );
			
			if ( duplicateResponse != null )
			{
				listResponseDuplicate.add( duplicateResponse );
			}
		}
		
		questionDuplicate.setEntryResponse( listResponseDuplicate );
		
		return questionDuplicate;
	}
	
	private Response duplicateResponse( Response response )
	{
		Response responseDuplicate = new Response( );
		
		responseDuplicate.setEntry( response.getEntry( ) );
		if ( response.getFile( ) != null )
		{
			File duplicateFile = duplicateFile( response.getFile( ).getFileKey( ) );
			
			if ( duplicateFile == null )
			{
				return null;
			}
			
			responseDuplicate.setFile( duplicateFile );
		}
		responseDuplicate.setField( response.getField( ) );
		responseDuplicate.setIsImage( response.getIsImage( ) );
		responseDuplicate.setIterationNumber( response.getIterationNumber( ) );
		responseDuplicate.setResponseValue( response.getResponseValue( ) );
		responseDuplicate.setSortOrder( response.getSortOrder( ) );
		responseDuplicate.setStatus( response.getStatus( ) );
		responseDuplicate.setToStringValueResponse( response.getToStringValueResponse( ) );
		
		return responseDuplicate;
	}

	private File duplicateFile( String strFileKey )
	{
		File file;
		try {
			file = FileService.getInstance( ).getFileStoreServiceProvider( ).getFile( strFileKey );
		} catch (FileServiceException e) {
			AppLogService.error(e);
			return null;
		}
		
		if ( file == null )
		{
			return null;
		}
		
		File fileDuplicate = new File( );
		
		PhysicalFile physicalFileDuplicate = new PhysicalFile( );
		if ( file.getPhysicalFile( ) != null )
		{
			physicalFileDuplicate.setValue( file.getPhysicalFile( ).getValue( ) );
		}
		
		fileDuplicate.setDateCreation( file.getDateCreation( ) );
		fileDuplicate.setExtension( file.getExtension( ) );
		fileDuplicate.setFileKey( file.getFileKey( ) );
		fileDuplicate.setMimeType( file.getMimeType( ) );
		fileDuplicate.setOrigin( file.getOrigin( ) );
		fileDuplicate.setPhysicalFile( physicalFileDuplicate );
		fileDuplicate.setSize( file.getSize( ) );
		fileDuplicate.setTitle( file.getTitle( ) );
		fileDuplicate.setUrl( file.getUrl( ) );
		
		return fileDuplicate;
	}
}