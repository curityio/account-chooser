package io.curity.identityserver.plugins.accountChooser

import io.curity.identityserver.plugins.accountChooser.AccountChooserAuthenticationActionDescriptor.Companion.ACTIVE_SESSIONS_ACRS
import io.curity.identityserver.plugins.accountChooser.AccountChooserAuthenticationActionDescriptor.Companion.CHOSEN_ACR
import io.curity.identityserver.plugins.accountChooser.AccountChooserAuthenticationActionDescriptor.Companion.FORCE_AUTHENTICATION
import io.curity.identityserver.plugins.accountChooser.AccountChooserAuthenticationActionDescriptor.Companion.HAS_ACTIVE_SESSIONS
import se.curity.identityserver.sdk.attribute.Attribute
import se.curity.identityserver.sdk.attribute.Attributes
import se.curity.identityserver.sdk.authenticationaction.completions.ActionCompletionRequestHandler
import se.curity.identityserver.sdk.authenticationaction.completions.ActionCompletionResult
import se.curity.identityserver.sdk.web.Request
import se.curity.identityserver.sdk.web.Response
import se.curity.identityserver.sdk.web.ResponseModel.templateResponseModel
import java.lang.Exception
import java.util.Optional

class AccountChooserRequestHandler(configuration: AccountChooserAuthenticationActionConfig): ActionCompletionRequestHandler<AccountChooserRequestModel>
{
    private val authenticatorDescriptorFactory = configuration.getAuthenticatorDescriptorFactory()
    private val sessionManager = configuration.getSessionManager()

    override fun preProcess(request: Request, response: Response): AccountChooserRequestModel
    {
        response.setResponseModel(templateResponseModel(mapOf(), "index"), Response.ResponseModelScope.ANY)
        val forceAuthenticationField = request.getFormParameterValueOrError("force_authentication")

        return AccountChooserRequestModel(
            request.getFormParameterValueOrError("chosen_acr"),
            (forceAuthenticationField != null && forceAuthenticationField == "true")
        )
    }

    override fun get(requestModel: AccountChooserRequestModel, response: Response): Optional<ActionCompletionResult>
    {
        val activeSessionsACRs = sessionManager.remove(ACTIVE_SESSIONS_ACRS)

        if (activeSessionsACRs != null) {
            val authenticators = (activeSessionsACRs.value as List<Map<String, String>>).map {
                val acr = it["acr"]!!
                val authenticator = authenticatorDescriptorFactory.getAuthenticatorDescriptors(acr).first
                return@map AuthenticatorForView(
                    acr,
                    authenticator.description,
                    authenticator.type,
                    it["subject"]
                )
            }

            response.putViewData("authenticators", authenticators, Response.ResponseModelScope.ANY)
            response.putViewData("hasActiveSessions", sessionManager.remove(HAS_ACTIVE_SESSIONS)?.value ?: false, Response.ResponseModelScope.ANY)

            return Optional.empty()
        }

        throw InvalidActionState()
    }

    override fun post(requestModel: AccountChooserRequestModel, response: Response): Optional<ActionCompletionResult>
    {
        // Put the chosen ACR in the session. If no ACR present, throw an error.
        if (requestModel.chosenAcr == null) {
            throw InvalidActionState()
        }

        sessionManager.put(Attribute.of(CHOSEN_ACR, requestModel.chosenAcr))
        sessionManager.put(Attribute.of(FORCE_AUTHENTICATION, requestModel.forceAuthentication))

        return Optional.of(ActionCompletionResult.complete())
    }
}

class AccountChooserRequestModel(val chosenAcr: String?, val forceAuthentication: Boolean)

class AuthenticatorForView(val acr: String, val description: String, val type: String, val subject: String?)

class InvalidActionState: Exception()
