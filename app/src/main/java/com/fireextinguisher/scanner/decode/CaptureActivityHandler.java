package com.fireextinguisher.scanner.decode;


import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fireextinguisher.QRCodeScannerActivity;
import com.fireextinguisher.R;
import com.fireextinguisher.client.ClientQRCodeScanActivity;
import com.fireextinguisher.scanner.camera.CameraManager;
import com.google.zxing.Result;

/**
 * This class handles all the messaging which comprises the state machine for capture.
 */
public final class CaptureActivityHandler extends Handler {
    private static final String TAG = CaptureActivityHandler.class.getName();

    private final Activity mActivity;
    private final DecodeThread mDecodeThread;
    private State mState;

    public CaptureActivityHandler(Activity activity) {
        this.mActivity = activity;
        mDecodeThread = new DecodeThread(activity);
        mDecodeThread.start();
        mState = State.SUCCESS;
        // Start ourselves capturing previews and decoding.
        restartPreviewAndDecode();
    }

    @Override
    public void handleMessage(Message message) {
        if (message.what == R.id.auto_focus) {
            Log.d(TAG, "Got auto-focus message");
            // When one auto focus pass finishes, start another. This is the closest thing to
            // continuous AF. It does seem to hunt a bit, but I'm not sure what else to do.
            if (mState == State.PREVIEW) {
                CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
            }
        } else if (message.what == R.id.decode_succeeded) {
            Log.e(TAG, "Got decode succeeded message");
            mState = State.SUCCESS;
            if (mActivity instanceof QRCodeScannerActivity) {
                ((QRCodeScannerActivity) mActivity).handleDecode((Result) message.obj);
            }  else {
                ((ClientQRCodeScanActivity) mActivity).handleDecode((Result) message.obj);
            }
        } else if (message.what == R.id.decode_failed) {
            // We're decoding as fast as possible, so when one decode fails, start another.
            mState = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
        }
    }

    public void quitSynchronously() {
        mState = State.DONE;
        CameraManager.get().stopPreview();
        Message quit = Message.obtain(mDecodeThread.getHandler(), R.id.quit);
        quit.sendToTarget();
        try {
            mDecodeThread.join();
        } catch (InterruptedException e) {
            // continue
        }
        removeMessages(R.id.decode_succeeded);
        removeMessages(R.id.decode_failed);
    }

    public void restartPreviewAndDecode() {
        if (mState != State.PREVIEW) {
            CameraManager.get().startPreview();
            mState = State.PREVIEW;
            CameraManager.get().requestPreviewFrame(mDecodeThread.getHandler(), R.id.decode);
            CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
        }
    }

    private enum State {
        PREVIEW, SUCCESS, DONE
    }

}
