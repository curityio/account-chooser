/*
 *  Copyright 2023 Curity AB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
