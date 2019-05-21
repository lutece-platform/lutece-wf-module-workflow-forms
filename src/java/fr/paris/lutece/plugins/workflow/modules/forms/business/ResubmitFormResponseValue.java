package fr.paris.lutece.plugins.workflow.modules.forms.business;

public class ResubmitFormResponseValue {

	private int _nIdHistory;
    private int _nIdEntry;

    /**
     * Get the id history
     * 
     * @return the id history
     */
    public int getIdHistory( )
    {
        return _nIdHistory;
    }

    /**
     * Set the id history
     * 
     * @param nIdHistory
     *            the id history
     */
    public void setIdHistory( int nIdHistory )
    {
        _nIdHistory = nIdHistory;
    }

    /**
     * Get the id entry
     * 
     * @return the id entry
     */
    public int getIdEntry( )
    {
        return _nIdEntry;
    }

    /**
     * Set the id entry
     * 
     * @param nIdEntry
     *            the id entry
     */
    public void setIdEntry( int nIdEntry )
    {
        _nIdEntry = nIdEntry;
    }
}
