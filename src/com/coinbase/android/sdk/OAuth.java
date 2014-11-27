package com.coinbase.android.sdk;

import java.io.IOException;
import java.net.URI;
import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.coinbase.api.Coinbase;
import com.coinbase.api.CoinbaseBuilder;
import com.coinbase.api.entity.OAuthCodeRequest;
import com.coinbase.api.entity.OAuthTokensResponse;
import com.coinbase.api.exception.CoinbaseException;
import com.coinbase.api.exception.UnauthorizedException;

public class OAuth {

    public static String KEY_LOGIN_CSRF_TOKEN = "com.coinbase.android.sdk.login_csrf_token";

    public static void beginAuthorization(Context context, String clientId,
            String scope, String redirectUri, OAuthCodeRequest.Meta meta)
            throws CoinbaseException {

        Coinbase coinbase = new CoinbaseBuilder().build();

        OAuthCodeRequest request = new OAuthCodeRequest();
        request.setClientId(clientId);
        request.setScope(scope);
        request.setRedirectUri(redirectUri);
        request.setMeta(meta);

        URI authorizationUri = coinbase.getAuthorizationUri(request);

        Intent i = new Intent(Intent.ACTION_VIEW);
        Uri androidUri = Uri.parse(authorizationUri.toString());
        androidUri = androidUri.buildUpon().appendQueryParameter("state", getLoginCSRFToken(context)).build();
        i.setData(androidUri);
        context.startActivity(i);
    }

    public static OAuthTokensResponse completeAuthorization(Context context, String clientId,
            String clientSecret, Uri redirectUri) throws UnauthorizedException, IOException {

        String csrfToken = redirectUri.getQueryParameter("state");
        String authCode = redirectUri.getQueryParameter("code");

        if (csrfToken == null || !csrfToken.equals(getLoginCSRFToken(context))) {
            throw new UnauthorizedException("CSRF Detected!");
        } else if (authCode == null) {
            String errorDescription = redirectUri.getQueryParameter("error_description");
            throw new UnauthorizedException(errorDescription);
        } else {
            try {
                Coinbase coinbase = new CoinbaseBuilder().build();
                Uri redirectUriWithoutQuery = redirectUri.buildUpon().clearQuery().build();
                return coinbase.getTokens(clientId, clientSecret, authCode, redirectUriWithoutQuery.toString());
            } catch (CoinbaseException ex) {
                throw new UnauthorizedException(ex.getMessage());
            }
        }
    }

    public static String getLoginCSRFToken(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

        int result = prefs.getInt(KEY_LOGIN_CSRF_TOKEN, 0);
        if (result == 0) {
            result = (new Random()).nextInt();
            SharedPreferences.Editor e = prefs.edit();
            e.putInt(KEY_LOGIN_CSRF_TOKEN, result);
            e.commit();
        }

        return new Integer(result).toString();
    }
}
