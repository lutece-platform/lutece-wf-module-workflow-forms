/*
 * Copyright (c) 2002-2026, City of Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.forms.service.task;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ConditionalTargetDisplayDecisionTest
{
    private final FormsTaskService formsTaskService = new FormsTaskService();

    @Test
    public void testAndAllValidIsDisplayed( )
    {
        assertTrue( formsTaskService.isDisplayedFromControlOutcomes( false, 2, 0, false ), "AND with every control valid ⇒ displayed (kept)" );
    }

    @Test
    public void testAndOneNotValidIsHidden( )
    {
        // the conditional target became hidden again : its response must be removed
        assertFalse( formsTaskService.isDisplayedFromControlOutcomes( false, 1, 1, false ), "AND with one control not valid ⇒ hidden (removed)" );
    }

    @Test
    public void testAndUnevaluableIsKept( )
    {
        // KEY : nothing is positively "not met", one control is unevaluable ⇒ keep the response
        assertTrue( formsTaskService.isDisplayedFromControlOutcomes( false, 0, 0, true ), "AND with an unevaluable control (none invalid) ⇒ kept" );
    }

    @Test
    public void testAndValidPlusUnevaluableIsKept( )
    {
        assertTrue( formsTaskService.isDisplayedFromControlOutcomes( false, 1, 0, true ), "AND with a valid and an unevaluable control ⇒ kept" );
    }

    @Test
    public void testAndNotValidWinsOverUnevaluable( )
    {
        // a control that is positively "not met" still hides, even if another is unevaluable
        assertFalse( formsTaskService.isDisplayedFromControlOutcomes( false, 0, 1, true ), "AND : a positively-invalid control hides despite an unevaluable one" );
    }

    @Test
    public void testOrOneValidIsDisplayed( )
    {
        assertTrue( formsTaskService.isDisplayedFromControlOutcomes( true, 1, 1, false ), "OR with one control valid ⇒ displayed (kept)" );
    }

    @Test
    public void testOrNoneValidIsHidden( )
    {
        // the conditional target became hidden again : its response must be removed
        assertFalse( formsTaskService.isDisplayedFromControlOutcomes( true, 0, 2, false ), "OR with no control valid and none unevaluable ⇒ hidden (removed)" );
    }

    @Test
    public void testOrUnevaluableIsKept( )
    {
        // KEY : no control is valid, but one is unevaluable ⇒ keep the response
        assertTrue( formsTaskService.isDisplayedFromControlOutcomes( true, 0, 1, true ), "OR with an unevaluable control (none valid) ⇒ kept" );
    }
}
