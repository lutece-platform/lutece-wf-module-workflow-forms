package fr.paris.lutece.plugins.workflow.modules.forms.service;

import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

import fr.paris.lutece.plugins.forms.business.FormResponse;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.genericattributes.business.Entry;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponse;
import fr.paris.lutece.plugins.workflow.modules.forms.business.ResubmitFormResponseValue;
import fr.paris.lutece.portal.service.message.SiteMessageException;

/**
 * Service for ResubmitFormResponseTask
 */
public interface IResubmitFormResponseService {

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
   
   /** 
    * Remove a ResubmitFormResponse
    * @param nIdHistory
    *            the id history
    * @param nIdTask
    *            the id task
    */
   void removeByIdHistory( int nIdHistory, int nIdTask );
   
   /**
    * Remove a ResubmitFormResponse by id task
    * 
    * @param nIdTask
    *            the id task
    */
   void removeByIdTask( int nIdTask );
   
   /**
    * Create a ResubmitFormResponse
    * 
    * @param resubmitFormResponse
    */
   void create( ResubmitFormResponse resubmitFormResponse );

   /**
    * Update a ResubmitFormResponse
    * 
    * @param resubmitFormResponse
    */
   void update( ResubmitFormResponse resubmitFormResponse );
   
   /**
    * Check if the response has the same state before executing the action
    * 
    * @param resubmitFormResponse
    *            the edit record
    * @param locale
    *            the locale
    * @return true if the record has a valid state, false otherwise
    */
   boolean isRecordStateValid( ResubmitFormResponse resubmitFormResponse, Locale locale );
   
   /**
    * Get the list of questions to edit
    * 
    * @param listEditRecordValues
    *            the list of edit record values
    * @return a list of entries
    */
   List<Question> getListQuestionToEdit( FormResponse formResponse, List<ResubmitFormResponseValue> listEditRecordValues );
   
   /**
    * Do edit the response
    * 
    * @param request
    *            the HTTP request
    * @param resubmitFormResponse
    *            the response
    * @return true if the user the record must be updated, false otherwise
    * @throws SiteMessageException
    *             site message if there is a problem
    */
   boolean doEditResponseData( HttpServletRequest request, ResubmitFormResponse resubmitFormResponse ) throws SiteMessageException;
   
   /**
    * Do change the Response
    * 
    * @param resubmitFormResponse
    *            the response
    * @param locale
    *            the locale
    */
   void doChangeResponseState( ResubmitFormResponse resubmitFormResponse, Locale locale );
   
   /**
    * Do change the Response to complete
    * 
    * @param resubmitFormResponse
    *            the Response
    */
   void doCompleteResponse( ResubmitFormResponse resubmitFormResponse );
}
