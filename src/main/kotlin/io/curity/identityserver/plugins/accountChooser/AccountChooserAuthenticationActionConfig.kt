package io.curity.identityserver.plugins.accountChooser

import se.curity.identityserver.sdk.config.Configuration
import se.curity.identityserver.sdk.config.annotation.Description
import se.curity.identityserver.sdk.config.annotation.ListKey
import se.curity.identityserver.sdk.config.annotation.Name
import se.curity.identityserver.sdk.service.SessionManager
import se.curity.identityserver.sdk.service.authentication.AuthenticatorInformationProvider
import se.curity.identityserver.sdk.service.authenticationaction.AuthenticatorDescriptorFactory
import java.util.Optional

interface AccountChooserAuthenticationActionConfig: Configuration
{
    @Description("List of ACRs of the authenticators that can be used to log in using the account chooser action.")
    @Name("allowed-for-account-chooser")
    fun getAllowedForAccountChooser(): List<String>

    fun getAuthenticatorDescriptorFactory(): AuthenticatorDescriptorFactory

    fun getSessionManager(): SessionManager


}
