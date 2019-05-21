package fr.paris.lutece.plugins.workflow.modules.forms.utils;

import fr.paris.lutece.plugins.forms.business.FormQuestionResponse;
import fr.paris.lutece.plugins.forms.business.Question;

public class EditableResponse {

	private final Question _question;
    private final FormQuestionResponse _responseSaved;
    private final FormQuestionResponse _responseFromForm;

    /**
     * Constructor
     * 
     * @param responseSaved
     *            the saved response
     * @param responseFromForm
     *            the response from the form
     */
    public EditableResponse( FormQuestionResponse responseSaved, FormQuestionResponse responseFromForm )
    {
        _responseSaved = responseSaved;
        _responseFromForm = responseFromForm;

        if ( responseSaved != null )
        {
            responseFromForm.setId( responseSaved.getId( ) );
        }

        _question = _responseFromForm.getQuestion( );
    }

	/**
	 * @return the _question
	 */
	public Question getQuestion() {
		return _question;
	}

	/**
	 * @return the _responseSaved
	 */
	public FormQuestionResponse getResponseSaved() {
		return _responseSaved;
	}

	/**
	 * @return the _responseFromForm
	 */
	public FormQuestionResponse getResponseFromForm() {
		return _responseFromForm;
	}
}
