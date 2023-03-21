package io.curity.identityserver.plugins.accountChooser

import io.curity.identityserver.plugins.accountChooser.AccountChooserAuthenticationActionDescriptor.Companion.ACTIVE_SESSIONS_ACRS
import io.curity.identityserver.plugins.accountChooser.AccountChooserAuthenticationActionDescriptor.Companion.AUTHENTICATION_ACR
import io.curity.identityserver.plugins.accountChooser.AccountChooserAuthenticationActionDescriptor.Companion.CHOSEN_ACR
import io.curity.identityserver.plugins.accountChooser.AccountChooserAuthenticationActionDescriptor.Companion.FORCE_AUTHENTICATION
import io.curity.identityserver.plugins.accountChooser.AccountChooserAuthenticationActionDescriptor.Companion.HAS_ACTIVE_SESSIONS
import io.curity.identityserver.plugins.accountChooser.AccountChooserAuthenticationActionDescriptor.Companion.TRANSACTION_ID
import se.curity.identityserver.sdk.attribute.Attribute
import se.curity.identityserver.sdk.attribute.Attributes
import se.curity.identityserver.sdk.authenticationaction.AuthenticationAction
import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionContext
import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionResult
import se.curity.identityserver.sdk.authenticationaction.completions.RequiredActionCompletion.AuthenticateUser
import se.curity.identityserver.sdk.authenticationaction.completions.RequiredActionCompletion.AuthenticateUser.authenticate
import se.curity.identityserver.sdk.authenticationaction.completions.RequiredActionCompletion.PromptUser.prompt

class AccountChooserAuthenticationAction(configuration: AccountChooserAuthenticationActionConfig): AuthenticationAction
{
    private val sessionManager = configuration.getSessionManager()
    private val authenticatorDescriptorFactory = configuration.getAuthenticatorDescriptorFactory()
    private val configACRs = configuration.getAllowedForAccountChooser()

    override fun apply(context: AuthenticationActionContext): AuthenticationActionResult
    {
        // If chosen-acr is present in the session then try to authenticate with the chosen authenticator.
        val chosenACR = sessionManager.remove(CHOSEN_ACR)
        if (chosenACR != null) {
            return authenticateWithChosenACR(chosenACR.getValueOfType(String::class.java))
        }

        // When there is a transaction ID in session and it is the same ID as the current transaction, it means that
        // we're back in this action after authenticating with the chosen ACR. Complete the action.
        val transactionIdFromSession = sessionManager.get(TRANSACTION_ID)?.getValueOfType(String::class.java)

        if (transactionIdFromSession == context.authenticationTransactionId) {
            completeChooseAccountAction(context)
        }

        prepareDataForChooseAccountPrompt(context)

        return AuthenticationActionResult.pendingResult(prompt())
    }

    private fun authenticateWithChosenACR(chosenACR: String): AuthenticationActionResult
    {
        val authenticator = authenticatorDescriptorFactory.getAuthenticatorDescriptors(chosenACR).first
        var authenticateActionCompletion = authenticate(authenticator) as AuthenticateUser

        val forceAuthentication = sessionManager.remove(FORCE_AUTHENTICATION)
        if (forceAuthentication != null && forceAuthentication.value == true) {
            authenticateActionCompletion = authenticateActionCompletion.withForceAuthentication(true)
        }

        sessionManager.put(Attribute.of(AUTHENTICATION_ACR, chosenACR))
        return AuthenticationActionResult.pendingResult(authenticateActionCompletion)
    }

    private fun completeChooseAccountAction(context: AuthenticationActionContext): AuthenticationActionResult
    {
        val acrUsedForAuthentication = sessionManager.remove(AUTHENTICATION_ACR)
        val subjectFromSSO = context.authenticatedSessions.matching(acrUsedForAuthentication.getValueOfType(String::class.java))?.subject

        val attributes = if (subjectFromSSO != null) {
            context.authenticationAttributes.withSubjectAttribute(Attribute.of("subject", subjectFromSSO))
        } else
        {
            context.authenticationAttributes
        }

        return AuthenticationActionResult.successfulResult(attributes)
    }

    private fun prepareDataForChooseAccountPrompt(context: AuthenticationActionContext)
    {
        val sessionACRs = context.authenticatedSessions

        val availableACRs = configACRs.map {acr ->
            val authenticatedSession = sessionACRs.matching(acr)
            return@map if (authenticatedSession != null) {
                mapOf("subject" to authenticatedSession.subject, "acr" to acr)
            } else {
                mapOf("acr" to acr)
            }
        }

        sessionManager.put(Attribute.of(ACTIVE_SESSIONS_ACRS, availableACRs))
        sessionManager.put(Attribute.of(TRANSACTION_ID, context.authenticationTransactionId))
        sessionManager.put(Attribute.of(HAS_ACTIVE_SESSIONS, sessionACRs.isNotEmpty))
    }
}
