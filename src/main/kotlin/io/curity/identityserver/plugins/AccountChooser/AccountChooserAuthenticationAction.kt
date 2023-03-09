package io.curity.identityserver.plugins.AccountChooser

import se.curity.identityserver.sdk.authenticationaction.AuthenticationAction
import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionContext
import se.curity.identityserver.sdk.authenticationaction.AuthenticationActionResult

class AccountChooserAuthenticationAction(private val _configuration: AccountChooserAuthenticationActionConfig): AuthenticationAction
{
    override fun apply(context: AuthenticationActionContext): AuthenticationActionResult
    {

        // Have a look at this example: https://github.com/curityio/time-authentication-action
        // to see what you can do in a plugin. If you prefer going straight to the docs, here's a link:
        // https://curity.io/docs/idsvr/latest/developer-guide/plugins/index.html

        return AuthenticationActionResult.successfulResult(context.getAuthenticationAttributes())
    }
}
