package com.appcelerator.cloud.sdk.oauth2;

import android.os.Bundle;

import com.appcelerator.cloud.sdk.CocoafishError;

/**
 * Callback interface for dialog requests.
 *
 */
public interface DialogListener {

    /**
     * Called when a dialog completes.
     *
     * Executed by the thread that initiated the dialog.
     *
     * @param values
     *            Key-value string pairs extracted from the response.
     */
    public void onComplete(Bundle values);

    /**
     * Called when a response to a dialog with an error.
     *
     * Executed by the thread that initiated the dialog.
     *
     */
    public void onCocoafishError(CocoafishError e);

    /**
     * Called when a dialog has an error.
     *
     * Executed by the thread that initiated the dialog.
     *
     */
    public void onError(DialogError e);

    /**
     * Called when a dialog is canceled by the user.
     *
     * Executed by the thread that initiated the dialog.
     *
     */
    public void onCancel();

}
