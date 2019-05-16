package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponse;
import fr.paris.lutece.util.ReferenceList;

/**
 * Service for ResubmitFormResponseTask
 */
public interface IResubmitFormResponseService {

	 /**
     * Get the list of states
     * 
     * @param nIdAction
     *            the id action
     * @return a ReferenceList
     */
    ReferenceList getListStates( int nIdAction );
    
    /**
    * Find an ResubmitFormResponse
    * 
    * @param nIdHistory
    *            the id history
    * @param nIdTask
    *            the id task
    * @return a ResubmitFormResponse
    */
   ResubmitFormResponse find( int nIdHistory, int nIdTask );
   
   /**
    * Get the list of entries for information
    * 
    * @param nIdHistory
    *            the id edit record
    * @return a list of entries
    */
   List<Entry> getInformationListEntries( int nIdHistory );
   
   /**
    * Get the list of entries for the form
    * 
    * @param nIdRecord
    *            the id record
    * @param request
    *            the HTTP request
    * @return a list of entries
    */
   List<Entry> getFormListEntries( int nIdRecord, String strResourceType );
   
   /**
    * Find the list of question which can be shown in completensess.
    * @param formResponse
    * @return
    */
   List<Question> findListQuestionShownCompleteness( FormResponse formResponse );
}
