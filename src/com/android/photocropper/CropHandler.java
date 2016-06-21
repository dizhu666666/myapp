package com.android.photocropper;

import android.app.Activity;
import android.net.Uri;

/**
 * Created with Eclipse . User: me.weizh@live.com Date: 10/1/14 Time: 11:00 AM
 * Desc: CropHandler Revision: - 10:20 2012/10/01 The basic interfaces. - 13:00
 * 2012/10/03 Able to get access to the CropParams the related Context.
 */
public interface CropHandler {

	Activity getContext();

	CropParams getCropParams();

	void onCropCancel();

	void onCropFailed(String message);

	void onPhotoCropped(Uri uri);
}
