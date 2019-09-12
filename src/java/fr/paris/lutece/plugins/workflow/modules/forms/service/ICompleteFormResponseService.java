package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponseValue;
import fr.paris.lutece.portal.service.message.SiteMessageException;

public interface ICompleteFormResponseService {

	/**
	 * Find the list of question without response.
	 * 
	 * @param formResponse
	 * @return
	 */
	List<Question> findListQuestionUsedCorrectForm( FormResponse formResponse );

	/**
	 * Find an CompleteFormResponse
	 * 
	 * @param nIdHistory the id history
	 * @param nIdTask    the id task
	 * @return a CompleteFormResponse
	 */
	CompleteFormResponse find( int nIdHistory, int nIdTask );

	/**
	 * Get the list of entries for information
	 * 
	 * @param nIdHistory the id edit record
	 * @return a list of entries
	 */
	List<Entry> getInformationListEntries( int nIdHistory );
	
	/**
	 * Create a CompleteFormResponse
	 * 
	 * @param completeFormResponse
	 */
	void create( CompleteFormResponse completeFormResponse );

	/**
	 * Update a CompleteFormResponse
	 * 
	 * @param completeFormResponse
	 */
	void update( CompleteFormResponse completeFormResponse );
	
	/**
	 * Remove a CompleteFormResponse
	 * 
	 * @param nIdHistory the id history
	 * @param nIdTask    the id task
	 */
	void removeByIdHistory( int nIdHistory, int nIdTask );

	/**
	 * Remove a CompleteFormResponse by id task
	 * 
	 * @param nIdTask the id task
	 */
	void removeByIdTask( int nIdTask );
	
	/**
	 * Check if the response has the same state before executing the action
	 * 
	 * @param completeFormResponse the edit record
	 * @param locale               the locale
	 * @return true if the record has a valid state, false otherwise
	 */
	boolean isRecordStateValid( CompleteFormResponse completeFormResponse, Locale locale );
	
	/**
	 * Get the list of questions to edit
	 * 
	 * @param listEditRecordValues the list of edit record values
	 * @return a list of entries
	 */
	List<Question> getListQuestionToEdit( FormResponse formResponse, List<CompleteFormResponseValue> listEditRecordValues );
	
	/**
	 * Do edit the response
	 * 
	 * @param request              the HTTP request
	 * @param completeFormResponse the response
	 * @return true if the user the record must be updated, false otherwise
	 * @throws SiteMessageException site message if there is a problem
	 */
	boolean doEditResponseData( HttpServletRequest request, CompleteFormResponse completeFormResponse ) throws SiteMessageException;

	/**
	 * Do change the Response
	 * 
	 * @param completeFormResponse the response
	 * @param locale               the locale
	 */
	void doChangeResponseState( CompleteFormResponse completeFormResponse, Locale locale );
	
	/**
	 * Do change the Response to complete
	 * 
	 * @param completeFormResponse the Response
	 */
	void doCompleteResponse( CompleteFormResponse completeFormResponse );
}
