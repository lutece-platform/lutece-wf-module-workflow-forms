package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.workflow.modules.forms.business.CompleteFormResponse;

public interface ICompleteFormResponseService {

	/**
	 * Find the list of question without response.
	 * 
	 * @param formResponse
	 * @return
	 */
	List<Question> findListQuestionWithoutResponse( FormResponse formResponse );

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
}
