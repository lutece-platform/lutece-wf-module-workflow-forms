package fr.paris.lutece.plugins.workflow.modules.forms.business;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Named;

@ApplicationScoped
public class EditFormResponseConfigProducer 
{
	@Produces
    @Dependent
    @Named( "workflow-forms.editFormResponseConfig" )
    public EditFormResponseConfig produceEditFormResponseConfig( )
    {
        return new EditFormResponseConfig( );
    }

	@Produces
    @Dependent
    @Named( "workflow-forms.editFormResponseAutoUpdateConfig" )
    public EditFormResponseConfig produceEditFormResponseAutoUpdateConfig( )
    {
        return new EditFormResponseConfig( );
    }
}
