package fr.paris.lutece.plugins.workflow.modules.forms.business;

import fr.paris.lutece.plugins.forms.business.Question;

public class EditFormResponseTaskHistory
{

    private int _nIdHistory;
    private Question _question;
    private int _nIdTask;
    private String _strPreviousValue;
    private String _strNewValue;

    /**
     * @return the idHistory
     */
    public int getIdHistory( )
    {
        return _nIdHistory;
    }

    /**
     * @param idHistory
     *            the idHistory to set
     */
    public void setIdHistory( int idHistory )
    {
        _nIdHistory = idHistory;
    }

    /**
     * @return the _question
     */
    public Question getQuestion( )
    {
        return _question;
    }

    /**
     * @param question
     *            the _question to set
     */
    public void setQuestion( Question question )
    {
        _question = question;
    }

    /**
     * @return the idTask
     */
    public int getIdTask( )
    {
        return _nIdTask;
    }

    /**
     * @param idTask
     *            the idTask to set
     */
    public void setIdTask( int idTask )
    {
        _nIdTask = idTask;
    }

    /**
     * @return the previousValue
     */
    public String getPreviousValue( )
    {
        return _strPreviousValue;
    }

    /**
     * @param previousValue
     *            the previousValue to set
     */
    public void setPreviousValue( String previousValue )
    {
        _strPreviousValue = previousValue;
    }

    /**
     * @return the newValue
     */
    public String getNewValue( )
    {
        return _strNewValue;
    }

    /**
     * @param newValue
     *            the newValue to set
     */
    public void setNewValue( String newValue )
    {
        _strNewValue = newValue;
    }

}
