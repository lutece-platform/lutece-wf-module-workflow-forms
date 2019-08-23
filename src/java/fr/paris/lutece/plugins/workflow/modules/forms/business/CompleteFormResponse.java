package fr.paris.lutece.plugins.workflow.modules.forms.business;

import java.util.List;

public class CompleteFormResponse {

	private int _nIdHistory;
    private int _nIdTask;
    private String _strMessage;
    private boolean _bIsComplete;
    private List<CompleteFormResponseValue> _listCompleteReponseValues;
    
    /**
     * Set the id edit record
     * 
     * @param nIdHistory
     *            the id edit record
     */
    public void setIdHistory( int nIdHistory )
    {
        _nIdHistory = nIdHistory;
    }

    /**
     * Get the id edit record
     * 
     * @return the id edit record
     */
    public int getIdHistory( )
    {
        return _nIdHistory;
    }

    /**
     * Get the id task
     * 
     * @return the task id
     */
    public int getIdTask( )
    {
        return _nIdTask;
    }

    /**
     * Set the task id
     * 
     * @param nIdTask
     *            the task id
     */
    public void setIdTask( int nIdTask )
    {
        _nIdTask = nIdTask;
    }

    /**
     * Set the message
     * 
     * @param strMessage
     *            the message
     */
    public void setMessage( String strMessage )
    {
        _strMessage = strMessage;
    }

    /**
     * Get the message
     * 
     * @return the message
     */
    public String getMessage( )
    {
        return _strMessage;
    }

    /**
     * Set the list of CompleteReponse values
     * 
     * @param listCompleteReponseValues
     *            of CompleteReponse values
     */
    public void setListCompleteReponseValues( List<CompleteFormResponseValue> listCompleteReponseValues )
    {
        _listCompleteReponseValues = listCompleteReponseValues;
    }

    /**
     * Get the list of CompleteReponse
     * 
     * @return the list of CompleteReponse values
     */
    public List<CompleteFormResponseValue> getListCompleteReponseValues( )
    {
        return _listCompleteReponseValues;
    }

    /**
     * Set is complete
     * 
     * @param bIsComplete
     *            true if it is complete, false otherwise
     */
    public void setIsComplete( boolean bIsComplete )
    {
        _bIsComplete = bIsComplete;
    }

    /**
     * Check if the record is complete
     * 
     * @return true if it is complete, false otherwise
     */
    public boolean isComplete( )
    {
        return _bIsComplete;
    }
}
