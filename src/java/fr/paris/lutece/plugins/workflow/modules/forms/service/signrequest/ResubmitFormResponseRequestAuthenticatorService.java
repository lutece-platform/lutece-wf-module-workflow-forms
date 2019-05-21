package fr.paris.lutece.plugins.workflow.modules.forms.service.signrequest;

import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.util.signrequest.AbstractPrivateKeyAuthenticator;

public final class ResubmitFormResponseRequestAuthenticatorService {

	private static final String BEAN_RESUBMIT_FORM_REQUEST_AUTHENTICATOR = "workflow-forms.resubmitFormResponseRequestAuthenticator";

    /**
     * Private constructor
     */
    private ResubmitFormResponseRequestAuthenticatorService( )
    {
    }

    /**
     * Get the instance of {@link AbstractPrivateKeyAuthenticator} defined in the context.xml
     * 
     * @return the instance of {@link AbstractPrivateKeyAuthenticator}
     */
    public static AbstractPrivateKeyAuthenticator getRequestAuthenticator( )
    {
        return (AbstractPrivateKeyAuthenticator) SpringContextService.getBean( BEAN_RESUBMIT_FORM_REQUEST_AUTHENTICATOR );
    }
}
