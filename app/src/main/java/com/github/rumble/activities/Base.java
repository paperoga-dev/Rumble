/*
 * Rumble
 * Copyright (C) 2020

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.rumble.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.github.rumble.Constants;
import com.github.rumble.TumblrClient;
import com.github.rumble.api.Authenticate;
import com.github.rumble.exception.BaseException;

import org.scribe.model.Token;

public abstract class Base extends Activity {

    private Authenticate authenticator;
    static private TumblrClient client = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        createClient();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (getClient().isLogged())
            setToLoggedState();
        else
            resetToLoginState();
    }

    private void createClient() {
        if (client != null)
            return;

        client = new TumblrClient(getApplicationContext());

        client.setOnLoginListener(new TumblrClient.OnLoginListener() {
            @Override
            public void onAccessGranted() {
                setToLoggedState();
            }

            @Override
            public void onAccessRequest(
                    Authenticate authenticator,
                    Token requestToken,
                    String authenticationUrl) {

                setAuthenticator(authenticator);

                Intent i = new Intent(Base.this, Login.class);
                i.putExtra(Constants.REQUEST_TOKEN, requestToken);
                i.putExtra(Constants.AUTH_URL, authenticationUrl);
                startActivityForResult(i, Constants.PERFORM_LOGIN);
            }

            @Override
            public void onAccessDenied() {
                Toast.makeText(getApplicationContext(), "Access denied", Toast.LENGTH_SHORT).show();
                resetToLoginState();
            }

            @Override
            public void onLoginFailure(BaseException e) {
                Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                resetToLoginState();
            }
        });
    }

    private void setAuthenticator(Authenticate authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.v(Constants.APP_NAME, "ActivityResult");

        switch (requestCode) {
            case Constants.PERFORM_LOGIN:
                if (resultCode != RESULT_OK) {
                    Toast.makeText(getApplicationContext(), "Login failed: " + resultCode, Toast.LENGTH_SHORT).show();
                    resetToLoginState();
                    return;
                }

                authenticator.verify(
                        (Token) data.getSerializableExtra(Constants.REQUEST_TOKEN),
                        data.getStringExtra(Constants.OAUTH_VERIFIER)
                );
                break;

            default:
                break;
        }
    }

    protected void resetToLoginState() { }

    protected void setToLoggedState() { }

    static public TumblrClient getClient() {
        return client;
    }
}
