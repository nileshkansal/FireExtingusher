package com.fireextinguisher.scanner.decode;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.fireextinguisher.QRCodeScannerActivity;
import com.fireextinguisher.R;
import com.fireextinguisher.client.ClientQRCodeScanActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.PlanarYUVLuminanceSource;
import com.google.zxing.ReaderException;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;


final class DecodeHandler extends Handler {

    private final Activity mActivity;
    private final QRCodeReader mQrCodeReader;
    private final Map<DecodeHintType, Object> mHints;
    private byte[] mRotatedData;

    DecodeHandler(Activity activity) {
        this.mActivity = activity;
        mQrCodeReader = new QRCodeReader();
        mHints = new Hashtable<>();
        mHints.put(DecodeHintType.CHARACTER_SET, "utf-8");
        mHints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
        mHints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.QR_CODE);
    }

    @Override
    public void handleMessage(Message message) {
        if( message.what == R.id.decode)
        {
            decode((byte[]) message.obj, message.arg1, message.arg2);
        }else if( message.what == R.id.quit)
        {
            Looper looper = Looper.myLooper();
                if (null != looper) {
                    looper.quit();
                }
        }
    }

    /**
     * Decode the data within the viewfinder rectangle, and time how long it took. For efficiency, reuse the same reader
     * objects from one decode to the next.
     *
     * @param data The YUV preview frame.
     * @param width The width of the preview frame.
     * @param height The height of the preview frame.
     */
    private void decode(byte[] data, int width, int height) {
        if (null == mRotatedData) {
            mRotatedData = new byte[width * height];
        } else {
            if (mRotatedData.length < width * height) {
                mRotatedData = new byte[width * height];
            }
        }
        Arrays.fill(mRotatedData, (byte) 0);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x + y * width >= data.length) {
                    break;
                }
                mRotatedData[x * height + height - y - 1] = data[x + y * width];
            }
        }
        int tmp = width; // Here we are swapping, that's the difference to #11
        width = height;
        height = tmp;

        Result rawResult = null;
        try {
            PlanarYUVLuminanceSource source =
                    new PlanarYUVLuminanceSource(mRotatedData, width, height, 0, 0, width, height, false);
            BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
            rawResult = mQrCodeReader.decode(bitmap1, mHints);
        } catch (ReaderException e) {
        } finally {
            mQrCodeReader.reset();
        }

        if (rawResult != null) {
            Message message;
            if (mActivity instanceof QRCodeScannerActivity) {
                message = Message.obtain(((QRCodeScannerActivity) mActivity).getCaptureActivityHandler(), R.id.decode_succeeded,
                        rawResult);
            } else {
                message = Message.obtain(((ClientQRCodeScanActivity) mActivity).getCaptureActivityHandler(), R.id.decode_succeeded,
                        rawResult);
            }
            message.sendToTarget();
        } else {
            Message message;
            if (mActivity instanceof QRCodeScannerActivity) {
                message = Message.obtain(((QRCodeScannerActivity) mActivity).getCaptureActivityHandler(), R.id.decode_failed);
            } else {
                message = Message.obtain(((ClientQRCodeScanActivity) mActivity).getCaptureActivityHandler(), R.id.decode_failed);
            }
            message.sendToTarget();
        }
    }
}
