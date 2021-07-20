package fr.paris.lutece.plugins.workflow.modules.forms.business;

import fr.paris.lutece.plugins.forms.business.Form;
import fr.paris.lutece.plugins.forms.business.Question;
import fr.paris.lutece.plugins.forms.business.Step;

public class FormResponseValueStateControllerConfig
{
    private int _id;
    private int _idTask;
    private Form _form;
    private Step _step;
    private Question _question;
    private String _value;
    
    /**
     * @return the id
     */
    public int getId( )
    {
        return _id;
    }
    /**
     * @param id the id to set
     */
    public void setId( int id )
    {
        _id = id;
    }
    /**
     * @return the idTask
     */
    public int getIdTask( )
    {
        return _idTask;
    }
    /**
     * @param idTask the idTask to set
     */
    public void setIdTask( int idTask )
    {
        _idTask = idTask;
    }
    /**
     * @return the form
     */
    public Form getForm( )
    {
        return _form;
    }
    /**
     * @param form the form to set
     */
    public void setForm( Form form )
    {
        _form = form;
    }
    /**
     * @return the step
     */
    public Step getStep( )
    {
        return _step;
    }
    /**
     * @param step the step to set
     */
    public void setStep( Step step )
    {
        _step = step;
    }
    /**
     * @return the question
     */
    public Question getQuestion( )
    {
        return _question;
    }
    /**
     * @param question the question to set
     */
    public void setQuestion( Question question )
    {
        _question = question;
    }
    /**
     * @return the value
     */
    public String getValue( )
    {
        return _value;
    }
    /**
     * @param value the value to set
     */
    public void setValue( String value )
    {
        _value = value;
    }
}
